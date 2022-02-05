package me.jishuna.modernenchants.listener;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class AnvilListener implements Listener {

	private final EnchantmentRegistry registry;

	public AnvilListener(EnchantmentRegistry registry) {
		this.registry = registry;
	}

	// TODO Adapted from vanilla, clean-up
	@EventHandler(priority = EventPriority.LOW)
	public void onAnvil(PrepareAnvilEvent event) {
		ItemStack first = event.getInventory().getItem(0);

		if (first == null)
			return;

		AnvilInventory inventory = event.getInventory();
		ItemMeta firstMeta = first.getItemMeta();

		ItemStack copy = first.clone();
		ItemMeta copyMeta = copy.getItemMeta();

		ItemStack second = inventory.getItem(1);

		Map<Enchantment, Integer> firstEnchantments = new HashMap<>(EnchantmentHelper.getEnchants(firstMeta));
		int baseCost = getRepairCost(first) + (second == null ? 0 : getRepairCost(second));
		int cost = 0;
		byte extraCost = 0;

		if (second != null) {
			ItemMeta secondMeta = second.getItemMeta();
			boolean book = second.getType() == Material.ENCHANTED_BOOK
					&& !((EnchantmentStorageMeta) secondMeta).getStoredEnchants().isEmpty();

			if (copyMeta instanceof Damageable damageable && isValidRepairItem(copy, first, second)) {
				int repairItemCountCost = Math.min(damageable.getDamage(), copy.getType().getMaxDurability() / 4);

				if (repairItemCountCost <= 0) {
					event.setResult(null);
					inventory.setRepairCost(0);
					return;
				}

				for (int i2 = 0; repairItemCountCost > 0 && i2 < second.getAmount(); ++i2) {
					repairItemCountCost = Math.min(damageable.getDamage(), copy.getType().getMaxDurability() / 4);
					final int l = damageable.getDamage() - repairItemCountCost;
					damageable.setDamage(l);
					cost++;
				}
			} else {
				if (!book && (copy.getType() != second.getType() || !(copyMeta instanceof Damageable))) {
					event.setResult(null);
					inventory.setRepairCost(0);
					return;
				}

				if (copyMeta instanceof Damageable damageable && !book) {
					final int firstDiff = first.getType().getMaxDurability() - getDamage(firstMeta);
					final int secondDiff = second.getType().getMaxDurability() - getDamage(secondMeta);
					final int l = secondDiff + copy.getType().getMaxDurability() * 12 / 100;
					final int j2 = firstDiff + l;

					int k2 = copy.getType().getMaxDurability() - j2;
					if (k2 < 0) {
						k2 = 0;
					}

					if (k2 < damageable.getDamage()) {
						damageable.setDamage(k2);
						cost += 2;
					}
				}

				Map<Enchantment, Integer> secondEnchantments = EnchantmentHelper.getEnchants(secondMeta);
				boolean lastValid = false;
				boolean lastInvalid = false;

				for (Enchantment enchantment : secondEnchantments.keySet()) {
					IEnchantment customEnchantment = EnchantmentHelper.getCustomEnchantment(enchantment, this.registry);

					if (customEnchantment == null)
						continue;

					int firstLevel = firstEnchantments.getOrDefault(enchantment, 0);
					int secondLevel = secondEnchantments.get(enchantment);

					secondLevel = ((firstLevel == secondLevel) ? (secondLevel + 1) : Math.max(secondLevel, firstLevel));

					boolean valid = customEnchantment.canEnchantItem(first, false);
					if (first.getType() == Material.ENCHANTED_BOOK)
						valid = true;

					for (final Enchantment enchantment2 : firstEnchantments.keySet()) {
						IEnchantment customEnchantment2 = EnchantmentHelper.getCustomEnchantment(enchantment2,
								this.registry);

						if (customEnchantment2 != customEnchantment
								&& customEnchantment.conflictsWith(customEnchantment2)) {
							valid = false;
							cost++;
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
		}

		String name = event.getInventory().getRenameText();

		if (StringUtils.isBlank(name)) {
			if (firstMeta.hasDisplayName()) {
				extraCost = 1;
				cost += extraCost;
				copyMeta.setDisplayName(null);
			}
		} else if (!name.equals(firstMeta.getDisplayName())) {
			extraCost = 1;
			cost += extraCost;
			copyMeta.setDisplayName(name);
		}

		if (cost <= 0) {
			copy = null;
		}

		inventory.setRepairCost(baseCost + cost);

		if (extraCost == cost && extraCost > 0 && inventory.getRepairCost() > inventory.getMaximumRepairCost()) {
			inventory.setRepairCost(inventory.getMaximumRepairCost() - 1);
		}

		if (inventory.getRepairCost() >= inventory.getMaximumRepairCost())
			copy = null;

		if (copy != null) {
			int repairCost = getRepairCost(copy);
			if (second != null && repairCost < getRepairCost(second))
				repairCost = getRepairCost(second);

			if (extraCost != cost || extraCost == 0) {
				repairCost = repairCost * 2 + 1;
			}
			if (copyMeta instanceof Repairable repair) {
				repair.setRepairCost(repairCost);
			}

			EnchantmentHelper.setEnchants(copyMeta, firstEnchantments);
			copy.setItemMeta(copyMeta);
		}
		event.setResult(copy);
	}

	private boolean isValidRepairItem(ItemStack target, ItemStack first, ItemStack second) {
		net.minecraft.world.item.ItemStack nmsFirst = CraftItemStack.asNMSCopy(first);
		net.minecraft.world.item.ItemStack nmsSecond = CraftItemStack.asNMSCopy(second);

		return CraftItemStack.asNMSCopy(target).c().a(nmsFirst, nmsSecond);
	}

	private int getRepairCost(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Repairable repair) {
			return repair.getRepairCost();
		}
		return 0;
	}

	private int getDamage(ItemMeta meta) {
		if (meta instanceof Damageable damage) {
			return damage.getDamage();
		}
		return 0;
	}
}
