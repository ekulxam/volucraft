package survivalblock.volucraft.mixin.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.compat.recipeviewer.AmalgamationClientRecipeType;

@Mixin(RecipeViewMenu.class)
public abstract class RecipeViewMenuMixin {
    @Shadow
    public abstract ReliableClientRecipeType getClientRecipeType();

    @ModifyReturnValue(method = "calculateRecipesPerPage", at = @At("RETURN"))
    private int limitToOneAmalgamationRecipe(int original) {
        if (this.getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE) {
            return Math.min(original, 1);
        }
        return original;
    }
}
