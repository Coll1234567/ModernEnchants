package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "has_permission")
public class PermissionCondition extends EnchantmentCondition {

	private final ActionTarget target;
	private final String perm;
	private final boolean bool;

	public PermissionCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 3);

		this.target = readTarget(data[0]);
		this.perm = data[1];
		this.bool = Boolean.parseBoolean(data[2]);
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity entity = context.getTargetDirect(target);

		if (entity == null)
			return false;
		return entity.hasPermission(perm) == bool;
	}

}
