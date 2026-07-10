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
package survivalblock.volucraft.mixin.client.sodiumfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.ProjectionType;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;

@Mixin(PictureInPictureRenderer.class)
public class PictureInPictureRendererMixin {

    @ModifyExpressionValue(method = "prepareTexturesAndProjection", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/ProjectionType;ORTHOGRAPHIC:Lcom/mojang/blaze3d/ProjectionType;", opcode = Opcodes.GETSTATIC))
    private ProjectionType useVolucraftOrthoInstead(ProjectionType original) {
        if ((PictureInPictureRenderer<?>) (Object) this instanceof CubeOfSlotsRenderer) {
            return ProjectionType.VOLUCRAFT_ORTHOGRAPHIC;
        }
        return original;
    }
}
