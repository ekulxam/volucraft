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

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.ProjectionType;
import net.minecraft.client.renderer.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Projection.class)
public class ProjectionMixin {

    @Definition(id = "ORTHOGRAPHIC", field = "Lcom/mojang/blaze3d/ProjectionType;ORTHOGRAPHIC:Lcom/mojang/blaze3d/ProjectionType;")
    @Expression("? != ORTHOGRAPHIC")
    @WrapOperation(method = "setupOrtho", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkVolucraftToo(Object left, Object right, Operation<Boolean> original) {
        return original.call(left, right) && original.call(left, ProjectionType.VOLUCRAFT_ORTHOGRAPHIC);
    }
}
