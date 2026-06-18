package survivalblock.volucraft.mixin.client;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("clickedSlot")
    Slot volucraft$getClickedSlot();

    @Accessor("draggingItem")
    ItemStack volucraft$getDraggingItem();
}
