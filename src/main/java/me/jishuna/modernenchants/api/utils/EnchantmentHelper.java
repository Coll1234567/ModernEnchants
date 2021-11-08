package me.jishuna.modernenchants.api.utils;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentLevel;
import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;

public class EnchantmentHelper {

	public static void populateEnchantments(ItemStack item, Map<Enchantment, Integer> enchantmentMap,
			EnchantmentRegistry registry, ThreadLocalRandom random, int count, int cost, boolean book) {
		for (int i = 0; i < count; i++) {
			int tries = 10;
			boolean valid = false;

			IEnchantment enchantment;
			do {
				enchantment = registry.getRandomEnchantment();
				valid = book || canEnchant(registry, item, enchantment, enchantmentMap.keySet());
				tries--;
			} while (!valid && tries > 0);

			if (!valid)
				continue;

			int[] levels = getLevelRange(enchantment, cost);
			enchantmentMap.put(enchantment.getEnchantment(), random.nextInt(levels[0], levels[1] + 1));
		}
	}

	public static boolean addEnchant(ItemStack result, Map<Enchantment, Integer> enchantMap,
			EnchantmentRegistry registry, IEnchantment enchant, int level) {
		enchantMap.remove(enchant.getEnchantment());

		if (enchant.canEnchantItem(result) && canEnchant(registry, result, enchant, enchantMap.keySet())) {
			enchantMap.put(enchant.getEnchantment(), level);
			return true;
		}
		return false;
	}

	public static int[] getLevelRange(IEnchantment enchantment, int cost) {
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

	private static boolean canEnchant(EnchantmentRegistry registry, ItemStack item, IEnchantment enchantment,
			Set<Enchantment> enchantments) {
		if (!enchantment.canEnchantItem(item) || enchantments.contains(enchantment.getEnchantment()))
			return false;

		for (Enchantment enchant : enchantments) {
			IEnchantment custom = registry.getEnchantment(enchant.getKey());

			if (custom != null) {
				if (custom.conflictsWith(enchantment) || enchantment.conflictsWith(custom))
					return false;
			} else {
				if (enchant.conflictsWith(enchantment.getEnchantment()) || enchantment.conflictsWith(enchant))
					return false;
			}
		}
		return true;
	}
}
