package survivalblock.volucraft.common.recipe.extrude;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

public interface ExtrudedRecipes {
    default BiMap<ResourceKey<Recipe<?>>, ResourceKey<Recipe<?>>> volucraft$getRecipePairs() {
        throw new UnsupportedOperationException();
    }
}
