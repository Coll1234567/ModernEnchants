package me.jishuna.modernenchants.listeners;

import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public class BlockListener implements Listener {

	private boolean ignore = false;

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (ignore)
			return;

		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemStack item = player.getEquipment().getItemInMainHand();

		if (item.getType().isAir())
			return;

		EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.BREAK_BLOCK).withItem(item)
				.withTargetBlock(block).withUser(player).build();

		ignore = true;
		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();

			enchantment.processActions(level, context);
		}
		ignore = false;
	}

	@EventHandler(ignoreCancelled = true)
	public void onDropItems(BlockDropItemEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemStack item = player.getEquipment().getItemInMainHand();

		if (item.getType().isAir())
			return;

		EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.BLOCK_DROP_ITEMS)
				.withItem(item).withTargetBlock(block).withUser(player).build();

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			enchantment.processActions(level, context);
		}
	}
}
