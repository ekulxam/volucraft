package survivalblock.volucraft.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import survivalblock.volucraft.common.recipe.datagen.ShapedAmalgamationRecipeBuilder;
import survivalblock.volucraft.mixin.RecipeProviderAccessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VolucraftRecipeGenerator extends FabricRecipeProvider {
    public VolucraftRecipeGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
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
            }
        };
    }

    @Override
    public String getName() {
        return "Recipes";
    }
}
