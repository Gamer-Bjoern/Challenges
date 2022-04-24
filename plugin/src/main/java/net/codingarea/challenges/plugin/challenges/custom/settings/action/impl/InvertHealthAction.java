package net.codingarea.challenges.plugin.challenges.custom.settings.action.impl;

import net.codingarea.challenges.plugin.challenges.custom.settings.action.PlayerTargetAction;
import net.codingarea.challenges.plugin.challenges.implementation.challenge.InvertHealthChallenge;
import net.codingarea.challenges.plugin.challenges.type.helper.SubSettingsHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.1.0
 */
public class InvertHealthAction extends PlayerTargetAction {

	public InvertHealthAction(String name) {
		super(name, SubSettingsHelper.createEntityTargetSettingsBuilder(false, true));
	}

	@Override
	public Material getMaterial() {
		return Material.REDSTONE;
	}


	@Override
	public void executeForPlayer(Player player, Map<String, String[]> subActions) {
		InvertHealthChallenge.invertHealth(player);
	}

}
