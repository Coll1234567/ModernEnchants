package me.jishuna.modernenchants.api.enchantment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import me.jishuna.commonlib.random.WeightedRandom;
import me.jishuna.modernenchants.api.ObtainMethod;

public class EnchantmentRegistry {

	private final WeightedRandom<IEnchantment> enchantRandom = new WeightedRandom<>();
	private final Map<NamespacedKey, IEnchantment> enchantmentMap = new TreeMap<>();

	public void registerAndInjectEnchantment(IEnchantment enchantment) {
		this.enchantmentMap.put(enchantment.getKey(), enchantment);

		double enchantWeight = enchantment.getWeight(ObtainMethod.ENCHANTING);
		if (enchantWeight > 0)
			this.enchantRandom.add(enchantWeight, enchantment);

		if (enchantment instanceof CustomEnchantment enchant)
			Enchantment.registerEnchantment(enchant);
	}

	public IEnchantment getEnchantment(String name) {
		return getEnchantment(NamespacedKey.fromString(name));
	}

	public IEnchantment getEnchantment(NamespacedKey key) {
		return this.enchantmentMap.get(key);
	}

	public Set<String> getNames() {
		return this.enchantmentMap.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toSet());
	}

	public Collection<IEnchantment> getAllEnchantments() {
		return this.enchantmentMap.values();
	}

	public IEnchantment getRandomEnchantment() {
		return this.enchantRandom.poll();
	}

}