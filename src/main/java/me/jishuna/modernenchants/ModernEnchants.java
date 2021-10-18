package me.jishuna.modernenchants;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.jishuna.commonlib.language.MessageConfig;
import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.modernenchants.api.InvalidEnchantmentException;
import me.jishuna.modernenchants.api.conditions.ConditionRegistry;
import me.jishuna.modernenchants.api.effects.EffectRegistry;
import me.jishuna.modernenchants.api.enchantment.CustomEnchantment;
import me.jishuna.modernenchants.api.enchantment.EnchantmentRegistry;
import me.jishuna.modernenchants.commands.ModernEnchantsCommandHandler;
import me.jishuna.modernenchants.listeners.BlockListener;
import me.jishuna.modernenchants.listeners.CombatListener;
import me.jishuna.modernenchants.packets.IncomingItemListener;
import me.jishuna.modernenchants.packets.OutgoingItemListener;

public class ModernEnchants extends JavaPlugin {
	private static final String PATH = "Enchantments";

	private EffectRegistry effectRegistry;
	private ConditionRegistry conditionRegistry;

	private EnchantmentRegistry enchantmentRegistry;
	private MessageConfig messageConfig;

	@Override
	public void onEnable() {
		this.effectRegistry = new EffectRegistry();
		this.conditionRegistry = new ConditionRegistry();
		this.enchantmentRegistry = new EnchantmentRegistry(this);

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new CombatListener(), this);

		this.registerPackets();

		this.loadEnchantments();

		getCommand("modernenchants").setExecutor(new ModernEnchantsCommandHandler(this));
	}

	private void loadEnchantments() {
		try {
			ReflectionUtil.setFieldValue(Enchantment.class.getDeclaredField("acceptingNew"), null, true);
		} catch (NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
			return;
		}

		File enchantFolder = new File(this.getDataFolder(), PATH);
		if (!enchantFolder.exists()) {
			enchantFolder.mkdirs();
			this.copyDefaults();
		}

		for (File file : enchantFolder.listFiles()) {
			String name = file.getName();
			if (!name.endsWith(".yml"))
				continue;

			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			try {
				CustomEnchantment enchantment = new CustomEnchantment(this, this.effectRegistry, this.conditionRegistry,
						config);
				this.enchantmentRegistry.registerAndInjectEnchantment(enchantment);
			} catch (InvalidEnchantmentException e) {
				this.getLogger().warning(
						"An error occured loading enchantment \"" + config.getString("name", "Unknown") + "\" - " + e.getLocalizedMessage());
			}
		}
		Enchantment.stopAcceptingRegistrations();
	}

	private void copyDefaults() {
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {
			try (final JarFile jar = new JarFile(jarFile);) {
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final String name = entries.nextElement().getName();
					if (name.startsWith(PATH + "/")) {
						if (!name.endsWith(".yml"))
							continue;

						FileUtils.loadResourceFile(this, name);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
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

	public EnchantmentRegistry getEnchantmentRegistry() {
		return enchantmentRegistry;
	}

	public MessageConfig getMessageConfig() {
		return this.messageConfig;
	}

}
