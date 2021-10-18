package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.RegisterEffect;

@RegisterEffect(name = "message")
public class MessageEffect extends EnchantmentEffect {
	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		return context -> context.getTarget(target).ifPresent(entity -> entity.sendMessage(data[1]));
	}

}
