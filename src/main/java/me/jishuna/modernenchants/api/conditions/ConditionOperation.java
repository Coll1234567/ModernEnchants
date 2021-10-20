package me.jishuna.modernenchants.api.conditions;

import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public enum ConditionOperation {
	EQUAL, GREATER_THAN, LESS_THAN;

	public static ConditionOperation parseCondition(String string) throws InvalidEnchantmentException {
		switch (string) {
		case "=":
			return EQUAL;
		case ">":
			return GREATER_THAN;
		case "<":
			return LESS_THAN;
		default:
			throw new InvalidEnchantmentException(
					"Invalid format, expected the first character to be \"=\", \">\" or \">\" but found: " + string);
		}
	}
}
