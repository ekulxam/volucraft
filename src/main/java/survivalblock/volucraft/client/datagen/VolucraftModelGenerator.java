package survivalblock.volucraft.client.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import survivalblock.volucraft.common.init.VolucraftBlocks;

public class VolucraftModelGenerator extends FabricModelProvider {
    public VolucraftModelGenerator(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        // WHAT IS THIS
        blockModelGenerators.blockStateOutput.accept(
                BlockModelGenerators.createSimpleBlock(
                        VolucraftBlocks.AMALGAMATION_TABLE,
                        BlockModelGenerators.plainVariant(
                                ModelTemplates.CUBE_ALL.create(
                                        VolucraftBlocks.AMALGAMATION_TABLE,
                                        new TextureMapping()
                                                .put(
                                                    TextureSlot.ALL,
                                                    TextureMapping.getBlockTexture(
                                                            Blocks.CRAFTING_TABLE,
                                                            "_top"
                                                    )
                                                ),
                                        blockModelGenerators.modelOutput
                                )
                        )
                )
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
    }
}
