package me.jishuna.modernenchants.listener;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentLevel;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;

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

		for (int i = 0; i < count; i++) {
			int tries = 10;
			boolean valid = false;

			IEnchantment enchantment;
			do {
				enchantment = plugin.getEnchantmentRegistry().getRandomEnchantment();
				valid = book || checkConflicts(target, enchantment, toAdd.keySet());
				tries--;
			} while (!valid && tries > 0);

			if (!valid)
				continue;

			int[] levels = getLevelRange(enchantment, cost);
			toAdd.put(enchantment.getEnchantment(), random.nextInt(levels[0], levels[1] + 1));
		}

		if (book) {
			handleBook(event);
		}
	}

	private boolean checkConflicts(ItemStack item, IEnchantment enchantment, Set<Enchantment> enchantments) {
		if (!enchantment.canEnchantItem(item) || enchantments.contains(enchantment.getEnchantment()))
			return false;

		for (Enchantment enchant : enchantments) {
			IEnchantment custom = this.plugin.getEnchantmentRegistry().getEnchantment(enchant.getKey());

			if (custom != null) {
				if (custom.conflictsWith(enchantment.getEnchantment())
						|| enchantment.conflictsWith(custom.getEnchantment()))
					return false;
			} else {
				if (enchant.conflictsWith(enchantment.getEnchantment()) || enchantment.conflictsWith(enchant))
					return false;
			}
		}
		return true;

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

	private int[] getLevelRange(IEnchantment enchantment, int cost) {
		int lowerCost = cost / 2;
		int[] levels = new int[] { enchantment.getStartLevel(), enchantment.getStartLevel() };

		if (!(enchantment instanceof CustomEnchantment enchant))
			return new int[] { Math.max(enchantment.getMaxLevel() - 2, enchantment.getStartLevel()),
					enchantment.getMaxLevel() };

		for (Entry<Integer, EnchantmentLevel> levelEntry : enchant.getLevels().entrySet()) {
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
