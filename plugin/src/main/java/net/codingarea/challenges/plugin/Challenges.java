package net.codingarea.challenges.plugin;

import net.codingarea.challenges.plugin.core.BukkitModule;
import net.codingarea.challenges.plugin.lang.loader.ContentLoader;
import net.codingarea.challenges.plugin.lang.loader.LanguageLoader;
import net.codingarea.challenges.plugin.lang.loader.PrefixLoader;
import net.codingarea.challenges.plugin.management.challenges.ChallengeLoader;
import net.codingarea.challenges.plugin.management.challenges.ChallengeManager;
import net.codingarea.challenges.plugin.management.database.DatabaseManager;
import net.codingarea.challenges.plugin.management.files.ConfigManager;
import net.codingarea.challenges.plugin.management.inventory.PlayerInventoryManager;
import net.codingarea.challenges.plugin.management.menu.MenuManager;
import net.codingarea.challenges.plugin.management.scheduler.ScheduleManager;
import net.codingarea.challenges.plugin.management.scheduler.timer.ChallengeTimer;
import net.codingarea.challenges.plugin.management.server.ServerManager;
import net.codingarea.challenges.plugin.management.server.WorldManager;
import net.codingarea.challenges.plugin.spigot.command.*;
import net.codingarea.challenges.plugin.spigot.listener.InventoryListener;
import net.codingarea.challenges.plugin.spigot.listener.PlayerConnectionListener;
import net.codingarea.challenges.plugin.spigot.listener.RestrictionListener;
import net.codingarea.challenges.plugin.utils.misc.Utils;

import javax.annotation.Nonnull;

/**
 * @author anweisen | https://github.com/anweisen
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public final class Challenges extends BukkitModule {

	private static Challenges instance;

	@Nonnull
	public static Challenges getInstance() {
		return instance;
	}

	private PlayerInventoryManager playerInventoryManager;
	private ChallengeManager challengeManager;
	private DatabaseManager databaseManager;
	private ServerManager serverManager;
	private ConfigManager configManager;
	private ScheduleManager scheduler;
	private WorldManager worldManager;
	private MenuManager menuManager;
	private ChallengeTimer timer;

	private boolean validationFailed = false;

	@Override
	public void onLoad() {
		instance = this;
		super.onLoad();

		if (validationFailed = (validate() || validationFailed)) return; // Handle in enable

		saveDefaultConfig();
		createManagers();

	}

	@Override
	public void onEnable() {
		super.onEnable();

		if (validationFailed) {
			Utils.disablePlugin();
			return;
		}

		enableManagers();

		registerCommands();
		registerListeners();

	}

	private void createManagers() {

		ContentLoader.executeLoaders(new LanguageLoader(), new PrefixLoader());

		configManager = new ConfigManager();
		databaseManager = new DatabaseManager();
		worldManager = new WorldManager();
		serverManager = new ServerManager();
		scheduler = new ScheduleManager();
		timer = new ChallengeTimer();
		challengeManager = new ChallengeManager();
		menuManager = new MenuManager();
		playerInventoryManager = new PlayerInventoryManager();

		new ChallengeLoader().load(); // Register challenges, replace with better way later

	}

	private void enableManagers() {

		configManager.loadConfigs();
		worldManager.executeWorldResetIfNecessary();
		databaseManager.connectIfCreated();
		timer.loadSession();
		challengeManager.enable();
		scheduler.start();

	}

	private void registerCommands() {
		registerCommand(new ChallengesCommand(), "challenges");
		registerCommand(new TimerCommand(), "timer");
		registerCommand(new PauseCommand(), "pause");
		registerCommand(new StartCommand(), "start");
	}

	private void registerListeners() {
		registerListener(
				new InventoryListener(),
				new PlayerConnectionListener(),
				new RestrictionListener()
		);
	}

	@Override
	public void onDisable() {
		super.onDisable();

		if (timer != null && worldManager != null && !worldManager.isShutdownBecauseOfReset()) timer.saveSession(false);
		if (scheduler != null) scheduler.stop();
		if (menuManager != null) menuManager.close();

		if (challengeManager != null) {
			challengeManager.saveLocalSettings(false);
			if (worldManager != null && !worldManager.isShutdownBecauseOfReset())
				challengeManager.saveGamestate(false);
			challengeManager.clearChallengeCache();
		}
	}

	@Nonnull
	public ChallengeManager getChallengeManager() {
		return challengeManager;
	}

	@Nonnull
	public MenuManager getMenuManager() {
		return menuManager;
	}

	@Nonnull
	public ChallengeTimer getChallengeTimer() {
		return timer;
	}

	@Nonnull
	public ServerManager getServerManager() {
		return serverManager;
	}

	@Nonnull
	public ScheduleManager getScheduler() {
		return scheduler;
	}

	@Nonnull
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Nonnull
	public PlayerInventoryManager getPlayerInventoryManager() {
		return playerInventoryManager;
	}

	@Nonnull
	public WorldManager getWorldManager() {
		return worldManager;
	}

}
