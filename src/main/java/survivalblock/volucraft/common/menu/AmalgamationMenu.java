package survivalblock.volucraft.common.menu;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftBlocks;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.AmalgamationInput;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;
import survivalblock.volucraft.common.menu.slot.AmalgamationResultSlot;

import java.util.List;
import java.util.Optional;

/**
 * @see AbstractCraftingMenu
 * @see CraftingMenu
 */
public class AmalgamationMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT_INDEX = 0;
    public static final int CRAFT_SLOTS_START = 1;
    public static final int CRAFT_SLOTS_END = CRAFT_SLOTS_START + Volucraft.SLOTS; // 28
    public static final int INV_SLOTS_START = CRAFT_SLOTS_END; // 28
    public static final int INV_SLOTS_END = CRAFT_SLOTS_END + 27; // 55
    public static final int HOTBAR_SLOTS_START = INV_SLOTS_END; // 55
    public static final int HOTBAR_SLOTS_END = HOTBAR_SLOTS_START + 9; // 64

    protected final AmalgamationContainer craftSlots = new TransientAmalgamationContainer(this, Volucraft.SIDE_LENGTH, Volucraft.SIDE_LENGTH, Volucraft.SIDE_LENGTH);
    protected final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    @Nullable
    private final Player player;

    @SuppressWarnings("unused")
    protected AmalgamationMenu(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
        this.access = ContainerLevelAccess.NULL;
        this.player = null;
    }

    public AmalgamationMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(VolucraftMenuTypes.AMALGAMATING, containerId);
        this.access = access;
        this.player = inventory.player;
        this.addResultSlot(this.player, 80, 35);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            this.addSlot(new Slot(this.craftSlots, i, i, 0));
        }
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    public AmalgamationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected Slot addResultSlot(final Player player, final int x, final int y) {
        return this.addSlot(new AmalgamationResultSlot(player, this.craftSlots, this.resultSlots, RESULT_SLOT_INDEX, x, y));
    }

    @SuppressWarnings("SameParameterValue")
    protected static void slotChangedCraftingGrid(
            final AbstractContainerMenu menu,
            final ServerLevel level,
            final Player player,
            final AmalgamationContainer container,
            final ResultContainer resultSlots,
            final @Nullable RecipeHolder<AmalgamationRecipe> recipeHint
    ) {
        AmalgamationInput input = container.asCraftInput();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<AmalgamationRecipe>> maybeRecipe = level.getServer().getRecipeManager().getRecipeFor(VolucraftRecipeTypes.AMALGAMATION, input, level, recipeHint);
        if (maybeRecipe.isPresent()) {
            RecipeHolder<AmalgamationRecipe> recipeHolder = maybeRecipe.get();
            AmalgamationRecipe recipe = recipeHolder.value();
            if (resultSlots.setRecipeUsed(serverPlayer, recipeHolder)) {
                ItemStack recipeResult = recipe.assemble(input);
                if (recipeResult.isItemEnabled(level.enabledFeatures())) {
                    result = recipeResult;
                }
            }
        }

        resultSlots.setItem(RESULT_SLOT_INDEX, result);
        menu.setRemoteSlot(RESULT_SLOT_INDEX, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), RESULT_SLOT_INDEX, result));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void slotsChanged(final Container container) {
        this.access.execute((level, _) -> {
            if (level instanceof ServerLevel serverLevel) {
                slotChangedCraftingGrid(this, serverLevel, this.player, this.craftSlots, this.resultSlots, null);
            }
        });
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
    public ItemStack quickMoveStack(final Player player, final int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        //noinspection ConstantValue
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();
        if (slotIndex == RESULT_SLOT_INDEX) {
            stack.getItem().onCraftedBy(stack, player);
            if (!this.moveItemStackTo(stack, INV_SLOTS_START, HOTBAR_SLOTS_END, true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(stack, clicked);
        } else if (slotIndex >= INV_SLOTS_START && slotIndex < HOTBAR_SLOTS_END) {
            if (!this.moveItemStackTo(stack, CRAFT_SLOTS_START, CRAFT_SLOTS_END, false)) {
                if (slotIndex < HOTBAR_SLOTS_START) {
                    if (!this.moveItemStackTo(stack, HOTBAR_SLOTS_START, HOTBAR_SLOTS_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, INV_SLOTS_START, INV_SLOTS_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!this.moveItemStackTo(stack, INV_SLOTS_START, HOTBAR_SLOTS_END, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == clicked.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        if (slotIndex == RESULT_SLOT_INDEX) {
            player.drop(stack, false);
        }

        return clicked;
    }

    @Override
    public boolean canTakeItemForPickAll(final ItemStack carried, final Slot target) {
        return target.container != this.resultSlots && super.canTakeItemForPickAll(carried, target);
    }

    @SuppressWarnings("unused")
    public Slot getResultSlot() {
        return this.slots.getFirst();
    }

    @SuppressWarnings("unused")
    public List<Slot> getInputGridSlots() {
        return this.slots.subList(CRAFT_SLOTS_START, CRAFT_SLOTS_END);
    }
}
