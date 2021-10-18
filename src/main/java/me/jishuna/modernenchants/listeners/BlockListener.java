package me.jishuna.modernenchants.listeners;

import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

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

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			EnchantmentContext context = EnchantmentContext.Builder.fromEvent(event).withTargetBlock(block)
					.withUser(player).build();

			ignore = true;
			enchantment.processActions(level, ActionType.BREAK_BLOCK, context);
			ignore = false;
		}
	}
}
