package me.jishuna.modernenchants.api.effects;

import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;

public abstract class EnchantmentEffect {

	public EnchantmentEffect(String[] data) {}

	public abstract void handle(EnchantmentContext context);
}
