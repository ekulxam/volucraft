package survivalblock.volucraft.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;

public class AmalgamationMenu extends AbstractContainerMenu {
    protected final AmalgamationContainer craftSlots = new TransientAmalgamationContainer(this);
    protected final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;

    protected AmalgamationMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        this.access = ContainerLevelAccess.NULL;
    }

    public AmalgamationMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(VolucraftMenuTypes.AMALGAMATING, containerId);
        this.access = access;
    }

    public AmalgamationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);
        this.access.execute((_, _) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public boolean stillValid(final Player player) {
        return stillValid(this.access, player, VolucraftBlocks.AMALGAMATION_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return null;
    }

    @Override
    public boolean canTakeItemForPickAll(final ItemStack carried, final Slot target) {
        return target.container != this.resultSlots && super.canTakeItemForPickAll(carried, target);
    }
}
