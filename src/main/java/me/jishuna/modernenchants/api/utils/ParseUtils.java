package me.jishuna.modernenchants.api.utils;

import java.util.Collections;
import java.util.Set;

import org.bukkit.Material;

import me.jishuna.commonlib.MaterialSets;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

public class ParseUtils {
	public static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder().character('&')
			.hexCharacter('#').hexColors().useUnusualXRepeatedCharacterHexFormat().build();

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
			return MaterialSets.BOOTS;
		default:
			return Collections.emptySet();
		}
	}

	public static String colorString(String string) {
		return ChatColor.translateAlternateColorCodes('&',
				SERIALIZER.serialize(MiniMessage.miniMessage().parse(string)));
	}
}
