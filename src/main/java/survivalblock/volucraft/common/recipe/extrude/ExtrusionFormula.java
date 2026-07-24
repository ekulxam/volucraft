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
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

import java.util.HashMap;
import java.util.Map;

public final class ExtrusionFormula {
    private ExtrusionFormula() {
    }

    @ApiStatus.Internal
    @VisibleForTesting
    public static final Map<RecipeSerializer<? extends CraftingRecipe>, Extruder<?, ?>> HANDLERS = new HashMap<>(2);

    /**
     * Registers an {@linkplain Extruder} to a {@linkplain RecipeSerializer}.
     * @param serializer the recipe serializer used by this recipe
     * @param extruder the function that converts 2D crafting recipes to 3D amalgamation recipes
     * @param <C> the lower bound of the input {@linkplain CraftingRecipe}
     * @param <A> the lower bound of the output {@linkplain AmalgamationRecipe}
     * @throws IllegalStateException if an existing Extruder is already bound to the RecipeSerializer
     */
    public static <C extends CraftingRecipe, A extends AmalgamationRecipe> void register(RecipeSerializer<C> serializer, Extruder<? super C, A> extruder) {
        if (HANDLERS.containsKey(serializer)) {
            Identifier existing = HANDLERS.get(serializer).id();
            throw new IllegalStateException("A provided Extruder is already present! Attempting to replace " + existing + " with " + extruder.id() + ", which is not allowed.");
        }
        HANDLERS.put(serializer, extruder);
    }

    @ApiStatus.Internal
    @Nullable
    public static <C extends CraftingRecipe, A extends AmalgamationRecipe> Extruder<? super C, A> getHandler(RecipeSerializer<C> serializer) {
        //noinspection unchecked
        return (Extruder<C, A>) HANDLERS.get(serializer);
    }

    @ApiStatus.Internal
    public static void init() {
        ExtrusionFormula.register(ShapedRecipe.SERIALIZER, ShapedRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(ShapelessRecipe.SERIALIZER, ShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(DyeRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(ImbueRecipe.SERIALIZER, Flattener.INSTANCE);
        ExtrusionFormula.register(TransmuteRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(DecoratedPotRecipe.SERIALIZER, Flattener.INSTANCE);
        ExtrusionFormula.register(BookCloningRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(MapExtendingRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(FireworkRocketRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(FireworkStarRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(FireworkStarFadeRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(BannerDuplicateRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(ShieldDecorationRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        ExtrusionFormula.register(RepairItemRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
    }

    /**
     * A function that converts/extrudes 2D crafting recipes into 3D amalgamation recipes
     * @param <C> the lower bound of the input {@linkplain CraftingRecipe}
     * @param <A> the lower bound of the output {@linkplain AmalgamationRecipe}
     */
    public interface Extruder<C extends CraftingRecipe, A extends AmalgamationRecipe> {
        /**
         * Converts a 2D crafting recipe to a 3D amalgamation recipe
         * @param craftingRecipe the 2D recipe to be extruded
         * @return the Amalgamation Recipe, or null if one could not be created
         */
        @Nullable
        A create(C craftingRecipe);

        /**
         * Used to diagnose conflicts when the same {@linkplain RecipeSerializer} is given two Extruders
         * @return an id that represents this {@linkplain Extruder}
         */
        Identifier id();
    }
}
