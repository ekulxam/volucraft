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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import survivalblock.volucraft.common.init.VolucraftRegistries;
import survivalblock.volucraft.common.recipe.extrude.ExtrusionFormula;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class ExtrusionFormulaGenerator extends FabricCodecDataProvider<Identifier> {
    protected ExtrusionFormulaGenerator(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture, PackOutput.Target.DATA_PACK, ExtrusionFormula.DIRECTORY, Identifier.CODEC);
    }

    @Override
    protected final void configure(BiConsumer<Identifier, Identifier> provider, HolderLookup.Provider registryLookup) {
        configure((recipeSerializer, extruder) ->
                provider.accept(
                        Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipeSerializer)),
                        Objects.requireNonNull(VolucraftRegistries.EXTRUDER.getKey(extruder))
                )
        );
    }

    protected abstract void configure(BiConsumer<RecipeSerializer<?>, ExtrusionFormula.Extruder<?, ?>> provider);

    @Override
    public String getName() {
        return "Extrusion Formula";
    }
}
