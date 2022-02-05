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

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

public class MiscListener implements Listener {
	boolean ignore = false;

	@EventHandler(ignoreCancelled = true)
	public void onFish(PlayerFishEvent event) {
		Player player = event.getPlayer();
		ItemStack item = getRod(player);

		if (item == null || item.getType().isAir())
			return;

		if (event.getState() == State.CAUGHT_FISH) {
			ActionContext context = new ActionContext.Builder(TriggerRegistry.CATCH_FISH).event(event).item(item)
					.user(player).build();

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
		if (ignore || !(event.getEntity().getShooter() instanceof LivingEntity entity))
			return;

		Set<ItemStack> items = new HashSet<>();
		items.add(entity.getEquipment().getItemInMainHand());
		items.add(entity.getEquipment().getItemInOffHand());

		ignore = true;
		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			ActionContext context = new ActionContext.Builder(TriggerRegistry.SHOOT_PROJECTILE).event(event).item(item)
					.user(entity).build();

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

		ActionContext context = new ActionContext.Builder(TriggerRegistry.DURABILITY_LOST).event(event).item(item)
				.user(event.getPlayer()).build();

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
