package me.jishuna.modernenchants.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class WornEnchantmentCheckEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	private final ItemStack[] armor;

	public WornEnchantmentCheckEvent(Player player, ItemStack[] armor) {
		this.player = player;
		this.armor = armor;
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
