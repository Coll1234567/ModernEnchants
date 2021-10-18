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
import me.jishuna.modernenchants.api.effects.EnchantmentEffect;
import net.md_5.bungee.api.ChatColor;

public class EffectsCommand extends SimpleCommandHandler {

	private final ModernEnchants plugin;

	public EffectsCommand(ModernEnchants plugin) {
		super("modernenchants.command.effects");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(ChatColor.GOLD + "Registered Effects:");
			for (String type : this.plugin.getEffectRegistry().getEffects()) {
				sender.sendMessage(ChatColor.GREEN + "  - " + type);
			}
			return true;
		}

		String name = args[0];
		EnchantmentEffect effect = this.plugin.getEffectRegistry().getEffect(name);

		if (effect == null) {
			sendUsage(sender, name);
			return true;
		}

		sender.sendMessage("");
		for (String line : effect.getDescription()) {
			sender.sendMessage(line);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 2) {
			Set<String> effects = this.plugin.getEffectRegistry().getEffects();
			List<String> suggestions = new ArrayList<>();

			StringUtil.copyPartialMatches(args[0], effects, suggestions);

			return suggestions;
		}
		return Collections.emptyList();
	}

	private void sendUsage(CommandSender sender, String arg) {
		Set<String> effects = this.plugin.getEffectRegistry().getEffects();

		String msg = this.plugin.getMessage("command-usage");
		msg = msg.replace("%arg%", arg);
		msg = msg.replace("%args%", String.join(", ", effects));

		sender.sendMessage(msg);
	}

}
