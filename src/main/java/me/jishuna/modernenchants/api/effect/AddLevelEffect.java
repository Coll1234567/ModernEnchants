package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "add_levels")
public class AddLevelEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN
					+ "Gives the target a certain number of experience levels, negative values will remove levels instead.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "add_levels(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN
					+ "The entity to give levels to, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The number of levels to give." };

	private final ActionTarget target;
	private final int amount;

	public AddLevelEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.amount = readInt(data[1], "amount");
	}

	@Override
	public void handle(EnchantmentContext context) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity instanceof Player player) {
			if (amount > 0) {
				player.giveExpLevels(amount);
			} else {
				player.setLevel(Math.max(0, player.getLevel() + amount));
			}
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}