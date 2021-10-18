package me.jishuna.modernenchants.commands;

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
		addArgumentExecutor("effects", new EffectsCommand(plugin));
		addArgumentExecutor("actions", new ActionsCommand(plugin));
	}
}