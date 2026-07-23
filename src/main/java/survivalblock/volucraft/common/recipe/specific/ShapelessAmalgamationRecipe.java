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
package survivalblock.volucraft.common.recipe.specific;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.impl.recipe.ingredient.ShapelessMatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftItems;
import survivalblock.volucraft.common.recipe.AmalgamationInput;
import survivalblock.volucraft.common.recipe.display.ShapelessAmalgamationRecipeDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @see net.minecraft.world.item.crafting.ShapelessRecipe
 * @see net.fabricmc.fabric.mixin.recipe.ingredient.ShapelessRecipeMixin
 */
@SuppressWarnings("UnstableApiUsage")
public class ShapelessAmalgamationRecipe extends NormalAmalgamationRecipe {
    public static final MapCodec<ShapelessAmalgamationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                    Ingredient.CODEC.listOf(1, Volucraft.SLOTS).fieldOf("ingredients").forGetter(o -> o.ingredients)
            ).apply(instance, ShapelessAmalgamationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessAmalgamationRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, o -> o.commonInfo,
            ItemStackTemplate.STREAM_CODEC, o -> o.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
            ShapelessAmalgamationRecipe::new
    );

    public static final RecipeSerializer<ShapelessAmalgamationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;
    private final boolean requiresFAPITesting;

    public ShapelessAmalgamationRecipe(final CommonInfo commonInfo, final ItemStackTemplate result, final List<Ingredient> ingredients) {
        super(commonInfo);
        this.result = result;
        this.ingredients = ingredients;

        boolean temporary = false;
        for (Ingredient ingredient : ingredients) {
            if (ingredient.requiresTesting()) {
                temporary = true;
                break;
            }
        }
        this.requiresFAPITesting = temporary;
    }

    @Override
    public RecipeSerializer<ShapelessAmalgamationRecipe> getSerializer() {
        return SERIALIZER;
    }

    public ItemStackTemplate getResult() {
        return this.result;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.create(this.ingredients);
    }

    @Override
    public boolean matches(final AmalgamationInput input, final Level level) {
        if (this.requiresFAPITesting) {
            List<ItemStack> present = new ArrayList<>(input.ingredientCount());

            for (ItemStack stack : input.items()) {
                if (stack.isEmpty()) {
                    continue;
                }
                present.add(stack);
            }

            return ShapelessMatch.isMatch(present, this.ingredients);
        }

        if (input.ingredientCount() != this.ingredients.size()) {
            return false;
        }

        return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(input.getItem(0)) : input.stackedContents().canCraft(this, null);
    }

    public ItemStack assemble(final AmalgamationInput input) {
        return this.result.create();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapelessAmalgamationRecipeDisplay(
                this.ingredients.stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(VolucraftItems.AMALGAMATION_TABLE)
        ));
    }
}
