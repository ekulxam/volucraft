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
package survivalblock.volucraft.common.recipe.specific;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;

import java.util.*;
import java.util.function.Function;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.recipe.AmalgamationInput;
import survivalblock.volucraft.common.recipe.ThirdDimensionalStacksContainer;

import static net.minecraft.world.item.crafting.ShapedRecipePattern.EMPTY_SLOT;
import static survivalblock.volucraft.mixin.ShapedRecipePatternInvoker.volucraft$invokeFirstNonEmpty;
import static survivalblock.volucraft.mixin.ShapedRecipePatternInvoker.volucraft$invokeLastNonEmpty;

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
            ByteBufCodecs.VAR_INT, recipe -> recipe.length,
            ByteBufCodecs.VAR_INT, recipe -> recipe.width,
            ByteBufCodecs.VAR_INT, recipe -> recipe.height,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.ingredients,
            ShapedAmalgamationRecipePattern::createFromNetwork
	);
    private final int length;
	private final int width;
	private final int height;
	private final List<Optional<Ingredient>> ingredients;
	private final Optional<ShapedAmalgamationRecipePattern.Data> data;
	private final int ingredientCount;
    private final Map<ThreePeasInAPod, List<OctahedralGroup>> transformsPreservingDimensions = new HashMap<>();

	public ShapedAmalgamationRecipePattern(final int length, final int width, final int height, final List<Optional<Ingredient>> ingredients, final Optional<ShapedAmalgamationRecipePattern.Data> data) {
		this.length = length;
        this.width = width;
		this.height = height;
		this.ingredients = ingredients;
		this.data = data;
		this.ingredientCount = (int) ingredients.stream().flatMap(Optional::stream).count();
        this.computeTransformsPreservingDimensions();
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

    private void computeTransformsPreservingDimensions() {
        if (this.length == 1 && this.width == 1 && this.height == 1) {
            // naive optimization for 1x1x1 cubes
            this.transformsPreservingDimensions.put(new ThreePeasInAPod(1, 1, 1), List.of(OctahedralGroup.IDENTITY));
            return;
        }

        // I think this is fine? ArrayList, Optional, and Ingredient all implement correct equals methods so Set#contains should function correctly
        Map<ThreePeasInAPod, Set<List<Optional<Ingredient>>>> uniques = new HashMap<>();
        Vector3f dimensions = new Vector3f();
        Vector3f coordinates = new Vector3f();
        List<Optional<Ingredient>> simulatedItemStacks;

        // 48 symmetries of a 3x3x3 grid, apparently
        for (OctahedralGroup symmetry : OctahedralGroup.values()) {
            Matrix3fc transform = symmetry.transformation();
            //noinspection SuspiciousNameCombination
            dimensions.set(this.length, this.width, this.height).mul(transform);
            final int actualLength = Math.abs(Math.round(dimensions.x));
            final int actualWidth = Math.abs(Math.round(dimensions.y));
            final int actualHeight = Math.abs(Math.round(dimensions.z));
            simulatedItemStacks = new ArrayList<>(Collections.nCopies(actualLength * actualWidth * actualHeight, Optional.empty()));
            final int offsetX = dimensions.x < 0 ? actualLength - 1 : 0;
            final int offsetY = dimensions.y < 0 ? actualWidth - 1 : 0;
            final int offsetZ = dimensions.z < 0 ? actualHeight - 1 : 0;

            for (int z = 0; z < this.height; z++) {
                for (int y = 0; y < this.width; y++) {
                    for (int x = 0; x < this.length; x++) {
                        coordinates.set(x, y, z).mul(transform);
                        int finalX = Math.round(coordinates.x) + offsetX;
                        int finalY = Math.round(coordinates.y) + offsetY;
                        int finalZ = Math.round(coordinates.z) + offsetZ;

                        simulatedItemStacks.set(
                                finalX + (finalY * actualLength) + (finalZ * actualLength * actualWidth),
                                this.ingredients.get(x + y * this.length + z * this.length * this.width)
                        );
                    }
                }
            }

            ThreePeasInAPod peas = new ThreePeasInAPod(actualLength, actualWidth, actualHeight);
            Set<List<Optional<Ingredient>>> seen = uniques.computeIfAbsent(peas, _ -> new HashSet<>());
            if (!seen.contains(simulatedItemStacks)) {
                seen.add(simulatedItemStacks);
                this.transformsPreservingDimensions.computeIfAbsent(peas, _ -> new ArrayList<>()).add(symmetry);
            }
        }
    }

	public boolean matches(final AmalgamationInput input) {
        if (input.ingredientCount() != ShapedAmalgamationRecipePattern.this.ingredientCount) {
            return false;
        }

        ThreePeasInAPod peas = new ThreePeasInAPod(input.length(), input.width(), input.height());
        List<OctahedralGroup> transformations = this.transformsPreservingDimensions.get(peas);
        if (transformations == null) {
            return false; // if no transformation would get to my dimensions, then they don't match
        }

        ItemStack[] items;
        Vector3f dimensions = new Vector3f();
        Vector3f coordinates = new Vector3f();
        for (OctahedralGroup symmetry : transformations) {
            Matrix3fc transform = symmetry.transformation();
            //noinspection SuspiciousNameCombination
            dimensions.set(this.length, this.width, this.height).mul(transform);
            // at this point, actuals are now equal to input dimensions (probably)
            final int actualLength = Math.abs(Math.round(dimensions.x));
            final int actualWidth = Math.abs(Math.round(dimensions.y));
            final int actualHeight = Math.abs(Math.round(dimensions.z));

            items = new ItemStack[this.length * this.width * this.height];

            final int offsetX = dimensions.x < 0 ? actualLength - 1 : 0;
            final int offsetY = dimensions.y < 0 ? actualWidth - 1 : 0;
            final int offsetZ = dimensions.z < 0 ? actualHeight - 1 : 0;

            for (int z = 0; z < this.height; z++) {
                for (int y = 0; y < this.width; y++) {
                    for (int x = 0; x < this.length; x++) {
                        coordinates.set(x, y, z).mul(transform);
                        int finalX = Math.round(coordinates.x) + offsetX;
                        int finalY = Math.round(coordinates.y) + offsetY;
                        int finalZ = Math.round(coordinates.z) + offsetZ;
                        items[x + (y * this.length) + (z * this.length * this.width)] = input.getItem(finalX + finalY * actualLength + finalZ * actualLength * actualWidth);
                    }
                }
            }

            if (matchesAfterTransform(ThirdDimensionalStacksContainer.fromArray(this.length, this.width, items))) {
                return true;
            }
        }

        return false;
	}

    private boolean matchesAfterTransform(final ThirdDimensionalStacksContainer stacksContainer) {
        for (int z = 0; z < this.height; z++) {
            for (int y = 0; y < this.width; y++) {
                for (int x = 0; x < this.length; x++) {
                    int index = x + (y * this.length) + (z * this.length * this.width);
                    if (!Ingredient.testOptionalIngredient(
                            this.ingredients.get(index),
                            stacksContainer.getItem(x, y, z)
                    )) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int length() {
        return this.length;
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
				return DataResult.error(() -> "Invalid 3D pattern: too many layers, 3 is maximum");
			}
			if (lists.isEmpty()) {
				return DataResult.error(() -> "Invalid 3D pattern: empty pattern not allowed");
			}

            int firstLength = lists.getFirst().getFirst().length();

            for (List<String> list : lists) {
                if (list.size() > 3) {
                    return DataResult.error(() -> "Invalid 2D layer: too many rows, 3 is maximum");
                }
                if (list.isEmpty()) {
                    return DataResult.error(() -> "Invalid 2D layer: empty layer not allowed");
                }

                for (String line : list) {
                    if (line.length() > 3) {
                        return DataResult.error(() -> "Invalid 1D line: too many columns, 3 is maximum");
                    }
                    if (firstLength != line.length()) {
                        return DataResult.error(() -> "Invalid 1D line: each row must be the same width");
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
					ExtraCodecs.strictUnboundedMap(SYMBOL_CODEC, Ingredient.CODEC).fieldOf("key").forGetter(data -> data.key),
					PATTERN_CODEC.fieldOf("pattern").forGetter(data -> data.pattern)
				)
				.apply(i, ShapedAmalgamationRecipePattern.Data::new)
		);
	}

    public record ThreePeasInAPod(int length, int width, int height) {
    }
}
