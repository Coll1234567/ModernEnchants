package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public abstract class EnchantmentEffect {
	private final String[] description;
	
	
	public EnchantmentEffect(String[] description) {
		this.description = description;
	}

	public abstract Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException;

	protected void checkLength(String[] args, int min) throws InvalidEnchantmentException {
		if (args.length < min)
			throw new InvalidEnchantmentException("This effect requires at least " + min + "arguments");
	}

	public String[] getDescription() {
		return description;
	}
}
