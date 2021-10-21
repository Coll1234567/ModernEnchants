package me.jishuna.modernenchants.listeners;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantingListener implements Listener {

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		ItemStack target = event.getItem();

		Iterator<Entry<Enchantment, Integer>> iterator = event.getEnchantsToAdd().entrySet().iterator();
		while (iterator.hasNext()) {
			Enchantment enchant = iterator.next().getKey();

			if (!enchant.canEnchantItem(target))
				iterator.remove();
		}
	}
}
