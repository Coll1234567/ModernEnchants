package me.jishuna.modernenchants.api.enchantment;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readMaterial;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.exceptions.ParsingException;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.utils.ParseUtils;
import net.md_5.bungee.api.ChatColor;

public class CustomEnchantment extends Enchantment implements IEnchantment {

	private final String name;
	private final String description;
	private final String longDescription;
	private String displayName;
	private final String group;

	private final int minLevel;
	private final int maxLevel;
	private final boolean cursed;
	private final boolean treasure;

	private final Set<String> validItemsRaw = new HashSet<>();
	private final Set<Material> validItems = EnumSet.noneOf(Material.class);
	private final Set<Material> validItemsAnvil = EnumSet.noneOf(Material.class);
	private final Set<String> conflicts;

	private final Map<ObtainMethod, Double> weights = new EnumMap<>(ObtainMethod.class);
	private final Map<Integer, EnchantmentLevel> levels = new HashMap<>();

	public CustomEnchantment(ModernEnchants plugin, ConfigurationSection section) throws ParsingException {
		super(new NamespacedKey(plugin, section.getString("name")));
		
		this.name = section.getString("name").toLowerCase();

		this.cursed = section.getBoolean("cursed", false);
		this.treasure = section.getBoolean("treasure", false);

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

		for (String item : section.getStringList("valid-items")) {
			this.validItems.addAll(readMaterial(item.toUpperCase()));
			this.validItemsRaw.add(StringUtils.capitalize(item.toLowerCase().replace("_", " ")));
		}

		this.validItemsAnvil.addAll(this.validItems);
		for (String item : section.getStringList("valid-items-anvil")) {
			this.validItemsAnvil.addAll(readMaterial(item.toUpperCase()));
			this.validItemsRaw.add(StringUtils.capitalize(item.toLowerCase().replace("_", " ")));
		}

		this.conflicts = Sets.newHashSet(section.getStringList("conflicts"));

		ConfigurationSection levelsSection = section.getConfigurationSection("levels");

		for (String levelString : levelsSection.getKeys(false)) {
			ConfigurationSection levelSection = levelsSection.getConfigurationSection(levelString);

			if (levelSection == null)
				continue;

			int level = Integer.parseInt(levelString);

			// Parse a single level
			try {
				this.levels.put(level, new EnchantmentLevel(plugin.getActionLib(), levelSection));
			} catch (ParsingException ex) {
				throw new ParsingException("Error parsing enchantment level " + level, ex);
			}
		}
	}

	public void processActions(int level, ActionContext context) {
		EnchantmentLevel enchantmentLevel = this.levels.get(level);
		if (enchantmentLevel != null) {
			enchantmentLevel.processActions(context);
		}
	}

	@Override
	public Enchantment getEnchantment() {
		return this;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

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
	public int getMinLevelCost() {
		EnchantmentLevel level = this.levels.get(this.minLevel);

		if (level == null)
			return 0;
		return level.getMinExperienceLevel();
	}

	@Override
	public double getWeight(ObtainMethod method) {
		return this.weights.getOrDefault(method, 0d);
	}

	@Override
	public Set<Material> getValidItems() {
		return validItems;
	}

	@Override
	public Set<String> getValidItemsRaw() {
		return validItemsRaw;
	}

	public Map<Integer, EnchantmentLevel> getLevels() {
		return levels;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BREAKABLE;
	}

	@Override
	public boolean isTreasure() {
		return this.treasure;
	}

	@Override
	public boolean isCursed() {
		return this.cursed;
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
		return canEnchantItem(item, false);
	}

	@Override
	public boolean canEnchantItem(ItemStack item, boolean table) {
		return table ? this.validItems.contains(item.getType()) : this.validItemsAnvil.contains(item.getType());
	}
}
