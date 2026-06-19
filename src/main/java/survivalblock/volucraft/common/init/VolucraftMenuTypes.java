package survivalblock.volucraft.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import survivalblock.atmosphere.registrar.Registrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;

public final class VolucraftMenuTypes {
    private VolucraftMenuTypes() {
    }

    private static final MenuTypeRegistrant REGISTRANT = new MenuTypeRegistrant(Volucraft.MOD_ID, BuiltInRegistries.MENU);

    @SuppressWarnings("NullableProblems")
    public static final MenuType<AmalgamationMenu> AMALGAMATING = REGISTRANT.register("amalgamating", (MenuType.MenuSupplier<AmalgamationMenu>) AmalgamationMenu::new);

    public static void init() {
        // NO-OP
    }

    public static class MenuTypeRegistrant extends Registrant<MenuType<?>> {

        public MenuTypeRegistrant(String modId, Registry<MenuType<?>> registry) {
            super(modId, registry);
        }

        public <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> factory) {
            return this.register(name, new MenuType<>(factory, FeatureFlags.VANILLA_SET));
        }
    }
}
