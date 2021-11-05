package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import org.bukkit.util.Vector;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterEffect(name = "launch")
public class LaunchEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final int x;
	private final int y;
	private final int z;

	public LaunchEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 4);

		this.target = readTarget(data[0]);
		this.x = readInt(data[1], "x velocity");
		this.y = readInt(data[2], "y velocity");
		this.z = readInt(data[3], "z velocity");
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.setVelocity(new Vector(x, y, z)));
	}
}
