package me.jishuna.modernenchants.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.triggers.TriggerRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;

public class DeathListener implements Listener {

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		EntityDamageEvent lastCause = event.getEntity().getLastDamageCause();
		Set<ItemStack> items;
		Entity killer = null;
		if (lastCause instanceof EntityDamageByEntityEvent damageEvent) {
			killer = damageEvent.getDamager();

			if (killer instanceof LivingEntity living) {

				items = new HashSet<>();

				items.add(living.getEquipment().getItemInMainHand());
				items.add(living.getEquipment().getItemInOffHand());
				items.addAll(Arrays.asList(living.getEquipment().getArmorContents()));

				for (ItemStack item : items) {
					if (item == null || item.getType().isAir())
						continue;

					ActionContext context = new ActionContext.Builder(TriggerRegistry.KILL_ENTITY).event(event)
							.item(item).user(living).opponent(entity).build();

					handleItem(item, context);
				}
			}
		}

		items = new HashSet<>();

		items.add(entity.getEquipment().getItemInMainHand());
		items.add(entity.getEquipment().getItemInOffHand());
		items.addAll(Arrays.asList(entity.getEquipment().getArmorContents()));

		for (ItemStack item : items) {
			if (item == null || item.getType().isAir())
				continue;

			ActionContext context = new ActionContext.Builder(TriggerRegistry.DEATH).event(event).item(item)
					.user(entity).opponent(killer).build();

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
