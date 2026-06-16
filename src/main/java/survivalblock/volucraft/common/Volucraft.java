package survivalblock.volucraft.common;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.init.VolucraftItems;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;

public class Volucraft implements ModInitializer {
	public static final String MOD_ID = "volucraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int SIDE_LENGTH = 3;
    public static final int SLOTS = SIDE_LENGTH * SIDE_LENGTH * SIDE_LENGTH;

	@Override
	public void onInitialize() {
        VolucraftBlocks.init();
        VolucraftItems.init();
        VolucraftMenuTypes.init();
	}

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Identifier makeCustomStat(final String id, final StatFormatter formatter) {
        Identifier location = id(id);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, location);
        Stats.CUSTOM.get(location, formatter);
        return location;
    }
}