package me.jishuna.modernenchants.listeners;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;

public class AnvilListener implements Listener {

	@EventHandler
	public void onAnvil(PrepareAnvilEvent event) {
		ItemStack first = event.getInventory().getItem(0);
		ItemStack result = event.getResult();

		if (first == null || result == null)
			return;

		ItemStack second = event.getInventory().getItem(1);

		ItemMeta meta = first.getItemMeta();
		ItemMeta resultMeta = result.getItemMeta();

		if (second == null || (second.getType() != first.getType() && second.getType() != Material.ENCHANTED_BOOK)) {

			for (Entry<Enchantment, Integer> toAdd : meta.getEnchants().entrySet()) {
				Enchantment enchant = toAdd.getKey();

				if (!(enchant instanceof CustomEnchantment))
					continue;

				addEnchant(result, resultMeta, enchant, toAdd.getValue());
			}

			result.setItemMeta(resultMeta);
			event.setResult(result);
		} else {
			ItemMeta secondMeta = second.getItemMeta();

			if (secondMeta instanceof EnchantmentStorageMeta storageMeta) {
				resultMeta = handleCombine(result, meta.getEnchants(), storageMeta.getStoredEnchants(), resultMeta);
			} else {
				resultMeta = handleCombine(result, meta.getEnchants(), secondMeta.getEnchants(), resultMeta);
			}
			result.setItemMeta(resultMeta);
			event.setResult(result);
		}
	}

	private ItemMeta handleCombine(ItemStack result, Map<Enchantment, Integer> first, Map<Enchantment, Integer> second,
			ItemMeta resultMeta) {
		Set<Enchantment> enchants = new HashSet<>();
		enchants.addAll(first.keySet());
		enchants.addAll(second.keySet());

		for (Enchantment enchant : enchants) {
			if (!(enchant instanceof CustomEnchantment))
				continue;

			Integer firstLevel = first.get(enchant);
			Integer secondLevel = second.get(enchant);

			if (firstLevel == null && secondLevel == null)
				continue;

			if (firstLevel == null) {
				addEnchant(result, resultMeta, enchant, secondLevel);
				continue;
			} else if (secondLevel == null) {
				addEnchant(result, resultMeta, enchant, firstLevel);
				continue;
			}

			int higher = Math.max(firstLevel, secondLevel);

			if (higher == enchant.getMaxLevel() || !firstLevel.equals(secondLevel)) {
				addEnchant(result, resultMeta, enchant, higher);
				continue;
			}
			addEnchant(result, resultMeta, enchant, firstLevel + 1);
		}
		return resultMeta;
	}

	private void addEnchant(ItemStack result, ItemMeta meta, Enchantment enchant, int level) {
		if (meta.hasEnchant(enchant))
			meta.removeEnchant(enchant);

		if (enchant.canEnchantItem(result))
			meta.addEnchant(enchant, level, true);
	}
}
