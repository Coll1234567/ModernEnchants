package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "food")
public class FoodCondition extends EnchantmentCondition {

	private final ActionTarget target;
	private final ConditionOperation operation;
	private final int food;

	public FoodCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		String raw = data[1];

		this.operation = ConditionOperation.parseCondition(raw.substring(0, 1));
		this.food = readInt(raw.substring(1));
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity entity = context.getTargetDirect(target);

		if (!(entity instanceof Player player))
			return false;

		int currentFood = player.getFoodLevel();

		switch (operation) {
		case EQUAL:
			return currentFood == food;
		case GREATER_THAN:
			return currentFood > food;
		case LESS_THAN:
			return currentFood < food;
		default:
			return false;
		}
	}
}
