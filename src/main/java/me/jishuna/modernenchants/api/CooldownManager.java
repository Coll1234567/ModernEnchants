package me.jishuna.modernenchants.api;

import java.util.UUID;

import org.bukkit.NamespacedKey;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;

public class CooldownManager {

	private static CooldownManager instance;

	private final Table<UUID, NamespacedKey, Long> cooldownTable = HashBasedTable.create();

	public void setCooldown(UUID id, CustomEnchantment enchant, long time) {
		this.cooldownTable.put(id, enchant.getKey(), System.currentTimeMillis() + time);
	}

	public long getCooldownTime(UUID id, CustomEnchantment enchant) {
		Long time = cooldownTable.get(id, enchant.getKey());

		if (time == null)
			return 0;

		return time - System.currentTimeMillis();
	}

	public boolean isOnCooldown(UUID id, CustomEnchantment enchant) {
		return getCooldownTime(id, enchant) > 0;
	}

	public static CooldownManager getInstance() {
		if (instance == null)
			instance = new CooldownManager();

		return instance;
	}

}
