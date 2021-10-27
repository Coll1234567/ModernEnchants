package me.jishuna.modernenchants.api;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Material;

import me.jishuna.commonlib.MaterialSets;
import me.jishuna.modernenchants.api.effects.ActionTarget;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class ParseUtils {
	public static void checkLength(String[] args, int min) throws InvalidEnchantmentException {
		if (args.length < min)
			throw new InvalidEnchantmentException("This effect requires at least " + min + " arguments");
	}

	public static ActionTarget readTarget(String string) throws InvalidEnchantmentException {
		if (!ActionTarget.ALL_TARGETS.contains(string.toUpperCase()))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + string);
		return ActionTarget.valueOf(string.toUpperCase());
	}

	public static int readInt(String string) throws InvalidEnchantmentException {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a whole number but found: " + string);
		}
	}

	public static double readDouble(String string) throws InvalidEnchantmentException {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a number but found: " + string);
		}
	}

	public static float readFloat(String string) throws InvalidEnchantmentException {
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException ex) {
			throw new InvalidEnchantmentException("Expected a number but found: " + string);
		}
	}

	public static Set<Material> readMaterial(String string) {
		Material material = Material.matchMaterial(string);

		if (material != null) {
			return Set.of(material);
		}

		switch (string) {
		case "SWORDS":
			return MaterialSets.SWORDS;
		case "AXES":
			return MaterialSets.AXES;
		case "PICKAXES":
			return MaterialSets.PICKAXES;
		case "SHOVELS":
			return MaterialSets.SHOVELS;
		case "HOES":
			return MaterialSets.HOES;
		case "HELMETS":
			return MaterialSets.HELMETS;
		case "CHESTPLATES":
			return MaterialSets.CHESTPLATE;
		case "PANTS", "LEGGINGS":
			return MaterialSets.PANTS;
		case "BOOTS":
			return MaterialSets.CHESTPLATE;
		default:
			return Collections.emptySet();
		}
	}

	public static String colorString(String string) {
		LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexCharacter('#')
				.hexColors().useUnusualXRepeatedCharacterHexFormat().build();
		
		return ChatColor.translateAlternateColorCodes('&',
				serializer.serialize(MiniMessage.miniMessage().parse(string)));
	}
}
