package me.jishuna.modernenchants.api.effects.curses;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readDouble;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;
import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.effects.EnchantmentEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "curse_aim")
public class BadAimEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Deal damage to the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "damage(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN
					+ "The entity to damage, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The amount of damage to deal, 2 damage = 1 heart." };

	private final double intensity;

	public BadAimEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.intensity = readDouble(data[0]);

		if (this.intensity <= 0)
			throw new InvalidEnchantmentException("Intensity must be greater than 0");
	}

	@Override
	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof ProjectileLaunchEvent event) {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			double x = random.nextDouble(-intensity, intensity);
			double y = random.nextDouble(-intensity, intensity);
			double z = random.nextDouble(-intensity, intensity);

			event.getEntity().setVelocity(event.getEntity().getVelocity().add(new Vector(x, y, z)));
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
