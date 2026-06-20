package survivalblock.volucraft.common.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

/**
 * @see net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay
 */
public record ShapedAmalgamationRecipeDisplay(int length, int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<ShapedAmalgamationRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> i.group(
                Codec.INT.fieldOf("length").forGetter(display -> display.length),
				Codec.INT.fieldOf("width").forGetter(display -> display.width),
				Codec.INT.fieldOf("height").forGetter(display -> display.height),
				SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(display -> display.ingredients),
				SlotDisplay.CODEC.fieldOf("result").forGetter(display -> display.result),
				SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(display -> display.craftingStation)
			)
			.apply(i, ShapedAmalgamationRecipeDisplay::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, ShapedAmalgamationRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, display -> display.length,
		ByteBufCodecs.VAR_INT, display -> display.width,
		ByteBufCodecs.VAR_INT, display -> display.height,
		SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), display -> display.ingredients,
		SlotDisplay.STREAM_CODEC, display -> display.result,
		SlotDisplay.STREAM_CODEC, display -> display.craftingStation,
		ShapedAmalgamationRecipeDisplay::new
	);
	public static final RecipeDisplay.Type<ShapedAmalgamationRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

	public ShapedAmalgamationRecipeDisplay {
		if (ingredients.size() != length * width * height) {
			throw new IllegalArgumentException("Invalid shaped amalgamation recipe display contents");
		}
	}

	@Override
	public RecipeDisplay.Type<ShapedAmalgamationRecipeDisplay> type() {
		return TYPE;
	}

	@Override
	public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
		return this.ingredients.stream().allMatch(e -> e.isEnabled(enabledFeatures)) && RecipeDisplay.super.isEnabled(enabledFeatures);
	}
}
