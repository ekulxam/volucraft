package survivalblock.volucraft.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public class AmalgamationTableBlock extends Block {
	public static final MapCodec<AmalgamationTableBlock> CODEC = simpleCodec(AmalgamationTableBlock::new);
	private static final Component CONTAINER_TITLE = Component.translatable("container.volucraft.amalgamating");

	@Override
	public MapCodec<? extends AmalgamationTableBlock> codec() {
		return CODEC;
	}

	public AmalgamationTableBlock(final BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			player.openMenu(state.getMenuProvider(level, pos));
			player.awardStat(VolucraftBlocks.INTERACT_WITH_AMALGAMATION_TABLE_STAT);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected MenuProvider getMenuProvider(final BlockState state, final Level level, final BlockPos pos) {
		return new SimpleMenuProvider(
			(containerId, inventory, player) -> new AmalgamationMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE
		);
	}
}
