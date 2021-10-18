package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

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

	public ActionBarEffect() {
		super(DESCRIPTION);
	}

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		String msg = ChatColor.translateAlternateColorCodes('&', data[1]);

		return handle(ActionTarget.valueOf(targetString), msg);
	}

	public Consumer<EnchantmentContext> handle(ActionTarget target, String msg) {
		return context -> {
			context.getTarget(target).ifPresent(entity -> {
				if (entity instanceof Player player) {
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
				}
			});
		};
	}

}
