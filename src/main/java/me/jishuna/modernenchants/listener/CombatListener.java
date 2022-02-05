package me.jishuna.modernenchants.listener;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.utils.Utils;

public class CombatListener implements Listener {
	private static final EnumSet<Material> BOWS = EnumSet.of(Material.BOW, Material.CROSSBOW);

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK
				|| event.getCause() == DamageCause.PROJECTILE)
			return;

		if (!(event.getEntity() instanceof LivingEntity target))
			return;

		Set<ItemStack> items = new HashSet<>();

		items.add(target.getEquipment().getItemInMainHand());
		items.add(target.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(target.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			ActionContext context = new ActionContext.Builder(TriggerRegistry.DAMAGED_BY_OTHER).event(event).item(item)
					.user(target).build();

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
		Optional<String> owner = Utils.getMobOwner(event.getEntity());
		if (owner.isPresent() && owner.get().equals(event.getDamager().getUniqueId().toString())) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof LivingEntity target))
			return;

		if (event.getDamager() instanceof LivingEntity damager) {
			handleDamage(event, damager, target, false);
		} else if (event.getDamager() instanceof Projectile projectile
				&& projectile.getShooter() instanceof LivingEntity shooter) {
			if (owner.isPresent() && owner.get().equals(shooter.getUniqueId().toString())) {
				event.setCancelled(true);
				return;
			}
			handleDamage(event, shooter, target, true);
		}
	}

	private void handleDamage(EntityDamageByEntityEvent event, LivingEntity damager, LivingEntity target, boolean bow) {
		// Damager
		Set<ItemStack> items = new HashSet<>();

		items.add(damager.getEquipment().getItemInMainHand());
		items.add(damager.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(damager.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			if (!bow && BOWS.contains(item.getType()))
				continue;

			ActionContext context = new ActionContext.Builder(
					bow ? TriggerRegistry.ATTACK_PROJECTILE : TriggerRegistry.ATTACK_ENTITY).event(event)
							.item(item).user(damager).opponent(target).build();

			handleItem(item, context);
		}

		// Target
		items.clear();

		items.add(target.getEquipment().getItemInMainHand());
		items.add(target.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(target.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			ActionContext context = new ActionContext.Builder(
					bow ? TriggerRegistry.DAMAGED_BY_PROJECTILE : TriggerRegistry.DAMAGED_BY_ENTITY).event(event)
							.item(item).user(target).opponent(damager).build();

			handleItem(item, context);
		}
	}

	private void handleItem(ItemStack item, ActionContext context) {
		for (Entry<Enchantment, Integer> enchants : item.getEnchantments().entrySet()) {
			Enchantment enchant = enchants.getKey();

			if (!(enchant instanceof CustomEnchantment enchantment))
				continue;

			int level = enchants.getValue();

			enchantment.processActions(level, context);
		}
	}
}
