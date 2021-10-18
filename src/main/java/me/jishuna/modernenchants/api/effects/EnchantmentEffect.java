package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public abstract class EnchantmentEffect {
	public abstract Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException;

	protected void checkLength(String[] args, int min) throws InvalidEnchantmentException {
		if (args.length < min)
			throw new InvalidEnchantmentException("This effect requires at least " + min + "arguments");
	}
}
