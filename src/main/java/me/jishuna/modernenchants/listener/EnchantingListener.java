package me.jishuna.modernenchants.listener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class EnchantingListener implements Listener {

	private final ModernEnchants plugin;

	public EnchantingListener(ModernEnchants plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		int cost = event.getExpLevelCost();
		Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
		ItemStack target = event.getItem();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int count = random.nextInt(toAdd.size(), 5);
		boolean book = target.getType() == Material.BOOK;

		toAdd.clear();

		EnchantmentHelper.populateEnchantments(target, toAdd, this.plugin.getEnchantmentRegistry(), random, count, cost,
				book);

		if (book) {
			handleBook(event);
		}
	}

	private void handleBook(EnchantItemEvent event) {
		Bukkit.getScheduler().runTask(this.plugin, () -> {
			ItemStack item = event.getInventory().getItem(0);
			if (item == null || item.getType() != Material.ENCHANTED_BOOK)
				return;

			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

			for (Entry<Enchantment, Integer> toAdd : event.getEnchantsToAdd().entrySet()) {
				Enchantment enchant = toAdd.getKey();

				if (!meta.hasStoredEnchant(enchant))
					meta.addStoredEnchant(enchant, toAdd.getValue(), true);
			}

			item.setItemMeta(meta);
			event.getInventory().setItem(0, item);
		});
	}
}
