package me.jishuna.modernenchants.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.ObtainMethod;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchanterData;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;
import me.jishuna.modernenchants.api.enchantment.VanillaEnchantment;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;

public class EnchantingListener implements Listener {

	private final ModernEnchants plugin;
	private final Random random = new Random();

	private final Map<UUID, EnchanterData> enchanterMap = new HashMap<>();

	public EnchantingListener(ModernEnchants plugin) {
		this.plugin = plugin;
	}

	// TODO Ugly, fix
	@EventHandler
	public void onPrepare(PrepareItemEnchantEvent event) {
		ItemStack item = event.getItem();
		if (item.getEnchantments().size() > 0)
			return;

		boolean valid = this.plugin.getEnchantmentRegistry().isEnchantable(event.getItem().getType());

		if (event.isCancelled() && valid)
			event.setCancelled(false);

		CustomEnchantment def = this.plugin.getEnchantmentRegistry().getPlaceholderEnchant();
		if (def == null)
			return;

		EnchanterData data = this.enchanterMap.computeIfAbsent(event.getEnchanter().getUniqueId(),
				key -> new EnchanterData());

		this.random.setSeed(data.getSeed());

		for (int i = 0; i < 3; i++) {
			EnchantmentOffer offer = event.getOffers()[i];

			if (offer == null) {
				VanillaEnchantment enchant = this.plugin.getEnchantmentRegistry().getRandomVanillaEnchantment(this.random, item);

				if (enchant == null) {
					offer = new EnchantmentOffer(
							this.plugin.getEnchantmentRegistry().getPlaceholderEnchant().getEnchantment(), 1,
							EnchantmentHelper.getEnchantmentCost(this.random, i, event.getEnchantmentBonus(), 1));
				} else {
					int cost = EnchantmentHelper.getEnchantmentCost(this.random, i, event.getEnchantmentBonus(), 1);
					int[] range = EnchantmentHelper.getLevelRange(enchant, cost);
					int level = ThreadLocalRandom.current().nextInt(range[0], range[1] + 1);

					offer = new EnchantmentOffer(enchant.getEnchantment(), level, cost);
				}
				event.getOffers()[i] = offer;
				data.setKey(i, offer.getEnchantment().getKey());
				data.setLevel(i, offer.getEnchantmentLevel());
				continue;
			}
			IEnchantment enchantment = this.plugin.getEnchantmentRegistry()
					.getEnchantment(offer.getEnchantment().getKey());

			if (enchantment == null) {
				VanillaEnchantment enchant = this.plugin.getEnchantmentRegistry().getRandomVanillaEnchantment(this.random, item);

				if (enchant == null) {
					offer.setEnchantment(this.plugin.getEnchantmentRegistry().getPlaceholderEnchant().getEnchantment());
					offer.setEnchantmentLevel(1);
				} else {
					offer.setEnchantment(enchant.getEnchantment());
					int[] range = EnchantmentHelper.getLevelRange(enchant, offer.getCost());
					int level = ThreadLocalRandom.current().nextInt(range[0], range[1] + 1);
					offer.setEnchantmentLevel(level);
				}
			}
			data.setKey(i, offer.getEnchantment().getKey());
			data.setLevel(i, offer.getEnchantmentLevel());
		}
	}

	@EventHandler
	public void onEnchant(EnchantItemEvent event) {
		if (event.getViewers().isEmpty())
			return;
		HumanEntity user = event.getViewers().get(0);

		int index = event.whichButton();
		int cost = event.getExpLevelCost();
		Map<Enchantment, Integer> toAdd = event.getEnchantsToAdd();
		ItemStack target = event.getItem();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		boolean book = target.getType() == Material.BOOK;

		int count = Math.max(1, toAdd.size());
		if (count < 5 + 1) {
			count = random.nextInt(count, 5 + 1);
		}

		//TODO Ugly, fix
		toAdd.clear();
		EnchanterData data = this.enchanterMap.get(user.getUniqueId());
		if (data != null) {
			data.randomizeSeed();
			Enchantment enchant = Enchantment.getByKey(data.getKey(index));
			
			if (!(enchant instanceof CustomEnchantment)) {
				toAdd.put(enchant, data.getLevel(index));
				count--;
			}
		}

		toAdd.putAll(EnchantmentHelper.populateEnchantments(target, toAdd, this.plugin.getEnchantmentRegistry(), random,
				count, cost, ObtainMethod.ENCHANTING, book));

		if (book) {
			handleBook(event);
		}
	}

	private void handleBook(EnchantItemEvent event) {
		Bukkit.getScheduler().runTask(this.plugin, () -> {
			ItemStack item = event.getInventory().getItem(0);
			if (item == null || item.getType() != Material.ENCHANTED_BOOK)
				return;

			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

			for (Entry<Enchantment, Integer> toAdd : event.getEnchantsToAdd().entrySet()) {
				Enchantment enchant = toAdd.getKey();

				if (!meta.hasStoredEnchant(enchant))
					meta.addStoredEnchant(enchant, toAdd.getValue(), true);
			}

			item.setItemMeta(meta);
			event.getInventory().setItem(0, item);
		});
	}
}
