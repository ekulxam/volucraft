package survivalblock.volucraft.common.recipe.datagen;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public abstract class AmalgamationRecipeBuilder implements RecipeBuilder {
    protected final HolderGetter<Item> items;
    protected final RecipeCategory category;
    protected final ItemStackTemplate result;
    protected final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    protected boolean showNotification = true;

    protected AmalgamationRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        this.items = items;
        this.category = category;
        this.result = result;
    }

    @Override
    public AmalgamationRecipeBuilder unlockedBy(final String name, final Criterion<?> criterion) {
        this.advancementBuilder.unlockedBy(name, criterion);
        return this;
    }

    @Override
    @ApiStatus.Obsolete
    public AmalgamationRecipeBuilder group(final @Nullable String group) {
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AmalgamationRecipeBuilder showNotification(final boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    public void save(RecipeOutput output, Identifier id) {
        RecipeBuilder.super.save(output, id.toString());
    }
}
