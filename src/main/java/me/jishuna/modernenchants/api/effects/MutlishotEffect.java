package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readDouble;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;

import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;
import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "multishot")
public class MutlishotEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Deal damage to the target.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "damage(target,amount)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN
					+ "The entity to damage, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Amount: " + ChatColor.GREEN + "The amount of damage to deal, 2 damage = 1 heart." };

	private final int count;
	private final double spread;

	public MutlishotEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.count = readInt(data[0]);

		if (this.count <= 0)
			throw new InvalidEnchantmentException("Count must be greater than 0");

		this.spread = readDouble(data[1]);

		if (this.spread <= 0)
			throw new InvalidEnchantmentException("Spread must be greater than 0");
	}

	@Override
	public void handle(EnchantmentContext context) {
		if (context.getEvent()instanceof ProjectileLaunchEvent event && event.getEntity()instanceof Arrow arrow) {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			for (int i = 0; i < count; i++) {
				Arrow newArrow = arrow.getShooter().launchProjectile(Arrow.class);

				double x = random.nextDouble(-spread, spread);
				double y = random.nextDouble(-spread / 2, spread / 2);
				double z = random.nextDouble(-spread, spread);

//				newArrow.setColor(arrow.getColor());
				if (arrow.getBasePotionData().getType() != PotionType.UNCRAFTABLE)
					newArrow.setBasePotionData(arrow.getBasePotionData());
				arrow.getCustomEffects().forEach(effect -> newArrow.addCustomEffect(effect, true));
				newArrow.setPickupStatus(PickupStatus.CREATIVE_ONLY);

				newArrow.setVelocity(event.getEntity().getVelocity().add(new Vector(x, y, z)));
			}
		}
	}

	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
