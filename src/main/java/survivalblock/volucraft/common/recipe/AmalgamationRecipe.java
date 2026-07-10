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
package survivalblock.volucraft.common.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;

/**
 * @see CraftingRecipe
 */
public interface AmalgamationRecipe extends Recipe<AmalgamationInput> {
	@Override
	default RecipeType<AmalgamationRecipe> getType() {
		return VolucraftRecipeTypes.AMALGAMATION;
	}

	@Override
    RecipeSerializer<? extends AmalgamationRecipe> getSerializer();

	default NonNullList<ItemStack> getRemainingItems(final AmalgamationInput input) {
		return defaultCraftingReminder(input);
	}

	static NonNullList<ItemStack> defaultCraftingReminder(final AmalgamationInput input) {
		NonNullList<ItemStack> result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for (int slot = 0; slot < result.size(); slot++) {
			Item item = input.getItem(slot).getItem();
			ItemStackTemplate remainder = item.getCraftingRemainder();
			result.set(slot, remainder != null ? remainder.create() : ItemStack.EMPTY);
		}

		return result;
	}

	@Override
	default RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

    /*
	record CraftingBookInfo(CraftingBookCategory category, String group) implements Recipe.BookInfo<CraftingBookCategory> {
		public static final MapCodec<AmalgamationRecipe.CraftingBookInfo> MAP_CODEC = Recipe.BookInfo.mapCodec(
			CraftingBookCategory.CODEC, CraftingBookCategory.MISC, AmalgamationRecipe.CraftingBookInfo::new
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, AmalgamationRecipe.CraftingBookInfo> STREAM_CODEC = Recipe.BookInfo.streamCodec(
			CraftingBookCategory.STREAM_CODEC, AmalgamationRecipe.CraftingBookInfo::new
		);
	}
     */
}
