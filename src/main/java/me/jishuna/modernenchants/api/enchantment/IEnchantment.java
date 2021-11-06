package me.jishuna.modernenchants.api.enchantment;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.ObtainMethod;

public interface IEnchantment {
	
	public Enchantment getEnchantment();
	
	public NamespacedKey getKey();
	
	public String getName();
	
	public String getDisplayName();
	
	public String getLongDescription();
	
	public String getGroup();
	
	public double getWeight(ObtainMethod method);
	
	public boolean conflictsWith(Enchantment enchantment);
	
	public boolean canEnchantItem(ItemStack item);
	
	public int getStartLevel();
	
	public int getMaxLevel();
	
	public List<String> getValidItemsRaw();

}
