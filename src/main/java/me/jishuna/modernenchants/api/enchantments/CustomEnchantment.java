package me.jishuna.modernenchants.api.enchantments;

import static me.jishuna.modernenchants.api.ParseUtils.readMaterial;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.DescriptionFormat;
import me.jishuna.modernenchants.api.ParseUtils;
import me.jishuna.modernenchants.api.conditions.ConditionRegistry;
import me.jishuna.modernenchants.api.conditions.CooldownCondition;
import me.jishuna.modernenchants.api.conditions.EnchantmentCondition;
import me.jishuna.modernenchants.api.effects.DelayEffect;
import me.jishuna.modernenchants.api.effects.EffectRegistry;
import me.jishuna.modernenchants.api.effects.EnchantmentEffect;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public class CustomEnchantment extends Enchantment {
	private final JavaPlugin plugin;
	private final String name;
	private final String description;
	private final String displayName;
	private final double weight;
	private final int minLevel;
	private final int maxLevel;
	private final DescriptionFormat format;
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);
	private final Set<ActionType> actions = new HashSet<>();

	private final Map<Integer, EnchantmentLevel> levels = new HashMap<>();

	public CustomEnchantment(JavaPlugin plugin, EffectRegistry effectRegistry, ConditionRegistry conditionRegistry,
			ConfigurationSection section) throws InvalidEnchantmentException {
		super(new NamespacedKey(plugin, section.getString("name")));
		this.plugin = plugin;

		this.name = section.getString("name").toLowerCase();
		this.displayName = ParseUtils.colorString(section.getString("display-name", name));
		this.description = ParseUtils.colorString(section.getString("description"));

		this.weight = section.getDouble("weight", 100d);

		this.minLevel = section.getInt("min-level", 1);
		this.maxLevel = section.getInt("max-level", 5);

		String format = section.getString("description-format");
		if (!DescriptionFormat.ALL_FORMATS.contains(format))
			throw new InvalidEnchantmentException("Invalid description format: " + format);

		this.format = DescriptionFormat.valueOf(format);

		for (String action : section.getStringList("actions")) {
			action = action.toUpperCase();

			if (!ActionType.ALL_ACTIONS.contains(action))
				throw new InvalidEnchantmentException("Invalid enchantment action: " + action);

			this.actions.add(ActionType.valueOf(action));
		}

		for (String item : section.getStringList("valid-items")) {
			this.validItems.addAll(readMaterial(item.toUpperCase()));
		}

		ConfigurationSection levels = section.getConfigurationSection("levels");

		for (String levelString : levels.getKeys(false)) {
			ConfigurationSection levelSection = levels.getConfigurationSection(levelString);

			if (levelSection == null)
				continue;

			int level = Integer.parseInt(levelString);
			this.levels.put(level, new EnchantmentLevel(levelSection, effectRegistry, conditionRegistry));
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
			processActionsDirect(level, context, encahntmentLevel);
		} else {
			processActionsDelay(level, context, encahntmentLevel);
		}
	}

	private void processActionsDirect(int level, EnchantmentContext context, EnchantmentLevel encahntmentLevel) {
		for (EnchantmentEffect effect : encahntmentLevel.getEffects()) {
			effect.handle(context);
		}

	}

	private void processActionsDelay(int level, EnchantmentContext context, EnchantmentLevel encahntmentLevel) {
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

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getStartLevel() {
		return this.minLevel;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	public Map<Integer, EnchantmentLevel> getLevels() {
		return levels;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
	}

	public DescriptionFormat getDescriptionFormat() {
		return format;
	}

	@Override
	public boolean isTreasure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCursed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment var1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return this.validItems.contains(item.getType());
	}

}
