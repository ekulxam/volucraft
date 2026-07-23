package survivalblock.volucraft.mixin.extrude;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipePattern;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.extrude.ExtrudedRecipes;

import java.util.*;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin implements ExtrudedRecipes {
    @Unique
    private final BiMap<ResourceKey<Recipe<?>>, ResourceKey<Recipe<?>>> volucraft$recipePairs = HashBiMap.create();

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At("HEAD"))
    private void resetRecipePairs(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir) {
        this.volucraft$recipePairs.clear();
    }

    @SuppressWarnings("DiscouragedShift")
    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At(value = "INVOKE", target = "Ljava/util/SortedMap;size()I", shift = At.Shift.BEFORE))
    private void translateBasic2DRecipes(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir, @Local(name = "recipes") SortedMap<Identifier, Recipe<?>> recipes, @Share(value = "translated", namespace = "volucraft")LocalRef<Map<CraftingRecipe, AmalgamationRecipe>> localRef) {
        Map<Identifier, AmalgamationRecipe> additions = new HashMap<>();
        Map<CraftingRecipe, AmalgamationRecipe> translated = new HashMap<>();
        for (Map.Entry<Identifier, Recipe<?>> entry : recipes.entrySet()) {
            if (!(entry.getValue() instanceof CraftingRecipe recipe)) {
                continue;
            }

            AmalgamationRecipe amal = null;
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                Optional<ShapedRecipePattern.Data> optional = ((ShapedRecipePatternAccessor) (Object) ((ShapedRecipeAccessor) shapedRecipe).volucraft$getPattern()).volucraft$getData();
                if (optional.isPresent()) {
                    ShapedRecipePattern.Data data = optional.get();
                    try {
                        amal = new ShapedAmalgamationRecipe(
                                ((NormalCraftingRecipeAccessor) shapedRecipe).volucraft$getCommonInfo(),
                                ShapedAmalgamationRecipePattern.of(
                                        data.key(),
                                        List.of(data.pattern())
                                ),
                                ((ShapedRecipeAccessor) shapedRecipe).volucraft$getResult()
                        );
                    } catch (Exception e) {
                        Volucraft.LOGGER.error("Unable to create ShapedAmalgamationRecipe from ShapedRecipe!", e);
                    }
                }
            } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                amal = new ShapelessAmalgamationRecipe(
                        ((NormalCraftingRecipeAccessor) shapelessRecipe).volucraft$getCommonInfo(),
                        ((ShapelessRecipeAccessor) shapelessRecipe).volucraft$getResult(),
                        ((ShapelessRecipeAccessor) shapelessRecipe).volucraft$getIngredients()
                );
            }

            if (amal != null) {
                amal.setTranslatedFrom2D(true);
                additions.put(volucraft$translate(entry.getKey()), amal);
                translated.put(recipe, amal);
            }
        }

        localRef.set(translated);
        recipes.putAll(additions);
    }

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;", at = @At("RETURN"))
    private void addToBiMap(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> cir, @Local(name = "recipeHolders") List<RecipeHolder<?>> recipeHolders, @Share(value = "translated", namespace = "volucraft")LocalRef<Map<CraftingRecipe, AmalgamationRecipe>> localRef) {
        Map<CraftingRecipe, AmalgamationRecipe> translated = localRef.get();
        Map<Recipe<?>, ResourceKey<Recipe<?>>> ids = new HashMap<>();

        for (RecipeHolder<?> recipeHolder : recipeHolders) {
            var recipe = recipeHolder.value();
            if (recipe instanceof CraftingRecipe || recipe instanceof AmalgamationRecipe) {
                ids.put(recipe, recipeHolder.id());
            }
        }

        for (Map.Entry<CraftingRecipe, AmalgamationRecipe> entry : translated.entrySet()) {
            ResourceKey<Recipe<?>> crafting = ids.get(entry.getKey());
            if (crafting == null) {
                continue;
            }

            ResourceKey<Recipe<?>> amalgamation = ids.get(entry.getValue());
            if (amalgamation == null) {
                continue;
            }

            this.volucraft$recipePairs.put(crafting, amalgamation);
        }
    }

    @Unique
    private static Identifier volucraft$translate(Identifier identifier) {
        return identifier.withPath(s -> s + "_volucraft.autoextruded");
    }

    @Override
    public BiMap<ResourceKey<Recipe<?>>, ResourceKey<Recipe<?>>> volucraft$getRecipePairs() {
        return this.volucraft$recipePairs;
    }
}
