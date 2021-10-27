package me.jishuna.modernenchants.api.effects;

import static me.jishuna.modernenchants.api.ParseUtils.checkLength;
import static me.jishuna.modernenchants.api.ParseUtils.readInt;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.modernenchants.api.PluginKeys;
import me.jishuna.modernenchants.api.annotations.RegisterEffect;
import me.jishuna.modernenchants.api.enchantments.EnchantmentContext;
import me.jishuna.modernenchants.api.exceptions.InvalidEnchantmentException;
import net.md_5.bungee.api.ChatColor;

@RegisterEffect(name = "minions")
public class MinionEffect extends EnchantmentEffect {
	private static final Set<String> ALL_TYPES = Arrays.stream(EntityType.values()).map(Enum::toString)
			.collect(Collectors.toSet());

	private static final String[] DESCRIPTION = new String[] { "test", "test2" };

	private final EntityType type;
	private int count;
	private String name;

	public MinionEffect(String[] data) throws InvalidEnchantmentException {
		super(data);
		checkLength(data, 3);

		String typeString = data[0].toUpperCase();
		if (!ALL_TYPES.contains(typeString))
			throw new InvalidEnchantmentException("Invalid sound type: " + typeString);

		this.type = EntityType.valueOf(typeString);

		this.count = readInt(data[1]);
		this.name = ChatColor.translateAlternateColorCodes('&', data[2]);
	}

	@Override
	public void handle(EnchantmentContext context) {
		context.getUser().ifPresent(user -> {
			LivingEntity target = context.getOpponentDirect();
			for (int i = 0; i < count; i++) {
				Entity entity = user.getWorld().spawn(user.getLocation(), type.getEntityClass());
				entity.getPersistentDataContainer().set(PluginKeys.MINION_OWNER.getKey(), PersistentDataType.STRING,
						user.getUniqueId().toString());
				entity.setCustomName(name.replace("%owner%", user.getName()));

				if (entity instanceof Mob mob && target != null) {
					mob.setTarget(target);
				}
			}
		});
	}
}
