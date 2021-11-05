package me.jishuna.modernenchants.api.condition;

import static me.jishuna.modernenchants.api.utils.ParseUtils.checkLength;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.annotation.RegisterCondition;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentContext;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;

@RegisterCondition(name = "correct_tool")
public class CorrectToolCondition extends EnchantmentCondition {

	private final boolean bool;

	public CorrectToolCondition(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 1);

		this.bool = Boolean.parseBoolean(data[0]);
	}

	@Override
	public boolean check(EnchantmentContext context, CustomEnchantment enchant) {
		ItemStack item = context.getItemDirect();
		Block block = context.getTargetBlockDirect();

		if (item == null || block == null)
			return true;

		return block.isPreferredTool(item) == bool;
	}

}
