package me.jishuna.modernenchants.packet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

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

		handleItem(item);
		packet.getItemModifier().writeSafely(0, item);

		event.setPacket(packet);
	}

	private void handleItem(ItemStack item) {
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
			IEnchantment enchantment = EnchantmentHelper.getCustomEnchantment(enchant,
					this.plugin.getEnchantmentRegistry());

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

	private List<String> getLore(ItemMeta meta) {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}
}
