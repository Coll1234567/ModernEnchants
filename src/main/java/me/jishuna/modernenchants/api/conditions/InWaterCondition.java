package me.jishuna.modernenchants.api.conditions;

import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;

@RegisterCondition(name = "in_water")
public class InWaterCondition extends EnchantmentCondition {

	@Override
	public Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 1);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);	
		boolean bool = Boolean.parseBoolean(data[1]);

		return context -> {
			Optional<LivingEntity> entityOptional = context.getTarget(target);

			if (entityOptional.isPresent()) {
				return entityOptional.get().isInWater() == bool;
			}
			return false;
		};
	}

}

