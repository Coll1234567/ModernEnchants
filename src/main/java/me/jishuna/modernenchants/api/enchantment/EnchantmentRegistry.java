package me.jishuna.modernenchants.api.enchantment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import me.jishuna.commonlib.random.WeightedRandom;

public class EnchantmentRegistry {

	private final WeightedRandom<CustomEnchantment> enchantRandom = new WeightedRandom<>();
	private final Map<NamespacedKey, CustomEnchantment> enchantmentMap = new TreeMap<>();

	public void registerAndInjectEnchantment(CustomEnchantment enchantment) {
		Enchantment.registerEnchantment(enchantment);
		this.enchantmentMap.put(enchantment.getKey(), enchantment);

		double enchantWeight = enchantment.getEnchantingWeight();
		if (enchantWeight > 0)
			this.enchantRandom.add(enchantWeight, enchantment);
	}

	public CustomEnchantment getEnchantment(String name) {
		return getEnchantment(NamespacedKey.fromString(name));
	}

	public CustomEnchantment getEnchantment(NamespacedKey key) {
		return this.enchantmentMap.get(key);
	}

	public Set<String> getNames() {
		return this.enchantmentMap.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toSet());
	}

	public Collection<CustomEnchantment> getAllEnchantments() {
		return this.enchantmentMap.values();
	}

	public CustomEnchantment getRandomEnchantment() {
		return this.enchantRandom.poll();
	}

}