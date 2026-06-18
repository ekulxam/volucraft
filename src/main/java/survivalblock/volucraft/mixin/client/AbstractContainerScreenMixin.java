package survivalblock.volucraft.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;

@Debug(export = true)
@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Shadow
    @Nullable
    protected Slot hoveredSlot;

    @Shadow
    @Final
    protected AbstractContainerMenu menu;

    @ModifyExpressionValue(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isActive()Z")))
    private boolean noRender(boolean original, @Local(argsOnly = true, name = "slot") Slot slot, @Cancellable CallbackInfo ci) {
        if ((AbstractContainerScreen) (Object) this instanceof AmalgamationScreen) {
            if (slot.index >= 1 && slot.index <= Volucraft.SLOTS + 1) {
                ci.cancel();
            }
        }
        return original;
    }

    @Inject(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
    private void noRender2DHightlightIfAmal(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!((AbstractContainerScreen) (Object) this instanceof AmalgamationScreen)) {
            return;
        }
        if (this.hoveredSlot == null) {
            return;
        }
        if (this.hoveredSlot.index >= 1 && this.hoveredSlot.index <= Volucraft.SLOTS + 1) {
            ci.cancel();
        }
    }

    @Inject(method = "getHoveredSlot", at = @At("HEAD"), cancellable = true)
    private void get3DSlotInstead(double x, double y, CallbackInfoReturnable<Slot> cir) {
        if (!((AbstractContainerScreen) (Object) this instanceof AmalgamationScreen amal)) {
            return;
        }
        int maybe3D = amal.getHovered3DSlot(x, y);
        if (maybe3D >= 0) {
            cir.setReturnValue(this.menu.getSlot(maybe3D + 1));
        }
    }

    @ModifyReturnValue(method = "getHoveredSlot", at = @At("RETURN"))
    private Slot noReturning3DSlotOtherwise(Slot original) {
        if (!((AbstractContainerScreen) (Object) this instanceof AmalgamationScreen)) {
            return original;
        }
        if (original == null) {
            return null;
        }
        return original.index > 0 && original.index < Volucraft.SLOTS ? null : original;
    }
}
