package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "kill")
public class KillEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Instantly kill the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "kill(target)", ChatColor.GOLD + "  - Target: "
					+ ChatColor.GREEN + "The entity to kill, either \"user\" or \"opponent\"." };

	private final ActionTarget target;

	public KillEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.target = readTarget(data[0]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.setHealth(0.0d));
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
