package me.jishuna.modernenchants;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.event.WornEnchantmentCheckEvent;

public class WornEnchantmentRunnable extends BukkitRunnable {

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			ItemStack[] armor = player.getEquipment().getArmorContents();
			
			WornEnchantmentCheckEvent event = new WornEnchantmentCheckEvent(player, armor);
			Bukkit.getPluginManager().callEvent(event);
			
			for (ItemStack item : armor) {
				if (item == null || item.getType().isAir())
					continue;

				EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.WORN)
						.withItem(item).withUser(player).build();

				for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
					Enchantment enchant = enchants.getKey();

					if (!(enchant instanceof CustomEnchantment enchantment))
						continue;

					int level = enchants.getValue();

					enchantment.processActions(level, context);
				}
			}
		}
	}

}
