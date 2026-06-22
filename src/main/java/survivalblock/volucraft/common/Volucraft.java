package survivalblock.volucraft.common;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.init.VolucraftItems;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.display.ShapedAmalgamationRecipeDisplay;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;

import java.util.function.Supplier;

public class Volucraft implements ModInitializer {
	public static final String MOD_ID = "volucraft";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int SIDE_LENGTH = 3;
    public static final int SLOTS = SIDE_LENGTH * SIDE_LENGTH * SIDE_LENGTH;

    public static final Identifier ALL_TRANSLUCENT_SLOTS_PACK = Volucraft.id("all_translucent_slots");
    public static final Identifier MORE_TRANSLUCENT_SLOTS_PACK = Volucraft.id("more_translucent_slots");
    public static final Identifier EXAMPLE_RECIPES_PACK = Volucraft.id("example_recipes");

    public static boolean datapacking = false;

    @SuppressWarnings("PointlessBooleanExpression")
    public static boolean debugSlotSelector = false && FabricLoader.getInstance().isDevelopmentEnvironment();

	@Override
	public void onInitialize() {
        VolucraftRecipeTypes.init();
        VolucraftBlocks.init();
        VolucraftItems.init();
        VolucraftMenuTypes.init();
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Volucraft.id("amalgamation"), ShapedAmalgamationRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY, Volucraft.id("amalgamation"), ShapedAmalgamationRecipeDisplay.TYPE);
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> {
            ResourceLoader.registerBuiltinPack(ALL_TRANSLUCENT_SLOTS_PACK, modContainer, Component.translatable("resourcePack.volucraft.all_translucent_slots.name"), PackActivationType.NORMAL);
            ResourceLoader.registerBuiltinPack(MORE_TRANSLUCENT_SLOTS_PACK, modContainer, Component.translatable("resourcePack.volucraft.more_translucent_slots.name"), PackActivationType.NORMAL);
            Volucraft.wrapDatapack(() ->
                    ResourceLoader.registerBuiltinPack(EXAMPLE_RECIPES_PACK, modContainer, Component.translatable("dataPack.volucraft.example_recipes.name"), PackActivationType.NORMAL)
            );
        });
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

    public static <T> T wrapDatapack(Supplier<T> supplier) {
        datapacking = true;
        T obj = supplier.get();
        datapacking = false;
        return obj;
    }
}