package me.jishuna.modernenchants.api.effect;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ActionTarget {
	USER, OPPONENT;
	
	public static final Set<String> ALL_TARGETS = Arrays.stream(values()).map(Enum::toString)
			.collect(Collectors.toSet());
}
