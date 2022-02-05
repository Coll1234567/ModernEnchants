package me.jishuna.modernenchants.api.effect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.base.Enums;

import me.jishuna.actionconfiglib.ActionContext;
import me.jishuna.actionconfiglib.ArgumentFormat;
import me.jishuna.actionconfiglib.ConfigurationEntry;
import me.jishuna.actionconfiglib.effects.Effect;
import me.jishuna.actionconfiglib.effects.RegisterEffect;
import me.jishuna.actionconfiglib.enums.EntityTarget;
import me.jishuna.actionconfiglib.exceptions.ParsingException;
import me.jishuna.modernenchants.api.PluginKeys;
import net.md_5.bungee.api.ChatColor;

@ArgumentFormat(format = { "target", "type", "count", "name", "x-offset", "y-offset", "z-offset"})
@RegisterEffect(name = "MINIONS")
public class MinionEffect extends Effect {
	private final EntityTarget target;
	private final EntityType type;
	private final int count;
	private final String name;

	private final double xOffset;
	private final double yOffset;
	private final double zOffset;

	public MinionEffect(ConfigurationEntry entry) throws ParsingException {
		this.target = EntityTarget.fromString(entry.getString("target"));

		String type = entry.getStringOrThrow("type");

		this.type = Enums.getIfPresent(EntityType.class, type.toUpperCase()).toJavaUtil()
				.orElseThrow(() -> new ParsingException("The entity type \"" + type + "\" could not be found."));

		this.count = entry.getIntOrThrow("count");
		this.name = ChatColor.translateAlternateColorCodes('&', entry.getString("name"));

		this.xOffset = entry.getDouble("x-offset", 0);
		this.yOffset = entry.getDouble("y-offset", 0);
		this.zOffset = entry.getDouble("z-offset", 0);
	}

	@Override
	public void evaluate(ActionContext context) {
		context.getTargetOptional(this.target).ifPresent(owner -> {
			LivingEntity target = context.getLivingTarget(EntityTarget.getOpposite(this.target));
			for (int i = 0; i < this.count; i++) {
				Entity entity = owner.getWorld().spawn(owner.getLocation().add(xOffset, yOffset, zOffset),
						type.getEntityClass());

				entity.getPersistentDataContainer().set(PluginKeys.MINION_OWNER.getKey(), PersistentDataType.STRING,
						owner.getUniqueId().toString());
				entity.setCustomName(name.replace("%owner%", owner.getName()));

				if (entity instanceof Mob mob && target != null) {
					mob.setTarget(target);
				}
			}
		});
	}
}
