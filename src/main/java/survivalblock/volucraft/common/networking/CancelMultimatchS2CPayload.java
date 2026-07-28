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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import survivalblock.volucraft.common.Volucraft;

public final class CancelMultimatchS2CPayload implements CustomPacketPayload {
    public static final CancelMultimatchS2CPayload INSTANCE = new CancelMultimatchS2CPayload();
    public static final StreamCodec<FriendlyByteBuf, CancelMultimatchS2CPayload> CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<CancelMultimatchS2CPayload> ID = new Type<>(Volucraft.id("cancel_multimatch_s2c"));

    private CancelMultimatchS2CPayload() {
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
