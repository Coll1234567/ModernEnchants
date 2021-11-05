package me.jishuna.modernenchants.api.condition;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readDouble;

import java.util.Random;

import me.jishuna.modernenchants.api.annotation.RegisterCondition;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterCondition(name = "chance")
public class ChanceCondition extends EnchantmentCondition {
	private static final Random RANDOM = new Random();
	private final double chance;

	public ChanceCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.chance = readDouble(data[0], "chance");
		if (this.chance <= 0)
			throw new InvalidEnchantmentException("Chance must be greater than 0");
		if (this.chance > 100)
			throw new InvalidEnchantmentException("Chance must not be greater than 100");
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		return (RANDOM.nextDouble() * 100d) < chance;
	}

}
