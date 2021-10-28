package me.jishuna.modernenchants.api;

import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class Utils {

	public static Optional<String> getMobOwner(Entity entity) {
		return Optional.ofNullable(
				entity.getPersistentDataContainer().get(PluginKeys.MINION_OWNER.getKey(), PersistentDataType.STRING));
	}

}
