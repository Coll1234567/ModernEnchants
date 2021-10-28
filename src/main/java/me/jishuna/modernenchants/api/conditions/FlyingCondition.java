package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "flying")
public class FlyingCondition extends EnchantmentCondition {
	
	private final ActionTarget target;
	private final boolean bool;

	public FlyingCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);
		
		this.target = readTarget(data[0]);
		this.bool = Boolean.parseBoolean(data[1]);
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity instanceof Player player) {
			return player.isFlying() == bool;
		}
		return false;
	}
}
