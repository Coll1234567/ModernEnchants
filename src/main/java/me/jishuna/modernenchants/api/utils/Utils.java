package me.jishuna.modernenchants.api.utils;

import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.modernenchants.api.PluginKeys;

public class Utils {

	public static Optional<String> getMobOwner(Entity entity) {
		return Optional.ofNullable(
				entity.getPersistentDataContainer().get(PluginKeys.MINION_OWNER.getKey(), PersistentDataType.STRING));
	}
	
	public static String getDisplayFormat(Player player) {
		return player.getPersistentDataContainer().getOrDefault(PluginKeys.DISPLAY_FORMAT.getKey(),
				PersistentDataType.STRING, "inline");
	}

}
