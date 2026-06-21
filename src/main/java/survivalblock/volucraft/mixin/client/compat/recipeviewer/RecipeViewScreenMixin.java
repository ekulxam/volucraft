package survivalblock.volucraft.mixin.client.compat.recipeviewer;

import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
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

    @Unique
    private CubeModel volucraft$cubeModel = null;

    @Unique
    private CubeModel volucraft$cubeModelWithItem = null;

    public RecipeViewScreenMixin(RecipeViewMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCubes(CallbackInfo ci) {
        this.volucraft$cubeModel = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE));
        this.volucraft$cubeModelWithItem = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE), RenderTypes::entityTranslucentEmissive);
    }

    @Override
    public CubeModel volucraft$getCubeModel() {
        return this.volucraft$cubeModel;
    }

    @Override
    public CubeModel volucraft$getCubeModelWithItem() {
        return this.volucraft$cubeModelWithItem;
    }
}
