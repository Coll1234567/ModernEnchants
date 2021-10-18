package me.jishuna.modernenchants.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

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
				handleItem(item);

			}
			packet.getItemListModifier().writeSafely(0, items);
		} else if (packet.getType().equals(PacketType.Play.Server.SET_SLOT)) {
			ItemStack item = packet.getItemModifier().readSafely(0);

			if (item == null)
				return;

			handleItem(item);
			packet.getItemModifier().writeSafely(0, item);
		}
		event.setPacket(packet);
	}

	private void handleItem(ItemStack item) {
		if (item == null || item.getType().isAir())
			return;

		ItemMeta meta = item.getItemMeta();

		List<String> lore = getLore(meta);
		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();

			lore.add(enchantment.getDisplayName() + " " + level);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	private List<String> getLore(ItemMeta meta) {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}
}
