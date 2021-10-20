package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readFloat;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "explosion")
public class ExplosionEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final float power;
	private final boolean fire;
	private final boolean breakBlocks;
	private final int x;
	private final int y;
	private final int z;

	public ExplosionEffect(String[] data) throws InvalidEnchantmentException {
		super(DESCRIPTION);
		checkLength(data, 4);

		this.target = readTarget(data[0]);
		this.power = readFloat(data[1]);
		this.fire = Boolean.parseBoolean(data[2]);
		this.breakBlocks = Boolean.parseBoolean(data[3]);

		if (data.length >= 7) {
			this.x = readInt(data[4]);
			this.y = readInt(data[5]);
			this.z = readInt(data[6]);
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.getWorld()
				.createExplosion(entity.getLocation().add(x, y, z), power, fire, breakBlocks));
	}
}
