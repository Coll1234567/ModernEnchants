package me.jishuna.modernenchants.api.effects;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "smelt")
public class SmeltEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Smelts an item as if it was put in a furance.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "smelt()" };

	private Map<Material, Material> smeltingMap;

	public SmeltEffect(String[] data) {
		super(data);
	}

	public void handle(EnchantmentContext context) {
		if (this.smeltingMap == null)
			loadSmeltables();

		if (context.getEvent()instanceof BlockDropItemEvent event) {
			for (Item item : event.getItems()) {
				handleItem(item);
			}
		}

		if (context.getEvent()instanceof PlayerFishEvent event && event.getCaught()instanceof Item item) {
			handleItem(item);
		}
	}

	private void handleItem(Item item) {
		ItemStack itemstack = item.getItemStack();
		Material material = itemstack.getType();
		Material smelted = this.smeltingMap.get(material);

		if (smelted != null) {
			itemstack.setType(smelted);
			item.setItemStack(itemstack);
		}
	}

	private void loadSmeltables() {
		this.smeltingMap = new EnumMap<>(Material.class);
		Iterator<Recipe> iterator = Bukkit.recipeIterator();

		while (iterator.hasNext()) {
			Recipe recipe = iterator.next();

			if (recipe instanceof CookingRecipe) {
				CookingRecipe<?> furnaceRecipe = (CookingRecipe<?>) recipe;
				if (furnaceRecipe.getInputChoice() instanceof MaterialChoice) {
					for (Material input : ((MaterialChoice) furnaceRecipe.getInputChoice()).getChoices()) {
						this.smeltingMap.put(input, furnaceRecipe.getResult().getType());
					}
				} else {
					this.smeltingMap.put(furnaceRecipe.getInput().getType(), furnaceRecipe.getResult().getType());
				}
			}
		}
	}
	
	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
