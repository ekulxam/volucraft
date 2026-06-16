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

        translationBuilder.add(VolucraftBlocks.INTERACT_WITH_AMALGAMATION_TABLE_STAT, "Interactions with Amalgamation Table");

        translationBuilder.add("container.volucraft.amalgamating", "Magicking");
    }
}
