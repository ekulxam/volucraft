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
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.specific.wrapper.BasicallyShapelessAmalgamationRecipe;

/**
 * An {@link ExtrusionFormula.Extruder} that converts arbitrary {@link CraftingRecipe}s to {@link BasicallyShapelessAmalgamationRecipe}s.
 * <p>
 * "Basically Shapeless" amalgamation recipes are to be used for crafting recipes that can safely take crafting inputs of an arbitrary {@linkplain net.minecraft.world.item.crafting.CraftingInput#width() width} and {@linkplain net.minecraft.world.item.crafting.CraftingInput#height() height}. This is valid for recipes such as {@link net.minecraft.world.item.crafting.DyeRecipe}, which searches for two ingredients anywhere in the input.
 * @see Flattener
 */
public class BasicallyShapelessRecipeExtruder implements ExtrusionFormula.Extruder<CraftingRecipe, BasicallyShapelessAmalgamationRecipe> {
    public static final BasicallyShapelessRecipeExtruder INSTANCE = new BasicallyShapelessRecipeExtruder();
    public static final Identifier ID = Volucraft.id("basically_shapeless");

    @Override
    public @Nullable BasicallyShapelessAmalgamationRecipe create(CraftingRecipe craftingRecipe) {
        return new BasicallyShapelessAmalgamationRecipe(new Recipe.CommonInfo(false), craftingRecipe);
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
