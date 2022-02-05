package me.jishuna.modernenchants.listener;

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

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

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

			ActionContext.Builder builder = new ActionContext.Builder(TriggerRegistry.RIGHT_CLICK).event(event)
					.item(item).user(player);

			if (block != null)
				builder.targetLocation(block.getLocation());

			ActionContext context = builder.build();

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
