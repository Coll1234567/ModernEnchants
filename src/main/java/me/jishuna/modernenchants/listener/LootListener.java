package me.jishuna.modernenchants.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class LootListener implements Listener {

	private final ModernEnchants plugin;

	public LootListener(ModernEnchants plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onLootGenerate(LootGenerateEvent event) {
		List<ItemStack> loot = event.getLoot();

		for (ItemStack item : loot) {
			if (item == null)
				continue;

			ItemMeta meta = item.getItemMeta();

			Map<Enchantment, Integer> enchants = new HashMap<>(EnchantmentHelper.getEnchants(meta));
			int count = enchants.size();

			if (count == 0)
				continue;

			enchants.clear();

			enchants = EnchantmentHelper.populateEnchantments(item, this.plugin.getEnchantmentRegistry(),
					ThreadLocalRandom.current(), count, 30, ObtainMethod.LOOT,
					item.getType() == Material.ENCHANTED_BOOK);

			EnchantmentHelper.setEnchants(meta, enchants);
			item.setItemMeta(meta);
		}
	}

}
