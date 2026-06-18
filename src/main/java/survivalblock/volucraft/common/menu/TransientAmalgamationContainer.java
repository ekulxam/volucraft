package survivalblock.volucraft.common.menu;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * @see net.minecraft.world.inventory.TransientCraftingContainer
 */
public class TransientAmalgamationContainer implements AmalgamationContainer {
	private final NonNullList<ItemStack> items;
    private final int length;
    private final int width;
    private final int height;
	private final AbstractContainerMenu menu;

    public TransientAmalgamationContainer(final AbstractContainerMenu menu, final int length, final int width, final int height) {
        this(menu, length, width, height, NonNullList.withSize(length * width * height, ItemStack.EMPTY));
    }

	public TransientAmalgamationContainer(final AbstractContainerMenu menu, final int length, final int width, final int height, final NonNullList<ItemStack> items) {
        this.menu = menu;
        this.length = length;
        this.width = width;
        this.height = height;
        this.items = items;
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.items) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(final int slot) {
		return slot >= this.getContainerSize() ? ItemStack.EMPTY : this.items.get(slot);
	}

	@Override
	public ItemStack removeItemNoUpdate(final int slot) {
		return ContainerHelper.takeItem(this.items, slot);
	}

	@Override
	public ItemStack removeItem(final int slot, final int count) {
		ItemStack result = ContainerHelper.removeItem(this.items, slot, count);
		if (!result.isEmpty()) {
			this.menu.slotsChanged(this);
		}

		return result;
	}

	@Override
	public void setItem(final int slot, final ItemStack itemStack) {
		this.items.set(slot, itemStack);
		this.menu.slotsChanged(this);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(final Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

	@Override
	public List<ItemStack> getItems() {
		return List.copyOf(this.items);
	}

    @Override
	public void fillStackedContents(final StackedItemContents contents) {
		for (ItemStack itemStack : this.items) {
			contents.accountSimpleStack(itemStack);
		}
	}
}
