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
package survivalblock.volucraft.client.render.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public class GameCubeScreen extends AmalgamationScreen {
    private boolean cubing;
    private long animationStart;

    public GameCubeScreen(AmalgamationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.cubing = true;
    }

    @Override
    protected float gameCubeAnimationProgress() {
        if (this.animationStart == 0L && this.cubing) {
            this.animationStart = Util.getMillis();
        }

        if (this.cubing) {
            float anim = (float) (Util.getMillis() - this.animationStart) / 3000.0F;

            if (anim > 1.0F) {
                this.cubing = false;
                return 1.0F;
            }

            return Mth.clamp(anim, 0.0F, 1.0F);
        }

        return 1.0F;
    }

    @Override
    protected void onDisplayTouched() {
        this.cubing = false;
    }

    @Override
    public int getHovered3DSlot(double mouseX, double mouseY, float scale, Quaternionfc rotation, @Nullable GuiGraphicsExtractor graphics) {
        return super.getHovered3DSlot(mouseX, mouseY, scale, rotation, graphics);
    }
}
