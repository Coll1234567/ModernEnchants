package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterEffect(name = "lightning")
public class LightningEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final int x;
	private final int y;
	private final int z;

	public LightningEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.target = readTarget(data[0]);

		if (data.length >= 4) {
			this.x = readInt(data[1], "x");
			this.y = readInt(data[2], "y");
			this.z = readInt(data[3], "z");
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target)
				.ifPresent(entity -> entity.getWorld().strikeLightning(entity.getLocation().add(x, y, z)));
	}
}
