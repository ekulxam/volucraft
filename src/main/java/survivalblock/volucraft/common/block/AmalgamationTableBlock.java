/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.volucraft.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public class AmalgamationTableBlock extends Block {
	public static final MapCodec<AmalgamationTableBlock> CODEC = simpleCodec(AmalgamationTableBlock::new);
	public static final Component CONTAINER_TITLE = Component.translatable("container.volucraft.amalgamating");

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
