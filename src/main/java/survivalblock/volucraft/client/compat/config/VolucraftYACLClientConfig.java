package survivalblock.volucraft.client.compat.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
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

    public static final OptionFlag USE_ARGB = _ -> { };

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
                                        .name(Component.translatable("volucraft.yacl.option.color.cubeBackgroundColor"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.color.cubeBackgroundColor.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().cubeBackgroundColor, () -> VolucraftYACLClientConfig.HANDLER.instance().cubeBackgroundColor, newVal -> VolucraftYACLClientConfig.HANDLER.instance().cubeBackgroundColor = newVal)
                                        .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                                        .flag(USE_ARGB)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("volucraft.yacl.option.integer.cubeAlpha"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.integer.cubeAlpha.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().cubeAlpha, () -> VolucraftYACLClientConfig.HANDLER.instance().cubeAlpha, newVal -> VolucraftYACLClientConfig.HANDLER.instance().cubeAlpha = newVal)
                                        .controller(option -> IntegerSliderControllerBuilder.create(option).range(0, 255).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("volucraft.yacl.option.integer.cubeWithItemAlpha"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.integer.cubeWithItemAlpha.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().cubeWithItemAlpha, () -> VolucraftYACLClientConfig.HANDLER.instance().cubeWithItemAlpha, newVal -> VolucraftYACLClientConfig.HANDLER.instance().cubeWithItemAlpha = newVal)
                                        .controller(option -> IntegerSliderControllerBuilder.create(option).range(0, 255).step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Component.translatable("volucraft.yacl.option.integer.cubeHighlightAlpha"))
                                        .description(OptionDescription.of(Component.translatable("volucraft.yacl.option.integer.cubeHighlightAlpha.desc")))
                                        .binding(VolucraftYACLClientConfig.HANDLER.defaults().cubeHighlightAlpha, () -> VolucraftYACLClientConfig.HANDLER.instance().cubeHighlightAlpha, newVal -> VolucraftYACLClientConfig.HANDLER.instance().cubeHighlightAlpha = newVal)
                                        .controller(option -> IntegerSliderControllerBuilder.create(option).range(0, 255).step(1))
                                        .build())
                                .build())
                        .build())
                .save(VolucraftYACLClientConfig.HANDLER::save)
                .build()
                .generateScreen(parent);
    }

    static VolucraftYACLClientConfig getInstance() {
        VolucraftYACLClientConfig.HANDLER.load();
        return VolucraftYACLClientConfig.HANDLER.instance();
    }

    @Override
    public int getCubeBackgroundColor() {
        return this.cubeBackgroundColor.getRGB();
    }

    @Override
    public int getCubeAlpha() {
        return this.cubeAlpha;
    }

    @Override
    public int getCubeWithItemAlpha() {
        return this.cubeWithItemAlpha;
    }

    @Override
    public int getCubeHighlightAlpha() {
        return this.cubeHighlightAlpha;
    }

    @SerialEntry
    public boolean displayARGB = true;
    @SerialEntry
    public Color cubeBackgroundColor = Color.BLACK;
    @SerialEntry
    public int cubeAlpha = 255;
    @SerialEntry
    public int cubeWithItemAlpha = 127;
    @SerialEntry
    public int cubeHighlightAlpha = 95;
}