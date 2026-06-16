package survivalblock.volucraft.common.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import survivalblock.atmosphere.registrar.BlockRegistrant;
import survivalblock.atmosphere.registrar.ItemRegistrant;
import survivalblock.volucraft.common.Volucraft;

public final class VolucraftBlocks {
    private VolucraftBlocks() {
    }

    private static final BlockRegistrant REGISTRANT = new BlockRegistrant(Volucraft.MOD_ID);

    public static final Block AMALGAMATION_TABLE = REGISTRANT.register(
            "amalgamation_table",
            Block::new,
            BlockBehaviour.Properties.of()
    );

    public static void init() {
        // NO-OP
    }
}
