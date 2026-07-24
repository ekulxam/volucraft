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
package survivalblock.volucraft.common.recipe.extrude;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public interface ExtrudedRecipes {
    default BiMap<ResourceKey<Recipe<?>>, ResourceKey<Recipe<?>>> volucraft$getRecipePairs() {
        throw new UnsupportedOperationException();
    }
}
