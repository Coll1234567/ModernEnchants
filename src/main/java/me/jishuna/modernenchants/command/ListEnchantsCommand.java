package me.jishuna.modernenchants.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.commonlib.commands.SimpleCommandHandler;
import me.jishuna.commonlib.inventory.CustomInventory;
import me.jishuna.commonlib.items.ItemBuilder;
import me.jishuna.commonlib.utils.StringUtils;
import me.jishuna.modernenchants.ModernEnchants;
import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import net.md_5.bungee.api.ChatColor;

public class ListEnchantsCommand extends SimpleCommandHandler {

	private static final NamespacedKey INDEX_KEY = PluginKeys.PAGE_INDEX.getKey();
	private final ModernEnchants plugin;

	public ListEnchantsCommand(ModernEnchants plugin) {
		super("modernenchants.command.listenchants");
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission(getPermission())) {
			sender.sendMessage(this.plugin.getMessage("no-permission"));
			return true;
		}

		if (!(sender instanceof Player player))
			return true;

		showPluginGUI(player, 0);
		return true;
	}

	private void showPluginGUI(HumanEntity player, int start) {
		CustomInventory inventory = new CustomInventory(null, 54, this.plugin.getMessage("enchantment-gui-name"));
		inventory.addClickConsumer(event -> event.setCancelled(true));

		final List<CustomEnchantment> enchants = new ArrayList<>(plugin.getEnchantmentRegistry().getAllEnchantments());

		for (int i = 0; i < Math.min(45, enchants.size() - start); i++) {
			CustomEnchantment enchantment = enchants.get(start + i);

			ItemStack item = new ItemBuilder(Material.ENCHANTED_BOOK)
					.withName(enchantment.getDisplayName())
					.withLore(StringUtils.splitString(enchantment.getLongDescription(), 40))
					.addLore("")
					.addLore(this.plugin.getMessage("max-level") + ChatColor.DARK_GREEN + enchantment.getMaxLevel())
					.addLore("")
					.addLore(StringUtils.splitString(this.plugin.getMessage("applicable-items") + ChatColor.GRAY + String.join(", ", enchantment.getValidItemsRaw()) + ".", 40))
					.withStoredEnchantment(enchantment, 1)
					.withFlags(ItemFlag.HIDE_ENCHANTS)
					.build();

			inventory.addItem(item);
		}

		ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).withName(" ").build();
		for (int i = 45; i < 54; i++) {
			inventory.setItem(i, filler);
		}

		inventory.addButton(49,
				new ItemBuilder(Material.BARRIER).withName(this.plugin.getMessage("close-inventory")).build(),
				event -> Bukkit.getScheduler().runTask(this.plugin, () -> event.getWhoClicked().closeInventory()));

		if (start > 0) {
			inventory.addButton(45,
					new ItemBuilder(Material.ARROW).withName(this.plugin.getMessage("previous-page"))
							.withPersistantData(INDEX_KEY, PersistentDataType.INTEGER, start - 45).build(),
					this::gotoPage);
		}
		if (start + 45 < enchants.size()) {
			inventory.addButton(53,
					new ItemBuilder(Material.ARROW).withName(this.plugin.getMessage("next-page"))
							.withPersistantData(INDEX_KEY, PersistentDataType.INTEGER, start + 45).build(),
					this::gotoPage);
		}

		this.plugin.getInventoryManager().openGui(player, inventory);
	}

	private void gotoPage(InventoryClickEvent event) {
		PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
		showPluginGUI(event.getWhoClicked(), container.getOrDefault(INDEX_KEY, PersistentDataType.INTEGER, 0));
	}
}
