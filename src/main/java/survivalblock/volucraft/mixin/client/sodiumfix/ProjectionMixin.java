package survivalblock.volucraft.mixin.client.sodiumfix;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.ProjectionType;
import net.minecraft.client.renderer.Projection;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(Projection.class)
public class ProjectionMixin {

    @Definition(id = "ORTHOGRAPHIC", field = "Lcom/mojang/blaze3d/ProjectionType;ORTHOGRAPHIC:Lcom/mojang/blaze3d/ProjectionType;")
    @Expression("? != ORTHOGRAPHIC")
    @WrapOperation(method = "setupOrtho", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean checkVolucraftToo(Object left, Object right, Operation<Boolean> original) {
        return original.call(left, right) && original.call(left, ProjectionType.VOLUCRAFT_ORTHOGRAPHIC);
    }
}
