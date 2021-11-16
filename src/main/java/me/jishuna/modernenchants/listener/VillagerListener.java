package me.jishuna.modernenchants.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;

public class VillagerListener implements Listener {

	private static final Integer[] DEF = new Integer[] { 17, 71 };
	private static final Map<Integer, Integer[]> priceMap = new HashMap<>();

	// https://minecraft.fandom.com/wiki/Trading#cite_note-enchanted-book-9
	static {
		priceMap.put(1, new Integer[] { 5, 19 });
		priceMap.put(2, new Integer[] { 8, 32 });
		priceMap.put(3, new Integer[] { 11, 45 });
		priceMap.put(4, new Integer[] { 14, 58 });
		priceMap.put(5, DEF);
	}

	private final EnchantmentRegistry registry;

	public VillagerListener(EnchantmentRegistry registry) {
		this.registry = registry;
	}

	@EventHandler
	public void onTradeGenerate(VillagerAcquireTradeEvent event) {
		MerchantRecipe oldRecipe = event.getRecipe();
		if (oldRecipe.getResult().getType() != Material.ENCHANTED_BOOK)
			return;

		ThreadLocalRandom random = ThreadLocalRandom.current();
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);

		IEnchantment enchantment = this.registry.getRandomEnchantment(item,
				ObtainMethod.VILLAGER, true);
		int level = random.nextInt(enchantment.getStartLevel(), enchantment.getMaxLevel() + 1);

		Integer[] costs = priceMap.getOrDefault(level, DEF);
		int cost = random.nextInt(costs[0], costs[1]);

		if (enchantment.isTreasure())
			cost *= 2;

		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment.getEnchantment(), level, true);
		item.setItemMeta(meta);

		MerchantRecipe newRecipe = new MerchantRecipe(item, oldRecipe.getMaxUses());
		newRecipe.setVillagerExperience(oldRecipe.getVillagerExperience());
		newRecipe.setPriceMultiplier(oldRecipe.getPriceMultiplier());

		newRecipe.setIngredients(
				Arrays.asList(new ItemStack(Material.BOOK), new ItemStack(Material.EMERALD, Math.min(64, cost))));
		
		event.setRecipe(newRecipe);
	}

}
