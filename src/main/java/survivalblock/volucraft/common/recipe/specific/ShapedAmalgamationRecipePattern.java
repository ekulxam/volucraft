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
import survivalblock.volucraft.common.recipe.AmalgamationInput;

import static net.minecraft.world.item.crafting.ShapedRecipePattern.EMPTY_SLOT;

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

    /**
     * I'm too tired so this is AI-ed for now
     */
	@VisibleForTesting
	public static String[][] shrink(final List<List<String>> pattern) {
        if (pattern.isEmpty()) {
            return new String[0][0];
        }

        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;

        boolean empty = true;

        // Step 1: Scan the 3D space to find the bounding box of non-empty slots
        for (int z = 0; z < pattern.size(); z++) {
            List<String> layer = pattern.get(z);

            for (int y = 0; y < layer.size(); y++) {
                String row = layer.get(y);

                int firstX = firstNonEmpty(row);
                int lastX = lastNonEmpty(row);

                // If lastX >= 0, it means this row contains actual crafting ingredients
                if (lastX >= 0) {
                    empty = false;

                    // Update Z (Layer) boundaries
                    minZ = Math.min(minZ, z);
                    maxZ = Math.max(maxZ, z);

                    // Update Y (Row) boundaries
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);

                    // Update X (Column) boundaries
                    minX = Math.min(minX, firstX);
                    maxX = Math.max(maxX, lastX);
                }
            }
        }

        // If the entire 3D grid is empty spaces, return an empty 2D array
        if (empty) {
            return new String[0][0];
        }

        // Step 2: Slice and build the final trimmed 3D array [Z][Y] -> String (X)
        int depth = maxZ - minZ + 1;
        int height = maxY - minY + 1;
        String[][] trimmedRecipe = new String[depth][height];

        for (int z = 0; z < depth; z++) {
            List<String> originalLayer = pattern.get(z + minZ);
            for (int y = 0; y < height; y++) {
                String originalRow = originalLayer.get(y + minY);
                // Substring handles trimming the X (columns) dimension
                trimmedRecipe[z][y] = originalRow.substring(minX, maxX + 1);
            }
        }

        return trimmedRecipe;
	}
    // end AI

	private static int firstNonEmpty(final String line) {
		int index = 0;

		while (index < line.length() && line.charAt(index) == EMPTY_SLOT) {
			index++;
		}

		return index;
	}

	private static int lastNonEmpty(final String line) {
		int index = line.length() - 1;

		while (index >= 0 && line.charAt(index) == EMPTY_SLOT) {
			index--;
		}

		return index;
	}

	public boolean matches(final AmalgamationInput input) {
        return new Matcher().matches(input);
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

    /**
     * Also done by AI because I actually don't know how to do this
     */
    public class Matcher {
        public static final int[][] AXES_PERMUTATIONS = {
                {0, 1, 2},
                {0, 2, 1},
                {1, 0, 2},
                {1, 2, 0},
                {2, 0, 1},
                {2, 1, 0}
        };

        protected boolean matches(AmalgamationInput input) {
            if (input.ingredientCount() != ShapedAmalgamationRecipePattern.this.ingredientCount) {
                return false;
            }

            // We slide our recipe bounding box through the input grid's space.
            // To handle 3D rotations, the recipe's dimensions (width, height, depth)
            // can map to different input axes depending on orientation.

            // Loop through all 48 possible 3D orientations (24 rotations * 2 for mirroring)
            for (int rotationIdx = 0; rotationIdx < 48; rotationIdx++) {
                if (matchesOrientation(input, rotationIdx)) {
                    return true;
                }
            }

            return false;
        }

        private boolean matchesOrientation(final AmalgamationInput input, int orientation) {
            // Unpack orientation details:
            // There are 6 possible face directions the "front" of our recipe could look,
            // 4 rotations around that face vector, and 2 states for mirroring (reflection).
            int face = orientation % 6;
            int spin = (orientation / 6) % 4;
            boolean mirror = (orientation / 24) == 1;

            // Map recipe (x, y, z) lengths to input (X, Y, Z) targets based on orientation
            int matchLength = getOrientedDim(0, face, spin);
            int matchWidth = getOrientedDim(1, face, spin);
            int matchHeight = getOrientedDim(2, face, spin);

            // Quick check: If the oriented recipe size doesn't fit inside the input bounds, it's invalid
            if (matchLength != input.length() || matchWidth != input.width() || matchHeight != input.height()) {
                return false;
            }

            // Scan every slot in the grid
            for (int z = 0; z < ShapedAmalgamationRecipePattern.this.height; z++) {
                for (int y = 0; y < ShapedAmalgamationRecipePattern.this.width; y++) {
                    for (int x = 0; x < ShapedAmalgamationRecipePattern.this.length; x++) {

                        // 1. Get expected ingredient from our flat list
                        int flatIndex = x + (y * ShapedAmalgamationRecipePattern.this.length) + (z * ShapedAmalgamationRecipePattern.this.length * ShapedAmalgamationRecipePattern.this.width);
                        Optional<Ingredient> expected = ShapedAmalgamationRecipePattern.this.ingredients.get(flatIndex);

                        // 2. Map recipe coordinates (x, y, z) to input coordinates based on this orientation
                        int inputX = transformX(x, y, z, face, spin, mirror, input.length());
                        int inputY = transformY(x, y, z, face, spin, input.width());
                        int inputZ = transformZ(x, y, z, face, spin, input.height());

                        // 3. Test if the ingredient matches
                        ItemStack actual = input.getItem(inputX, inputY, inputZ);
                        if (!Ingredient.testOptionalIngredient(expected, actual)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        // --- 3D Transformation Math helpers ---

        private int getOrientedDim(int axis, int face, int spin) {
            // Simplifies matching dimensions to orientations.
            // Returns which recipe axis (0=L, 1=W, 2=H) matches the requested input axis.
            return switch (AXES_PERMUTATIONS[(face + spin) % 6][axis]) {
                case 0 -> ShapedAmalgamationRecipePattern.this.length;
                case 1 -> ShapedAmalgamationRecipePattern.this.width;
                default -> ShapedAmalgamationRecipePattern.this.height;
            };
        }

        private int transformX(int x, int y, int z, int face, int spin, boolean mirror, int maxLength) {
            int targetX = switch ((face + spin) % 3) {
                case 1 -> (face >= 3) ? maxLength - 1 - y : y;
                case 2 -> (face >= 3) ? maxLength - 1 - z : z;
                default -> (face >= 3) ? maxLength - 1 - x : x;
            };
            return mirror ? maxLength - 1 - targetX : targetX;
        }

        private int transformY(int x, int y, int z, int face, int spin, int maxWidth) {
            return switch ((face + spin + 1) % 3) {
                case 0 -> (spin >= 2) ? maxWidth - 1 - x : x;
                case 2 -> (spin >= 2) ? maxWidth - 1 - z : z;
                default -> (spin >= 2) ? maxWidth - 1 - y : y;
            };
        }

        private int transformZ(int x, int y, int z, int face, int spin, int maxHeight) {
            return switch ((face + spin + 2) % 3) {
                case 0 -> (face % 2 == 1) ? maxHeight - 1 - x : x;
                case 1 -> (face % 2 == 1) ? maxHeight - 1 - y : y;
                default -> (face % 2 == 1) ? maxHeight - 1 - z : z;
            };
        }
    }
}
