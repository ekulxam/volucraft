package survivalblock.volucraft.common.compat.recipeviewer;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;

public class VolucraftRRVCompat implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        ServerRecipeManager.INSTANCE.synchronizeRecipeType(ShapedAmalgamationRecipe.SERIALIZER, VolucraftRecipeTypes.AMALGAMATION);
    }
}
