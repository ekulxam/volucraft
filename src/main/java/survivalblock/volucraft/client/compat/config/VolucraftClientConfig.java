package survivalblock.volucraft.client.compat.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;

@Environment(EnvType.CLIENT)
public interface VolucraftClientConfig {

    VolucraftClientConfig INSTANCE = getInstance();

    @Nullable
    default Screen create(Screen parent) {
        return null;
    }

    default int getCubeBackgroundColor() {
        return 0xFF000000;
    }

    default int getCubeAlpha() {
        return 255;
    }

    default int getCubeWithItemAlpha() {
        return 127;
    }

    default int getCubeHighlightAlpha() {
        return 95;
    }

    private static VolucraftClientConfig getInstance() {
        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) {
            return VolucraftYACLClientConfig.getInstance();
        }
        Volucraft.LOGGER.warn("YACL is not installed, so Volucraft's YACL config will not be accessible!");
        return new VolucraftClientConfig() {};
    }

    static void init() {
        // NO-OP
    }
}
