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

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.crafting.RecipeHolder;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

public record SelectFromMultimatchC2SPayload(RecipeHolder<?> match) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectFromMultimatchC2SPayload> CODEC = RecipeHolder.STREAM_CODEC.map(SelectFromMultimatchC2SPayload::new, payload -> payload.match);
    public static final Type<SelectFromMultimatchC2SPayload> ID = new Type<>(Volucraft.id("select_from_multimatch_c2s"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final class Receiver implements ServerPlayNetworking.PlayPayloadHandler<SelectFromMultimatchC2SPayload> {
        public static final Receiver INSTANCE = new Receiver();

        private Receiver() {
        }

        @Override
        public void receive(SelectFromMultimatchC2SPayload payload, ServerPlayNetworking.Context context) {
            if (!(context.player().containerMenu instanceof AmalgamationMenu amalgamationMenu)) {
                return;
            }
            //noinspection unchecked, NullableProblems
            amalgamationMenu.multimatch((RecipeHolder<AmalgamationRecipe>) payload.match);
        }
    }
}
