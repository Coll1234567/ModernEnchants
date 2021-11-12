package me.jishuna.modernenchants.api.enchantment;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readMaterial;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.utils.ParseUtils;
import net.md_5.bungee.api.ChatColor;

public class VanillaEnchantment implements IEnchantment {
	private final Enchantment delegate;

	private final String name;
	private String displayName;
	private final String description;
	private final String longDescription;
	private final String group;

	private final int minLevel;
	private final int maxLevel;
	private final boolean cursed;

	private final List<String> validItemsRaw = new ArrayList<>();
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);
	private final Set<String> conflicts;

	private final Map<ObtainMethod, Double> weights = new EnumMap<>(ObtainMethod.class);

	public VanillaEnchantment(ModernEnchants plugin, ConfigurationSection section) throws InvalidEnchantmentException {
		this.name = section.getString("name").toLowerCase();

		Enchantment enchantment = Enchantment.getByKey(NamespacedKey.fromString(this.name));
		if (enchantment == null)
			throw new InvalidEnchantmentException("Invalid enchantment key: " + this.name);

		this.delegate = enchantment;
		
		this.cursed = section.getBoolean("cursed", false);

		this.displayName = ParseUtils.colorString(section.getString("display-name", name));
		if (plugin.getConfiguration().getBoolean("force-vanilla-enchantment-colors", false)) {
			this.displayName = (cursed ? ChatColor.RED : ChatColor.GRAY) + ChatColor.stripColor(this.displayName);
		}

		this.description = ParseUtils.colorString(section.getString("description"));
		this.longDescription = ParseUtils.colorString(section.getString("description-long", this.description));

		this.group = section.getString("group", null);

		ConfigurationSection weightSection = section.getConfigurationSection("weights");

		this.weights.put(ObtainMethod.ENCHANTING, weightSection.getDouble("enchanting", 100d));
		this.weights.put(ObtainMethod.VILLAGER, weightSection.getDouble("trading", 100d));
		this.weights.put(ObtainMethod.LOOT, weightSection.getDouble("loot", 100d));

		this.minLevel = section.getInt("min-level", 1);
		this.maxLevel = section.getInt("max-level", 5);

		List<String> validItemStrings = section.getStringList("valid-items");
		for (String item : validItemStrings) {
			this.validItems.addAll(readMaterial(item.toUpperCase()));
			this.validItemsRaw.add(StringUtils.capitalize(item.toLowerCase().replace("_", " ")));
		}

		this.conflicts = Sets.newHashSet(section.getStringList("conflicts"));
	}

	@Override
	public NamespacedKey getKey() {
		return this.delegate.getKey();
	}

	@Override
	public Enchantment getEnchantment() {
		return this.delegate;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLongDescription() {
		return longDescription;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getStartLevel() {
		return this.minLevel;
	}

	@Override
	public double getWeight(ObtainMethod method) {
		return this.weights.getOrDefault(method, 0d);
	}

	@Override
	public List<String> getValidItemsRaw() {
		return validItemsRaw;
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		if (!(other instanceof IEnchantment enchant)) {
			return this.conflicts.contains(other.getKey().toString());
		}

		if (this.group != null && enchant.getGroup() != null && this.group.equals(enchant.getGroup())) {
			return true;
		}

		return this.conflicts.contains(enchant.getName());
	}

	@Override
	public boolean conflictsWith(IEnchantment other) {
		if (this.group != null && other.getGroup() != null && this.group.equals(other.getGroup())) {
			return true;
		}

		return this.conflicts.contains(other.getName());
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return this.validItems.contains(item.getType());
	}
}
