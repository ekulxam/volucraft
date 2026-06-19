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
        translationBuilder.add("resourcePack.volucraft.all_translucent_slots.name", "Volucraft: All Translucent Slots");
        translationBuilder.add("resourcePack.volucraft.more_translucent_slots.name", "Volucraft: More Translucent Slots");

        translationBuilder.add("volucraft.yacl.category.main", "Volucraft Config (Powered by YACL)");
        translationBuilder.add("volucraft.yacl.category.main.tooltip", "Config");
        translationBuilder.add("volucraft.yacl.group.client", "Client");
        translationBuilder.add("volucraft.yacl.option.boolean.cubeBackgroundColor", "Cube Background Color");
        translationBuilder.add("volucraft.yacl.option.boolean.cubeBackgroundColor.desc", "Determines the color of the background of the cube area in the Amalgamation Table screen. Vanilla uses #8b8b8b for slots.");
        translationBuilder.add("volucraft.yacl.option.boolean.displayARGB", "Display ARGB");
        translationBuilder.add("volucraft.yacl.option.boolean.displayARGB.desc", "Determines whether the Cube Background Color option is displayed in ARGB or RGBA.");
    }
}
