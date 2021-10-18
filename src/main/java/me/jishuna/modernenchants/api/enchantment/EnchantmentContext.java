package me.jishuna.modernenchants.api.enchantment;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import me.jishuna.modernenchants.api.effects.ActionTarget;

public class EnchantmentContext {

	private final Event event;
	private final Block targetBlock;
	private final LivingEntity user;
	private final LivingEntity opponent;

	public EnchantmentContext(Event event, Block targetBlock, LivingEntity user, LivingEntity opponent) {
		this.event = event;
		this.targetBlock = targetBlock;
		this.user = user;
		this.opponent = opponent;
	}

	public Optional<Block> getTargetBlock() {
		return Optional.ofNullable(targetBlock);
	}

	public Optional<LivingEntity> getUser() {
		return Optional.ofNullable(user);
	}

	public Optional<LivingEntity> getOpponent() {
		return Optional.ofNullable(opponent);
	}

	public Optional<LivingEntity> getTarget(ActionTarget target) {
		if (target == ActionTarget.USER)
			return getUser();
		return getOpponent();
	}

	public Event getEvent() {
		return event;
	}

	public static class Builder {
		private final Event event;
		private Block targetBlock;
		private LivingEntity user;
		private LivingEntity opponent;

		private Builder(Event event) {
			this.event = event;
		}

		public static Builder fromEvent(Event event) {
			return new Builder(event);
		}

		public Builder withTargetBlock(Block block) {
			this.targetBlock = block;
			return this;
		}

		public Builder withUser(LivingEntity entity) {
			this.user = entity;
			return this;
		}

		public Builder withOpponent(LivingEntity entity) {
			this.opponent = entity;
			return this;
		}

		public EnchantmentContext build() {
			return new EnchantmentContext(event, targetBlock, user, opponent);
		}
	}
}
