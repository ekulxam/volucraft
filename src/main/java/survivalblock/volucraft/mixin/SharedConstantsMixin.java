package survivalblock.volucraft.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {

    /*
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/SharedConstants;debugFlag(Ljava/lang/String;)Z"))
    private static boolean showRectInDev(String name, Operation<Boolean> original) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment() && name.equals("RENDER_UI_LAYERING_RECTANGLES")) {
            return true;
        }
        return original.call(name);
    }*/
}
