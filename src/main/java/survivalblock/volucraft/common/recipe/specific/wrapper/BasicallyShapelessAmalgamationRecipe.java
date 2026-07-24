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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import survivalblock.volucraft.common.recipe.AmalgamationInput;

@ApiStatus.Internal
public class BasicallyShapelessAmalgamationRecipe extends ExtrudedAmalgamationRecipe {
    public static final MapCodec<BasicallyShapelessAmalgamationRecipe> MAP_CODEC = createCodec(BasicallyShapelessAmalgamationRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasicallyShapelessAmalgamationRecipe> STREAM_CODEC = createPacketCodec(BasicallyShapelessAmalgamationRecipe::new);
    public static final RecipeSerializer<BasicallyShapelessAmalgamationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public BasicallyShapelessAmalgamationRecipe(final Recipe.CommonInfo commonInfo, final CraftingRecipe delegate) {
        super(commonInfo, delegate);
    }

    @Override
    public RecipeSerializer<BasicallyShapelessAmalgamationRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean matches(final AmalgamationInput input, final Level level) {
        return this.delegate.matches(input.asBasicallyShapelessCraftInput(), level);
    }

    public ItemStack assemble(final AmalgamationInput input) {
        return this.delegate.assemble(input.asBasicallyShapelessCraftInput());
    }
}
