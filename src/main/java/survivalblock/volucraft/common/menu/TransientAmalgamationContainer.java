package survivalblock.volucraft.common.menu;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;

public class TransientAmalgamationContainer implements AmalgamationContainer {
	private final NonNullList<ItemStack> items;
	private final AbstractContainerMenu menu;

	public TransientAmalgamationContainer(final AbstractContainerMenu menu) {
        this.items = NonNullList.withSize(Volucraft.SLOTS, ItemStack.EMPTY);
		this.menu = menu;
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
	public List<ItemStack> getItems() {
		return List.copyOf(this.items);
	}

    @Override
    public AmalgamationInput.Positioned asPositionedCraftInput() {
        return null;
    }

    @Override
	public void fillStackedContents(final StackedItemContents contents) {
		for (ItemStack itemStack : this.items) {
			contents.accountSimpleStack(itemStack);
		}
	}
}
