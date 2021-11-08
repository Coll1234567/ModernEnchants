package me.jishuna.modernenchants.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;

public class AnvilListener implements Listener {

	private final EnchantmentRegistry registry;

	public AnvilListener(EnchantmentRegistry registry) {
		this.registry = registry;
	}

	// TODO Adapted from vanilla, clean-up
	@EventHandler
	public void onAnvil(PrepareAnvilEvent event) {
		ItemStack first = event.getInventory().getItem(0);

		if (first == null)
			return;

		AnvilInventory inventory = event.getInventory();
		ItemMeta firstMeta = first.getItemMeta();

		ItemStack copy = first.clone();
		ItemMeta copyMeta = copy.getItemMeta();

		ItemStack second = inventory.getItem(1);

		Map<Enchantment, Integer> firstEnchantments = new HashMap<>(first.getEnchantments());
		int baseCost = getRepairCost(first) + (second == null ? 0 : getRepairCost(second));
		int cost = 0;
		byte b2 = 0;

		if (second != null) {
			ItemMeta secondMeta = second.getItemMeta();
			boolean book = second.getType() == Material.ENCHANTED_BOOK
					&& !((EnchantmentStorageMeta) secondMeta).getStoredEnchants().isEmpty();

			Map<Enchantment, Integer> secondEnchantments = second.getEnchantments();

			boolean lastValid = false;
			boolean lastInvalid = false;

			for (Enchantment enchantment : secondEnchantments.keySet()) {
				// Can this even be null?
				if (enchantment == null)
					continue;
				IEnchantment customEnchantment = getCustomEnchantment(enchantment);

				if (customEnchantment == null)
					continue;

				int firstLevel = firstEnchantments.getOrDefault(enchantment, 0);
				int secondLevel = secondEnchantments.get(enchantment);

				secondLevel = ((firstLevel == secondLevel) ? (secondLevel + 1) : Math.max(secondLevel, firstLevel));

				boolean valid = customEnchantment.canEnchantItem(first);
				if (first.getType() == Material.ENCHANTED_BOOK)
					valid = true;

				for (final Enchantment enchantment2 : firstEnchantments.keySet()) {
					IEnchantment customEnchantment2 = getCustomEnchantment(enchantment2);

					if (customEnchantment2 != customEnchantment
							&& customEnchantment.conflictsWith(customEnchantment2)) {
						valid = false;
						++cost;
					}
				}

				if (!valid) {
					lastInvalid = true;
				} else {
					lastValid = true;

					if (secondLevel > customEnchantment.getMaxLevel())
						secondLevel = customEnchantment.getMaxLevel();

					firstEnchantments.put(enchantment, secondLevel);
					int enchantCost = 1;

					if (book)
						enchantCost = Math.max(1, enchantCost / 2);
					cost += enchantCost * secondLevel;

					if (first.getAmount() <= 1) {
						continue;
					}
					cost = 40;
				}
			}
			if (lastInvalid && !lastValid) {
				event.setResult(null);
				inventory.setRepairCost(0);
				return;
			}
		}

		String name = event.getInventory().getRenameText();

		if (StringUtils.isBlank(name)) {
			if (firstMeta.hasDisplayName()) {
				b2 = 1;
				cost += b2;
				copyMeta.setDisplayName(null);
			}
		} else if (!name.equals(firstMeta.getDisplayName())) {
			b2 = 1;
			cost += b2;
			copyMeta.setDisplayName(name);
		}

		if (cost <= 0) {
			copy = null;
		}

		inventory.setRepairCost(baseCost + cost);

		if (b2 == cost && b2 > 0 && inventory.getRepairCost() > inventory.getMaximumRepairCost()) {
			inventory.setRepairCost(inventory.getMaximumRepairCost() - 1);
		}

		if (inventory.getRepairCost() >= inventory.getMaximumRepairCost())
			copy = null;

		if (copy != null) {
			int repairCost = getRepairCost(copy);
			if (second != null && repairCost < getRepairCost(second))
				repairCost = getRepairCost(second);

			if (b2 != cost || b2 == 0) {
				repairCost = repairCost * 2 + 1;
			}
			if (copyMeta instanceof Repairable repair) {
				repair.setRepairCost(repairCost);
			}
			setEnchants(copyMeta, firstEnchantments);
			copy.setItemMeta(copyMeta);
		}
		event.setResult(copy);
	}

	private IEnchantment getCustomEnchantment(Enchantment enchantment) {
		if (enchantment instanceof IEnchantment enchant) {
			return enchant;
		}

		return this.registry.getEnchantment(enchantment.getKey());
	}

	private boolean isValidRepairItem(ItemStack target, ItemStack first, ItemStack second) {
		net.minecraft.world.item.ItemStack nmsFirst = CraftItemStack.asNMSCopy(first);
		net.minecraft.world.item.ItemStack nmsSecond = CraftItemStack.asNMSCopy(second);

		return CraftItemStack.asNMSCopy(target).getItem().a(nmsFirst, nmsSecond);
	}

	private int getRepairCost(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Repairable repair)
			return repair.getRepairCost();
		return 0;
	}

	private void setEnchants(ItemMeta meta, Map<Enchantment, Integer> enchantMap) {
		meta.getEnchants().forEach((enchantment, level) -> meta.removeEnchant(enchantment));

		enchantMap.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
	}
}
