package survivalblock.volucraft.mixin.extrude;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {

    @ModifyVariable(method = {"addRecipes", "removeRecipes"}, at = @At("HEAD"), name = "recipes", argsOnly = true)
    private Collection<RecipeHolder<?>> accountForAmalPairs(Collection<RecipeHolder<?>> recipes, @Local(argsOnly = true, name = "player") ServerPlayer player) {
        return this.volucraft$addExtrudedRecipes(recipes, player);
    }

    @Unique
    private Collection<RecipeHolder<?>> volucraft$addExtrudedRecipes(Collection<RecipeHolder<?>> recipes, ServerPlayer player) {
        RecipeManager recipeManager = player.level().getServer().getRecipeManager();
        var pairs = recipeManager.volucraft$getRecipePairs();
        if (pairs.isEmpty()) {
            return recipes;
        }

        var reversed = pairs.inverse();
        List<ResourceKey<Recipe<?>>> additions = new ArrayList<>();
        List<RecipeHolder<?>> modified = new ArrayList<>(recipes);

        for (RecipeHolder<?> recipeHolder : modified) {
            ResourceKey<Recipe<?>> id = recipeHolder.id();

            if (pairs.containsKey(id)) {
                additions.add(pairs.get(id));
            } else if (reversed.containsKey(id)) {
                additions.add(reversed.get(id));
            }
        }

        if (additions.isEmpty()) {
            return recipes;
        }

        for (ResourceKey<Recipe<?>> id : additions) {
            recipeManager.byKey(id).ifPresent(recipeHolder -> {
                if (!modified.contains(recipeHolder)) {
                    modified.add(recipeHolder);
                }
            });
        }

        return modified;
    }
}
