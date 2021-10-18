package me.jishuna.modernenchants.listeners;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public class MiscListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		ItemStack item = getRod(event.getPlayer());

		if (item == null || item.getType().isAir())
			return;

		if (event.getState() == State.CAUGHT_FISH) {
			EnchantmentContext context = EnchantmentContext.Builder.fromEvent(event).withUser(player).build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();

				enchantment.processActions(level, ActionType.CATCH_FISH, context);
			}
		}
	}

	private ItemStack getRod(Player player) {
		ItemStack item = player.getEquipment().getItemInMainHand();

		if (item.getType() == Material.FISHING_ROD)
			return item;

		ItemStack item2 = player.getEquipment().getItemInOffHand();

		if (item2.getType() == Material.FISHING_ROD)
			return item2;

		return null;
	}
}
