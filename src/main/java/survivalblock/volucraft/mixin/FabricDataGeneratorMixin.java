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
package survivalblock.volucraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.common.Volucraft;

@Mixin(FabricDataGenerator.class)
public class FabricDataGeneratorMixin {
    @ModifyExpressionValue(method = "createBuiltinResourcePack", at = @At(value = "CONSTANT", args = "stringValue=resourcepacks"))
    private String packDataInstead(String original) {
        return Volucraft.datapacking ? "datapacks" : original;
    }
}
