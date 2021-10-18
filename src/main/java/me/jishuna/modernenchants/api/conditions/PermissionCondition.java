package me.jishuna.modernenchants.api.conditions;

import java.util.function.Predicate;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "has_permission")
public class PermissionCondition extends EnchantmentCondition {

	@Override
	public Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 1);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);
		
		String perm = data[1];
		
		boolean bool = Boolean.parseBoolean(data[2]);

		return context -> {
			LivingEntity entity = context.getTarget(target).orElse(null);

			if (entity instanceof Player player) {
				return player.hasPermission(perm) == bool;
			}
			return false;
		};
	}

}
