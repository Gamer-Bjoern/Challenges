package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.commons.anntations.Since;
import net.codingarea.challenges.plugin.challenges.type.Setting;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
@Since("2.0")
public class HealthDisplaySetting extends Setting {

	public static final String OBJECTIVE_NAME = "health_display";

	public HealthDisplaySetting() {
		super(MenuType.SETTINGS);
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.RED_STAINED_GLASS, Message.forName("item-health-display-setting"));
	}

	@Override
	protected void onEnable() {
		Bukkit.getOnlinePlayers().forEach(this::show);
	}

	@Override
	protected void onDisable() {
		Bukkit.getOnlinePlayers().forEach(this::hide);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		if (isEnabled()) {
			show(event.getPlayer());

			for (Player player : Bukkit.getOnlinePlayers()) {
				Objective objective = player.getScoreboard().getObjective(OBJECTIVE_NAME);
				if (objective == null) continue;
				objective.getScore(event.getPlayer().getName()).setScore((int) event.getPlayer().getHealth());
			}
		} else {
			hide(event.getPlayer());
		}
	}

	private void show(@Nonnull Player player) {

		Scoreboard scoreboard = player.getScoreboard();
		if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard())
			player.setScoreboard(scoreboard = Bukkit.getScoreboardManager().getNewScoreboard());

		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null)
			objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "health", OBJECTIVE_NAME);

		objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);

		try {
			objective.setRenderType(RenderType.HEARTS);
		} catch (Exception ex) {
			// In some versions of spigot RenderType does not exist
		}

		for (Player current : Bukkit.getOnlinePlayers()) {
			objective.getScore(current.getName()).setScore((int) current.getHealth());
		}

	}

	private void hide(@Nonnull Player player) {

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null) return;

		try {
			objective.unregister();
		} catch (Exception ex) {
		}

	}

}
