package me.jishuna.modernenchants.api.enchantments;

import static me.jishuna.modernenchants.api.ParseUtils.readMaterial;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.conditions.ConditionRegistry;
import me.jishuna.modernenchants.api.conditions.EnchantmentCondition;
import me.jishuna.modernenchants.api.effects.DelayEffect;
import me.jishuna.modernenchants.api.effects.EffectRegistry;
import me.jishuna.modernenchants.api.effects.EnchantmentEffect;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

public class CustomEnchantment extends Enchantment {
	private final JavaPlugin plugin;
	private final String name;
	private final String displayName;
	private final int minLevel;
	private final int maxLevel;
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);

	private boolean hasDelay = false;

	private final Set<ActionType> actions = new HashSet<>();
	private final Multimap<Integer, EnchantmentEffect> effects = ArrayListMultimap.create();
	private final Multimap<Integer, EnchantmentCondition> conditions = ArrayListMultimap.create();

	public CustomEnchantment(JavaPlugin plugin, EffectRegistry effectRegistry, ConditionRegistry conditionRegistry,
			ConfigurationSection section) throws InvalidEnchantmentException {
		super(new NamespacedKey(plugin, section.getString("name")));
		this.plugin = plugin;

		this.name = section.getString("name").toLowerCase();
		this.displayName = ChatColor.translateAlternateColorCodes('&', section.getString("display-name", name));

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
		}

		ConfigurationSection levels = section.getConfigurationSection("levels");

		for (String levelString : levels.getKeys(false)) {
			ConfigurationSection levelSection = levels.getConfigurationSection(levelString);

			if (levelSection == null)
				continue;

			int level = Integer.parseInt(levelString);
			for (String actionString : levelSection.getStringList("effects")) {
				EnchantmentEffect effect = effectRegistry.parseString(actionString);

				if (effect != null) {
					effects.put(level, effect);
				}

				if (effect instanceof DelayEffect delay) {
					this.hasDelay = true;
				}
			}

			for (String conditionString : levelSection.getStringList("conditions")) {
				EnchantmentCondition condition = conditionRegistry.parseString(conditionString);

				if (condition != null) {
					conditions.put(level, condition);
				}
			}
		}
	}

	public void processActions(int level, EnchantmentContext context) {
		if (!listensFor(context.getType()))
			return;

		for (EnchantmentCondition condition : this.conditions.get(level)) {
			if (!condition.check(context))
				return;
		}

		if (!hasDelay) {
			processActionsDirect(level, context);
		} else {
			processActionsDelay(level, context);
		}
	}

	private void processActionsDirect(int level, EnchantmentContext context) {
		for (EnchantmentEffect effect : this.effects.get(level)) {
			effect.handle(context);
		}

	}

	private void processActionsDelay(int level, EnchantmentContext context) {
		List<EnchantmentEffect> effectList = new ArrayList<>(this.effects.get(level));
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

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getStartLevel() {
		return this.minLevel;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
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
