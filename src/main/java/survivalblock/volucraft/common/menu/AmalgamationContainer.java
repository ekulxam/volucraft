package survivalblock.volucraft.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;

import java.util.List;

public interface AmalgamationContainer extends Container, StackedContentsCompatible {
    List<ItemStack> getItems();

    AmalgamationInput asCraftInput();
}
