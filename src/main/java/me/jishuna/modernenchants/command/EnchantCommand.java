package me.jishuna.modernenchants.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.enchantment.IEnchantment;
import net.minecraft.world.level.block.Blocks;

public class EnchantCommand extends SimpleCommandHandler {

	private final ModernEnchants plugin;

	public EnchantCommand(ModernEnchants plugin) {
		super("modernenchants.command.enchant");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		if (sender instanceof Player player) {
			IEnchantment enchant = plugin.getEnchantmentRegistry().getByName(args[0]);

			int level = 1;

			if (args.length >= 3 && StringUtils.isNumeric(args[1])) {
				level = Integer.parseInt(args[1]);
			}

			if (enchant != null) {
				if (level > 0) {
					player.getEquipment().getItemInMainHand().addUnsafeEnchantment(enchant.getEnchantment(), level);
				} else {
					player.getEquipment().getItemInMainHand().removeEnchantment(enchant.getEnchantment());
				}
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 2) {
			Set<String> modules = this.plugin.getEnchantmentRegistry().getNames();
			return StringUtil.copyPartialMatches(args[0], modules, new ArrayList<>());
		}

		if (args.length == 3) {
			IEnchantment enchant = plugin.getEnchantmentRegistry().getByName(args[0]);
			if (enchant != null) {
				List<String> levels = new ArrayList<>();
				for (int i = enchant.getStartLevel(); i <= enchant.getMaxLevel(); i++) {
					levels.add(Integer.toString(i));
				}
				return levels;
			}
		}
		return Collections.emptyList();
	}

}
