package me.jishuna.modernenchants.api.enchantments;

import java.util.Optional;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.effects.ActionTarget;

public class EnchantmentContext {

	private final Event event;
	private final ActionType type;
	private final ItemStack item;
	private final Block targetBlock;
	private final LivingEntity user;
	private final LivingEntity opponent;

	public EnchantmentContext(Event event, ActionType type, ItemStack item, Block targetBlock, LivingEntity user,
			LivingEntity opponent) {
		this.event = event;
		this.type = type;
		this.item = item;
		this.targetBlock = targetBlock;
		this.user = user;
		this.opponent = opponent;
	}

	public ItemStack getItemDirect() {
		return this.item;
	}

	public Optional<ItemStack> getItem() {
		return Optional.ofNullable(getItemDirect());
	}

	public Block getTargetBlockDirect() {
		return this.targetBlock;
	}

	public Optional<Block> getTargetBlock() {
		return Optional.ofNullable(getTargetBlockDirect());
	}

	public LivingEntity getUserDirect() {
		return this.user;
	}

	public Optional<LivingEntity> getUser() {
		return Optional.ofNullable(getUserDirect());
	}

	public LivingEntity getOpponentDirect() {
		return this.opponent;
	}

	public Optional<LivingEntity> getOpponent() {
		return Optional.ofNullable(getOpponentDirect());
	}

	public LivingEntity getTargetDirect(ActionTarget target) {
		if (target == ActionTarget.USER)
			return getUserDirect();
		return getOpponentDirect();
	}

	public Optional<LivingEntity> getTarget(ActionTarget target) {
		return Optional.ofNullable(getTargetDirect(target));
	}

	public Event getEvent() {
		return event;
	}

	public ActionType getType() {
		return type;
	}

	public static class Builder {
		private final Event event;
		private final ActionType type;
		private ItemStack item;
		private Block targetBlock;
		private LivingEntity user;
		private LivingEntity opponent;

		private Builder(Event event, ActionType type) {
			this.event = event;
			this.type = type;
		}

		public static Builder create(Event event, ActionType type) {
			return new Builder(event, type);
		}

		public Builder withTargetBlock(Block block) {
			this.targetBlock = block;
			return this;
		}

		public Builder withItem(ItemStack item) {
			this.item = item;
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
			return new EnchantmentContext(event, type, item, targetBlock, user, opponent);
		}
	}
}
