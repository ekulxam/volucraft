package survivalblock.volucraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.common.Volucraft;

@Mixin(FabricDataGenerator.class)
public class FabricDataGeneratorMixin {

    @ModifyExpressionValue(method = "createBuiltinResourcePack", at = @At(value = "CONSTANT", args = "stringValue=resourcepacks"))
    private String packDataInstead(String original) {
        return Volucraft.datapacking ? "datapacks" : original;
    }
}
