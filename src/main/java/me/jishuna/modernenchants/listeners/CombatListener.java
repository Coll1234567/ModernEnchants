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

		handleAttacker(event, damager, target);
	}

	private void handleAttacker(EntityDamageByEntityEvent event, LivingEntity damager, LivingEntity target) {
		boolean targetIsPlayer = (target instanceof Player);

		ItemStack item = damager.getEquipment().getItemInMainHand();

		if (item.getType().isAir())
			return;

		EnchantmentContext playerContext = EnchantmentContext.Builder.create(event, ActionType.ATTACK_PLAYER)
				.withItem(item).withUser(damager).withOpponent(target).build();

		EnchantmentContext mobContext = EnchantmentContext.Builder.create(event, ActionType.ATTACK_MOB).withItem(item)
				.withUser(damager).withOpponent(target).build();

		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();

			if (targetIsPlayer) {
				enchantment.processActions(level, playerContext);
			} else {
				enchantment.processActions(level, mobContext);
			}
		}

		for (ItemStack armor : damager.getEquipment().getArmorContents()) {
			if (armor == null || armor.getType().isAir())
				continue;
			
			playerContext = EnchantmentContext.Builder.create(event, ActionType.ATTACK_PLAYER)
					.withItem(armor).withUser(damager).withOpponent(target).build();

			mobContext = EnchantmentContext.Builder.create(event, ActionType.ATTACK_MOB).withItem(armor)
					.withUser(damager).withOpponent(target).build();
			
			for (Entry<Enchantment, Integer> enchants : armor.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();

				if (targetIsPlayer) {
					enchantment.processActions(level, playerContext);
				} else {
					enchantment.processActions(level, mobContext);
				}
			}
		}

	}
}
