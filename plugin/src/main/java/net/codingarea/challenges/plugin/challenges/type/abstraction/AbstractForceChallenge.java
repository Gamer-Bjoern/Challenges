package net.codingarea.challenges.plugin.challenges.type.abstraction;

import net.anweisen.utilities.bukkit.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.management.server.scoreboard.ChallengeBossBar.BossBarInstance;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public abstract class AbstractForceChallenge extends TimedChallenge {

	public static final int WAITING = 0,
							COUNTDOWN = 1;

	private int state = WAITING;

	public AbstractForceChallenge(@Nonnull MenuType menu) {
		super(menu, false);
	}

	public AbstractForceChallenge(@Nonnull MenuType menu, int max) {
		super(menu, max, false);
	}

	public AbstractForceChallenge(@Nonnull MenuType menu, int min, int max) {
		super(menu, min, max, false);
	}

	public AbstractForceChallenge(@Nonnull MenuType menu, int min, int max, int defaultValue) {
		super(menu, min, max, defaultValue, false);
	}

	@Override
	protected void onTimeActivation() {
		switch (state) {
			case WAITING:
				state = COUNTDOWN;
				chooseForcing();
				restartTimer(getForcingTime());
				SoundSample.BASS_ON.broadcast();
				bossbar.update();
				break;
			case COUNTDOWN:
				state = WAITING;
				restartTimer();
				handleCountdownEnd();
				bossbar.update();
				break;
		}
	}

	protected final void endForcing() {
		state = WAITING;
		restartTimer();
		bossbar.update();
	}

	protected abstract void handleCountdownEnd();

	@Override
	protected void handleCountdown() {
		bossbar.update();
	}

	@Override
	protected void onEnable() {
		bossbar.setContent(setupBossbar());
		bossbar.show();
	}

	@Override
	protected void onDisable() {
		bossbar.hide();
	}

	protected abstract void chooseForcing();
	protected abstract int getForcingTime();

	@Nonnull
	protected abstract BiConsumer<BossBarInstance, Player> setupBossbar();

	public final int getState() {
		return state;
	}

}
