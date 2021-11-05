package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readFloat;

import org.bukkit.event.player.PlayerItemDamageEvent;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "multiply_durability")
public class MultiplyDurabilityEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Multiplies the durability damage an item takes.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "multiply_durability(multiplier)",
			ChatColor.GOLD + "  Multiplier: " + ChatColor.GREEN + "The multiplier to apply to the durability." };

	private final float multiplier;

	public MultiplyDurabilityEffect(String[] data) throws InvalidEnchantmentException {
		super(data);

		this.multiplier = readFloat(data[0], "multiplier");

		if (this.multiplier < 0)
			throw new InvalidEnchantmentException("Multiplier must be greater than 0");
	}

	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof PlayerItemDamageEvent event) {
			event.setDamage(Math.round(event.getDamage() * multiplier));
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
