package survivalblock.volucraft.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import survivalblock.volucraft.client.screen.AmalgamationScreen;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public class VolucraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MenuScreens.register(VolucraftMenuTypes.AMALGAMATING, AmalgamationScreen::new);
    }
}
