package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "fake_lightning")
public class FakeLightningEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final int x;
	private final int y;
	private final int z;

	public FakeLightningEffect(String[] data) throws InvalidEnchantmentException {
		super(DESCRIPTION);
		checkLength(data, 1);

		this.target = readTarget(data[0]);

		if (data.length >= 4) {
			this.x = readInt(data[1]);
			this.y = readInt(data[2]);
			this.z = readInt(data[3]);
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target)
				.ifPresent(entity -> entity.getWorld().strikeLightningEffect(entity.getLocation().add(x, y, z)));
	}
}
