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
