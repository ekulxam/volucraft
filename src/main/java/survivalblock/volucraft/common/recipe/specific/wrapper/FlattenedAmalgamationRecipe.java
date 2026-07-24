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
package survivalblock.volucraft.common.recipe.specific.wrapper;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import survivalblock.volucraft.common.recipe.AmalgamationInput;

@ApiStatus.Internal
public class FlattenedAmalgamationRecipe extends ExtrudedAmalgamationRecipe {
    public static final MapCodec<FlattenedAmalgamationRecipe> MAP_CODEC = createCodec(FlattenedAmalgamationRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FlattenedAmalgamationRecipe> STREAM_CODEC = createPacketCodec(FlattenedAmalgamationRecipe::new);
    public static final RecipeSerializer<FlattenedAmalgamationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public FlattenedAmalgamationRecipe(final CommonInfo commonInfo, final CraftingRecipe delegate) {
        super(commonInfo, delegate);
    }

    @Override
    public RecipeSerializer<FlattenedAmalgamationRecipe> getSerializer() {
        return SERIALIZER;
    }

    protected PlacementInfo createPlacementInfo() {
        return this.delegate.placementInfo();
    }

    @Override
    public boolean matches(final AmalgamationInput input, final Level level) {
        for (CraftingInput craftingInput : input.asPossibleCraftInputs()) {
            if (this.delegate.matches(craftingInput, level)) {
                return true;
            }
        }
        return false;
    }

    public ItemStack assemble(final AmalgamationInput input) {
        ItemStack stack;
        for (CraftingInput craftingInput : input.asPossibleCraftInputs()) {
            stack = this.delegate.assemble(craftingInput);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return this.delegate.isSpecial();
    }

    /**
     * From what I can tell, this is correct for Basically Shapeless but not for Flattened.
     * By this, I mean that the correct remainders will be given, but it isn't guaranteed
     * that they will end up in the correct locations for Flattened recipes.
     */
    @Override
    public NonNullList<ItemStack> getRemainingItems(AmalgamationInput input) {
        return super.getRemainingItems(input);
    }
}
