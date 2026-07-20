/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.volucraft.common.compat.recipeviewer.AmalgamationClientRecipeType;
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
    private boolean rotateInRecipeViewRRV(boolean original, MouseButtonEvent event, double dx, double dy) {
        //noinspection rawtypes
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return original;
        }
        if (!volucraft$isAmalgamation(screen)) {
            return original;
        }
        float sensitivity = 0.05F;
        ((ScreenWithCubes) screen).volucraft$getRotation().add((float) dx * sensitivity, (float) dy * sensitivity);
        return original;
    }

    // see the other AbstractContainerScreenMixin
    @ModifyExpressionValue(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isActive()Z")))
    private boolean noRenderRRV(boolean original, @Local(argsOnly = true, name = "slot") Slot slot, @Cancellable CallbackInfo ci) {
        if ((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen && volucraft$isAmalgamation(screen)) {
            if (slot.index >= 1 && slot.index <= Volucraft.SLOTS) {
                ci.cancel();
            }
        }
        return original;
    }

    @Inject(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
    private void noRender2DHightlightIfAmalRRV(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return;
        }
        if (!volucraft$isAmalgamation(screen)) {
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
    private void get3DSlotInsteadRRV(double x, double y, CallbackInfoReturnable<Slot> cir) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return;
        }
        if (!volucraft$isAmalgamation(screen)) {
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
    private Slot noReturning3DSlotOtherwiseRRV(Slot original) {
        if (!((AbstractContainerScreen) (Object) this instanceof RecipeViewScreen screen)) {
            return original;
        }
        if (!volucraft$isAmalgamation(screen)) {
            return original;
        }
        if (original == null) {
            return null;
        }
        return original.index > 0 && original.index <= Volucraft.SLOTS ? null : original;
    }

    @Unique
    private static boolean volucraft$isAmalgamation(RecipeViewScreen screen) {
        return screen.getMenu().getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE;
    }
}
