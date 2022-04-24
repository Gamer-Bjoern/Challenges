package net.codingarea.challenges.plugin.utils.bukkit.container;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author anweisen | https://github.com/anweisen
 * @since 2.0
 */
public final class PlayerData {

	private final Collection<PotionEffect> effects;
	private final GameMode gamemode;
	private final Location location;
	private final ItemStack[] inventory;
	private final ItemStack[] armor;

	private final double health;
	private final int food;
	private final float saturation;
	private final int heldItemSlot;
	private final boolean allowedFlight;
	private final boolean flying;

	public PlayerData(@Nonnull Player player) {
		this(player.getGameMode(), player.getLocation(), player.getInventory().getContents(), player.getInventory().getArmorContents(),
				player.getActivePotionEffects(), player.getHealth(), player.getFoodLevel(), player.getSaturation(), player.getInventory().getHeldItemSlot(),
				player.getAllowFlight(), player.isFlying());
	}

	public PlayerData(@Nonnull GameMode gamemode, @Nonnull Location location, @Nonnull ItemStack[] inventory, @Nonnull ItemStack[] armor,
					  @Nonnull Collection<PotionEffect> effects, double health, int food, float saturation, int heldItemSlot, boolean allowedFlight, boolean flying) {
		this.gamemode = gamemode;
		this.location = location;
		this.inventory = inventory;
		this.armor = armor;
		this.effects = effects;
		this.health = health;
		this.food = food;
		this.saturation = saturation;
		this.heldItemSlot = heldItemSlot;
		this.allowedFlight = allowedFlight;
		this.flying = allowedFlight && flying;
	}

	public void apply(@Nonnull Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setGameMode(gamemode);
		player.teleport(location);
		player.setHealth(health);
		player.setFoodLevel(food);
		player.setSaturation(saturation);
		player.getInventory().setContents(inventory);
		player.getInventory().setArmorContents(armor);
		player.getInventory().setHeldItemSlot(heldItemSlot);
		player.addPotionEffects(effects);
		player.setAllowFlight(allowedFlight);
		player.setFlying(flying);
	}

	@Override
	public String toString() {
		return "PlayerData{" +
				"effects=" + effects +
				", gamemode=" + gamemode +
				", location=" + location +
				", inventory=" + Arrays.toString(inventory) +
				", armor=" + Arrays.toString(armor) +
				", health=" + health +
				", food=" + food +
				", saturation=" + saturation +
				", heldItemSlot=" + heldItemSlot +
				", allowedFlight=" + allowedFlight +
				", flying=" + flying +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PlayerData that = (PlayerData) o;
		return Double.compare(that.health, health) == 0 && food == that.food && Float.compare(that.saturation, saturation) == 0 && heldItemSlot == that.heldItemSlot && allowedFlight == that.allowedFlight && flying == that.flying && Objects.equals(effects, that.effects) && gamemode == that.gamemode && Objects.equals(location, that.location) && Arrays.equals(inventory, that.inventory) && Arrays.equals(armor, that.armor);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(effects, gamemode, location, health, food, saturation, heldItemSlot, allowedFlight, flying);
		result = 31 * result + Arrays.hashCode(inventory);
		result = 31 * result + Arrays.hashCode(armor);
		return result;
	}

}
