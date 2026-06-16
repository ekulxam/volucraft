package survivalblock.volucraft.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;

import java.util.List;

public interface AmalgamationContainer extends Container, StackedContentsCompatible {
    List<ItemStack> getItems();

    default AmalgamationInput asCraftInput() {
        return this.asPositionedCraftInput().input();
    }

    default AmalgamationInput.Positioned asPositionedCraftInput() {
        return AmalgamationInput.ofPositioned(this.getLength(), this.getWidth(), this.getHeight(), this.getItems());
    }

    default int getLength() {
        return Volucraft.SIDE_LENGTH;
    }

    default int getWidth() {
        return Volucraft.SIDE_LENGTH;
    }

    default int getHeight() {
        return Volucraft.SIDE_LENGTH;
    }
}
