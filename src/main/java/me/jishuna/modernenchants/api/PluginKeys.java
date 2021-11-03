package me.jishuna.modernenchants.api;

import org.bukkit.NamespacedKey;

import me.jishuna.modernenchants.ModernEnchants;

public enum PluginKeys {
	MINION_OWNER("owner"), DISPLAY_FORMAT("display_format"), PAGE_INDEX("page_index");

	private final NamespacedKey key;

	private PluginKeys(String name) {
		this.key = new NamespacedKey(ModernEnchants.getPlugin(ModernEnchants.class), name);
	}

	public NamespacedKey getKey() {
		return this.key;
	}

}
