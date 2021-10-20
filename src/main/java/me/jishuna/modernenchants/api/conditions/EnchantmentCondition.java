package me.jishuna.modernenchants.api.conditions;

import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public abstract class EnchantmentCondition {

	public EnchantmentCondition(String[] data) {}

	public abstract boolean check(EnchantmentContext context);

}
