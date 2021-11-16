package me.jishuna.modernenchants.api;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public enum ActionType {
	BREAK_BLOCK, BLOCK_DROP_ITEMS, ATTACK_PLAYER, ATTACK_MOB, HURTBY_PLAYER, HURTBY_MOB, ATTACK_PLAYER_PROJECTILE,
	ATTACK_MOB_PROJECTILE, HURTBY_PLAYER_PROJECTILE, HURTBY_MOB_PROJECTILE, HURTBY_OTHER, CATCH_FISH, RIGHT_CLICK, WORN,
	SHOOT_PROJECTILE, DURABILITY_LOSS, KILL_MOB, KILL_PLAYER, KILLEDBY_PLAYER, KILLEDBY_MOB;

	public static final Set<String> ALL_ACTIONS = Arrays.stream(values()).map(Enum::toString)
			.collect(Collectors.toCollection(TreeSet::new));
}
