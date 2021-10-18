package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "burn")
public class BurnEffect extends EnchantmentEffect {

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		int duration = Integer.parseInt(data[1]);
		if (duration < 0)
			throw new InvalidEnchantmentException("Duration must be greater than 0");

		return context -> context.getTarget(target).ifPresent(entity -> entity.setFireTicks(duration));
	}
}