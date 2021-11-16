package me.jishuna.modernenchants.packet;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.utils.ItemUtils;

public class IncomingItemListener extends PacketAdapter {
	private final ModernEnchants plugin;

	public IncomingItemListener(ModernEnchants plugin) {
		super(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT);
		this.plugin = plugin;
	}

	@Override
	public void onPacketReceiving(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		ItemStack item = packet.getItemModifier().readSafely(0);

		if (item == null)
			return;

		ItemUtils.handleIncomingItem(item, this.plugin.getEnchantmentRegistry());
		packet.getItemModifier().writeSafely(0, item);

		event.setPacket(packet);
	}
}
