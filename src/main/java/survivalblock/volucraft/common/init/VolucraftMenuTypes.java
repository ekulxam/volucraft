package survivalblock.volucraft.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import survivalblock.atmosphere.registrar.Registrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public final class VolucraftMenuTypes {
    private VolucraftMenuTypes() {
    }

    private static final Registrant<MenuType<?>> REGISTRANT = new Registrant<>(Volucraft.MOD_ID, BuiltInRegistries.MENU);

    public static final MenuType<AmalgamationMenu> AMALGAMATING = REGISTRANT.register("amalgamating", new MenuType<>(AmalgamationMenu::new, FeatureFlags.VANILLA_SET));

    public static void init() {
        // NO-OP
    }
}
