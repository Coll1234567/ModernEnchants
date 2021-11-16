package me.jishuna.modernenchants.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public class DeathListener implements Listener {

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
		if (lastCause instanceof EntityDamageByEntityEvent damageEvent
				&& damageEvent.getDamager()instanceof LivingEntity killer) {
			boolean killedPlayer = (entity instanceof Player);

			Set<ItemStack> items = new HashSet<>();

			items.add(killer.getEquipment().getItemInMainHand());
			items.add(killer.getEquipment().getItemInOffHand());
			items.addAll(Arrays.asList(killer.getEquipment().getArmorContents()));

			for (ItemStack item : items) {
				if (item == null || item.getType().isAir())
					continue;

				EnchantmentContext context = EnchantmentContext.Builder
						.create(event, killedPlayer ? ActionType.KILL_PLAYER : ActionType.KILL_MOB).withItem(item)
						.withUser(killer).withOpponent(entity).build();

				for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
					Enchantment enchant = enchants.getKey();

					if (!(enchant instanceof CustomEnchantment enchantment))
						continue;

					int level = enchants.getValue();

					enchantment.processActions(level, context);
				}
			}

			boolean killedByPlayer = (killer instanceof Player);
			items.clear();

			items.add(entity.getEquipment().getItemInMainHand());
			items.add(entity.getEquipment().getItemInOffHand());
			items.addAll(Arrays.asList(entity.getEquipment().getArmorContents()));

			for (ItemStack item : items) {
				if (item == null || item.getType().isAir())
					continue;

				EnchantmentContext context = EnchantmentContext.Builder
						.create(event, killedByPlayer ? ActionType.KILLEDBY_PLAYER : ActionType.KILLEDBY_MOB)
						.withItem(item).withUser(entity).withOpponent(killer).build();

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
