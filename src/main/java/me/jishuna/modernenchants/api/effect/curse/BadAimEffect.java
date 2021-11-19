package me.jishuna.modernenchants.api.effect.curse;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readDouble;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;
import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.effect.EnchantmentEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "curse_aim")
public class BadAimEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Reduces the accuracy of projectiles.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "curse_aim(intensity)",
			ChatColor.GOLD + "  - Intensity: " + ChatColor.GREEN + "The intensity of this effect, higher numbers will lead to less accuracy." };

	private final double intensity;

	public BadAimEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.intensity = readDouble(data[0], "intensity");

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
