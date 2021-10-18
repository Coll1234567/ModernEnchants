package me.jishuna.modernenchants.api.effects;

import java.util.function.Consumer;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.EnchantmentContext;
import me.jishuna.modernenchants.api.RegisterEffect;

@RegisterEffect(name = "break_relative")
public class BreakBlockEffect extends EnchantmentEffect {

	@Override
	public Consumer<EnchantmentContext> parseString(String[] data) {
		int x = Integer.parseInt(data[0]);
		int y = Integer.parseInt(data[1]);
		int z = Integer.parseInt(data[2]);

		return context -> {
			LivingEntity user = context.getUser().orElse(null);

			if (user instanceof Player player) {
				context.getTargetBlock().ifPresent(block -> player.breakBlock(block.getRelative(x, y, z)));
			}
		};
	}

}
