package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readFloat;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterEffect(name = "sound")
public class SoundEffect extends EnchantmentEffect {
	private static final Set<String> ALL_SOUNDS = Arrays.stream(Sound.values()).map(Enum::toString)
			.collect(Collectors.toSet());

	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final Sound sound;
	private final float volume;
	private final float pitch;
	private final int x;
	private final int y;
	private final int z;

	public SoundEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 4);

		this.target = readTarget(data[0]);

		String particleString = data[1].toUpperCase();
		if (!ALL_SOUNDS.contains(particleString))
			throw new InvalidEnchantmentException("Invalid sound type: " + particleString);

		this.sound = Sound.valueOf(particleString);

		this.volume = readFloat(data[2], "volume");
		this.pitch = readFloat(data[3], "pitch");

		if (data.length >= 7) {
			this.x = readInt(data[4], "x");
			this.y = readInt(data[5], "y");
			this.z = readInt(data[6], "z");
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
	}

	@Override
	public void handle(EnchantmentContext context) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity instanceof Player player) {
			player.playSound(player.getLocation().add(x, y, z), sound, volume, pitch);
		}
	}
}
