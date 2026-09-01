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
