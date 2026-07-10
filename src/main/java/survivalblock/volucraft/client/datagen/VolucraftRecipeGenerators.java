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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import survivalblock.volucraft.common.init.VolucraftItems;
import survivalblock.volucraft.common.recipe.datagen.ShapedAmalgamationRecipeBuilder;
import survivalblock.volucraft.mixin.RecipeProviderAccessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VolucraftRecipeGenerators {
    public static class Actual extends FabricRecipeProvider {
        public Actual(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output) {
                @Override
                public void buildRecipes() {
                    shaped(RecipeCategory.MISC, VolucraftItems.AMALGAMATION_TABLE)
                            .pattern("III")
                            .pattern("CRC")
                            .pattern("HCH")
                            .define('C', Blocks.CRAFTING_TABLE)
                            .define('R', ItemTags.SHULKER_BOXES)
                            .define('H', Blocks.HOPPER)
                            .define('I', Items.IRON_INGOT)
                            .unlockedBy(getHasName(Blocks.CRAFTING_TABLE), has(Blocks.CRAFTING_TABLE))
                            .unlockedBy("has_shulker_box", has(ItemTags.SHULKER_BOXES))
                            .unlockedBy(getHasName(Blocks.HOPPER), has(Blocks.HOPPER))
                            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                            .save(this.output);
                }
            };
        }

        @Override
        public String getName() {
            return "Example Recipes";
        }
    }

    public static class Examples extends FabricRecipeProvider {
        public Examples(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output) {
                @Override
                public void buildRecipes() {
                    HolderGetter<Item> items = ((RecipeProviderAccessor) (Object) this).volucraft$getItems();
                    ShapedAmalgamationRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.ENCHANTED_GOLDEN_APPLE)
                            .pattern(
                                    List.of(
                                            "GGG",
                                            "GGG",
                                            "GGG"
                                    )
                            )
                            .pattern(
                                    List.of(
                                            "GGG",
                                            "GAG",
                                            "GGG"
                                    )
                            )
                            .pattern(
                                    List.of(
                                            "GGG",
                                            "GGG",
                                            "GGG"
                                    )
                            )
                            .define('G', Blocks.GOLD_BLOCK)
                            .define('A', Items.APPLE)
                            .unlockedBy(getHasName(Blocks.GOLD_BLOCK), has(Blocks.GOLD_BLOCK))
                            .unlockedBy(getHasName(Items.APPLE), has(Items.APPLE))
                            .save(this.output);
                    ShapedAmalgamationRecipeBuilder.shaped(items, RecipeCategory.MISC, Items.TRIDENT)
                            .pattern(
                                    List.of(
                                            "V V",
                                            "   ",
                                            "  V"
                                    )
                            )
                            .pattern(
                                    List.of(
                                            "   ",
                                            " / ",
                                            "   "
                                    )
                            )
                            .pattern(
                                    List.of(
                                            "   ",
                                            "   ",
                                            "/  "
                                    )
                            )
                            .define('/', Items.PRISMARINE_SHARD)
                            .define('V', Items.BONE)
                            .unlockedBy(getHasName(Items.PRISMARINE_SHARD), has(Items.PRISMARINE_SHARD))
                            .unlockedBy(getHasName(Items.BONE), has(Items.BONE))
                            .save(this.output);
                    ShapedAmalgamationRecipeBuilder.shaped(items, RecipeCategory.MISC, Blocks.CRYING_OBSIDIAN, 4)
                            .pattern(
                                    List.of(
                                            "OO",
                                            "TB",
                                            "OO"
                                    )
                            )
                            .pattern(
                                    List.of(
                                            "OO",
                                            "BT",
                                            "OO"
                                    )
                            )
                            .define('O', Blocks.OBSIDIAN)
                            .define('B', Items.GHAST_TEAR)
                            .define('T', Items.DRAGON_BREATH)
                            .unlockedBy(getHasName(Blocks.OBSIDIAN), has(Blocks.OBSIDIAN))
                            .unlockedBy(getHasName(Items.GHAST_TEAR), has(Items.GHAST_TEAR))
                            .unlockedBy(getHasName(Items.DRAGON_BREATH), has(Items.DRAGON_BREATH))
                            .save(this.output);
                }
            };
        }

        @Override
        public String getName() {
            return "Example Recipes";
        }
    }
}
