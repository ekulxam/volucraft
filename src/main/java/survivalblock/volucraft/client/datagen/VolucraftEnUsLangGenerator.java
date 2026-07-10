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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import survivalblock.volucraft.common.init.VolucraftBlocks;

import java.util.concurrent.CompletableFuture;

public class VolucraftEnUsLangGenerator extends FabricLanguageProvider {
    protected VolucraftEnUsLangGenerator(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(packOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
        translationBuilder.add(VolucraftBlocks.AMALGAMATION_TABLE, "Amalgamation Table");

        translationBuilder.add(VolucraftBlocks.INTERACT_WITH_AMALGAMATION_TABLE_STAT.toLanguageKey("stat"), "Interactions with Amalgamation Table");

        translationBuilder.add("container.volucraft.amalgamating", "Amalgamation");
        translationBuilder.add("container.volucraft.amalgamating.expandbutton", "Expansion");
        translationBuilder.add("container.volucraft.amalgamating.expandbutton.expand", "Expand");
        translationBuilder.add("container.volucraft.amalgamating.expandbutton.shrink", "Shrink");

        translationBuilder.add("dataPack.volucraft.example_recipes.name", "Volucraft: Example Recipes");

        translationBuilder.add("volucraft.yacl.category.main", "Volucraft Config (Powered by YACL)");
        translationBuilder.add("volucraft.yacl.category.main.tooltip", "Config");
        translationBuilder.add("volucraft.yacl.group.client", "Client");
        translationBuilder.add("volucraft.yacl.option.color.cubeBackgroundColor", "Cube Background Color");
        translationBuilder.add("volucraft.yacl.option.color.cubeBackgroundColor.desc", "Determines the color of the background of the cube area in the Amalgamation Table screen. Vanilla uses #8b8b8b for slots.");
        translationBuilder.add("volucraft.yacl.option.boolean.displayARGB", "Display ARGB");
        translationBuilder.add("volucraft.yacl.option.boolean.displayARGB.desc", "Determines whether the Cube Background Color option is displayed in ARGB or RGBA.");
        translationBuilder.add("volucraft.yacl.option.integer.cubeAlpha", "Cube Alpha");
        translationBuilder.add("volucraft.yacl.option.integer.cubeAlpha.desc", "Determines the translucency of the 3D slots.");
        translationBuilder.add("volucraft.yacl.option.integer.cubeWithItemAlpha", "Cube With Item Alpha");
        translationBuilder.add("volucraft.yacl.option.integer.cubeWithItemAlpha.desc", "Determines the translucency of a 3D slot when it has an item.");
        translationBuilder.add("volucraft.yacl.option.integer.cubeHighlightAlpha", "Cube Highlight Alpha");
        translationBuilder.add("volucraft.yacl.option.integer.cubeHighlightAlpha.desc", "Determines the translucency of the highlight of the selected slot.");
    }
}
