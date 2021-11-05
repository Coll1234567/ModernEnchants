package me.jishuna.modernenchants.api.condition;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.jishuna.modernenchants.api.annotation.RegisterCondition;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterCondition(name = "damage_cause")
public class DamageCauseCondition extends EnchantmentCondition {
	private static final Set<String> ALL_CAUSES = Arrays.stream(DamageCause.values()).map(Enum::toString)
			.collect(Collectors.toSet());
	private final Set<DamageCause> causes = EnumSet.noneOf(DamageCause.class);

	public DamageCauseCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		for (String causeString : data) {
			causeString = causeString.toUpperCase();
			if (!ALL_CAUSES.contains(causeString))
				throw new InvalidEnchantmentException(
						"Invalid damage cause: " + causeString + " Valid causes are: " + String.join(", ", ALL_CAUSES));
			this.causes.add(DamageCause.valueOf(causeString));
		}
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		if (context.getEvent()instanceof EntityDamageEvent event) {
			return this.causes.contains(event.getCause());
		}
		return false;
	}

}
