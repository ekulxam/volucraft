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
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;
import survivalblock.volucraft.mixin.extrude.NormalCraftingRecipeAccessor;
import survivalblock.volucraft.mixin.extrude.ShapelessRecipeAccessor;

/**
 * An {@link ExtrusionFormula.Extruder} that converts {@link ShapelessRecipe}s to {@link ShapelessAmalgamationRecipe}s
 */
public class ShapelessRecipeExtruder implements ExtrusionFormula.Extruder<ShapelessRecipe, ShapelessAmalgamationRecipe> {
    public static final ShapelessRecipeExtruder INSTANCE = new ShapelessRecipeExtruder();
    public static final Identifier ID = Volucraft.id("shapeless");

    @Override
    public @Nullable ShapelessAmalgamationRecipe create(ShapelessRecipe shapelessRecipe) {
        return new ShapelessAmalgamationRecipe(
                ((NormalCraftingRecipeAccessor) shapelessRecipe).volucraft$getCommonInfo(),
                ((ShapelessRecipeAccessor) shapelessRecipe).volucraft$getResult(),
                ((ShapelessRecipeAccessor) shapelessRecipe).volucraft$getIngredients()
        );
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
