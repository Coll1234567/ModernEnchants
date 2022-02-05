package me.jishuna.modernenchants.command;

import me.jishuna.commonlib.commands.ArgumentCommandHandler;
import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.modernenchants.ModernEnchants;

public class ModernEnchantsCommandHandler extends ArgumentCommandHandler {
	public ModernEnchantsCommandHandler(ModernEnchants plugin) {
		super(plugin.getMessageConfig(), "modernenchants.command");

		SimpleCommandHandler info = new InfoCommand(plugin);

		setDefault(info);
		addArgumentExecutor("info", info);
		addArgumentExecutor("enchant", new EnchantCommand(plugin));
		addArgumentExecutor("actions", new ActionsCommand(plugin));
		addArgumentExecutor("descriptions", new DescriptionsCommand(plugin));
		addArgumentExecutor("enchants", new ListEnchantsCommand(plugin));
	}
}