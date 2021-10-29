package me.jishuna.modernenchants.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.PluginKeys;

public class DescriptionsCommand extends SimpleCommandHandler {
	private static final Set<String> ARGS = Set.of("inline", "hidden", "seperate");

	private final ModernEnchants plugin;

	public DescriptionsCommand(ModernEnchants plugin) {
		super("modernenchants.command.descriptions");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		if (!(sender instanceof Player player)) {
			return true;
		}

		if (args.length < 2) {
			sendUsage(sender, "none");
		}

		String type = args[0].toLowerCase();

		if (!ARGS.contains(type)) {
			sendUsage(sender, type);
			return true;
		}

		player.getPersistentDataContainer().set(PluginKeys.DISPLAY_FORMAT.getKey(), PersistentDataType.STRING, type);
		player.sendMessage(
				this.plugin.getMessage("description-format-change").replace("%type%", StringUtils.capitalize(type)));

		player.updateInventory();
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 2) {
			return StringUtil.copyPartialMatches(args[0], ARGS, new ArrayList<>());
		}
		return Collections.emptyList();
	}

	private void sendUsage(CommandSender sender, String arg) {

		String msg = this.plugin.getMessage("command-usage");
		msg = msg.replace("%arg%", arg);
		msg = msg.replace("%args%", String.join(", ", ARGS));

		sender.sendMessage(msg);
	}

}
