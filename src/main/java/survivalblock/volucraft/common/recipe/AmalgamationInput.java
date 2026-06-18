package survivalblock.volucraft.common.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Using x as length, y as width, and z as height
 * @see net.minecraft.world.item.crafting.CraftingInput
 */
@SuppressWarnings("unused")
public class AmalgamationInput implements RecipeInput {
	public static final AmalgamationInput EMPTY = new AmalgamationInput(0, 0, 0, List.of());
    private final int length;
	private final int width;
	private final int height;
	private final List<ItemStack> items;
	private final StackedItemContents stackedContents = new StackedItemContents();
	private final int ingredientCount;

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
                boolean squareEmpty = true;

                for (int y = 0; y < width; y++) {
                    boolean rowEmpty = true;

                    for (int x = 0; x < length; x++) {
                        ItemStack item = items.get(x + y * length + z * length * width); // hope i'm correct
                        if (!item.isEmpty()) {
                            left = Math.min(left, x);
                            right = Math.max(right, x);
                            rowEmpty = false;
                            squareEmpty = false;
                        }
                    }

                    if (!rowEmpty) {
                        back = Math.min(back, y);
                        front = Math.max(front, y);
                    }
                }

                if (!squareEmpty) {
                    top = Math.min(top, z);
                    bottom = Math.max(bottom, z);
                }
            }

            int newLength = right - left + 1;
			int newWidth = back - front + 1;
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
                        int index = x + left + (y + back) * length + (z + top) * length * width;
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
        // TODO: check
		return this.items.get(x + y * this.length + z * this.length * this.width);
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

	public record Positioned(AmalgamationInput input, int left, int back, int top) {
		public static final AmalgamationInput.Positioned EMPTY = new AmalgamationInput.Positioned(AmalgamationInput.EMPTY, 0, 0, 0);
	}
}
