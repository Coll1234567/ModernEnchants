package me.jishuna.modernenchants.api.effect;

import org.bukkit.event.Cancellable;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "cancel_event")
public class CancelEventEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Completly cancels an event and its effects.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "cancel_event()" };

	public CancelEventEffect(String[] data) {
		super(data);
	}

	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof Cancellable event) {
			event.setCancelled(true);
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
