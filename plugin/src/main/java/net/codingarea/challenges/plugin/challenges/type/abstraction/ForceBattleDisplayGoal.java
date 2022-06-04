package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.common.config.Document;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.spigot.events.PlayerIgnoreStatusChangeEvent;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sehrschlechtYT | https://github.com/sehrschlechtYT
 * @since 2.2.0
 */
public abstract class ForceBattleDisplayGoal<T> extends ForceBattleGoal<T> {
    private Map<Player, ArmorStand> displayStands;

    public ForceBattleDisplayGoal(@NotNull MenuType menu, @NotNull Message title) {
        super(menu, title);
    }

    @Override
    protected void onEnable() {
        displayStands = new HashMap<>();
        broadcastFiltered(this::updateDisplayStand);
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        displayStands.values().forEach(Entity::remove);
        displayStands = null;
    }

    public void updateDisplayStand(Player player) {
        ArmorStand armorStand = displayStands.computeIfAbsent(player, player1 -> {
            World world = player1.getWorld();
            ArmorStand entity = (ArmorStand) world
                    .spawnEntity(player1.getLocation().clone().add(0, 1, 0), EntityType.ARMOR_STAND);
            entity.setVisible(false);
            entity.setInvulnerable(true);
            entity.setGravity(false);
            entity.setMarker(true);
            entity.setSilent(true);
            entity.setSmall(true);
            return entity;
        });
        player.addPassenger(armorStand);

        handleDisplayStandUpdate(player, armorStand);
    }

    public abstract void handleDisplayStandUpdate(@NotNull Player player, @NotNull ArmorStand armorStand);

    @Override
    public void loadGameState(@NotNull Document document) {
        super.loadGameState(document);
        if(isEnabled()) {
            broadcastFiltered(this::updateDisplayStand);
        }
    }

    @Override
    public void setRandomTarget(Player player) {
        super.setRandomTarget(player);
        updateDisplayStand(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        if (!shouldExecuteEffect()) return;
        if (ignorePlayer(event.getPlayer())) return;
        updateDisplayStand(event.getPlayer());
    }

    @EventHandler
    public void onStatusChange(PlayerIgnoreStatusChangeEvent event) {
        if (!shouldExecuteEffect()) return;
        if (event.isNotIgnored()) {
            setRandomTargetIfCurrentlyNone(event.getPlayer());
            updateDisplayStand(event.getPlayer());
        } else {
            ArmorStand stand = displayStands.remove(event.getPlayer());
            if (stand != null) {
                stand.remove();
            }
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        for (Player player : displayStands.keySet()) {
            updateDisplayStand(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        if (!shouldExecuteEffect()) return;
        ArmorStand stand = displayStands.remove(event.getPlayer());
        if (stand != null) {
            stand.remove();
        }
    }
}
