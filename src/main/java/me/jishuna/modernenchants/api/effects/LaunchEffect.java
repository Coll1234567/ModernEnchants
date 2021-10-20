package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.util.Vector;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "launch")
public class LaunchEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final int x;
	private final int y;
	private final int z;

	public LaunchEffect(String[] data) throws InvalidEnchantmentException {
		super(DESCRIPTION);
		checkLength(data, 4);

		this.target = readTarget(data[0]);
		this.x = readInt(data[1]);
		this.y = readInt(data[2]);
		this.z = readInt(data[3]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.setVelocity(new Vector(x, y, z)));
	}
}
