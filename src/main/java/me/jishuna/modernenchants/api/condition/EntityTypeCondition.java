package me.jishuna.modernenchants.api.condition;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.utils.ParseUtils.readTarget;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import me.jishuna.modernenchants.api.annotation.RegisterCondition;
import me.jishuna.modernenchants.api.effect.ActionTarget;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterCondition(name = "entity_type")
public class EntityTypeCondition extends EnchantmentCondition {
	private static final Set<String> ALL_TYPES = Arrays.stream(EntityType.values()).map(Enum::toString)
			.collect(Collectors.toSet());

	private final ActionTarget target;
	private final Set<EntityType> types = EnumSet.noneOf(EntityType.class);

	public EntityTypeCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);

		for (int i = 1; i < data.length; i++) {
			String typeString = data[i].toUpperCase();
			if (!ALL_TYPES.contains(typeString))
				throw new InvalidEnchantmentException(
						"Invalid entity type: " + typeString + " Valid types are: " + String.join(", ", ALL_TYPES));
			this.types.add(EntityType.valueOf(typeString));
		}
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		LivingEntity entity = context.getTargetDirect(this.target);

		if (entity == null)
			return false;
		return this.types.contains(entity.getType());
	}

}
