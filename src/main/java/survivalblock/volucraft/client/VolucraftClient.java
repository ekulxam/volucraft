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
package survivalblock.volucraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import survivalblock.volucraft.client.compat.config.VolucraftClientConfig;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;

public class VolucraftClient implements ClientModInitializer {
    public static final ModelLayerLocation CUBE = new ModelLayerLocation(Volucraft.id("cube"), "main");

    @SuppressWarnings("PointlessBooleanExpression")
    public static boolean debugSlotSelector = true && FabricLoader.getInstance().isDevelopmentEnvironment();

    @Override
    public void onInitializeClient() {
        VolucraftClientConfig.init();
        ModelLayerRegistry.registerModelLayer(CUBE, CubeModel::createBodyLayer);
        PictureInPictureRendererRegistry.register(CubeOfSlotsRenderer::new);
        MenuScreens.register(VolucraftMenuTypes.AMALGAMATING, AmalgamationScreen::new);
    }
}
