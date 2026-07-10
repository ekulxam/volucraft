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
