package me.jishuna.modernenchants.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public class InteractListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (event.useItemInHand() == Result.DENY)
			return;

		Player player = event.getPlayer();
		Set<ItemStack> items = new HashSet<>();

		items.add(event.getItem());
		items.addAll(Arrays.asList(player.getEquipment().getArmorContents()));

		Block block = event.getClickedBlock();

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext.Builder builder = EnchantmentContext.Builder.create(event, ActionType.RIGHT_CLICK)
					.withItem(item).withUser(player);

			if (block != null)
				builder.withTargetBlock(block);

			EnchantmentContext context = builder.build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();
				enchantment.processActions(level, context);
			}
		}
	}
}
