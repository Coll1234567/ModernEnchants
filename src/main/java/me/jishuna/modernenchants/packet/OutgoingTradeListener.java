package me.jishuna.modernenchants.packet;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.utils.EnchantmentHelper;
import me.jishuna.modernenchants.api.utils.ItemUtils;

public class OutgoingTradeListener extends PacketAdapter {

	private final ModernEnchants plugin;

	public OutgoingTradeListener(ModernEnchants plugin) {
		super(plugin, PacketType.Play.Server.OPEN_WINDOW_MERCHANT);
		this.plugin = plugin;
	}

	@Override
	public void onPacketSending(PacketEvent event) {
		PacketContainer packet = event.getPacket();
		List<MerchantRecipe> recipes = packet.getMerchantRecipeLists().readSafely(0);

		if (recipes == null)
			return;

		for (int i = 0; i < recipes.size(); i++) {
			MerchantRecipe oldRecipe = recipes.get(i);

			ItemStack result = oldRecipe.getResult();
			ItemMeta meta = result.getItemMeta();

			if (EnchantmentHelper.getEnchants(meta).isEmpty())
				continue;

			ItemUtils.handleOutgoingItem(event.getPlayer(), result, this.plugin.getEnchantmentRegistry());

			MerchantRecipe newRecipe = new MerchantRecipe(result, oldRecipe.getMaxUses());
			newRecipe.setVillagerExperience(oldRecipe.getVillagerExperience());
			newRecipe.setPriceMultiplier(oldRecipe.getPriceMultiplier());
			newRecipe.setUses(oldRecipe.getUses());
			newRecipe.setIngredients(oldRecipe.getIngredients());

			recipes.set(i, newRecipe);
		}

		packet.getMerchantRecipeLists().writeSafely(0, recipes);
		event.setPacket(packet);
	}
}
