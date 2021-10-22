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

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;

public class IncomingItemListener extends PacketAdapter {

	public IncomingItemListener(Plugin plugin) {
		super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT);
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		ItemStack item = packet.getItemModifier().readSafely(0);

		if (item == null)
			return;

		handleItem(item);
		packet.getItemModifier().writeSafely(0, item);

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

			lore.removeIf(line -> line.startsWith(enchantment.getDisplayName()));
			
			for (String desc : StringUtils.splitString(enchantment.getDescription(), 30)) {
				lore.removeIf(line -> line.equals(desc));
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	private List<String> getLore(ItemMeta meta) {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}
}
