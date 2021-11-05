package me.jishuna.modernenchants.api.effect;

import org.bukkit.event.entity.EntityDamageEvent;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "cancel_damage")
public class CancelDamageEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Completly cancels damage and knockback.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "cancel_damage()" };

	public CancelDamageEffect(String[] data) {
		super(data);
	}

	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof EntityDamageEvent event) {
			event.setCancelled(true);
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
