package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterEffect(name = "break_block")
public class BreakBlockEffect extends EnchantmentEffect {
	private static final String[] DESCRIPTION = new String[] { "test", "test2" };
	
	private final int x;
	private final int y;
	private final int z;

	public BreakBlockEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 3);
		
		this.x = readInt(data[0]);
		this.y = readInt(data[1]);
		this.z = readInt(data[2]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		LivingEntity user = context.getUser().orElse(null);

		if (user instanceof Player player) {
			context.getTargetBlock().ifPresent(block -> player.breakBlock(block.getRelative(x, y, z)));
		}
	}
}
