package me.jishuna.modernenchants.api.condition;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readLong;

import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.CooldownManager;
import me.jishuna.modernenchants.api.annotation.RegisterCondition;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterCondition(name = "cooldown")
public class CooldownCondition extends EnchantmentCondition {

	private final long time;

	public CooldownCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.time = readLong(data[0], "seconds") * 1000;
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity user = context.getUserDirect();

		if (user == null)
			return true;

		return !CooldownManager.getInstance().isOnCooldown(user.getUniqueId(), enchant);
	}

	public void setCooldown(EnchantmentContext context, CustomEnchantment customEnchantment) {
		LivingEntity user = context.getUserDirect();

		if (user != null)
			CooldownManager.getInstance().setCooldown(user.getUniqueId(), customEnchantment, time);
	}
}
