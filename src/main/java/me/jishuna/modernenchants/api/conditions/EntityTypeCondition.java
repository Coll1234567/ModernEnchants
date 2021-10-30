package me.jishuna.modernenchants.api.conditions;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityEvent;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.enchantments.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "entity_type")
public class EntityTypeCondition extends EnchantmentCondition {
	private static final Set<String> ALL_TYPES = Arrays.stream(EntityType.values()).map(Enum::toString)
			.collect(Collectors.toSet());
	private final EntityType type;

	public EntityTypeCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		String typeString = data[0].toUpperCase();
		if (!ALL_TYPES.contains(typeString))
			throw new InvalidEnchantmentException("Invalid entity type: " + typeString);

		this.type = EntityType.valueOf(typeString);
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		if (context.getEvent() instanceof EntityEvent event) {
			return event.getEntityType() == type;
		}
		return false;
	}

}
