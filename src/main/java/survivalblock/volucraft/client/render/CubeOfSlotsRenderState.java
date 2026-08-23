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
package survivalblock.volucraft.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.client.compat.config.VolucraftClientConfig;

import java.util.List;

/**
 * Renders a 3x3x3 collection of slots as cubes
 * @param selected Denotes the index of the selected slot (to be highlighted)
 * @param lerpExpansion A value between 0 and 1 that represents how far the cube has been expanded
 */
public record CubeOfSlotsRenderState(
        Identifier texture,
        Identifier highlightTexture,
        List<ItemStackWith3DSlot> items,
        int highlightColor,
        int selected,
        float lerpExpansion,
        Quaternionfc rotation,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        float gameCubeAnimationProgress,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public CubeOfSlotsRenderState(
            Identifier texture,
            Identifier highlightTexture,
            List<ItemStackWith3DSlot> items,
            int highlightColor,
            int selected,
            float lerpExpansion,
            Quaternionfc rotation,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            float gameCubeAnimationProgress,
            @Nullable ScreenRectangle scissorArea
    ) {
        this(texture, highlightTexture, items, highlightColor, selected, lerpExpansion, rotation, x0, y0, x1, y1, scale, gameCubeAnimationProgress, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }

    public static CubeOfSlotsRenderState create(
            Minecraft client,
            CubeModel cubeModel,
            CubeModel cubeModelWithItem,
            Identifier texture,
            Identifier highlightTexture,
            NonNullList<ItemStack> items,
            int selected,
            float lerpExpansion,
            Quaternionfc rotation,
            int x0,
            int y0,
            int x1,
            int y1,
            float scale,
            float gameCubeAnimationProgress,
            @Nullable ScreenRectangle scissorArea
    ) {
        final int highlightColor = ((VolucraftClientConfig.INSTANCE.getCubeHighlightAlpha() & 0xFF) << 24) | 0xFFFFFF;
        List<ItemStackWith3DSlot> itemStackRenderStates = create3DSlots(items, client, cubeModel, cubeModelWithItem, gameCubeAnimationProgress);
        return new CubeOfSlotsRenderState(texture, highlightTexture, itemStackRenderStates, highlightColor, selected, lerpExpansion, rotation, x0, y0, x1, y1, scale, gameCubeAnimationProgress, scissorArea);
    }

    public static List<ItemStackWith3DSlot> create3DSlots(NonNullList<ItemStack> items, Minecraft client, CubeModel model, CubeModel modelWithItem, float anim) {
        if (items.isEmpty()) {
            return NonNullList.create();
        }

        final List<ItemStackWith3DSlot> list = NonNullList.createWithCapacity(items.size());
        final ItemDisplayContext displayContext = ItemDisplayContext.NONE;

        ItemStack stack;
        boolean empty;
        for (ItemStack item : items) {
            stack = item;
            empty = stack.isEmpty();
            ItemStackRenderState state = new ItemStackRenderState();
            client.getItemModelResolver().updateForTopItem(state, stack, displayContext, client.level, client.player, 0);
            list.add(
                    new ItemStackWith3DSlot(
                            state,
                            empty ? model : modelWithItem,
                            CubeOfSlotsRenderer.COLOR_COMPUTER.getColor(stack, anim),
                            !empty
                    )
            );
        }
        return list;
    }

    public record ItemStackWith3DSlot(ItemStackRenderState itemStackRenderState, CubeModel modelToUse, int color, boolean shouldRender) {
    }
}
