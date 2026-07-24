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
package survivalblock.volucraft.common.recipe;

import java.util.*;

import com.mojang.math.OctahedralGroup;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3fc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

/**
 * Using x as length, y as width, and z as height
 * @see CraftingInput
 */
public class AmalgamationInput implements RecipeInput, ThirdDimensionalStacksContainer {
	public static final AmalgamationInput EMPTY = new AmalgamationInput(0, 0, 0, List.of());
    private final int length;
	private final int width;
	private final int height;
	private final List<ItemStack> items;
	private final StackedItemContents stackedContents = new StackedItemContents();
	private final int ingredientCount;
    @Nullable
    private CraftingInput basicallyShapelessCraftInput = null;
    @Nullable
    private List<CraftingInput> possibleCraftInputs = null;

	private AmalgamationInput(final int length, final int width, final int height, final List<ItemStack> items) {
        this.length = length;
		this.width = width;
		this.height = height;
		this.items = items;
		int ingredientCount = 0;

		for (ItemStack item : items) {
			if (!item.isEmpty()) {
				ingredientCount++;
				this.stackedContents.accountStack(item, 1);
			}
		}

		this.ingredientCount = ingredientCount;
	}

	public static AmalgamationInput of(final int length, final int width, final int height, final List<ItemStack> items) {
		return ofPositioned(length, width, height, items).input();
	}

	public static AmalgamationInput.Positioned ofPositioned(final int length, final int width, final int height, final List<ItemStack> items) {
		if (length != 0 && width != 0 && height != 0) {
			int left = length - 1;
			int right = 0;
            int back = width - 1;
            int front = 0;
			int top = height - 1;
			int bottom = 0;

			for (int z = 0; z < height; z++) {
                boolean layerEmpty = true;

                for (int y = 0; y < width; y++) {
                    boolean rowEmpty = true;

                    for (int x = 0; x < length; x++) {
                        ItemStack item = items.get(x + y * length + z * length * width); // hope i'm correct
                        if (!item.isEmpty()) {
                            left = Math.min(left, x);
                            right = Math.max(right, x);
                            rowEmpty = false;
                            layerEmpty = false;
                        }
                    }

                    if (!rowEmpty) {
                        back = Math.min(back, y);
                        front = Math.max(front, y);
                    }
                }

                if (!layerEmpty) {
                    top = Math.min(top, z);
                    bottom = Math.max(bottom, z);
                }
            }

            int newLength = right - left + 1;
			int newWidth = front - back + 1;
			int newHeight = bottom - top + 1;
			if (newLength <= 0 || newWidth <= 0 || newHeight <= 0) {
				return AmalgamationInput.Positioned.EMPTY;
			}

			if (newLength == length && newWidth == width && newHeight == height) {
				return new AmalgamationInput.Positioned(new AmalgamationInput(length, width, height, items), left, back, top);
			}

			List<ItemStack> newItems = new ArrayList<>(newLength * newWidth * newHeight);

			for (int z = 0; z < newHeight; z++) {
				for (int y = 0; y < newWidth; y++) {
                    for (int x = 0; x < newLength; x++) {
                        int index = (x + left) + (y + back) * length + (z + top) * length * width;
                        newItems.add(items.get(index));
                    }
				}
			}

			return new AmalgamationInput.Positioned(new AmalgamationInput(newLength, newWidth, newHeight, newItems), left, back, top);
		} else {
			return AmalgamationInput.Positioned.EMPTY;
		}
	}

	@Override
	public ItemStack getItem(final int index) {
		return this.items.get(index);
	}

	public ItemStack getItem(final int x, final int y, final int z) {
		return this.items.get(x + (y * this.length) + (z * this.length * this.width));
	}

	@Override
	public int size() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		return this.ingredientCount == 0;
	}

    public StackedItemContents stackedContents() {
		return this.stackedContents;
	}

	public List<ItemStack> items() {
		return this.items;
	}

	public int ingredientCount() {
		return this.ingredientCount;
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

	@SuppressWarnings("deprecation")
    @Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else {
			return obj instanceof AmalgamationInput input
                    && this.length == input.length
                    && this.width == input.width
                    && this.height == input.height
                    && this.ingredientCount == input.ingredientCount
                    && ItemStack.listMatches(this.items, input.items);
		}
	}

    @SuppressWarnings("deprecation")
    @Override
	public int hashCode() {
		int result = ItemStack.hashStackList(this.items);
        result = 31 * result + this.length;
		result = 31 * result + this.width;
		return 31 * result + this.height;
	}

    @SuppressWarnings("SuspiciousNameCombination")
    @ApiStatus.Experimental
    public CraftingInput asBasicallyShapelessCraftInput() {
        if (this.basicallyShapelessCraftInput == null) {
            int max = Math.max(this.length, Math.max(this.width, this.height));
            int width2D;
            int height2D;
            if (this.length == max) {
                width2D = this.length;
                height2D = this.width * this.height;
            } else if (this.width == max) {
                width2D = this.width;
                height2D = this.length * this.height;
            } else {
                width2D = this.height;
                height2D = this.length * this.width;
            }
            this.basicallyShapelessCraftInput = CraftingInput.of(width2D, height2D, this.items);
        }

        return this.basicallyShapelessCraftInput;
    }

    /**
     * May output an incorrect result
     * @return a list of inputs that could be a correct 2D input
     */
    @ApiStatus.Experimental
    public List<CraftingInput> asPossibleCraftInputs() {
        if (this.possibleCraftInputs == null) {
            this.possibleCraftInputs = this.computePossibleCraftInputs();
        }

        return this.possibleCraftInputs;
    }

    /**
     * @see survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipePattern#computeTransformsPreservingDimensions()
     */
    @SuppressWarnings("JavadocReference")
    private List<CraftingInput> computePossibleCraftInputs() {
        if (this == EMPTY || this.length == 0 || this.width == 0 || this.height == 0) {
            return List.of(CraftingInput.EMPTY);
        }
        if (this.length != 1 && this.width != 1 && this.height != 1) {
            return List.of();
        }

        List<CraftingInput> uniques = new ArrayList<>();
        Vector3f dimensions = new Vector3f();
        Vector3f coordinates = new Vector3f();
        List<ItemStack> stacks2D;

        for (OctahedralGroup symmetry : OctahedralGroup.values()) {
            Matrix3fc transform = symmetry.transformation();
            //noinspection SuspiciousNameCombination
            dimensions.set(this.length, this.width, this.height).mul(transform);
            final int actualLength = Math.abs(Math.round(dimensions.x));
            final int actualWidth = Math.abs(Math.round(dimensions.y));
            final int actualHeight = Math.abs(Math.round(dimensions.z));

            if (actualHeight != 1) {
                continue; // just remove all the transforms that use height
            }

            final int offsetX = dimensions.x < 0 ? actualLength - 1 : 0;
            final int offsetY = dimensions.y < 0 ? actualWidth - 1 : 0;
            stacks2D = new ArrayList<>(Collections.nCopies(actualLength * actualWidth, ItemStack.EMPTY));
            for (int z = 0; z < this.height; z++) {
                for (int y = 0; y < this.width; y++) {
                    for (int x = 0; x < this.length; x++) {
                        coordinates.set(x, y, z).mul(transform);
                        int finalX = Math.round(coordinates.x) + offsetX;
                        int finalY = Math.round(coordinates.y) + offsetY;

                        stacks2D.set(
                                finalX + (finalY * actualLength),
                                this.items.get(x + y * this.length + z * this.length * this.width)
                        );
                    }
                }
            }

            //noinspection SuspiciousNameCombination
            CraftingInput craftingInput = CraftingInput.of(actualLength, actualWidth, stacks2D);
            if (!uniques.contains(craftingInput)) {
                uniques.add(craftingInput);
            }
        }
        return uniques;
    }

	public record Positioned(AmalgamationInput input, int left, int back, int top) {
		public static final AmalgamationInput.Positioned EMPTY = new AmalgamationInput.Positioned(AmalgamationInput.EMPTY, 0, 0, 0);
	}
}
