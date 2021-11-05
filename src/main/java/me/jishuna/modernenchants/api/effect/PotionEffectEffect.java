package me.jishuna.modernenchants.api.effect;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterEffect(name = "potion_effect")
public class PotionEffectEffect extends EnchantmentEffect {
	private static final Set<String> ALL_TYPES = Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName)
			.collect(Collectors.toSet());

	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final ActionTarget target;
	private final PotionEffectType type;
	private final int duration;
	private final int level;

	public PotionEffectEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 4);

		this.target = readTarget(data[0]);

		String typeString = data[1].toUpperCase();
		if (!ALL_TYPES.contains(typeString))
			throw new InvalidEnchantmentException("Invalid potion effect type: " + typeString);

		this.type = PotionEffectType.getByName(typeString);

		this.duration = readInt(data[2], "duration"); 
		this.level = readInt(data[3], "level") - 1;
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.addPotionEffect(new PotionEffect(type, duration, level, true)));
	}
}