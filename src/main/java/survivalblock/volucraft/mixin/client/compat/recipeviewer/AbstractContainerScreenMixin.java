package survivalblock.volucraft.mixin.client.compat.recipeviewer;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.volucraft.client.compat.recipeviewer.ScreenWithCubes;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;

import static survivalblock.volucraft.client.render.screen.AmalgamationScreen.PICTURE_IN_PICTURE_SCALE;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @Shadow
    protected int leftPos;

    @Shadow
    protected int topPos;

    @Shadow
    @Nullable
    protected Slot hoveredSlot;

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

    // see the other AbstractContainerScreenMixin
    @ModifyExpressionValue(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isActive()Z")))
    private boolean noRender(boolean original, @Local(argsOnly = true, name = "slot") Slot slot, @Cancellable CallbackInfo ci) {
        if ((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen) {
            if (slot.index >= 1 && slot.index <= Volucraft.SLOTS) {
                ci.cancel();
            }
        }
        return original;
    }

    @Inject(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
    private void noRender2DHightlightIfAmal(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen)) {
            return;
        }
        if (this.hoveredSlot == null) {
            return;
        }
        if (this.hoveredSlot.index >= 1 && this.hoveredSlot.index <= Volucraft.SLOTS) {
            ci.cancel();
        }
    }

    @Inject(method = "getHoveredSlot", at = @At("HEAD"), cancellable = true)
    private void get3DSlotInstead(double x, double y, CallbackInfoReturnable<Slot> cir) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return;
        }
        Vector2f rot = ((ScreenWithCubes) screen).volucraft$getRotation();
        final Quaternionfc rotation = new Quaternionf().rotateX(rot.y).rotateY(-rot.x);
        int maybe3D = AmalgamationScreen.getHovered3DSlot(x, y, PICTURE_IN_PICTURE_SCALE, rotation, this.leftPos + ((RecipeViewMenu) this.menu).guiOffsetLeft(), this.topPos + ((RecipeViewMenu) this.menu).guiOffsetTop(0) + 16, Minecraft.getInstance().getWindow().getGuiScale(), 1, null);
        if (maybe3D >= 0) {
            cir.setReturnValue(this.menu.getSlot(maybe3D + 1));
        }
    }

    @ModifyReturnValue(method = "getHoveredSlot", at = @At("RETURN"))
    private Slot noReturning3DSlotOtherwise(Slot original) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen)) {
            return original;
        }
        if (original == null) {
            return null;
        }
        return original.index > 0 && original.index <= Volucraft.SLOTS ? null : original;
    }
}
