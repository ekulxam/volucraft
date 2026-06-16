package survivalblock.volucraft.common.init;

import net.minecraft.world.item.Item;
import survivalblock.atmosphere.registrar.ItemRegistrant;
import survivalblock.volucraft.common.Volucraft;

public final class VolucraftItems {
    private VolucraftItems() {
    }

    private static final ItemRegistrant REGISTRANT = new ItemRegistrant(Volucraft.MOD_ID);

    public static final Item AMALGAMATION_TABLE = REGISTRANT.register(
            VolucraftBlocks.AMALGAMATION_TABLE, new Item.Properties().useBlockDescriptionPrefix()
    );

    public static void init() {
        // NO-OP
    }
}
