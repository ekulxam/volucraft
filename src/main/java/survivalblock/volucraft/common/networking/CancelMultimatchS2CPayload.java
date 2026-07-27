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
