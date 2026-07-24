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
import survivalblock.volucraft.common.recipe.specific.wrapper.FlattenedAmalgamationRecipe;

/**
 * An {@link ExtrusionFormula.Extruder} that converts arbitrary {@link CraftingRecipe}s to {@link FlattenedAmalgamationRecipe}s.
 * <p>
 * Note that Flattened recipes will attempt to {@linkplain survivalblock.volucraft.common.recipe.AmalgamationInput#computePossibleCraftInputs() transform the input} until a result can be produced.
 * @see BasicallyShapelessRecipeExtruder
 */
@SuppressWarnings("JavadocReference")
public class Flattener implements ExtrusionFormula.Extruder<CraftingRecipe, FlattenedAmalgamationRecipe> {
    public static final Flattener INSTANCE = new Flattener();
    public static final Identifier ID = Volucraft.id("flattened");

    @Override
    public @Nullable FlattenedAmalgamationRecipe create(CraftingRecipe craftingRecipe) {
        return new FlattenedAmalgamationRecipe(new Recipe.CommonInfo(false), craftingRecipe);
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
