package me.jishuna.modernenchants.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;
import net.md_5.bungee.api.ChatColor;

public class EffectsCommand extends SimpleCommandHandler {

	private final ModernEnchants plugin;

	public EffectsCommand(ModernEnchants plugin) {
		super("minetweaks.command.effects");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		sender.sendMessage(ChatColor.GOLD + "Registered Effects:");
		for (String type : this.plugin.getEffectRegistry().getEffects()) {
			sender.sendMessage(ChatColor.GREEN + "  - " + type);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 2) {
			Set<String> modules = this.plugin.getEnchantmentRegistry().getNames();
			List<String> suggestions = new ArrayList<>();

			StringUtil.copyPartialMatches(args[0], modules, suggestions);

			return suggestions;
		}
		return Collections.emptyList();

	}

}
