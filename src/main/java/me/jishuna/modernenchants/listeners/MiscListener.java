package me.jishuna.modernenchants.listeners;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
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
			EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.CATCH_FISH).withItem(item)
					.withUser(player).build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();

				enchantment.processActions(level, context);
			}
		}
	}

	@EventHandler
	public void onBowShoot(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof LivingEntity entity))
			return;
		
		Set<ItemStack> items = new HashSet<>();
		items.add(entity.getEquipment().getItemInMainHand());
		items.add(entity.getEquipment().getItemInOffHand());
		
		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.SHOOT_PROJECTILE).withItem(item)
					.withUser(entity).build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();
				enchantment.processActions(level, context);
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
