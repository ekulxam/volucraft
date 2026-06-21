package survivalblock.volucraft.client.compat.recipeviewer;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;

public class VolucraftClientRRVCompat implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        //noinspection CodeBlock2Expr
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(VolucraftRecipeTypes.AMALGAMATION).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof ShapedAmalgamationRecipe recipe)) {
                    return;
                }
                recipeList.add(new AmalgamationClientRecipe(recipeHolder.id().identifier(), recipe.getResult(), recipe.getIngredients()));
            });
        });
    }
}
