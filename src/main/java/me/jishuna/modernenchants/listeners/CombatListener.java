package me.jishuna.modernenchants.listeners;

import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public class CombatListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager()instanceof LivingEntity damager))
			return;

		if (!(event.getEntity()instanceof LivingEntity target))
			return;

		boolean isPlayer = (target instanceof Player);
		ItemStack item = damager.getEquipment().getItemInMainHand();

		if (item.getType().isAir())
			return;

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();
			EnchantmentContext context = EnchantmentContext.Builder.fromEvent(event).withUser(damager)
					.withOpponent(target).build();

			if (isPlayer) {
				enchantment.processActions(level, ActionType.ATTACK_PLAYER, context);
			} else {
				enchantment.processActions(level, ActionType.ATTACK_MOB, context);
			}
		}
	}
}
