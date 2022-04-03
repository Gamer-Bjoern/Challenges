package net.codingarea.challenges.plugin.challenges.custom.settings.trigger.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.trigger.ChallengeTrigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class HungerTrigger extends ChallengeTrigger {

  public HungerTrigger(String name) {
    super(name);
  }

  @Override
  public Material getMaterial() {
    return Material.ROTTEN_FLESH;
  }

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPickup(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
      createData()
          .entity(event.getEntity())
          .event(event)
          .execute();
    }
  }

}
