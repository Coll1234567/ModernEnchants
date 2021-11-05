package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "freeze")
public class FreezeEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Freezes the target for a certain length of time.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "freeze(target,time,[stack])",
			ChatColor.GOLD + "  Target: " + ChatColor.GREEN + "The entity to freeze, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  Duration: " + ChatColor.GREEN + "The duration of the freezing in ticks.",
			ChatColor.GOLD + "  Stack [Optional, default=false]: " + ChatColor.GREEN
					+ "Whether the duration should be added to the entity's current freeze duration or not." };

	private final ActionTarget target;
	private final int duration;
	private final boolean stack;

	public FreezeEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.duration = readInt(data[1], "duration");

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
				.ifPresent(entity -> entity.setFreezeTicks((stack ? entity.getFreezeTicks() : 0) + duration));
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}