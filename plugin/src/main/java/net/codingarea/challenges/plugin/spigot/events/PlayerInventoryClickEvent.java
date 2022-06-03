package net.codingarea.challenges.plugin.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
public class PlayerInventoryClickEvent extends InventoryClickEventWrapper {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;

	public PlayerInventoryClickEvent(@Nonnull InventoryClickEvent event) {
		super(event);
		player = ((Player) event.getWhoClicked());
	}

	@Nonnull
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Nonnull
	public Player getPlayer() {
		return player;
	}

	@Nonnull
	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

}