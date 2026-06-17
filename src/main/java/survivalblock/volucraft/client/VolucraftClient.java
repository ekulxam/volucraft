package survivalblock.volucraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;

public class VolucraftClient implements ClientModInitializer {

    public static final ModelLayerLocation CUBE = new ModelLayerLocation(Volucraft.id("cube"), "main");

    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(CUBE, CubeModel::createBodyLayer);
        PictureInPictureRendererRegistry.register(CubeOfSlotsRenderer::new);
        MenuScreens.register(VolucraftMenuTypes.AMALGAMATING, AmalgamationScreen::new);
    }
}
