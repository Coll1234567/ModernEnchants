package me.jishuna.modernenchants.api;

import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public class ParseUtils {
	public static void checkLength(String[] args, int min) throws InvalidEnchantmentException {
		if (args.length < min)
			throw new InvalidEnchantmentException("This effect requires at least " + min + "arguments");
	}

	public static ActionTarget readTarget(String string) throws InvalidEnchantmentException {
		if (!ActionTarget.ALL_TARGETS.contains(string.toUpperCase()))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + string);
		return ActionTarget.valueOf(string.toUpperCase());
	}

	public static int readInt(String string) throws InvalidEnchantmentException {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a whole number but found: " + string);
		}
	}

	public static double readDouble(String string) throws InvalidEnchantmentException {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a number but found: " + string);
		}
	}
	
	public static float readFloat(String string) throws InvalidEnchantmentException {
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a number but found: " + string);
		}
	}
}
