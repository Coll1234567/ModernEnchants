package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "burn")
public class BurnEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Burns the target for a certain length of time.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "burn(target,time,[stack])",
			ChatColor.GOLD + "  Target: " + ChatColor.GREEN + "The entity to ignite, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  Duration: " + ChatColor.GREEN + "The duration of the fire in ticks.",
			ChatColor.GOLD + "  Stack [Optional, default=false]: " + ChatColor.GREEN
					+ "Whether the duration should be added to the entity's current burn duration or not." };

	private final ActionTarget target;
	private final int duration;
	private final boolean stack;

	public BurnEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.duration = readInt(data[1]);

		if (this.duration <= 0)
			throw new InvalidEnchantmentException("Duration must be greater than 0");

		boolean shouldStack = false;

		if (data.length >= 3) {
			shouldStack = Boolean.parseBoolean(data[2]);
		}
		this.stack = shouldStack;
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target)
				.ifPresent(entity -> entity.setFireTicks((stack ? entity.getFireTicks() : 0) + duration));
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}