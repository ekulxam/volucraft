package survivalblock.volucraft.client.compat.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.impl.controller.ColorControllerBuilderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import survivalblock.volucraft.common.Volucraft;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class VolucraftYACLClientConfig implements VolucraftClientConfig {

    public static final ConfigClassHandler<VolucraftYACLClientConfig> HANDLER = ConfigClassHandler.createBuilder(VolucraftYACLClientConfig.class)
            .id(Volucraft.id("volucraft"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("volucraft.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    public static final OptionFlag USE_ARGB = minecraft -> {
    };

    @Override
    public Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("volucraft.yacl.category.main"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("volucraft.yacl.category.main"))
                        .tooltip(Component.translatable("volucraft.yacl.category.main.tooltip"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.translatable("volucraft.yacl.group.client"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.translatable("volucraft.yacl.option.boolean.displayARGB"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.boolean.displayARGB.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().displayARGB, () -> VolucraftYACLClientConfig.HANDLER.instance().displayARGB, newVal -> VolucraftYACLClientConfig.HANDLER.instance().displayARGB = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Component.translatable("volucraft.yacl.option.boolean.cubeBackgroundColor"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.boolean.cubeBackgroundColor.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().cubeBackgroundColor, () -> VolucraftYACLClientConfig.HANDLER.instance().cubeBackgroundColor, newVal -> VolucraftYACLClientConfig.HANDLER.instance().cubeBackgroundColor = newVal)
                                        .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                                        .flag(USE_ARGB)
                                        .build())
                                .build())
                        .build())
                .save(VolucraftYACLClientConfig.HANDLER::save)
                .build()
                .generateScreen(parent);
    }

    static VolucraftYACLClientConfig getInstance() {
        return VolucraftYACLClientConfig.HANDLER.instance();
    }

    @Override
    public int getCubeBackgroundColor() {
        return this.cubeBackgroundColor.getRGB();
    }

    @SerialEntry
    public boolean displayARGB = true;
    @SerialEntry
    public Color cubeBackgroundColor = Color.BLACK;
}