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

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import survivalblock.volucraft.common.Volucraft;

public class VolucraftDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(VolucraftModelGenerator::new);
        pack.addProvider(VolucraftEnUsLangGenerator::new);
        pack.addProvider(VolucraftLootTableGenerator::new);
        pack.addProvider(VolucraftRecipeGenerators.Actual::new);
        FabricDataGenerator.Pack exampleRecipes = Volucraft.wrapDatapack(
                () -> fabricDataGenerator.createBuiltinResourcePack(Volucraft.EXAMPLE_RECIPES_PACK)
        );
        exampleRecipes.addProvider(VolucraftRecipeGenerators.Examples::new);
	}
}
