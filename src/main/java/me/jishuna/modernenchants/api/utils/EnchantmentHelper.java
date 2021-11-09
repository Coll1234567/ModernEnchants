package me.jishuna.modernenchants.api.utils;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

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

	public static IEnchantment getCustomEnchantment(Enchantment enchantment, EnchantmentRegistry registry) {
		if (enchantment instanceof IEnchantment enchant) {
			return enchant;
		}

		return registry.getEnchantment(enchantment.getKey());
	}
	
	public static Map<Enchantment, Integer> getEnchants(ItemMeta meta) {
		if (meta instanceof EnchantmentStorageMeta storage)
			return storage.getStoredEnchants();
		return meta.getEnchants();
	}

	public static void setEnchants(ItemMeta meta, Map<Enchantment, Integer> enchantMap) {
		if (meta instanceof EnchantmentStorageMeta storageMeta) {
			storageMeta.getStoredEnchants()
					.forEach((enchantment, level) -> storageMeta.removeStoredEnchant(enchantment));

			enchantMap.forEach((enchantment, level) -> storageMeta.addStoredEnchant(enchantment, level, true));
		} else {
			meta.getEnchants().forEach((enchantment, level) -> meta.removeEnchant(enchantment));

			enchantMap.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
		}
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

	public static boolean canEnchant(EnchantmentRegistry registry, ItemStack item, IEnchantment enchantment,
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
