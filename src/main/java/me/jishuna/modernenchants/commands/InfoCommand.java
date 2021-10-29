package me.jishuna.modernenchants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;
import net.md_5.bungee.api.ChatColor;

public class InfoCommand extends SimpleCommandHandler {

	private final ModernEnchants plugin;

	public InfoCommand(ModernEnchants plugin) {
		super("modernenchants.command.info");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}
		PluginDescriptionFile descriptionFile = this.plugin.getDescription();
		sender.sendMessage(ChatColor.GOLD + "=".repeat(40));
		sender.sendMessage(ChatColor.GOLD + descriptionFile.getFullName() + ChatColor.GREEN + " by " + ChatColor.GOLD
				+ String.join(", ", descriptionFile.getAuthors()));
		sender.sendMessage(ChatColor.GOLD + "=".repeat(40));

		return true;
	}

}
