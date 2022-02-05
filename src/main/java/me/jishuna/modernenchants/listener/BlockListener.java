package me.jishuna.modernenchants.listener;

import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

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

		ActionContext context = new ActionContext.Builder(TriggerRegistry.BREAK_BLOCK).event(event).item(item)
				.targetLocation(block.getLocation()).user(player).build();

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

		ActionContext context = new ActionContext.Builder(TriggerRegistry.BLOCK_DROP_ITEM).event(event).item(item)
				.targetLocation(block.getLocation()).user(player).build();

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			enchantment.processActions(level, context);
		}
	}
}
