package survivalblock.volucraft.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import survivalblock.volucraft.common.init.VolucraftBlocks;

import java.util.concurrent.CompletableFuture;

public class VolucraftLootTableGenerator extends FabricBlockLootSubProvider {
    protected VolucraftLootTableGenerator(FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(packOutput, registriesFuture);
    }

    @Override
    public void generate() {
        this.dropSelf(VolucraftBlocks.AMALGAMATION_TABLE);
    }
}
