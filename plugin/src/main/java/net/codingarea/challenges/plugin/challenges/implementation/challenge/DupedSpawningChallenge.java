package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.codingarea.challenges.plugin.challenges.type.Setting;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public class DupedSpawningChallenge extends Setting {

	public DupedSpawningChallenge() {
		super(MenuType.CHALLENGES);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.ELDER_GUARDIAN_SPAWN_EGG, Message.forName("item-duped-spawning-challenge"));
	}

	private boolean inCustomSpawn = false;

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSpawn(@Nonnull EntitySpawnEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!(event.getEntity() instanceof LivingEntity)
				|| event.getEntity() instanceof Player
				|| event.getEntity() instanceof EnderDragon
		) return;
		if (inCustomSpawn) return;
		inCustomSpawn = true;
		Entity entity = event.getEntity().getWorld().spawnEntity(event.getLocation(), event.getEntityType());

		if (entity instanceof Slime && event.getEntity() instanceof Slime) {
			((Slime) entity).setSize(((Slime) event.getEntity()).getSize());
		}

		inCustomSpawn = false;
	}

}