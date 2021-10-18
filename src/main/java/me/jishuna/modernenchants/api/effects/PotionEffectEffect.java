package me.jishuna.modernenchants.api.effects;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "potion_effect")
public class PotionEffectEffect extends EnchantmentEffect {
	private static final Set<String> ALL_TYPES = Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName)
			.collect(Collectors.toSet());

	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	public PotionEffectEffect() {
		super(DESCRIPTION);
	}

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 4);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		String typeString = data[1].toUpperCase();
		if (!ALL_TYPES.contains(typeString))
			throw new InvalidEnchantmentException("Invalid potion effect type: " + typeString);

		PotionEffectType type = PotionEffectType.getByName(typeString);

		int duration = Integer.parseInt(data[2]);
		int level = Integer.parseInt(data[3]) - 1;

		return context -> context.getTarget(target)
				.ifPresent(entity -> entity.addPotionEffect(new PotionEffect(type, duration, level)));
	}
}
