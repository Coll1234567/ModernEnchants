package me.jishuna.modernenchants.api.conditions;

import java.util.function.Predicate;

import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public abstract class EnchantmentCondition {
	public abstract Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException;

	protected void checkLength(String[] args, int min) throws InvalidEnchantmentException {
		if (args.length < min)
			throw new InvalidEnchantmentException("This condition requires at least " + min + "arguments");
	}
}
