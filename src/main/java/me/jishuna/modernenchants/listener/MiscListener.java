package me.jishuna.modernenchants.listener;

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
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public class MiscListener implements Listener {
	boolean ignore = false;

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
		if (ignore || !(event.getEntity().getShooter()instanceof LivingEntity entity))
			return;

		Set<ItemStack> items = new HashSet<>();
		items.add(entity.getEquipment().getItemInMainHand());
		items.add(entity.getEquipment().getItemInOffHand());

		ignore = true;
		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.SHOOT_PROJECTILE)
					.withItem(item).withUser(entity).build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();
				enchantment.processActions(level, context);
			}
		}
		ignore = false;
	}

	@EventHandler
	public void onDurabilityLoss(PlayerItemDamageEvent event) {
		ItemStack item = event.getItem();
		if (item.getType().isAir())
			return;

		EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.DURABILITY_LOSS).withItem(item)
				.withUser(event.getPlayer()).build();

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			enchantment.processActions(level, context);
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
