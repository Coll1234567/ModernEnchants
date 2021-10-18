package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "damage")
public class DamageEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Deal damage to the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "damage(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN + "The entity to damage, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The amount of damage to deal, 2 damage = 1 heart." };

	public DamageEffect() {
		super(DESCRIPTION);
	}

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		double damage = Double.parseDouble(data[1]);
		if (damage <= 0)
			throw new InvalidEnchantmentException("Damage must be greater than 0");

		return context -> context.getTarget(target).ifPresent(entity -> entity.damage(damage));
	}
}
