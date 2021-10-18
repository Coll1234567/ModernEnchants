package me.jishuna.modernenchants.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.CustomEnchantment;

public class EnchantCommand extends SimpleCommandHandler {

	private final ModernEnchants plugin;

	public EnchantCommand(ModernEnchants plugin) {
		super("minetweaks.command.enchant");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		if (sender instanceof Player player) {
			CustomEnchantment enchant = plugin.getEnchantmentRegistry().getEnchantment(args[0]);

			if (enchant != null) {
				player.getEquipment().getItemInMainHand().addUnsafeEnchantment(enchant, 1);
			}
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
