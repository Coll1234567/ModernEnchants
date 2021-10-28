package me.jishuna.modernenchants.listeners;

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
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentLevel;

public class EnchantingListener implements Listener {

	private final ModernEnchants plugin;

	public EnchantingListener(ModernEnchants plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		int cost = event.getExpLevelCost();
		ItemStack target = event.getItem();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int count = random.nextInt(1, 4);
		boolean book = target.getType() == Material.BOOK;

		for (int i = 0; i < count; i++) {
			int tries = 10;
			boolean valid = false;

			CustomEnchantment enchantment;
			do {
				enchantment = plugin.getEnchantmentRegistry().getRandomEnchantment();
				valid = (book || enchantment.canEnchantItem(target))
						&& !event.getEnchantsToAdd().keySet().contains(enchantment);
				tries--;
			} while (!valid && tries > 0);

			if (!valid)
				continue;

			int[] levels = getLevelRange(enchantment, cost);
			event.getEnchantsToAdd().put(enchantment, random.nextInt(levels[0], levels[1] + 1));
		}

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

	private int[] getLevelRange(CustomEnchantment enchantment, int cost) {
		int lowerCost = cost / 2;
		int[] levels = new int[] { enchantment.getStartLevel(), enchantment.getStartLevel() };

		for (Entry<Integer, EnchantmentLevel> levelEntry : enchantment.getLevels().entrySet()) {
			EnchantmentLevel enchantmentLevel = levelEntry.getValue();
			int level = levelEntry.getKey();

			if (enchantmentLevel.getMinExperienceLevel() <= lowerCost && level > levels[0])
				levels[0] = level;

			if (enchantmentLevel.getMinExperienceLevel() <= cost && level > levels[1])
				levels[1] = level;
		}
		return levels;
	}
}
