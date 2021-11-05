package me.jishuna.modernenchants.api.effect;

import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;

public abstract class EnchantmentEffect {

	public EnchantmentEffect(String[] data) {}

	public abstract void handle(EnchantmentContext context);
}
