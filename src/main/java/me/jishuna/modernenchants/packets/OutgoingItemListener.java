package me.jishuna.modernenchants.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;

public class OutgoingItemListener extends PacketAdapter {

	public OutgoingItemListener(Plugin plugin) {
		super(plugin, PacketType.Play.Server.WINDOW_ITEMS, PacketType.Play.Server.SET_SLOT);
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

		Map<Enchantment, Integer> enchantMap;
		if (item.getType() == Material.ENCHANTED_BOOK) {
			enchantMap = ((EnchantmentStorageMeta) meta).getStoredEnchants();
		} else {
			enchantMap = meta.getEnchants();
		}

		List<String> lore = getLore(meta);
		for (Entry<Enchantment, Integer> enchants : enchantMap.entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			String text = enchantment.getDisplayName() + " " + StringUtils.toRomanNumeral(level);

			switch (getDisplayFormat(player)) {
			case "inline":
				List<String> desc = StringUtils.splitString(enchantment.getDescription(), 30);
				text += " " + (desc.isEmpty() ? "" : desc.get(0));
				lore.add(text);

				for (int i = 1; i < desc.size(); i++) {
					lore.add(desc.get(i));
				}
				break;
			case "seperate":
				lore.add(text);
				lore.addAll(StringUtils.splitString(enchantment.getDescription(), 30));
				break;
			default:
				lore.add(text);
				break;

			}
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
