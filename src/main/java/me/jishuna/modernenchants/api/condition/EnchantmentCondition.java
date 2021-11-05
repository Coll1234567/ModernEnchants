package me.jishuna.modernenchants.api.condition;

import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public abstract class EnchantmentCondition {

	public EnchantmentCondition(String[] data) {}

	public abstract boolean check(EnchantmentContext context, CustomEnchantment enchant);

}
