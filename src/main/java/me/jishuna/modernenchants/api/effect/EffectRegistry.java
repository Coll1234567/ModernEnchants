package me.jishuna.modernenchants.api.effect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.jishuna.commonlib.utils.ClassUtils;
import me.jishuna.modernenchants.api.annotation.RegisterEffect;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

public class EffectRegistry {
	private static final Class<?> TYPE_CLASS = EnchantmentEffect.class;

	private final Map<String, Class<? extends EnchantmentEffect>> effectMap = new HashMap<>();

	public EffectRegistry() {
		reloadEffects();
	}

	// Pretty sure this is not an unchecked cast
	@SuppressWarnings("unchecked")
	public void reloadEffects() {
		this.effectMap.clear();

		for (Class<?> clazz : ClassUtils.getAllClassesInSubpackages("me.jishuna.modernenchants.api.effect",
				this.getClass().getClassLoader())) {
			if (!TYPE_CLASS.isAssignableFrom(clazz))
				continue;

			for (RegisterEffect annotation : clazz.getAnnotationsByType(RegisterEffect.class)) {
				registerEffect(annotation.name(), (Class<? extends EnchantmentEffect>) clazz);
			}
		}
	}

	public String[] getDescription(String key) {
		Class<? extends EnchantmentEffect> clazz = this.effectMap.get(key);

		if (clazz == null)
			return null;

		String[] desc;
		try {
			Method method = clazz.getDeclaredMethod("getDescription");
			desc = (String[]) method.invoke(null);
		} catch (ReflectiveOperationException | IllegalArgumentException | ClassCastException e) {
			return null;
		}
		return desc;
	}

	public void registerEffect(String name, Class<? extends EnchantmentEffect> clazz) {
		this.effectMap.put(name, clazz);
	}

	public Set<String> getEffects() {
		return this.effectMap.keySet();
	}

	public EnchantmentEffect parseString(String string) throws InvalidEnchantmentException {
		int open = string.indexOf('(');
		int close = string.lastIndexOf(')');

		if (open < 0 || close < 0)
			return null;

		String type = string.substring(0, open).toLowerCase();

		String data = string.substring(open + 1, close);

		Class<? extends EnchantmentEffect> clazz = this.effectMap.get(type);

		if (clazz == null)
			throw new InvalidEnchantmentException("The action type \"" + type + "\" was not found.");

		EnchantmentEffect effect;
		try {
			effect = clazz.getDeclaredConstructor(String[].class).newInstance((Object) data.split(","));
		} catch (ReflectiveOperationException | IllegalArgumentException e) {
			if (e.getCause() instanceof InvalidEnchantmentException ex) {
				ex.addAdditionalInfo("Error parsing effect \"" + type + "\":");
				throw ex;
			}
			throw new InvalidEnchantmentException("Unknown error: " + e.getMessage());
		}

		return effect;
	}

}
