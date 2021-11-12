package me.jishuna.modernenchants.packet;

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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class OutgoingItemListener extends PacketAdapter {

	private final ModernEnchants plugin;

	public OutgoingItemListener(ModernEnchants plugin) {
		super(plugin, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT);
		this.plugin = plugin;
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = event.getPacket();

		if (packet.getType().equals(PacketType.Play.Server.WINDOW_ITEMS)) {
			List<ItemStack> items = packet.getItemListModifier().readSafely(0);

			if (items == null)
				return;

			for (ItemStack item : items) {
				handleItem(event.getPlayer(), item);

			}
			packet.getItemListModifier().writeSafely(0, items);
		} else if (packet.getType().equals(PacketType.Play.Server.SET_SLOT)) {
			ItemStack item = packet.getItemModifier().readSafely(0);

			if (item == null)
				return;

			handleItem(event.getPlayer(), item);
			packet.getItemModifier().writeSafely(0, item);
		}
		event.setPacket(packet);
	}

	private void handleItem(Player player, ItemStack item) {
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
			IEnchantment enchantment = EnchantmentHelper.getCustomEnchantment(enchant,
					this.plugin.getEnchantmentRegistry());

			if (enchantment == null)
				continue;

			int level = enchants.getValue();
			String text = enchantment.getDisplayName() + (enchantment.getMaxLevel() != enchantment.getStartLevel()
					? " " + StringUtils.toRomanNumeral(level)
					: "");

			switch (getDisplayFormat(player)) {
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

	private List<String> getLore(ItemMeta meta) {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}

	private String getDisplayFormat(Player player) {
		return player.getPersistentDataContainer().getOrDefault(PluginKeys.DISPLAY_FORMAT.getKey(),
				PersistentDataType.STRING, "inline");
	}
}
