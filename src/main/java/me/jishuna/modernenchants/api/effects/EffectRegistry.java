package me.jishuna.modernenchants.api.effects;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import me.jishuna.commonlib.utils.ClassUtils;
import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

public class EffectRegistry {
	private static final Class<?> TYPE_CLASS = EnchantmentEffect.class;

	private final Map<String, EnchantmentEffect> actionMap = new HashMap<>();

	public EffectRegistry() {
		reloadEffects();
	}

	public void reloadEffects() {
		this.actionMap.clear();

		for (Class<?> clazz : ClassUtils.getAllClassesInPackage("me.jishuna.modernenchants.api.effects",
				this.getClass().getClassLoader())) {
			if (!TYPE_CLASS.isAssignableFrom(clazz))
				continue;
			for (RegisterEffect annotation : clazz.getAnnotationsByType(RegisterEffect.class)) {
				try {
					EnchantmentEffect action = (EnchantmentEffect) clazz.getDeclaredConstructor().newInstance();
					registerEffect(annotation.name(), action);
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void registerEffect(String name, EnchantmentEffect action) {
		this.actionMap.put(name, action);
	}

	public Set<String> getEffects() {
		return this.actionMap.keySet();
	}

	public Consumer<EnchantmentContext> parseString(String string) throws InvalidEnchantmentException {
		int open = string.indexOf('(');
		int close = string.lastIndexOf(')');

		if (open < 0 || close < 0)
			return null;

		String type = string.substring(0, open).toLowerCase();

		String data = string.substring(open + 1, close);

		EnchantmentEffect actionType = this.actionMap.get(type);

		if (actionType == null)
			return null;

		return actionType.parseString(data.split(","));
	}

}
