package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
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
		
		this.ticks = readInt(data[0]);
	}

	public void handle(EnchantmentContext context) {
		// Should never actually be used
	}
	
	public int getDelay() {
		return ticks;
	}
	
	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
