package me.jishuna.modernenchants.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

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
		
		Map<Enchantment, Integer> enchantMap;
		if (item.getType() == Material.ENCHANTED_BOOK) {
			enchantMap = ((EnchantmentStorageMeta)meta).getStoredEnchants();
		} else {
			enchantMap = meta.getEnchants();
		}

		List<String> lore = getLore(meta);
		for (Entry<Enchantment, Integer> enchants : enchantMap.entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			lore.removeIf(line -> StringUtil.startsWithIgnoreCase(line, enchantment.getDisplayName()));

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
