package me.jishuna.modernenchants.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import me.jishuna.modernenchants.api.Utils;

public class MinionListener implements Listener {

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() == null)
			return;

		String uuid = event.getTarget().getUniqueId().toString();

		Utils.getMobOwner(event.getEntity()).ifPresent(owner -> {
			if (owner.equals(uuid))
				event.setCancelled(true);
		});
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		Utils.getMobOwner(event.getEntity()).ifPresent(owner -> {
			event.setDroppedExp(0);
			event.getDrops().clear();
		});
	}
}
