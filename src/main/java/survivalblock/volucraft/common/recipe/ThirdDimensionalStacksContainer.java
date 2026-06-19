package survivalblock.volucraft.common.recipe;

import net.minecraft.world.item.ItemStack;

public interface ThirdDimensionalStacksContainer {
    ItemStack getItem(int x, int y, int z);

    static ThirdDimensionalStacksContainer fromArray(int length, int width, ItemStack... items) {
        return (x, y, z) -> items[x + y * length + z * length * width];
    }
}