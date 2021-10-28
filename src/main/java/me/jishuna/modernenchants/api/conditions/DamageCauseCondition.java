package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "damage_cause")
public class DamageCauseCondition extends EnchantmentCondition {
	private static final Set<String> ALL_CAUSES = Arrays.stream(DamageCause.values()).map(Enum::toString)
			.collect(Collectors.toSet());
	private final DamageCause cause;

	public DamageCauseCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		String causeString = data[0].toUpperCase();
		if (!ALL_CAUSES.contains(causeString))
			throw new InvalidEnchantmentException("Invalid damage cause: " + causeString);

		this.cause = DamageCause.valueOf(causeString);
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		if (context.getEvent() instanceof EntityDamageEvent event) {
			return event.getCause() == cause;
		}
		return false;
	}

}
