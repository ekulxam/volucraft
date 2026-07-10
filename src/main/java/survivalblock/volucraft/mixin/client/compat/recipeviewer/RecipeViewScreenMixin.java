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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.compat.recipeviewer.ScreenWithCubes;
import survivalblock.volucraft.client.render.CubeModel;

@SuppressWarnings("NullableProblems")
@Mixin(RecipeViewScreen.class)
public abstract class RecipeViewScreenMixin extends AbstractContainerScreen<RecipeViewMenu> implements ScreenWithCubes {

    @Shadow
    @Final
    private long timestamp;

    @Unique
    private CubeModel volucraft$cubeModel = null;
    @Unique
    private CubeModel volucraft$cubeModelWithItem = null;
    @Unique
    private final Vector2f volucraft$rotation = new Vector2f();

    public RecipeViewScreenMixin(RecipeViewMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCubes(CallbackInfo ci) {
        this.volucraft$cubeModel = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE));
        this.volucraft$cubeModelWithItem = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE), RenderTypes::entityTranslucentEmissive);
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
    public CubeModel volucraft$getCubeModelWithItem() {
        return this.volucraft$cubeModelWithItem;
    }

    @Override
    public long volucraft$calculateTimeOpen() {
        return this.minecraft.player.level().getGameTime() - this.timestamp;
    }

    @Override
    public Vector2f volucraft$getRotation() {
        return this.volucraft$rotation;
    }
}
