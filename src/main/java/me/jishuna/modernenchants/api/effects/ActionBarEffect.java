package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readTarget;

import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@RegisterEffect(name = "action_bar")
public class ActionBarEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] {
			ChatColor.GOLD + "Description: " + ChatColor.GREEN + "Sends an action bar message to that targets chat.",
			ChatColor.GOLD + "Usage: " + ChatColor.GREEN + "action_bar(target,msg)",
			ChatColor.GOLD + "  - Target: " + ChatColor.GREEN
					+ "The entity to send the message to, either \"user\" or \"opponent\".",
			ChatColor.GOLD + "  - Msg: " + ChatColor.GREEN + "The message to send, colors are supported." };

	private final ActionTarget target;
	private final String message;

	public ActionBarEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 2);

		this.target = readTarget(data[0]);
		this.message = ChatColor.translateAlternateColorCodes('&', data[1]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getTarget(target).ifPresent(entity -> {
			if (entity instanceof Player player) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
			}
		});
	}
	
	public static String[] getDescription() {
		return DESCRIPTION;
	}
}
