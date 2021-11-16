package me.jishuna.modernenchants.packet;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.utils.ItemUtils;

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
				ItemUtils.handleOutgoingItem(event.getPlayer(), item, this.plugin.getEnchantmentRegistry());

			}
			packet.getItemListModifier().writeSafely(0, items);
		} else if (packet.getType().equals(PacketType.Play.Server.SET_SLOT)) {
			ItemStack item = packet.getItemModifier().readSafely(0);

			if (item == null)
				return;

			ItemUtils.handleOutgoingItem(event.getPlayer(), item, this.plugin.getEnchantmentRegistry());
			packet.getItemModifier().writeSafely(0, item);
		}
		event.setPacket(packet);
	}
}
