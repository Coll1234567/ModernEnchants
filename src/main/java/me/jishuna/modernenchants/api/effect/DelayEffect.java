package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "delay")
public class DelayEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Delays all following actions for a certain number of ticks.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "delay(ticks)",
			ChatColor.GOLD + "  Ticks: " + ChatColor.GREEN + "The number of ticks to wait"
		};
	
	private final int ticks;

	public DelayEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);
		
		this.ticks = readInt(data[0], "ticks");
	}

	public void handle(EnchantmentContext context) {
		// The delay effect is handled differently due to the nature of its function
	}
	
	public int getDelay() {
		return ticks;
	}
	
	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
