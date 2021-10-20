package me.jishuna.modernenchants.api.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.jishuna.commonlib.utils.ClassUtils;
import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.effects.EnchantmentEffect;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public class ConditionRegistry {
	private static final Class<?> TYPE_CLASS = EnchantmentCondition.class;

	private final Map<String, Class<? extends EnchantmentCondition>> actionMap = new HashMap<>();

	public ConditionRegistry() {
		reloadConditions();
	}

	// Pretty sure this is not an unchecked cast
	@SuppressWarnings("unchecked")
	public void reloadConditions() {
		this.actionMap.clear();

		for (Class<?> clazz : ClassUtils.getAllClassesInPackage("me.jishuna.modernenchants.api.conditions",
				this.getClass().getClassLoader())) {
			if (!TYPE_CLASS.isAssignableFrom(clazz))
				continue;

			for (RegisterCondition annotation : clazz.getAnnotationsByType(RegisterCondition.class)) {
				registerEffect(annotation.name(), (Class<? extends EnchantmentCondition>) clazz);
			}
		}
	}

	public EnchantmentEffect getEffect(String key) {
		return null;
	}

	public void registerEffect(String name, Class<? extends EnchantmentCondition> clazz) {
		this.actionMap.put(name, clazz);
	}

	public Set<String> getEffects() {
		return this.actionMap.keySet();
	}

	public EnchantmentCondition parseString(String string) throws InvalidEnchantmentException {
		int open = string.indexOf('(');
		int close = string.lastIndexOf(')');

		if (open < 0 || close < 0)
			return null;

		String type = string.substring(0, open).toLowerCase();

		String data = string.substring(open + 1, close);

		Class<? extends EnchantmentCondition> clazz = this.actionMap.get(type);

		if (clazz == null)
			throw new InvalidEnchantmentException("The condition type \"" + type + "\" was not found.");

		EnchantmentCondition effect;
		try {
			effect = clazz.getDeclaredConstructor(String[].class).newInstance((Object) data.split(","));
		} catch (ReflectiveOperationException | IllegalArgumentException e) {
			throw new InvalidEnchantmentException("Unknown error: " + e.getMessage());
		}

		return effect;
	}
}
