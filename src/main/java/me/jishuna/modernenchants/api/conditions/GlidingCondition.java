package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "gliding")
public class GlidingCondition extends EnchantmentCondition {

	private final ActionTarget target;
	private final boolean bool;

	public GlidingCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.bool = Boolean.parseBoolean(data[1]);
	}

	@Override
	public boolean check(EnchantmentContext context) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity == null)
			return false;
		return entity.isGliding() == bool;
	}
}
