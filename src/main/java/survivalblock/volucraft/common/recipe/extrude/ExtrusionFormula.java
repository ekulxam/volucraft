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

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import survivalblock.atmosphere.registrar.Registrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftRegistries;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

public final class ExtrusionFormula {
    private ExtrusionFormula() {
    }

    public static final String DIRECTORY = "volucraft_extrusion_formula";

    @ApiStatus.Internal
    public static void bootstrap() {
        Registrant<Extruder<?, ?>> registrant = new Registrant<>(Volucraft.MOD_ID, VolucraftRegistries.EXTRUDER);
        registrant.register("shaped", ShapedRecipeExtruder.INSTANCE);
        registrant.register("shapeless", ShapelessRecipeExtruder.INSTANCE);
        registrant.register("flattened", Flattener.INSTANCE);
        registrant.register("basically_shapeless", BasicallyShapelessRecipeExtruder.INSTANCE);
    }

    /**
     * A function that converts/extrudes 2D crafting recipes into 3D amalgamation recipes
     * @param <C> the lower bound of the input {@linkplain CraftingRecipe}
     * @param <A> the lower bound of the output {@linkplain AmalgamationRecipe}
     */
    public interface Extruder<C extends CraftingRecipe, A extends AmalgamationRecipe> {
        @ApiStatus.Experimental
        String APPENDED = ".volucraft_autoextruded";

        /**
         * Converts a 2D crafting recipe to a 3D amalgamation recipe
         * @param craftingRecipe the 2D recipe to be extruded
         * @return the Amalgamation Recipe, or null if one could not be created
         */
        @Nullable
        A create(C craftingRecipe);

        /**
         * Creates an id for the extruded recipe
         * @param original the id of the 2D crafting recipe
         * @return the id of the created 3D amalgamation recipe
         */
        default Identifier translate(Identifier original) {
            return original.withPath(s -> s + APPENDED);
        }
    }
}
