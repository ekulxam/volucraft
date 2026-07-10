/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.volucraft.common.recipe.datagen;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipePattern;

import static net.minecraft.world.item.crafting.ShapedRecipePattern.EMPTY_SLOT;

@SuppressWarnings("unused")
public class ShapedAmalgamationRecipeBuilder implements RecipeBuilder {
	private final HolderGetter<Item> items;
	private final RecipeCategory category;
	private final ItemStackTemplate result;
	private final List<List<String>> pattern = new ArrayList<>();
	private final Map<Character, Ingredient> key = new LinkedHashMap<>();
	private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private boolean showNotification = true;

	private ShapedAmalgamationRecipeBuilder(final HolderGetter<Item> items, final RecipeCategory category, final ItemStackTemplate result) {
		this.items = items;
		this.category = category;
		this.result = result;
	}

	private ShapedAmalgamationRecipeBuilder(final HolderGetter<Item> items, final RecipeCategory category, final ItemLike result, final int count) {
		this(items, category, new ItemStackTemplate(result.asItem(), count));
	}

	public static ShapedAmalgamationRecipeBuilder shaped(final HolderGetter<Item> items, final RecipeCategory category, final ItemLike item) {
		return shaped(items, category, item, 1);
	}

	public static ShapedAmalgamationRecipeBuilder shaped(final HolderGetter<Item> items, final RecipeCategory category, final ItemLike item, final int count) {
		return new ShapedAmalgamationRecipeBuilder(items, category, item, count);
	}

	public ShapedAmalgamationRecipeBuilder define(final Character symbol, final TagKey<Item> tag) {
		return this.define(symbol, Ingredient.of(this.items.getOrThrow(tag)));
	}

	@SuppressWarnings("unused")
    public ShapedAmalgamationRecipeBuilder define(final Character symbol, final ItemLike item) {
		return this.define(symbol, Ingredient.of(item));
	}

	public ShapedAmalgamationRecipeBuilder define(final Character symbol, final Ingredient ingredient) {
		if (this.key.containsKey(symbol)) {
			throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
		}

		if (symbol == EMPTY_SLOT) {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		}

		this.key.put(symbol, ingredient);
		return this;
	}

	public ShapedAmalgamationRecipeBuilder pattern(final List<String> grid) {
		if (!this.pattern.isEmpty() && grid.size() != this.pattern.getFirst().size()) {
			throw new IllegalArgumentException("Pattern must be the same height on every line!");
		}

        for (String row : grid) {
            // grid shouldn't be empty
            if (row.length() != grid.getFirst().length()) {
                throw new IllegalArgumentException("Pattern must be the same width on every line!");
            }
        }

		this.pattern.add(grid);
		return this;
	}

	public ShapedAmalgamationRecipeBuilder unlockedBy(final String name, final Criterion<?> criterion) {
		this.advancementBuilder.unlockedBy(name, criterion);
		return this;
	}

    @ApiStatus.Obsolete
	public ShapedAmalgamationRecipeBuilder group(final @Nullable String group) {
        return this;
	}

	public ShapedAmalgamationRecipeBuilder showNotification(final boolean showNotification) {
		this.showNotification = showNotification;
		return this;
	}

	@Override
	public ResourceKey<Recipe<?>> defaultId() {
		return RecipeBuilder.getDefaultRecipeId(this.result);
	}

	@Override
	public void save(final RecipeOutput output, final ResourceKey<Recipe<?>> id) {
        ShapedAmalgamationRecipePattern pattern = ShapedAmalgamationRecipePattern.of(this.key, this.pattern);
		ShapedAmalgamationRecipe recipe = new ShapedAmalgamationRecipe(
			RecipeBuilder.createCraftingCommonInfo(this.showNotification), pattern, this.result
		);
		output.accept(id, recipe, this.advancementBuilder.build(output, id, this.category));
	}
}
