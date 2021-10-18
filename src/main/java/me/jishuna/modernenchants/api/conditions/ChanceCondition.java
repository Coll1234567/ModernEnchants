package me.jishuna.modernenchants.api.conditions;

import java.util.Random;
import java.util.function.Predicate;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "chance")
public class ChanceCondition extends EnchantmentCondition {
	private final Random random = new Random();

	@Override
	public Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 1);

		double chance = Double.parseDouble(data[0]);
		if (chance <= 0)
			throw new InvalidEnchantmentException("Chance must be greater than 0");
		if (chance > 100)
			throw new InvalidEnchantmentException("Chance must not be greater than 100");

		return context -> (random.nextDouble() * 100d) < chance;
	}

}
