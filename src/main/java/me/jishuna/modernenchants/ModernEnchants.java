package me.jishuna.modernenchants;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.jishuna.commonlib.inventory.CustomInventoryManager;
import me.jishuna.commonlib.language.MessageConfig;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.modernenchants.api.condition.ConditionRegistry;
import me.jishuna.modernenchants.api.effect.EffectRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.api.enchantment.VanillaEnchantment;
import me.jishuna.modernenchants.api.exception.InvalidEnchantmentException;
import me.jishuna.modernenchants.command.ModernEnchantsCommandHandler;
import me.jishuna.modernenchants.listener.AnvilListener;
import me.jishuna.modernenchants.listener.BlockListener;
import me.jishuna.modernenchants.listener.CombatListener;
import me.jishuna.modernenchants.listener.DeathListener;
import me.jishuna.modernenchants.listener.EnchantingListener;
import me.jishuna.modernenchants.listener.InteractListener;
import me.jishuna.modernenchants.listener.MinionListener;
import me.jishuna.modernenchants.listener.MiscListener;
import me.jishuna.modernenchants.packet.IncomingItemListener;
import me.jishuna.modernenchants.packet.OutgoingItemListener;

public class ModernEnchants extends JavaPlugin {
	private static final String CUSTOM_PATH = "Enchantments/Custom";
	private static final String VANILLA_PATH = "Enchantments/Vanilla";

	private EffectRegistry effectRegistry;
	private ConditionRegistry conditionRegistry;

	private EnchantmentRegistry enchantmentRegistry;
	private MessageConfig messageConfig;

	private CustomInventoryManager inventoryManager = new CustomInventoryManager();

	@Override
	public void onEnable() {
		this.loadConfiguration();

		this.effectRegistry = new EffectRegistry();
		this.conditionRegistry = new ConditionRegistry();
		this.enchantmentRegistry = new EnchantmentRegistry();

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new CombatListener(), this);
		pm.registerEvents(new DeathListener(), this);
		pm.registerEvents(new MiscListener(), this);
		pm.registerEvents(new InteractListener(), this);
		pm.registerEvents(new MinionListener(), this);

		pm.registerEvents(new EnchantingListener(this), this);
		pm.registerEvents(new AnvilListener(), this);

		pm.registerEvents(this.inventoryManager, this);

		this.registerPackets();

		this.loadEnchantments();

		new WornEnchantmentRunnable().runTaskTimer(this, 0, 10);

		getCommand("modernenchants").setExecutor(new ModernEnchantsCommandHandler(this));
	}

	@Override
	public void onDisable() {
		try {
			Field byIdField = Enchantment.class.getDeclaredField("byKey");
			Field byNameField = Enchantment.class.getDeclaredField("byName");
			byIdField.setAccessible(true);
			byNameField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<NamespacedKey, Enchantment> keyMap = (Map<NamespacedKey, Enchantment>) byIdField.get(null);
			@SuppressWarnings("unchecked")
			Map<String, Enchantment> nameMap = (Map<String, Enchantment>) byNameField.get(null);

			Iterator<Entry<NamespacedKey, Enchantment>> keyIterator = keyMap.entrySet().iterator();
			while (keyIterator.hasNext()) {
				Enchantment enchant = keyIterator.next().getValue();

				if (enchant instanceof CustomEnchantment)
					keyIterator.remove();
			}

			Iterator<Entry<String, Enchantment>> nameIterator = nameMap.entrySet().iterator();
			while (nameIterator.hasNext()) {
				Enchantment enchant = nameIterator.next().getValue();

				if (enchant instanceof CustomEnchantment)
					nameIterator.remove();
			}

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private void loadEnchantments() {
		try {
			ReflectionUtil.setFieldValue(Enchantment.class.getDeclaredField("acceptingNew"), null, true);
		} catch (NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
			return;
		}

		File customEnchantFolder = new File(this.getDataFolder(), CUSTOM_PATH);
		if (!customEnchantFolder.exists()) {
			customEnchantFolder.mkdirs();
			FileUtils.copyDefaults(this, CUSTOM_PATH, name -> FileUtils.loadResourceFile(this, name));
		}

		this.loadCustom(customEnchantFolder);

		File vanillaEnchantFolder = new File(this.getDataFolder(), VANILLA_PATH);
		if (!vanillaEnchantFolder.exists()) {
			vanillaEnchantFolder.mkdirs();
			FileUtils.copyDefaults(this, VANILLA_PATH, name -> FileUtils.loadResourceFile(this, name));
		}

		this.loadVanilla(vanillaEnchantFolder);
		Enchantment.stopAcceptingRegistrations();
	}

	private void loadCustom(File folder) {
		for (File file : folder.listFiles()) {
			String name = file.getName();

			if (file.isDirectory())
				loadCustom(file);

			if (!name.endsWith(".yml"))
				continue;

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			try {
				CustomEnchantment enchantment = new CustomEnchantment(this, config);
				this.enchantmentRegistry.registerAndInjectEnchantment(enchantment);
			} catch (InvalidEnchantmentException ex) {
				String enchantName = config.getString("name", "Unknown");
				ex.addAdditionalInfo("Error while parsing enchantment \"" + enchantName + "\":");
				ex.log(getLogger());
			}
		}
	}

	private void loadVanilla(File folder) {
		for (File file : folder.listFiles()) {
			String name = file.getName();

			if (file.isDirectory())
				loadVanilla(file);

			if (!name.endsWith(".yml"))
				continue;

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			try {
				VanillaEnchantment enchantment = new VanillaEnchantment(this, config);
				this.enchantmentRegistry.registerAndInjectEnchantment(enchantment);
			} catch (InvalidEnchantmentException ex) {
				String enchantName = config.getString("name", "Unknown");
				ex.addAdditionalInfo("Error while parsing vanilla enchantment \"" + enchantName + "\":");
				ex.log(getLogger());
			}
		}
	}

	private void registerPackets() {
		ProtocolManager manager = ProtocolLibrary.getProtocolManager();

		manager.addPacketListener(new OutgoingItemListener(this));
		manager.addPacketListener(new IncomingItemListener(this));
	}

	public void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		FileUtils.loadResourceFile(this, "messages.yml")
				.ifPresent(file -> this.messageConfig = new MessageConfig(file));
	}

	public String getMessage(String key) {
		return this.messageConfig.getString(key);
	}

	public EffectRegistry getEffectRegistry() {
		return effectRegistry;
	}

	public ConditionRegistry getConditionRegistry() {
		return conditionRegistry;
	}

	public EnchantmentRegistry getEnchantmentRegistry() {
		return enchantmentRegistry;
	}

	public CustomInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public MessageConfig getMessageConfig() {
		return this.messageConfig;
	}

}
