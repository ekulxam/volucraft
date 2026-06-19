package survivalblock.volucraft.common.recipe.specific;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.AmalgamationInput;

import static net.minecraft.world.item.crafting.ShapedRecipePattern.EMPTY_SLOT;
import static survivalblock.volucraft.mixin.ShapedRecipePatternAccessor.volucraft$invokeFirstNonEmpty;
import static survivalblock.volucraft.mixin.ShapedRecipePatternAccessor.volucraft$invokeLastNonEmpty;

/**
 * @see ShapedRecipePattern
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ShapedAmalgamationRecipePattern {
	public static final MapCodec<ShapedAmalgamationRecipePattern> MAP_CODEC = ShapedAmalgamationRecipePattern.Data.MAP_CODEC
		.flatXmap(
                ShapedAmalgamationRecipePattern::unpack,
                pattern -> pattern.data.map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Cannot encode unpacked recipe"))
		);
	public static final StreamCodec<RegistryFriendlyByteBuf, ShapedAmalgamationRecipePattern> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            recipe -> recipe.length,
		ByteBufCodecs.VAR_INT,
            recipe -> recipe.width,
		ByteBufCodecs.VAR_INT,
            recipe -> recipe.height,
		Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            recipe -> recipe.ingredients,
		ShapedAmalgamationRecipePattern::createFromNetwork
	);
    private final int length;
	private final int width;
	private final int height;
	private final List<Optional<Ingredient>> ingredients;
	private final Optional<ShapedAmalgamationRecipePattern.Data> data;
	private final int ingredientCount;
	//private final boolean symmetrical;

	public ShapedAmalgamationRecipePattern(final int length, final int width, final int height, final List<Optional<Ingredient>> ingredients, final Optional<ShapedAmalgamationRecipePattern.Data> data) {
		this.length = length;
        this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.data = data;
		this.ingredientCount = (int)ingredients.stream().flatMap(Optional::stream).count();
		//this.symmetrical = Util.isSymmetrical(width, height, ingredients);
	}

	private static ShapedAmalgamationRecipePattern createFromNetwork(final Integer length, final Integer width, final Integer height, final List<Optional<Ingredient>> ingredients) {
		return new ShapedAmalgamationRecipePattern(length, width, height, ingredients, Optional.empty());
	}

	/*public static ShapedAmalgamationRecipePattern of(final Map<Character, Ingredient> key, final String... pattern) {
		return of(key, List.of(pattern));
	}*/

	public static ShapedAmalgamationRecipePattern of(final Map<Character, Ingredient> key, final List<List<String>> pattern) {
		ShapedAmalgamationRecipePattern.Data data = new ShapedAmalgamationRecipePattern.Data(key, pattern);
		return unpack(data).getOrThrow();
	}

	private static DataResult<ShapedAmalgamationRecipePattern> unpack(final ShapedAmalgamationRecipePattern.Data data) {
		String[][] shrunkPattern = shrink(data.pattern);
		int length = shrunkPattern[0][0].length();
        int width = shrunkPattern[0].length;
		int height = shrunkPattern.length;
		List<Optional<Ingredient>> ingredients = new ArrayList<>(length * width * height);
		CharSet unusedSymbols = new CharArraySet(data.key.keySet());

		for (String[] layer : shrunkPattern) {
            for (String line : layer) {
                for (int i = 0; i < line.length(); i++) {
                    char symbol = line.charAt(i);
                    Optional<Ingredient> ingredient;
                    if (symbol == EMPTY_SLOT) {
                        ingredient = Optional.empty();
                    } else {
                        Ingredient ingredientForSymbol = data.key.get(symbol);
                        if (ingredientForSymbol == null) {
                            return DataResult.error(() -> "Pattern references symbol '" + symbol + "' but it's not defined in the key");
                        }

                        ingredient = Optional.of(ingredientForSymbol);
                    }

                    unusedSymbols.remove(symbol);
                    ingredients.add(ingredient);
                }
            }
        }

		return !unusedSymbols.isEmpty()
			? DataResult.error(() -> "Key defines symbols that aren't used in pattern: " + unusedSymbols)
			: DataResult.success(new ShapedAmalgamationRecipePattern(length, width, height, ingredients, Optional.of(data)));
	}

    @VisibleForTesting
    public static String[][] shrink(final List<List<String>> pattern) {
        if (pattern.isEmpty()) {
            return new String[0][0];
        }

        // I'm doing this from memory so I can technically say it's mine
        boolean allEmpty = true;
        int minX = Volucraft.SIDE_LENGTH - 1;
        int maxX = 0;
        int minY = Volucraft.SIDE_LENGTH - 1;
        int maxY = 0;
        int minZ = Volucraft.SIDE_LENGTH - 1;
        int maxZ = 0;

        for (int z = 0; z < pattern.size(); z++) {
            List<String> layer = pattern.get(z);
            for (int y = 0; y < layer.size(); y++) {
                String line = layer.get(y);
                int lastNonSpace = volucraft$invokeLastNonEmpty(line);
                if (lastNonSpace >= 0) {
                    allEmpty = false;

                    minX = Math.min(minX, volucraft$invokeFirstNonEmpty(line));
                    maxX = Math.max(maxX, lastNonSpace);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                    minZ = Math.min(minZ, z);
                    maxZ = Math.max(maxZ, z);
                }
            }
        }

        //noinspection ConstantValue (intellij doesn't like my accessors, I suppose)
        if (allEmpty) {
            return new String[0][0];
        }

        String[][] result = new String[maxZ - minZ + 1][maxY - minY + 1];
        for (int z = 0; z < result.length; z++) {
            for (int y = 0; y < result[0].length; y++) {
                result[z][y] = pattern.get(z + minZ).get(y + minY).substring(minX, maxX + 1); // I'm slightly confused as to why we add 1 here but vanilla does it too so I suppose it's correct
            }
        }
        return result;
    }

	public boolean matches(final AmalgamationInput input) {
        if (input.ingredientCount() != ShapedAmalgamationRecipePattern.this.ingredientCount) {
            return false;
        }

        if (input.ingredientCount() == 1) {
            return matchesAfterTransform(input);
        }

        // 48 symmetries of a 3x3x3 grid, apparently
        for (int rotationId = 0; rotationId < 48; rotationId++) {
            if (matchesAfterTransform(input, rotationId)) {
                return true;
            }
        }

        return matchesAfterTransform(input);
	}

    private boolean matchesAfterTransform(final AmalgamationInput input) {
        // Scan every slot in the grid
        for (int z = 0; z < ShapedAmalgamationRecipePattern.this.height; z++) {
            for (int y = 0; y < ShapedAmalgamationRecipePattern.this.width; y++) {
                for (int x = 0; x < ShapedAmalgamationRecipePattern.this.length; x++) {

                    // 1. Get expected ingredient from our flat list
                    int flatIndex = x + (y * ShapedAmalgamationRecipePattern.this.length) + (z * ShapedAmalgamationRecipePattern.this.length * ShapedAmalgamationRecipePattern.this.width);
                    Optional<Ingredient> expected = ShapedAmalgamationRecipePattern.this.ingredients.get(flatIndex);

                    // 3. Test if the ingredient matches
                    ItemStack actual = input.getItem(x, y, z);
                    if (!Ingredient.testOptionalIngredient(expected, actual)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int length() {
        return this.width;
    }

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	public List<Optional<Ingredient>> ingredients() {
		return this.ingredients;
	}

	public record Data(Map<Character, Ingredient> key, List<List<String>> pattern) {
		private static final Codec<List<List<String>>> PATTERN_CODEC = Codec.STRING.listOf().listOf().comapFlatMap(lists -> {
			if (lists.size() > 3) {
				return DataResult.error(() -> "Invalid pattern: too many layers, 3 is maximum");
			}

			if (lists.isEmpty()) {
				return DataResult.error(() -> "Invalid pattern: empty pattern not allowed");
			}

            int firstLength = lists.getFirst().getFirst().length();

            for (List<String> list : lists) {
                if (list.size() > 3) {
                    return DataResult.error(() -> "Invalid pattern: too many rows, 3 is maximum");
                }

                if (list.isEmpty()) {
                    return DataResult.error(() -> "Invalid pattern: empty layer not allowed");
                }

                for (String line : list) {
                    if (line.length() > 3) {
                        return DataResult.error(() -> "Invalid pattern: too many columns, 3 is maximum");
                    }

                    if (firstLength != line.length()) {
                        return DataResult.error(() -> "Invalid pattern: each row must be the same width");
                    }
                }
            }


			return DataResult.success(lists);
		}, Function.identity());
		private static final Codec<Character> SYMBOL_CODEC = Codec.STRING.comapFlatMap(symbol -> {
			if (symbol.length() != 1) {
				return DataResult.error(() -> "Invalid key entry: '" + symbol + "' is an invalid symbol (must be 1 character only).");
			} else {
				return " ".equals(symbol) ? DataResult.error(() -> "Invalid key entry: ' ' is a reserved symbol.") : DataResult.success(symbol.charAt(0));
			}
		}, String::valueOf);
		public static final MapCodec<ShapedAmalgamationRecipePattern.Data> MAP_CODEC = RecordCodecBuilder.mapCodec(
			i -> i.group(
					ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC).fieldOf("key").forGetter(d -> d.key),
					PATTERN_CODEC.fieldOf("pattern").forGetter(d -> d.pattern)
				)
				.apply(i, ShapedAmalgamationRecipePattern.Data::new)
		);
	}
}
