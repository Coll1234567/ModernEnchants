package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.readFloat;

import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "multiply_experience")
public class MultiplyExperienceEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Multiplies the experience from a block or entity.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "multiply_experience(multiplier)",
			ChatColor.GOLD + "  Multiplier: " + ChatColor.GREEN + "The multiplier to apply to the experience." };

	private final float multiplier;

	public MultiplyExperienceEffect(String[] data) throws InvalidEnchantmentException {
		super(data);

		this.multiplier = readFloat(data[0], "multiplier");

		if (this.multiplier < 0)
			throw new InvalidEnchantmentException("Multiplier must be greater than 0");
	}

	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof EntityDeathEvent event && event.getEntityType() != EntityType.PLAYER) {
			event.setDroppedExp(Math.round(event.getDroppedExp() * this.multiplier));
		} else if (context.getEvent()instanceof BlockBreakEvent event) {
			event.setExpToDrop(Math.round(event.getExpToDrop() * this.multiplier));
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
