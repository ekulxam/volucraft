package survivalblock.volucraft.mixin.extrude;

import com.google.common.reflect.Reflection;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import survivalblock.volucraft.common.init.VolucraftRegistries;

@Mixin(BuiltInRegistries.class)
public class BuiltinRegistriesMixin {
    static {
        Reflection.initialize(VolucraftRegistries.class);
    }
}
