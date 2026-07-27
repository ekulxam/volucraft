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
