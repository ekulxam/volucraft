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
