package me.jishuna.modernenchants.api.enchantments;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import me.jishuna.modernenchants.ModernEnchants;

public class EnchantmentRegistry {

	// private final ModernEnchants plugin;
	private final Map<NamespacedKey, CustomEnchantment> enchantmentMap = new HashMap<>();

	public EnchantmentRegistry(ModernEnchants plugin) {
		// this.plugin = plugin;
	}

	public void registerAndInjectEnchantment(CustomEnchantment enchantment) {
		Enchantment.registerEnchantment(enchantment);
		this.enchantmentMap.put(enchantment.getKey(), enchantment);
	}

	public CustomEnchantment getEnchantment(String name) {
		return this.enchantmentMap.get(NamespacedKey.fromString(name));
	}

	public Set<String> getNames() {
		return this.enchantmentMap.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toSet());
	}

}