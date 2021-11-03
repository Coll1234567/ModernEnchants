package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readDouble;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "y_level")
public class YLevelCondition extends EnchantmentCondition {

	private final ActionTarget target;
	private final ConditionOperation operation;
	private final double level;

	public YLevelCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		String raw = data[1];

		this.operation = ConditionOperation.parseCondition(raw.substring(0, 1));
		this.level = readDouble(raw.substring(1));
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity == null)
			return false;

		double current = entity.getEyeLocation().getY();

		switch (operation) {
		case EQUAL:
			return current == level;
		case GREATER_THAN:
			return current > level;
		case LESS_THAN:
			return current < level;
		default:
			return false;
		}
	}
}
