package me.jishuna.modernenchants.api;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum DescriptionFormat {
	NONE, INLINE, SEPERATE;
	
	public static final Set<String> ALL_FORMATS = Arrays.stream(values()).map(Enum::toString)
			.collect(Collectors.toSet());
}
