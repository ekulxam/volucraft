package survivalblock.volucraft.common.recipe.specific;

import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

/**
 * @see net.minecraft.world.item.crafting.NormalCraftingRecipe
 */
public abstract class NormalAmalgamationRecipe implements AmalgamationRecipe {
    protected final Recipe.CommonInfo commonInfo;
    private @Nullable PlacementInfo placementInfo;

    protected NormalAmalgamationRecipe(final Recipe.CommonInfo commonInfo) {
        this.commonInfo = commonInfo;
    }

    @Override
    public abstract RecipeSerializer<? extends NormalAmalgamationRecipe> getSerializer();

    @Override
    public String group() {
        return "volumetric";
    }

    @Override
    public final boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    protected abstract PlacementInfo createPlacementInfo();

    @Override
    public final PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = this.createPlacementInfo();
        }

        return this.placementInfo;
    }
}
