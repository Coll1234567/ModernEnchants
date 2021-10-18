package me.jishuna.modernenchants.api;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ActionType {
	BREAK_BLOCK, BLOCK_DROP_ITEMS, ATTACK_PLAYER, ATTACK_MOB, CATCH_FISH;

	public static final Set<String> ALL_ACTIONS = Arrays.stream(values()).map(Enum::toString)
			.collect(Collectors.toSet());
}
