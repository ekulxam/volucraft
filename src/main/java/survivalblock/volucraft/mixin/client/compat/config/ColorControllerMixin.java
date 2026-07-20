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
package survivalblock.volucraft.mixin.client.compat.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.ColorController;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.volucraft.client.compat.config.VolucraftYACLClientConfig;

import java.awt.*;

@Mixin(ColorController.class)
public abstract class ColorControllerMixin {
    @Shadow
    @Final
    private Option<Color> option;

    @Shadow
    @Final
    private boolean allowAlpha;

    @Shadow
    protected abstract String toHex(int value);

    @Inject(method = "formatValue", at = @At("HEAD"), cancellable = true)
    private void reorder(CallbackInfoReturnable<Component> cir) {
        if (this.allowAlpha && VolucraftYACLClientConfig.HANDLER.instance().displayARGB && this.option.flags().contains(VolucraftYACLClientConfig.USE_ARGB)) {
            MutableComponent text = Component.literal("#");
            text.append(this.toHex(this.option.pendingValue().getAlpha()));
            text.append(Component.literal(this.toHex(this.option.pendingValue().getRed())).withStyle(ChatFormatting.RED));
            text.append(Component.literal(this.toHex(this.option.pendingValue().getGreen())).withStyle(ChatFormatting.GREEN));
            text.append(Component.literal(this.toHex(this.option.pendingValue().getBlue())).withStyle(ChatFormatting.BLUE));
            cir.setReturnValue(text);
        }
    }
}
