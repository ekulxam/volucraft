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
package survivalblock.volucraft.common.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

import java.util.List;

public record MultimatchS2CPayload(List<RecipeHolder<?>> matches) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, MultimatchS2CPayload> CODEC = RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()).map(MultimatchS2CPayload::new, payload -> payload.matches);
    public static final Type<MultimatchS2CPayload> ID = new Type<>(Volucraft.id("multimatch_s2c"));

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static MultimatchS2CPayload cast(List<RecipeHolder<AmalgamationRecipe>> matches) {
        return new MultimatchS2CPayload((List) matches);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<RecipeHolder<AmalgamationRecipe>> castMatches() {
        return (List) this.matches;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
