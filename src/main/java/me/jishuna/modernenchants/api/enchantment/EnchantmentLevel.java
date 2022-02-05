package me.jishuna.modernenchants.api.enchantment;

import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.jishuna.actionconfiglib.ActionConfigLib;
import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.Component;
import me.jishuna.actionconfiglib.exceptions.ParsingException;

public class EnchantmentLevel {
	private final List<Component> components;
	private final int minExperienceLevel;

	public EnchantmentLevel(ActionConfigLib actionLib, ConfigurationSection levelSection) throws ParsingException {
		this.minExperienceLevel = levelSection.getInt("min-enchanting-level");

		this.components = Arrays.asList(actionLib.parseComponents(levelSection.getMapList("actions")));
	}

	public void processActions(ActionContext context) {
		this.components.forEach(action -> action.handleAction(context));
	}

	public int getMinExperienceLevel() {
		return minExperienceLevel;
	}
}
