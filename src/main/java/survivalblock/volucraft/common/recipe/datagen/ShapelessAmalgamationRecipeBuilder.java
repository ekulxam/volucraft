package survivalblock.volucraft.common.recipe.datagen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;

public class ShapelessAmalgamationRecipeBuilder extends AmalgamationRecipeBuilder {
    protected final List<Ingredient> ingredients = new ArrayList<>();

    protected ShapelessAmalgamationRecipeBuilder(final HolderGetter<Item> items, final RecipeCategory category, final ItemStackTemplate result) {
        super(items, category, result);
    }

    public static ShapelessAmalgamationRecipeBuilder shapeless(final HolderGetter<Item> items, final RecipeCategory category, final ItemStackTemplate result) {
        return new ShapelessAmalgamationRecipeBuilder(items, category, result);
    }

    public static ShapelessAmalgamationRecipeBuilder shapeless(final HolderGetter<Item> items, final RecipeCategory category, final ItemLike item) {
        return shapeless(items, category, item, 1);
    }

    public static ShapelessAmalgamationRecipeBuilder shapeless(final HolderGetter<Item> items, final RecipeCategory category, final ItemLike item, final int count) {
        return new ShapelessAmalgamationRecipeBuilder(items, category, new ItemStackTemplate(item.asItem(), count));
    }

    public ShapelessAmalgamationRecipeBuilder requires(final TagKey<Item> tag) {
        return this.requires(Ingredient.of(this.items.getOrThrow(tag)));
    }

    public ShapelessAmalgamationRecipeBuilder requires(final ItemLike item) {
        return this.requires((ItemLike)item, 1);
    }

    public ShapelessAmalgamationRecipeBuilder requires(final ItemLike item, final int count) {
        for(int i = 0; i < count; ++i) {
            this.requires(Ingredient.of(item));
        }

        return this;
    }

    public ShapelessAmalgamationRecipeBuilder requires(final Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public ShapelessAmalgamationRecipeBuilder requires(final Ingredient ingredient, final int count) {
        for(int i = 0; i < count; ++i) {
            this.ingredients.add(ingredient);
        }

        return this;
    }

    @Override
    public ShapelessAmalgamationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        super.unlockedBy(name, criterion);
        return this;
    }

    @Override
    @ApiStatus.Obsolete
    public ShapelessAmalgamationRecipeBuilder group(final @Nullable String group) {
        super.group(group);
        return this;
    }

    @Override
    public ShapelessAmalgamationRecipeBuilder showNotification(final boolean showNotification) {
        super.showNotification(showNotification);
        return this;
    }

    @Override
    public void save(final RecipeOutput output, final ResourceKey<Recipe<?>> id) {
        ShapelessAmalgamationRecipe recipe = new ShapelessAmalgamationRecipe(
                RecipeBuilder.createCraftingCommonInfo(this.showNotification),
                this.result,
                this.ingredients
        );
        output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
    }
}
