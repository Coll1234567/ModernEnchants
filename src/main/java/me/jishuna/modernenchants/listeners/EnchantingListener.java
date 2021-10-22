package me.jishuna.modernenchants.listeners;

import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentLevel;
import me.jishuna.modernenchants.api.enchantments.EnchantmentRegistry;

public class EnchantingListener implements Listener {

	private final EnchantmentRegistry registry;

	public EnchantingListener(EnchantmentRegistry registry) {
		this.registry = registry;
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		int cost = event.getExpLevelCost();
		ItemStack target = event.getItem();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		int count = random.nextInt(1, 4);

		for (int i = 0; i < count; i++) {
			CustomEnchantment enchantment = registry.getRandomEnchantment();

			if (!enchantment.canEnchantItem(target))
				continue;

			if (event.getEnchantsToAdd().keySet().contains(enchantment))
				continue;

			int level = enchantment.getStartLevel();

			for (Entry<Integer, EnchantmentLevel> levelEntry : enchantment.getLevels().entrySet()) {
				EnchantmentLevel enchantmentLevel = levelEntry.getValue();

				if (enchantmentLevel.getMinExperienceLevel() <= cost)
					level = Math.max(level, levelEntry.getKey());
			}

			event.getEnchantsToAdd().put(enchantment, level);
		}
	}
}
