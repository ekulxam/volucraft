package survivalblock.volucraft.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import survivalblock.atmosphere.registrar.Registrant;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.recipe.AmalgamationRecipe;

public final class VolucraftRecipeTypes {
    private VolucraftRecipeTypes() {
    }

    private static final RecipeTypeRegistrant REGISTRANT = new RecipeTypeRegistrant(Volucraft.MOD_ID, BuiltInRegistries.RECIPE_TYPE);

    public static final RecipeType<AmalgamationRecipe> AMALGAMATION = REGISTRANT.register("amalgamation");

    public static class RecipeTypeRegistrant extends Registrant<RecipeType<?>> {

        public RecipeTypeRegistrant(String modId, Registry<RecipeType<?>> registry) {
            super(modId, registry);
        }

        public <T extends Recipe<?>> RecipeType<T> register(String name) {
            return register(name, new RecipeType<T>() {
                @Override
                public String toString() {
                    return name;
                }
            });
        }
    }
}
