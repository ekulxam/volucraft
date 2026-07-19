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

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/**
 * Renders a 3x3x3 collection of slots as cubes
 * @param unit The 1x1x1 cube model to be used to render all 27 cubes
 * @param selected Denotes the index of the selected slot (to be highlighted)
 * @param lerpExpansion A value between 0 and 1 that represents how far the cube has been expanded
 */
public record CubeOfSlotsRenderState(
        CubeModel unit,
        CubeModel unitWithItem,
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
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public CubeOfSlotsRenderState(
            CubeModel model,
            CubeModel model1,
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
        this(model, model1, texture, highlightTexture, items, selected, lerpExpansion, rotation, x0, y0, x1, y1, scale, gameCubeAnimationProgress, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }

    public CubeOfSlotsRenderState(
            CubeModel model,
            CubeModel model1,
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
            @Nullable ScreenRectangle scissorArea
    ) {
        this(model, model1, texture, highlightTexture, items, selected, lerpExpansion, rotation, x0, y0, x1, y1, scale, 1.0F, scissorArea);
    }
}
