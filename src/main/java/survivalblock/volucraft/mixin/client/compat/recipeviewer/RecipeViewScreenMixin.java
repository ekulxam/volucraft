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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.rendertype.RenderTypes;
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
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.compat.recipeviewer.ScreenWithCubes;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.compat.recipeviewer.AmalgamationClientRecipeType;

import static survivalblock.volucraft.client.render.screen.AmalgamationScreen.PICTURE_IN_PICTURE_SCALE;

@SuppressWarnings("NullableProblems")
@Mixin(RecipeViewScreen.class)
public abstract class RecipeViewScreenMixin extends Screen implements ScreenWithCubes {
    @Shadow
    @Final
    private long timestamp;

    @Shadow
    public abstract RecipeViewMenu getMenu();

    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Shadow
    @Nullable
    protected Slot hoveredSlot;
    @Unique
    private CubeModel volucraft$cubeModel = null;
    @Unique
    private final Vector2f volucraft$rotation = new Vector2f();

    public RecipeViewScreenMixin() {
        super(null);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCubes(CallbackInfo ci) {
        this.volucraft$cubeModel = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE));
    }

    @Inject(method = "checkGui", at = @At("HEAD"))
    private void resetRotation(CallbackInfo ci) {
        this.volucraft$rotation.set(Math.PI / 4, Math.PI / 4);
    }

    @Override
    public CubeModel volucraft$getCubeModel() {
        return this.volucraft$cubeModel;
    }

    @Override
    public long volucraft$calculateTimeOpen() {
        return this.minecraft.player.level().getGameTime() - this.timestamp;
    }

    @Override
    public Vector2f volucraft$getRotation() {
        return this.volucraft$rotation;
    }

    @ModifyReturnValue(method = "getHoveredSlot", at = @At("RETURN"))
    private Slot noReturning3DSlotOtherwiseRRV(Slot original) {
        if (original == null || original instanceof AmalgamationClientRecipeType.RecipeSlotShovedIntoACorner) {
            return null;
        }
        return original;
    }

    @Inject(method = "getHoveredSlot", at = @At("HEAD"), cancellable = true)
    private void get3DSlotInsteadRRV(double x, double y, CallbackInfoReturnable<Slot> cir) {
        RecipeViewMenu menu = this.getMenu();
        if (menu.getClientRecipeType() != AmalgamationClientRecipeType.INSTANCE) {
            return;
        }
        Vector2f rot = this.volucraft$getRotation();
        final Quaternionfc rotation = new Quaternionf().rotateX(rot.y).rotateY(-rot.x);
        int maybe3D = AmalgamationScreen.getHovered3DSlot(x, y, PICTURE_IN_PICTURE_SCALE, rotation, this.leftPos + menu.guiOffsetLeft(), this.topPos + menu.guiOffsetTop(0) + 16, Minecraft.getInstance().getWindow().getGuiScale(), 1, null);
        if (maybe3D >= 0) {
            cir.setReturnValue(menu.getSlot(maybe3D + 1));
        }
    }

    // see the other AbstractContainerScreenMixin
    @ModifyExpressionValue(method = "extractSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;isActive()Z")))
    private boolean noRenderRRV(boolean original, @Local(argsOnly = true, name = "slot") Slot slot, @Cancellable CallbackInfo ci) {
        if (this.getMenu().getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE) {
            if (slot.index >= 1 && slot.index <= Volucraft.SLOTS) {
                ci.cancel();
            }
        }
        return original;
    }

    @Inject(method = {"extractSlotHighlightBack", "extractSlotHighlightFront"}, at = @At("HEAD"), cancellable = true)
    private void noRender2DHightlightIfAmalRRV(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        if (this.getMenu().getClientRecipeType() != AmalgamationClientRecipeType.INSTANCE) {
            return;
        }
        if (this.hoveredSlot == null) {
            return;
        }
        if (this.hoveredSlot.index >= 1 && this.hoveredSlot.index <= Volucraft.SLOTS) {
            ci.cancel();
        }
    }
}
