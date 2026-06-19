package survivalblock.volucraft.common.recipe.specific;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import survivalblock.volucraft.common.init.VolucraftItems;
import survivalblock.volucraft.common.recipe.AmalgamationInput;

@SuppressWarnings("unused")
public class ShapedAmalgamationRecipe extends NormalAmalgamationRecipe {
    public static final MapCodec<ShapedAmalgamationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                    ShapedAmalgamationRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            ).apply(instance, ShapedAmalgamationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedAmalgamationRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            ShapedAmalgamationRecipePattern.STREAM_CODEC, recipe -> recipe.pattern,
            ItemStackTemplate.STREAM_CODEC, recipe -> recipe.result,
            ShapedAmalgamationRecipe::new);

    public static final RecipeSerializer<ShapedAmalgamationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
    private final ShapedAmalgamationRecipePattern pattern;
    private final ItemStackTemplate result;

    public ShapedAmalgamationRecipe(final Recipe.CommonInfo commonInfo, final ShapedAmalgamationRecipePattern pattern, final ItemStackTemplate result) {
        super(commonInfo);
        this.pattern = pattern;
        this.result = result;
    }

    public RecipeSerializer<ShapedAmalgamationRecipe> getSerializer() {
        return SERIALIZER;
    }

    @VisibleForTesting
    public List<Optional<Ingredient>> getIngredients() {
        return this.pattern.ingredients();
    }

    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    public boolean matches(final AmalgamationInput input, final Level level) {
        return this.pattern.matches(input);
    }

    public ItemStack assemble(final AmalgamationInput input) {
        return this.result.create();
    }

    public int getLength() {
        return this.pattern.length();
    }

    public int getWidth() {
        return this.pattern.width();
    }

    public int getHeight() {
        return this.pattern.height();
    }

    // hopefully this displays nothing
    @Override
    public List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(0, 0, List.of(), new SlotDisplay.ItemStackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(VolucraftItems.AMALGAMATION_TABLE)));
    }
}
