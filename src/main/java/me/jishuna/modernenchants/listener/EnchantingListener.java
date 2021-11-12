package me.jishuna.modernenchants.listener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class EnchantingListener implements Listener {

	private final ModernEnchants plugin;

	public EnchantingListener(ModernEnchants plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPrepare(PrepareItemEnchantEvent event) {
		for (EnchantmentOffer offer : event.getOffers()) {
			if (offer == null)
				continue;
			
			CustomEnchantment enchant = this.plugin.getEnchantmentRegistry().getPlaceholderEnchant();
			if (enchant != null)
				offer.setEnchantment(enchant.getEnchantment());
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		int cost = event.getExpLevelCost();
		Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
		ItemStack target = event.getItem();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		boolean book = target.getType() == Material.BOOK;

		int count = Math.max(1, toAdd.size());
		if (count < 5 + 1) {
			count = random.nextInt(count, 5 + 1);
		}

		toAdd.clear();

		toAdd.putAll(EnchantmentHelper.populateEnchantments(target, this.plugin.getEnchantmentRegistry(), random, count,
				cost, ObtainMethod.ENCHANTING, book));

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
