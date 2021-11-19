package me.jishuna.modernenchants.api.enchantment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.jishuna.modernenchants.api.condition.ConditionRegistry;
import me.jishuna.modernenchants.api.condition.EnchantmentCondition;
import me.jishuna.modernenchants.api.effect.DelayEffect;
import me.jishuna.modernenchants.api.effect.EffectRegistry;
import me.jishuna.modernenchants.api.effect.EnchantmentEffect;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

public class EnchantmentLevel {
	private final List<EnchantmentEffect> effects = new ArrayList<>();
	private final List<EnchantmentCondition> conditions = new ArrayList<>();
	private boolean hasDelay;
	private final int minExperienceLevel;

	public EnchantmentLevel(ConfigurationSection section, EffectRegistry effectRegistry,
			ConditionRegistry conditionRegistry) throws InvalidEnchantmentException {
		this.minExperienceLevel = section.getInt("min-enchanting-level");
		
		// Parse effects
		for (String actionString : section.getStringList("effects")) {
			EnchantmentEffect effect = effectRegistry.parseString(actionString);

			if (effect != null) {
				effects.add(effect);
			}

			if (effect instanceof DelayEffect) {
				this.hasDelay = true;
			}
		}

		// Parse conditions
		for (String conditionString : section.getStringList("conditions")) {
			EnchantmentCondition condition = conditionRegistry.parseString(conditionString);

			if (condition != null) {
				conditions.add(condition);
			}
		}
	}

	public List<EnchantmentEffect> getEffects() {
		return effects;
	}

	public List<EnchantmentCondition> getConditions() {
		return conditions;
	}

	public int getMinExperienceLevel() {
		return minExperienceLevel;
	}

	public boolean hasDelay() {
		return hasDelay;
	}
}
