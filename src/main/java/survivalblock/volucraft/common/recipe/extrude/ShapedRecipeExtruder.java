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
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.extrude.ExtrusionFormula.Extruder;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipePattern;
import survivalblock.volucraft.mixin.extrude.NormalCraftingRecipeAccessor;
import survivalblock.volucraft.mixin.extrude.ShapedRecipeAccessor;
import survivalblock.volucraft.mixin.extrude.ShapedRecipePatternAccessor;

import java.util.List;
import java.util.Optional;

/**
 * An {@link Extruder} that converts {@link ShapedRecipe}s to {@link ShapedAmalgamationRecipe}s
 */
public class ShapedRecipeExtruder implements Extruder<ShapedRecipe, ShapedAmalgamationRecipe> {
    public static final ShapedRecipeExtruder INSTANCE = new ShapedRecipeExtruder();
    public static final Identifier ID = Volucraft.id("shaped");

    @Override
    public @Nullable ShapedAmalgamationRecipe create(ShapedRecipe shapedRecipe) {
        Optional<ShapedRecipePattern.Data> optional = ((ShapedRecipePatternAccessor) (Object) ((ShapedRecipeAccessor) shapedRecipe).volucraft$getPattern()).volucraft$getData();
        if (optional.isEmpty()) {
            return null;
        }

        ShapedRecipePattern.Data data = optional.get();

        try {
            return new ShapedAmalgamationRecipe(
                    ((NormalCraftingRecipeAccessor) shapedRecipe).volucraft$getCommonInfo(),
                    ShapedAmalgamationRecipePattern.of(
                            data.key(),
                            List.of(data.pattern())
                    ),
                    ((ShapedRecipeAccessor) shapedRecipe).volucraft$getResult()
            );
        } catch (Exception e) {
            Volucraft.LOGGER.error("Unable to create ShapedAmalgamationRecipe from ShapedRecipe!", e);
        }

        return null;
    }

    @Override
    public Identifier id() {
        return ID;
    }
}
