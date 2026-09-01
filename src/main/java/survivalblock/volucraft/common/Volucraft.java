/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.volucraft.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import survivalblock.volucraft.common.init.*;
import survivalblock.volucraft.common.networking.CancelMultimatchS2CPayload;
import survivalblock.volucraft.common.networking.MultimatchS2CPayload;
import survivalblock.volucraft.common.networking.SelectFromMultimatchC2SPayload;
import survivalblock.volucraft.common.recipe.display.ShapedAmalgamationRecipeDisplay;
import survivalblock.volucraft.common.recipe.display.ShapelessAmalgamationRecipeDisplay;
import survivalblock.volucraft.common.recipe.extrude.ExtrusionFormula;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.wrapper.BasicallyShapelessAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.wrapper.FlattenedAmalgamationRecipe;

import java.util.function.Supplier;

public class Volucraft implements ModInitializer {
	public static final String MOD_ID = "volucraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int SIDE_LENGTH = 3;
    public static final int SLOTS = SIDE_LENGTH * SIDE_LENGTH * SIDE_LENGTH;

    public static final Identifier EXAMPLE_RECIPES_PACK = Volucraft.id("example_recipes");

    public static boolean datapacking = false;

	@Override
	public void onInitialize() {
        VolucraftRecipeTypes.init();
        VolucraftBlocks.init();
        VolucraftItems.init();
        VolucraftMenuTypes.init();
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Volucraft.id("amalgamation"), ShapedAmalgamationRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY, Volucraft.id("amalgamation"), ShapedAmalgamationRecipeDisplay.TYPE);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Volucraft.id("amalgamation_shapeless"), ShapelessAmalgamationRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY, Volucraft.id("amalgamation_shapeless"), ShapelessAmalgamationRecipeDisplay.TYPE);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Volucraft.id("amalgamation_flattened"), FlattenedAmalgamationRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Volucraft.id("amalgamation_basically_shapeless"), BasicallyShapelessAmalgamationRecipe.SERIALIZER);

        ExtrusionFormula.bootstrap();

        registerNetworking();

        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer ->
                wrapDatapack(
                        () -> ResourceLoader.registerBuiltinPack(EXAMPLE_RECIPES_PACK, modContainer, Component.translatable("dataPack.volucraft.example_recipes.name"), PackActivationType.NORMAL)
                )
        );
	}

    private void registerNetworking() {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> s2c = PayloadTypeRegistry.clientboundPlay();
        s2c.register(CancelMultimatchS2CPayload.ID, CancelMultimatchS2CPayload.CODEC);
        s2c.registerLarge(MultimatchS2CPayload.ID, MultimatchS2CPayload.CODEC, 1048576); // 2 ^ 20, probably fine

        PayloadTypeRegistry<RegistryFriendlyByteBuf> c2s = PayloadTypeRegistry.serverboundPlay();
        c2s.register(SelectFromMultimatchC2SPayload.ID, SelectFromMultimatchC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SelectFromMultimatchC2SPayload.ID, SelectFromMultimatchC2SPayload.Receiver.INSTANCE);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Identifier makeCustomStat(final String path, final StatFormatter formatter) {
        Identifier id = id(path);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        Stats.CUSTOM.get(id, formatter);
        return id;
    }

    public static <T> T wrapDatapack(Supplier<T> supplier) {
        datapacking = true;
        T obj = supplier.get();
        datapacking = false;
        return obj;
    }
}