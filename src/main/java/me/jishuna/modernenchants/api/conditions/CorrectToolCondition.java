package me.jishuna.modernenchants.api.conditions;

import java.util.function.Predicate;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.jishuna.modernenchants.api.annotations.RegisterCondition;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;

@RegisterCondition(name = "correct_tool")
public class CorrectToolCondition extends EnchantmentCondition {

	@Override
	public Predicate<EnchantmentContext> parseString(String[] data) throws InvalidEnchantmentException {
		checkLength(data, 1);

		boolean bool = Boolean.parseBoolean(data[0]);

		return context -> {
			ItemStack item = context.getItem().orElse(null);
			Block block = context.getTargetBlock().orElse(null);

			if (item == null || block == null)
				return true;

			return block.isPreferredTool(item) == bool;
		};
	}

}
