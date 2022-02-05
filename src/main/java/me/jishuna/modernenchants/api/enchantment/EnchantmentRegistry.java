package me.jishuna.modernenchants.api.enchantment;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.jishuna.commonlib.random.WeightedRandom;
import me.jishuna.modernenchants.api.FixedSizeLinkedHashMap;
import me.jishuna.modernenchants.api.ObtainMethod;

public class EnchantmentRegistry {

	private final EnumSet<Material> validItemSet = EnumSet.noneOf(Material.class);
	private CustomEnchantment placeholderEnchant = null;
	private final Map<Material, WeightedRandom<IEnchantment>> itemCache = new FixedSizeLinkedHashMap<>(50);
	private final Map<NamespacedKey, IEnchantment> enchantmentMap = new TreeMap<>();
	private final Set<VanillaEnchantment> vanillaEnchants = new HashSet<>();

	public void registerAndInjectEnchantment(IEnchantment enchantment) {
		this.enchantmentMap.put(enchantment.getKey(), enchantment);

		if (enchantment.getWeight(ObtainMethod.ENCHANTING) > 0)
			validItemSet.addAll(enchantment.getValidItems());

		if (enchantment instanceof CustomEnchantment enchant) {
			if (placeholderEnchant == null)
				placeholderEnchant = enchant;
			Enchantment.registerEnchantment(enchant);
		} else if (enchantment instanceof VanillaEnchantment enchant
				&& enchant.getWeight(ObtainMethod.ENCHANTING) > 0) {
			this.vanillaEnchants.add(enchant);
		}
	}

	public IEnchantment getRandomEnchantment(ItemStack item, ObtainMethod method, boolean book) {
		Material type = item.getType();
		WeightedRandom<IEnchantment> random = this.itemCache.get(type);

		if (random == null) {
			random = new WeightedRandom<>();

			for (IEnchantment enchantment : getAllEnchantments()) {
				if (book || enchantment.canEnchantItem(item, method == ObtainMethod.ENCHANTING)) {
					double weight = enchantment.getWeight(method);

					if (weight > 0)
						random.add(weight, enchantment);
				}
			}
			this.itemCache.put(type, random);
		}
		return random.isEmpty() ? null : random.poll();
	}

	public VanillaEnchantment getRandomVanillaEnchantment(Random rand, ItemStack item) {
		WeightedRandom<VanillaEnchantment> random = new WeightedRandom<>(rand);

		for (VanillaEnchantment enchantment : this.vanillaEnchants) {
			if (enchantment.canEnchantItem(item, true)) {
				double weight = enchantment.getWeight(ObtainMethod.ENCHANTING);

				if (weight > 0)
					random.add(weight, enchantment);
			}
		}

		return random.isEmpty() ? null : random.poll();
	}

	public IEnchantment getEnchantment(String name) {
		NamespacedKey key = NamespacedKey.fromString(name);
		if (key == null)
			return null;
		return getEnchantment(key);
	}

	public IEnchantment getEnchantment(NamespacedKey key) {
		return this.enchantmentMap.get(key);
	}

	public IEnchantment getEnchantmentByName(String name) {
		IEnchantment enchantment = getEnchantment("modernenchants:" + name);
		if (enchantment == null)
			return getEnchantment("minecraft:" + name);
		return enchantment;
	}

	public boolean isEnchantable(Material type) {
		return this.validItemSet.contains(type);
	}

	public Set<String> getKeys() {
		return this.enchantmentMap.keySet().stream().map(NamespacedKey::toString).collect(Collectors.toSet());
	}

	public Set<String> getNames() {
		return this.enchantmentMap.keySet().stream().map(NamespacedKey::getKey).collect(Collectors.toSet());
	}

	public Collection<IEnchantment> getAllEnchantments() {
		return this.enchantmentMap.values();
	}

	public CustomEnchantment getPlaceholderEnchant() {
		return placeholderEnchant;
	}

	public void unregisterAll() {
		try {
			Field byIdField = Enchantment.class.getDeclaredField("byKey");
			Field byNameField = Enchantment.class.getDeclaredField("byName");
			byIdField.setAccessible(true);
			byNameField.setAccessible(true);

			@SuppressWarnings("unchecked")
			Map<NamespacedKey, Enchantment> keyMap = (Map<NamespacedKey, Enchantment>) byIdField.get(null);
			@SuppressWarnings("unchecked")
			Map<String, Enchantment> nameMap = (Map<String, Enchantment>) byNameField.get(null);

			Iterator<Entry<NamespacedKey, Enchantment>> keyIterator = keyMap.entrySet().iterator();
			while (keyIterator.hasNext()) {
				Enchantment enchant = keyIterator.next().getValue();

				if (enchant instanceof CustomEnchantment)
					keyIterator.remove();
			}

			Iterator<Entry<String, Enchantment>> nameIterator = nameMap.entrySet().iterator();
			while (nameIterator.hasNext()) {
				Enchantment enchant = nameIterator.next().getValue();

				if (enchant instanceof CustomEnchantment)
					nameIterator.remove();
			}

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}