package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "burn")
public class BurnEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Burns the target for a certain length of time.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "burn(target,time)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN + "The entity to ignite, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Duration: " + ChatColor.GREEN + "The duration of the fire in ticks." };

	public BurnEffect() {
		super(DESCRIPTION);
	}

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		int duration = Integer.parseInt(data[1]);
		if (duration <= 0)
			throw new InvalidEnchantmentException("Duration must be greater than 0");

		return context -> context.getTarget(target).ifPresent(entity -> entity.setFireTicks(duration));
	}
}