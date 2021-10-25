package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readDouble;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "health")
public class HealthCondition extends EnchantmentCondition {

	private final ActionTarget target;
	private final ConditionOperation operation;
	private final double health;

	public HealthCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		String raw = data[1];

		this.operation = ConditionOperation.parseCondition(raw.substring(0, 1));
		this.health = readDouble(raw.substring(1));
	}

	@Override
	public boolean check(EnchantmentContext context) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity == null)
			return false;

		double current = entity.getHealth();
		if (context.getEvent() instanceof EntityDamageEvent event && event.getEntity() == entity) {
			current -= event.getFinalDamage();
		}

		switch (operation) {
		case EQUAL:
			return current == health;
		case GREATER_THAN:
			return current > health;
		case LESS_THAN:
			return current < health;
		default:
			return false;
		}
	}
}
