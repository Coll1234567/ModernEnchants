package me.jishuna.modernenchants;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

public class WornEnchantmentRunnable extends BukkitRunnable {

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			ItemStack[] armor = player.getEquipment().getArmorContents();

			for (ItemStack item : armor) {
				if (item == null || item.getType().isAir())
					continue;

				ActionContext context = new ActionContext.Builder(TriggerRegistry.TICK).item(item).user(player).build();

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
