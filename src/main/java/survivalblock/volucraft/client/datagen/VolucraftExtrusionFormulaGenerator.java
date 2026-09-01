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
package survivalblock.volucraft.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.*;
import survivalblock.volucraft.common.recipe.extrude.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class VolucraftExtrusionFormulaGenerator extends ExtrusionFormulaGenerator {
    protected VolucraftExtrusionFormulaGenerator(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    protected void configure(BiConsumer<RecipeSerializer<?>, ExtrusionFormula.Extruder<?, ?>> provider) {
        provider.accept(ShapedRecipe.SERIALIZER, ShapedRecipeExtruder.INSTANCE);
        provider.accept(ShapelessRecipe.SERIALIZER, ShapelessRecipeExtruder.INSTANCE);
        provider.accept(DyeRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(ImbueRecipe.SERIALIZER, Flattener.INSTANCE);
        provider.accept(TransmuteRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(DecoratedPotRecipe.SERIALIZER, Flattener.INSTANCE);
        provider.accept(BookCloningRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(MapExtendingRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(FireworkRocketRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(FireworkStarRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(FireworkStarFadeRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(BannerDuplicateRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(ShieldDecorationRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
        provider.accept(RepairItemRecipe.SERIALIZER, BasicallyShapelessRecipeExtruder.INSTANCE);
    }
}
