package me.jishuna.modernenchants.api.conditions;

import java.util.function.Predicate;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;

@RegisterCondition(name = "is_sneaking")
public class SneakingCondition extends EnchantmentCondition {

	@Override
	public Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 1);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);
		
		boolean bool = Boolean.parseBoolean(data[1]);

		return context -> {
			LivingEntity entity = context.getTarget(target).orElse(null);

			if (entity instanceof Player player) {
				return player.isSneaking() == bool;
			}
			return false;
		};
	}

}
