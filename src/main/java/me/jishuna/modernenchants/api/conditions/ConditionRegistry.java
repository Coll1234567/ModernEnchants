package me.jishuna.modernenchants.api.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import me.jishuna.commonlib.utils.ClassUtils;
import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.RegisterCondition;

public class ConditionRegistry {
	private static final Class<?> TYPE_CLASS = EnchantmentCondition.class;

	private final Map<String, EnchantmentCondition> actionMap = new HashMap<>();

	public ConditionRegistry() {
		reloadConditions();
	}

	public void reloadConditions() {
		this.actionMap.clear();

		for (Class<?> clazz : ClassUtils.getAllClassesInPackage("me.jishuna.modernenchants.api.conditions",
				this.getClass().getClassLoader())) {
			if (!TYPE_CLASS.isAssignableFrom(clazz))
				continue;
			for (RegisterCondition annotation : clazz.getAnnotationsByType(RegisterCondition.class)) {
				try {
					EnchantmentCondition action = (EnchantmentCondition) clazz.getDeclaredConstructor().newInstance();
					registerCondition(annotation.name(), action);
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void registerCondition(String name, EnchantmentCondition action) {
		this.actionMap.put(name, action);
	}

	public Set<String> getConditions() {
		return this.actionMap.keySet();
	}

	public Predicate<EnchantmentContext> parseString(String string) throws InvalidEnchantmentException {
		int open = string.indexOf('(');
		int close = string.lastIndexOf(')');

		if (open < 0 || close < 0)
			return null;

		String type = string.substring(0, open).toLowerCase();

		String data = string.substring(open + 1, close);

		EnchantmentCondition function = this.actionMap.get(type);

		if (function == null)
			return null;

		return function.parseString(data.split(","));
	}

}
