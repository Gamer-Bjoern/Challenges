package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.anweisen.utilities.commons.anntations.Since;
import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.MenuSetting;
import net.codingarea.challenges.plugin.lang.Message;
import net.codingarea.challenges.plugin.management.blocks.BlockDropManager.DropPriority;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.animation.SoundSample;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.misc.BlockUtils;
import net.codingarea.challenges.plugin.utils.misc.ItemUtils;
import net.codingarea.challenges.plugin.utils.misc.TimeUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author anweisen | https://github.com/anweisen
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
@Since("2.0")
public class CutCleanSetting extends MenuSetting {

	public CutCleanSetting() {
		super(MenuType.SETTINGS, "CutClean");
		registerSetting("iron->iron_ingot", new ConvertDropSubSetting(() -> new ItemBuilder(Material.IRON_INGOT, Message.forName("item-cut-clean-iron-setting")), true, Material.IRON_ORE, Material.IRON_INGOT));
		registerSetting("gold->gold_ingot", new ConvertDropSubSetting(() -> new ItemBuilder(Material.GOLD_INGOT, Message.forName("item-cut-clean-gold-setting")), true, Material.GOLD_ORE, Material.GOLD_INGOT));
		registerSetting("coal->torch",      new ConvertDropSubSetting(() -> new ItemBuilder(Material.COAL, Message.forName("item-cut-clean-coal-setting")), false, Material.COAL_ORE, Material.TORCH));
		registerSetting("ore->veins",       new BreakOreVeinsSubSetting(() -> new ItemBuilder(Material.GOLDEN_PICKAXE, Message.forName("item-cut-clean-vein-setting")), 10, 0));
		registerSetting("row->cooked",      new CookFoodSubSetting(() -> new ItemBuilder(Material.COOKED_BEEF, Message.forName("item-cut-clean-food-setting")), true));
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new ItemBuilder(Material.IRON_AXE, Message.forName("item-cut-clean"));
	}

	private class ConvertDropSubSetting extends BooleanSubSetting {

		protected final Material from, to;

		public ConvertDropSubSetting(@Nonnull Supplier<ItemBuilder> item, boolean enabledByDefault, @Nonnull Material from, @Nonnull Material to) {
			super(item, enabledByDefault);
			this.from = from;
			this.to = to;
		}

		@Override
		public void onEnable() {
			Challenges.getInstance().getBlockDropManager().setCustomDrops(from, to, DropPriority.CUT_CLEAN);
		}

		@Override
		public void onDisable() {
			Challenges.getInstance().getBlockDropManager().resetCustomDrop(from, DropPriority.CUT_CLEAN);
		}

	}

	private class BreakOreVeinsSubSetting extends NumberSubSetting {

		public BreakOreVeinsSubSetting(@Nonnull Supplier<ItemBuilder> item) {
			super(item);
		}

		public BreakOreVeinsSubSetting(@Nonnull Supplier<ItemBuilder> item, int max) {
			super(item, max);
		}

		public BreakOreVeinsSubSetting(@Nonnull Supplier<ItemBuilder> item, int max, int min) {
			super(item, max, min);
		}

		@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
		public void onBlockBreak(@Nonnull BlockBreakEvent event) {
			if (!isEnabled()) return;
			ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
			if (!canBeBroken(event.getBlock(), itemInMainHand)) return;
			if (!event.getBlock().getType().name().contains("ORE")) return;

			breakBlockVein(event.getPlayer(), event.getBlock(), itemInMainHand);
		}

		private void breakBlockVein(@Nonnull Player player, @Nonnull Block block, @Nonnull ItemStack itemInMainHand) {
			Material material = block.getType();

			List<Block> allBlocks = new ArrayList<>();
			List<Block> nextBlocks = new ArrayList<>();
			nextBlocks.add(block);

			for (int i = 0; i < getValue(); i++) {

				if (nextBlocks.isEmpty()) break;

				List<Block> lastBlocks = new ArrayList<>(nextBlocks);
				nextBlocks.clear();

				for (Block currentMiddleBlock : lastBlocks) {
					for (Block sideBlock : BlockUtils.getBlocksAroundBlock(currentMiddleBlock)) {
						if (sideBlock.getType() == material && sideBlock != block && !allBlocks.contains(sideBlock) && allBlocks.size() < getValue()) {
							allBlocks.add(sideBlock);
							nextBlocks.add(sideBlock);
						}
					}
				}
			}

			AtomicInteger index = new AtomicInteger();
			TimeUtils.createBukkitRunnable(timer -> {

				SoundSample.LOW_PLOP.play(player);
				for (int i = 0; i < 2; i++) {

					if (index.get() >= allBlocks.size()) {
						timer.cancel();
						return;
					}

					Block currentBlock = allBlocks.get(index.get());

					Material blockType = currentBlock.getType();
					if (blockType == material) {

						if (!Challenges.getInstance().getBlockDropManager().getDropChance(blockType).getAsBoolean()) return;

						List<Material> customDrops = Challenges.getInstance().getBlockDropManager().getCustomDrops(blockType);
						if (!customDrops.isEmpty()) {
							customDrops.forEach(drop -> currentBlock.getWorld().dropItemNaturally(currentBlock.getLocation(), new ItemStack(drop)));
							currentBlock.setType(Material.AIR);
							return;
						}

						currentBlock.breakNaturally(itemInMainHand);
					}

					index.getAndIncrement();
				}
			}).runTaskTimer(plugin, 0, 2);

		}

		private boolean canBeBroken(@Nonnull Block block, @Nonnull ItemStack tool) {
			return !block.getDrops(tool).isEmpty();
		}

		@Nonnull
		@Override
		public ItemBuilder getSettingsItem() {
			return getAsBoolean() ? DefaultItem.value(getValue(), "§7Max Vein Size: §e") : DefaultItem.disabled();
		}

	}

	private class CookFoodSubSetting extends BooleanSubSetting {

		public CookFoodSubSetting(@Nonnull Supplier<ItemBuilder> item, boolean enabledByDefault) {
			super(item, enabledByDefault);
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		public void onEntityKill(@Nonnull EntityDeathEvent event) {
			if (!isEnabled()) return;
			event.getDrops().replaceAll(item -> new ItemBuilder(ItemUtils.convertFoodToCookedFood(item.getType())).amount(item.getAmount()).build());
		}

	}

}
