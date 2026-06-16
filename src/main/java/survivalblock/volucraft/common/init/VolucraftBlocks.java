package survivalblock.volucraft.common.init;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import survivalblock.atmosphere.registrar.BlockRegistrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.block.AmalgamationTableBlock;

public final class VolucraftBlocks {
    private VolucraftBlocks() {
    }

    private static final BlockRegistrant REGISTRANT = new BlockRegistrant(Volucraft.MOD_ID);

    public static final Block AMALGAMATION_TABLE = REGISTRANT.register(
            "amalgamation_table",
            AmalgamationTableBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(10F, 10F)
                    .sound(SoundType.IRON)
    );

    public static final Identifier INTERACT_WITH_AMALGAMATION_TABLE_STAT = Volucraft.makeCustomStat("interact_with_amalgamation_table", StatFormatter.DEFAULT);

    public static void init() {
        //noinspection CodeBlock2Expr
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .register(output -> {
                    output.insertAfter(AMALGAMATION_TABLE, Blocks.CRAFTING_TABLE);
                });
    }
}
