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
package survivalblock.volucraft.mixin.client.multimatch;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.render.screen.MultimatchWidget;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin extends AbstractContainerWidget {
    public AbstractSelectionListMixin(int x, int y, int width, int height, Component message, ScrollbarSettings scrollbarSettings) {
        super(x, y, width, height, message, scrollbarSettings);
    }

    @ModifyReturnValue(method = "getFirstEntryY", at = @At("RETURN"))
    private int flushAgainstTheTop(int original) {
        if ((AbstractSelectionList) (Object) this instanceof MultimatchWidget) {
            return this.getY();
        }
        return original;
    }
}
