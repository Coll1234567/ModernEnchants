package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

@RegisterEffect(name = "action_bar")
public class ActionBarEffect extends EnchantmentEffect {

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 2);

		String targetString = data[0].toUpperCase();
		if (!ActionTarget.ALL_TARGETS.contains(targetString))
			throw new InvalidEnchantmentException("Target must be either USER or OPPONENT, found: " + targetString);

		ActionTarget target = ActionTarget.valueOf(targetString);

		return context -> {
			context.getTarget(target).ifPresent(entity -> {
				if (entity instanceof Player player) {
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(data[1]));
				}
			});
		};
	}

}
