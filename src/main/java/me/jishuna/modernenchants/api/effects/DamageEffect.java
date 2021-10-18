package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.RegisterEffect;

@RegisterEffect(name = "damage")
public class DamageEffect extends EnchantmentEffect {

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);
		
		ActionTarget target = ActionTarget.valueOf(targetString);

		double damage = Double.parseDouble(data[1]);
		if (damage < 0)
			throw new InvalidEnchantmentException("Damage must be greater than 0");

		return context -> context.getTarget(target).ifPresent(entity -> entity.damage(damage));
	}
}
