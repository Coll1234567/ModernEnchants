package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import me.jishuna.modernenchants.api.ParseUtils;
import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "message")
public class MessageEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Sends a message to that targets chat.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "message(target,msg)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN + "The entity to send the message to, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Msg: " + ChatColor.GREEN + "The message to send, colors are supported." };
	
	private final ActionTarget target;
	private final String message;

	public MessageEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);
		
		this.target = readTarget(data[0]);
		this.message = ParseUtils.colorString(data[1]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> entity.sendMessage(message));
	}
	
	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
