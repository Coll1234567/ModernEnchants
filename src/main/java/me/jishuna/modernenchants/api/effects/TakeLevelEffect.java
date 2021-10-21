package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "take_levels")
public class TakeLevelEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Takes a certain amount of experience levels from the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "take_levels(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN
					+ "The entity to take levels from, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The number of levels to take." };

	private final ActionTarget target;
	private final int amount;

	public TakeLevelEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.amount = readInt(data[1]);

		if (this.amount <= 0)
			throw new InvalidEnchantmentException("Amount must be greater than 0");
	}

	@Override
	public void handle(EnchantmentContext context) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity instanceof Player player) {
			player.setLevel(Math.max(0, player.getLevel() - amount));
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}