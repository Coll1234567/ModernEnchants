package me.jishuna.modernenchants;

import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentSlotType;

public class NMSEnchantmentWrapper extends Enchantment {

	private final CustomEnchantment handle;

	public NMSEnchantmentWrapper(CustomEnchantment enchant) {
		super(Rarity.a, EnchantmentSlotType.j, new EnumItemSlot[] { EnumItemSlot.a });
		this.handle = enchant;
	}

	@Override
	public int getStartLevel() {
		return handle.getStartLevel();
	}

	@Override
	public int getMaxLevel() {
		return handle.getMaxLevel();
	}

	@Override
	public IChatBaseComponent d(int var0) {
		return new ChatComponentText("Testing");
	}

}
