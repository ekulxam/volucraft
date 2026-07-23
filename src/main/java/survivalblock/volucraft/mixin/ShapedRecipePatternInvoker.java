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

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShapedRecipePattern.class)
public interface ShapedRecipePatternInvoker {
    @Invoker("firstNonEmpty")
    static int volucraft$invokeFirstNonEmpty(final String line) {
        throw new UnsupportedOperationException();
    }

    @Invoker("lastNonEmpty")
    static int volucraft$invokeLastNonEmpty(final String line) {
        throw new UnsupportedOperationException();
    }
}
