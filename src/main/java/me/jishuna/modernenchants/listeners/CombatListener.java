package me.jishuna.modernenchants.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ActionType;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public class CombatListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK
				|| event.getCause() == DamageCause.PROJECTILE)
			return;

		if (!(event.getEntity()instanceof LivingEntity target))
			return;

		Set<ItemStack> items = new HashSet<>();

		items.add(target.getEquipment().getItemInMainHand());
		items.add(target.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(target.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext context = EnchantmentContext.Builder.create(event, ActionType.HURTBY_OTHER)
					.withItem(item).withUser(target).build();

			for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
				Enchantment enchant = enchants.getKey();

				if (!(enchant instanceof CustomEnchantment enchantment))
					continue;

				int level = enchants.getValue();

				enchantment.processActions(level, context);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getEntity()instanceof LivingEntity target))
			return;

		if (event.getDamager()instanceof LivingEntity damager) {
			handleDamage(event, damager, target, false);
		} else if (event.getDamager()instanceof Projectile projectile
				&& projectile.getShooter()instanceof LivingEntity shooter) {
			handleDamage(event, shooter, target, true);
		}
	}

	private void handleDamage(EntityDamageByEntityEvent event, LivingEntity damager, LivingEntity target, boolean bow) {
		// Damager
		boolean targetIsPlayer = target.getType() == EntityType.PLAYER;
		Set<ItemStack> items = new HashSet<>();

		items.add(damager.getEquipment().getItemInMainHand());
		items.add(damager.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(damager.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext playerContext = EnchantmentContext.Builder
					.create(event, bow ? ActionType.ATTACK_PLAYER_PROJECTILE : ActionType.ATTACK_PLAYER).withItem(item)
					.withUser(damager).withOpponent(target).build();

			EnchantmentContext mobContext = EnchantmentContext.Builder
					.create(event, bow ? ActionType.ATTACK_MOB_PROJECTILE : ActionType.ATTACK_MOB).withItem(item)
					.withUser(damager).withOpponent(target).build();

			handleItem(item, targetIsPlayer, playerContext, mobContext);
		}

		// Target
		boolean damagerIsPlayer = damager.getType() == EntityType.PLAYER;
		items.clear();

		items.add(target.getEquipment().getItemInMainHand());
		items.add(target.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(target.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			EnchantmentContext playerContext = EnchantmentContext.Builder
					.create(event, bow ? ActionType.HURTBY_PLAYER_PROJECTILE : ActionType.HURTBY_PLAYER).withItem(item)
					.withUser(target).withOpponent(damager).build();

			EnchantmentContext mobContext = EnchantmentContext.Builder
					.create(event, bow ? ActionType.ATTACK_MOB_PROJECTILE : ActionType.HURTBY_MOB).withItem(item)
					.withUser(target).withOpponent(damager).build();

			handleItem(item, damagerIsPlayer, playerContext, mobContext);
		}
	}

	private void handleItem(ItemStack item, boolean player, EnchantmentContext playerContext,
			EnchantmentContext mobContext) {
		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();

			if (player) {
				enchantment.processActions(level, playerContext);
			} else {
				enchantment.processActions(level, mobContext);
			}
		}
	}
}
