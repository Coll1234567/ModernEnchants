package me.jishuna.modernenchants.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;

public class ItemUtils {

	public static void handleOutgoingItem(Player player, ItemStack item, EnchantmentRegistry registry) {
		if (item == null || item.getType().isAir())
			return;
		ItemMeta meta = item.getItemMeta();
		boolean book = item.getType() == Material.ENCHANTED_BOOK;

		if ((!book && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
				|| (book && meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))))
			return;

		meta.getPersistentDataContainer().set(PluginKeys.MODIFIED_KEY.getKey(), PersistentDataType.BYTE, (byte) 1);

		Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchants(meta);

		List<String> lore = getLore(meta);
		int index = 0;

		for (Entry<Enchantment, Integer> enchants : enchantMap.entrySet()) {
			Enchantment enchant = enchants.getKey();
			IEnchantment enchantment = EnchantmentHelper.getCustomEnchantment(enchant, registry);

			if (enchantment == null)
				continue;

			int level = enchants.getValue();
			String text = enchantment.getDisplayName() + (enchantment.getMaxLevel() != enchantment.getStartLevel()
					? " " + StringUtils.toRomanNumeral(level)
					: "");

			switch (Utils.getDisplayFormat(player)) {
			case "inline":
				List<String> desc = StringUtils.splitString(enchantment.getDescription(), 30);
				text += " " + (desc.isEmpty() ? "" : desc.get(0));
				lore.add(index++, text);

				for (int i = 1; i < desc.size(); i++) {
					lore.add(index++, desc.get(i));
				}
				break;
			case "seperate":
				lore.add(index++, text);
				lore.addAll(StringUtils.splitString(enchantment.getDescription(), 30));
				break;
			default:
				lore.add(index++, text);
				break;

			}
		}
		if (book) {
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		} else {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static void handleIncomingItem(ItemStack item, EnchantmentRegistry registry) {
		if (item == null || item.getType().isAir())
			return;

		ItemMeta meta = item.getItemMeta();

		if (!meta.getPersistentDataContainer().has(PluginKeys.MODIFIED_KEY.getKey(), PersistentDataType.BYTE))
			return;

		boolean book = item.getType() == Material.ENCHANTED_BOOK;

		if (book) {
			meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		} else {
			meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		Map<Enchantment, Integer> enchantMap = EnchantmentHelper.getEnchants(meta);

		List<String> lore = getLore(meta);
		for (Entry<Enchantment, Integer> enchants : enchantMap.entrySet()) {
			Enchantment enchant = enchants.getKey();
			IEnchantment enchantment = EnchantmentHelper.getCustomEnchantment(enchant, registry);

			if (enchantment == null)
				continue;

			lore.removeIf(line -> StringUtil.startsWithIgnoreCase(line, enchantment.getDisplayName()));

			for (String desc : StringUtils.splitString(enchantment.getDescription(), 30)) {
				lore.removeIf(line -> line.equals(desc));
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static List<String> getLore(ItemMeta meta) {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}

}
