package survivalblock.volucraft.mixin.client.sodiumfix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.ProjectionType;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;

@Mixin(PictureInPictureRenderer.class)
public class PictureInPictureRendererMixin {

    @ModifyExpressionValue(method = "prepareTexturesAndProjection", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/ProjectionType;ORTHOGRAPHIC:Lcom/mojang/blaze3d/ProjectionType;", opcode = Opcodes.GETSTATIC))
    private ProjectionType useVolucraftOrthoInstead(ProjectionType original) {
        if ((PictureInPictureRenderer<?>) (Object) this instanceof CubeOfSlotsRenderer) {
            return ProjectionType.VOLUCRAFT_ORTHOGRAPHIC;
        }
        return original;
    }
}
