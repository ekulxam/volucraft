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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import org.jetbrains.annotations.ApiStatus;
import survivalblock.volucraft.common.recipe.AmalgamationInput;
import survivalblock.volucraft.common.recipe.specific.NormalAmalgamationRecipe;

import java.util.function.BiFunction;

@ApiStatus.Internal
public abstract class ExtrudedAmalgamationRecipe extends NormalAmalgamationRecipe {
    protected final CraftingRecipe delegate;

    protected ExtrudedAmalgamationRecipe(CommonInfo commonInfo, final CraftingRecipe delegate) {
        super(commonInfo);
        this.delegate = delegate;
    }

    protected PlacementInfo createPlacementInfo() {
        return this.delegate.placementInfo();
    }

    @Override
    public boolean isSpecial() {
        return this.delegate.isSpecial();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(AmalgamationInput input) {
        NonNullList<ItemStack> stacks;
        for (CraftingInput craftingInput : input.asPossibleCraftInputs()) {
            stacks = this.delegate.getRemainingItems(craftingInput);
            if (!stacks.isEmpty()) {
                return stacks;
            }
        }
        return super.getRemainingItems(input);
    }

    // I LOVE DISPATCH SO MUCH, THANK YOU MOJANG
    protected static <T extends ExtrudedAmalgamationRecipe> MapCodec<T> createCodec(BiFunction<CommonInfo, CraftingRecipe, T> constructor) {
        return RecordCodecBuilder.mapCodec(
                (instance) -> instance.group(
                        CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                        BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().<CraftingRecipe>dispatchMap(CraftingRecipe::getSerializer, serializer -> (MapCodec<? extends CraftingRecipe>) serializer.codec()).forGetter(recipe -> recipe.delegate)
                ).apply(instance, constructor)
        );
    }

    protected static <T extends ExtrudedAmalgamationRecipe> StreamCodec<RegistryFriendlyByteBuf, T> createPacketCodec(BiFunction<CommonInfo, CraftingRecipe, T> constructor) {
        return StreamCodec.composite(
                CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
                ByteBufCodecs.registry(Registries.RECIPE_SERIALIZER).dispatch(CraftingRecipe::getSerializer, serializer -> (StreamCodec<? super RegistryFriendlyByteBuf, ? extends CraftingRecipe>) serializer.streamCodec()), recipe -> recipe.delegate,
                constructor
        );
    }
}
