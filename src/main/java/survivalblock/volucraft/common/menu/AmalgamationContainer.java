package survivalblock.volucraft.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;

import java.util.List;

/**
 * @see net.minecraft.world.inventory.CraftingContainer
 */
public interface AmalgamationContainer extends Container, StackedContentsCompatible {
    List<ItemStack> getItems();

    default AmalgamationInput asCraftInput() {
        return this.asPositionedCraftInput().input();
    }

    default AmalgamationInput.Positioned asPositionedCraftInput() {
        return AmalgamationInput.ofPositioned(this.getLength(), this.getWidth(), this.getHeight(), this.getItems());
    }

    int getLength();

    int getWidth();

    int getHeight();
}
