package me.jishuna.modernenchants.api.enchantment;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readMaterial;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Sets;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.condition.CooldownCondition;
import me.jishuna.modernenchants.api.condition.EnchantmentCondition;
import me.jishuna.modernenchants.api.effect.DelayEffect;
import me.jishuna.modernenchants.api.effect.EnchantmentEffect;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.utils.ParseUtils;
import net.md_5.bungee.api.ChatColor;

public class CustomEnchantment extends Enchantment implements IEnchantment {
	private final ModernEnchants plugin;

	private final String name;
	private final String description;
	private final String longDescription;
	private String displayName;
	private final String group;

	private final int minLevel;
	private final int maxLevel;
	private final boolean cursed;
	private final boolean treasure;

	private final Set<String> validItemsRaw = new HashSet<>();
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);
	private final Set<Material> validItemsAnvil = EnumSet.noneOf(Material.class);
	private final Set<ActionType> actions = new HashSet<>();
	private final Set<String> conflicts;

	private final Map<ObtainMethod, Double> weights = new EnumMap<>(ObtainMethod.class);
	private final Map<Integer, EnchantmentLevel> levels = new HashMap<>();

	public CustomEnchantment(ModernEnchants plugin, ConfigurationSection section) throws InvalidEnchantmentException {
		super(new NamespacedKey(plugin, section.getString("name")));
		this.plugin = plugin;

		this.name = section.getString("name").toLowerCase();

		this.cursed = section.getBoolean("cursed", false);
		this.treasure = section.getBoolean("treasure", false);

		this.displayName = ParseUtils.colorString(section.getString("display-name", name));
		if (plugin.getConfiguration().getBoolean("force-vanilla-enchantment-colors", false)) {
			this.displayName = (cursed ? ChatColor.RED : ChatColor.GRAY) + ChatColor.stripColor(this.displayName);
		}

		this.description = ParseUtils.colorString(section.getString("description"));
		this.longDescription = ParseUtils.colorString(section.getString("description-long", this.description));

		this.group = section.getString("group", null);

		ConfigurationSection weightSection = section.getConfigurationSection("weights");

		this.weights.put(ObtainMethod.ENCHANTING, weightSection.getDouble("enchanting", 100d));
		this.weights.put(ObtainMethod.VILLAGER, weightSection.getDouble("trading", 100d));
		this.weights.put(ObtainMethod.LOOT, weightSection.getDouble("loot", 100d));

		this.minLevel = section.getInt("min-level", 1);
		this.maxLevel = section.getInt("max-level", 5);

		for (String action : section.getStringList("actions")) {
			action = action.toUpperCase();

			if (!ActionType.ALL_ACTIONS.contains(action))
				throw new InvalidEnchantmentException("Invalid enchantment action: " + action);

			this.actions.add(ActionType.valueOf(action));
		}

		for (String item : section.getStringList("valid-items")) {
			this.validItems.addAll(readMaterial(item.toUpperCase()));
			this.validItemsRaw.add(StringUtils.capitalize(item.toLowerCase().replace("_", " ")));
		}

		this.validItemsAnvil.addAll(this.validItems);
		for (String item : section.getStringList("valid-items-anvil")) {
			this.validItemsAnvil.addAll(readMaterial(item.toUpperCase()));
			this.validItemsRaw.add(StringUtils.capitalize(item.toLowerCase().replace("_", " ")));
		}

		this.conflicts = Sets.newHashSet(section.getStringList("conflicts"));

		ConfigurationSection levelsSection = section.getConfigurationSection("levels");

		for (String levelString : levelsSection.getKeys(false)) {
			ConfigurationSection levelSection = levelsSection.getConfigurationSection(levelString);

			if (levelSection == null)
				continue;

			int level = Integer.parseInt(levelString);

			// Parse a single level
			try {
				this.levels.put(level,
						new EnchantmentLevel(levelSection, plugin.getEffectRegistry(), plugin.getConditionRegistry()));
			} catch (InvalidEnchantmentException ex) {
				ex.addAdditionalInfo("Error parsing level " + level + ":");
				throw ex;
			}
		}
	}

	public void processActions(int level, EnchantmentContext context) {
		if (!listensFor(context.getType()))
			return;

		EnchantmentLevel encahntmentLevel = this.levels.get(level);
		CooldownCondition cooldown = null;

		for (EnchantmentCondition condition : encahntmentLevel.getConditions()) {
			if (!condition.check(context, this))
				return;

			if (condition instanceof CooldownCondition cool)
				cooldown = cool;
		}

		if (cooldown != null)
			cooldown.setCooldown(context, this);

		if (!encahntmentLevel.hasDelay()) {
			processActionsDirect(context, encahntmentLevel);
		} else {
			processActionsDelay(context, encahntmentLevel);
		}
	}

	private void processActionsDirect(EnchantmentContext context, EnchantmentLevel encahntmentLevel) {
		for (EnchantmentEffect effect : encahntmentLevel.getEffects()) {
			effect.handle(context);
		}

	}

	private void processActionsDelay(EnchantmentContext context, EnchantmentLevel encahntmentLevel) {
		List<EnchantmentEffect> effectList = new ArrayList<>(encahntmentLevel.getEffects());
		final int size = effectList.size();

		new BukkitRunnable() {
			int index;
			int delay;

			@Override
			public void run() {
				if (delay > 0) {
					delay--;
					return;
				}

				while (index < size) {
					EnchantmentEffect effect = effectList.get(index);
					index++;

					if (effect instanceof DelayEffect delayEffect) {
						delay = delayEffect.getDelay();
						return;
					} else {
						effect.handle(context);
					}
				}
				cancel();
			}
		}.runTaskTimer(plugin, 0, 1);

	}

	public boolean listensFor(ActionType type) {
		return this.actions.contains(type);
	}

	@Override
	public Enchantment getEnchantment() {
		return this;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String getLongDescription() {
		return longDescription;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getStartLevel() {
		return this.minLevel;
	}

	@Override
	public int getMinLevelCost() {
		EnchantmentLevel level = this.levels.get(this.minLevel);

		if (level == null)
			return 0;
		return level.getMinExperienceLevel();
	}

	@Override
	public double getWeight(ObtainMethod method) {
		return this.weights.getOrDefault(method, 0d);
	}

	@Override
	public Set<Material> getValidItems() {
		return validItems;
	}

	@Override
	public Set<String> getValidItemsRaw() {
		return validItemsRaw;
	}

	public Map<Integer, EnchantmentLevel> getLevels() {
		return levels;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
	}

	@Override
	public boolean isTreasure() {
		return this.treasure;
	}

	@Override
	public boolean isCursed() {
		return this.cursed;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		if (!(other instanceof IEnchantment enchant)) {
			return this.conflicts.contains(other.getKey().toString());
		}

		if (this.group != null && enchant.getGroup() != null && this.group.equals(enchant.getGroup())) {
			return true;
		}

		return this.conflicts.contains(enchant.getName());
	}

	@Override
	public boolean conflictsWith(IEnchantment other) {
		if (this.group != null && other.getGroup() != null && this.group.equals(other.getGroup())) {
			return true;
		}

		return this.conflicts.contains(other.getName());
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return canEnchantItem(item, false);
	}

	@Override
	public boolean canEnchantItem(ItemStack item, boolean table) {
		if (table)
			return this.validItems.contains(item.getType());
		return this.validItemsAnvil.contains(item.getType());
	}
}
