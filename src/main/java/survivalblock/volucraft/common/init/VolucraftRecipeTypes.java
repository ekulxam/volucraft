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
package survivalblock.volucraft.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import survivalblock.atmosphere.registrar.Registrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

public final class VolucraftRecipeTypes {
    private VolucraftRecipeTypes() {
    }

    private static final RecipeTypeRegistrant REGISTRANT = new RecipeTypeRegistrant(Volucraft.MOD_ID, BuiltInRegistries.RECIPE_TYPE);

    public static final RecipeType<AmalgamationRecipe> AMALGAMATION = REGISTRANT.register("amalgamation");

    public static void init() {
        // NO-OP
    }

    public static class RecipeTypeRegistrant extends Registrant<RecipeType<?>> {

        public RecipeTypeRegistrant(String modId, Registry<RecipeType<?>> registry) {
            super(modId, registry);
        }

        public <T extends Recipe<?>> RecipeType<T> register(String name) {
            return register(name, new RecipeType<T>() {
                @Override
                public String toString() {
                    return name;
                }
            });
        }
    }
}
