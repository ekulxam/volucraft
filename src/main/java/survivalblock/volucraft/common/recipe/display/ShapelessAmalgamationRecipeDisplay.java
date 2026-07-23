package survivalblock.volucraft.common.recipe.display;

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
 * @see net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay
 */
public record ShapelessAmalgamationRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<ShapelessAmalgamationRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
		i -> i.group(
                SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(display -> display.ingredients),
                SlotDisplay.CODEC.fieldOf("result").forGetter(display -> display.result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(display -> display.craftingStation)
			)
			.apply(i, ShapelessAmalgamationRecipeDisplay::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessAmalgamationRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
        SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), display -> display.ingredients,
        SlotDisplay.STREAM_CODEC, display -> display.result,
        SlotDisplay.STREAM_CODEC, display -> display.craftingStation,
		ShapelessAmalgamationRecipeDisplay::new
	);
	public static final RecipeDisplay.Type<ShapelessAmalgamationRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

	@Override
	public RecipeDisplay.Type<ShapelessAmalgamationRecipeDisplay> type() {
		return TYPE;
	}

	@Override
	public boolean isEnabled(final FeatureFlagSet enabledFeatures) {
		return this.ingredients.stream().allMatch(e -> e.isEnabled(enabledFeatures)) && RecipeDisplay.super.isEnabled(enabledFeatures);
	}
}
