package survivalblock.volucraft.mixin.client.compat.recipeviewer;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.compat.recipeviewer.ScreenWithCubes;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @ModifyReturnValue(method = "mouseDragged", at = @At("RETURN"))
    private boolean rotateInRecipeView(boolean original, MouseButtonEvent event, double dx, double dy) {
        //noinspection rawtypes
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return original;
        }
        float sensitivity = 0.05F;
        ((ScreenWithCubes) screen).volucraft$getRotation().add((float) dx * sensitivity, (float) dy * sensitivity);
        return original;
    }
}
