package net.codingarea.challenges.plugin.challenges.implementation.challenge;

import net.anweisen.utilities.common.annotations.Since;
import net.codingarea.challenges.plugin.challenges.type.abstraction.SettingModifier;
import net.codingarea.challenges.plugin.content.Message;
import net.codingarea.challenges.plugin.content.Prefix;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.scheduler.task.ScheduledTask;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.NameHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0.2
 */
@Since("2.0.2")
public class DontStopRunningChallenge extends SettingModifier {

	private final Map<Player, Integer> playerStandingCount = new HashMap<>();

	public DontStopRunningChallenge() {
		super(MenuType.CHALLENGES, 3, 20);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.CARROT_ON_A_STICK, Message.forName("item-dont-stop-running-challenge"));
	}

	@ScheduledTask(ticks = 20)
	public void onSecond() {
		removeOfflinePlayers();
		countUpOrKillEveryone();
	}

	private void removeOfflinePlayers() {
		for (Player player : new ArrayList<>(playerStandingCount.keySet())) {
			if (!player.isOnline() || ignorePlayer(player)) playerStandingCount.remove(player);
		}
	}

	private void countUpOrKillEveryone() {

		broadcastFiltered(player -> {
			Integer count = playerStandingCount.getOrDefault(player, 0);
			if (count >= getValue()) {
				Message.forName("stopped-moving").broadcast(Prefix.CHALLENGES, NameHelper.getName(player));
				playerStandingCount.remove(player);
				kill(player);
				return;
			}
			playerStandingCount.put(player, count + 1);

		});

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
		if (!shouldExecuteEffect()) return;
		if (ignorePlayer(event.getPlayer())) return;
		if (event.getTo() == null) return;
		if (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getZ() == event.getTo().getZ() && event.getFrom().getY() == event.getTo().getY()) return;
		playerStandingCount.remove(event.getPlayer());
	}

}