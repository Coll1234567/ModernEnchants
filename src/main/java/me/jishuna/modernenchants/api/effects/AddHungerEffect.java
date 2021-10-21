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

@RegisterEffect(name = "add_hunger")
public class AddHungerEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Gives a certain amount of hunger to the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "add_hunger(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN + "The entity to give hunger to, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The amount of hunger to give (1 bar = 2 hunger)" };

	private final ActionTarget target;
	private final int amount;

	public AddHungerEffect(String[] data) throws InvalidEnchantmentException {
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
			player.setFoodLevel(Math.min(20, player.getFoodLevel() + amount));
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}