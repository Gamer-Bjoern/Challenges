package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.AbstractChallengeTrigger;
import net.codingarea.challenges.plugin.spigot.events.PlayerPickupItemEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class PickupItemTrigger extends AbstractChallengeTrigger {

  public PickupItemTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.STICK;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPickup(PlayerPickupItemEvent event) {
    createData()
        .entity(event.getPlayer())
        .event(event)
        .execute();
  }

}
