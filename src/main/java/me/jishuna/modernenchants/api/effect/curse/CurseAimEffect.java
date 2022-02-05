package me.jishuna.modernenchants.api.effect.curse;

import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import io.netty.util.internal.ThreadLocalRandom;
import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.ArgumentFormat;
import me.jishuna.actionconfiglib.ConfigurationEntry;
import me.jishuna.actionconfiglib.effects.Effect;
import me.jishuna.actionconfiglib.effects.RegisterEffect;
import me.jishuna.actionconfiglib.exceptions.ParsingException;

@ArgumentFormat(format = { "intensity" })
@RegisterEffect(name = "CURSE_AIM")
public class CurseAimEffect extends Effect {
	private final double intensity;

	public CurseAimEffect(ConfigurationEntry entry) throws ParsingException {
		this.intensity = entry.getDoubleOrThrow("intensity");
	}

	@Override
	public void evaluate(ActionContext context) {
		if (context.getEvent() instanceof ProjectileLaunchEvent event) {
			ThreadLocalRandom random = ThreadLocalRandom.current();
			double x = random.nextDouble(-intensity, intensity);
			double y = random.nextDouble(-intensity, intensity);
			double z = random.nextDouble(-intensity, intensity);

			event.getEntity().setVelocity(event.getEntity().getVelocity().add(new Vector(x, y, z)));
		}
	}

}
