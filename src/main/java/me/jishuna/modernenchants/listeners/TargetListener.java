package me.jishuna.modernenchants.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.modernenchants.api.PluginKeys;

public class TargetListener implements Listener {

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() == null)
			return;
		String uuid = event.getTarget().getUniqueId().toString();

		if (event.getEntity().getPersistentDataContainer()
				.getOrDefault(PluginKeys.MINION_OWNER.getKey(), PersistentDataType.STRING, "").equals(uuid))
			event.setCancelled(true);
	}
}
