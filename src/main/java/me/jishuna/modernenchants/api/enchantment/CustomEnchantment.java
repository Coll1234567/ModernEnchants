package me.jishuna.modernenchants.api.enchantment;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readMaterial;

import java.util.ArrayList;
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
import me.jishuna.modernenchants.api.condition.CooldownCondition;
import me.jishuna.modernenchants.api.condition.EnchantmentCondition;
import me.jishuna.modernenchants.api.effect.DelayEffect;
import me.jishuna.modernenchants.api.effect.EnchantmentEffect;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.utils.ParseUtils;

public class CustomEnchantment extends Enchantment {
	private final ModernEnchants plugin;

	private final String name;
	private final String description;
	private final String longDescription;
	private final String displayName;
	private final String group;

	private final double enchantingWeight;
	private final int minLevel;
	private final int maxLevel;
	private final boolean cursed;
	private final boolean treasure;

	private final List<String> validItemsRaw = new ArrayList<>();
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);
	private final Set<ActionType> actions = new HashSet<>();
	private final Set<String> conflicts;

	private final Map<Integer, EnchantmentLevel> levels = new HashMap<>();

	public CustomEnchantment(ModernEnchants plugin, ConfigurationSection section) throws InvalidEnchantmentException {
		super(new NamespacedKey(plugin, section.getString("name")));
		this.plugin = plugin;

		this.name = section.getString("name").toLowerCase();
		this.displayName = ParseUtils.colorString(section.getString("display-name", name));
		this.description = ParseUtils.colorString(section.getString("description"));
		this.longDescription = ParseUtils.colorString(section.getString("description-long", this.description));

		this.group = section.getString("group", null);

		ConfigurationSection weights = section.getConfigurationSection("weights");

		this.enchantingWeight = weights.getDouble("enchanting", 100d);

		this.minLevel = section.getInt("min-level", 1);
		this.maxLevel = section.getInt("max-level", 5);

		this.cursed = section.getBoolean("cursed", false);
		this.treasure = section.getBoolean("treasure", false);

		for (String action : section.getStringList("actions")) {
			action = action.toUpperCase();

			if (!ActionType.ALL_ACTIONS.contains(action))
				throw new InvalidEnchantmentException("Invalid enchantment action: " + action);

			this.actions.add(ActionType.valueOf(action));
		}

		List<String> validItemStrings = section.getStringList("valid-items");
		for (String item : validItemStrings) {
			this.validItems.addAll(readMaterial(item.toUpperCase()));
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
	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public String getLongDescription() {
		return longDescription;
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getStartLevel() {
		return this.minLevel;
	}

	public double getEnchantingWeight() {
		return enchantingWeight;
	}

	public List<String> getValidItemsRaw() {
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
		if (!(other instanceof CustomEnchantment enchant)) {
			return this.conflicts.contains(other.getKey().toString());
		}

		if (this.group != null && enchant.group != null && this.group.equals(enchant.group)) {
			return true;
		}

		return this.conflicts.contains(enchant.getName());
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		if (!this.validItems.contains(item.getType()))
			return false;

		for (Enchantment enchant : item.getEnchantments().keySet()) {
			if (this.conflictsWith(enchant))
				return false;
		}
		return true;
	}
}
