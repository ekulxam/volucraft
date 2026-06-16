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
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftMenuTypes;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;
import survivalblock.volucraft.common.menu.recipe.AmalgamationRecipe;
import survivalblock.volucraft.common.menu.slot.AmalgamationResultSlot;

import java.util.List;
import java.util.Optional;

public class AmalgamationMenu extends AbstractContainerMenu {
    protected final AmalgamationContainer craftSlots = new TransientAmalgamationContainer(this);
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
        this.addResultSlot(this.player, 124, 35);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            this.addSlot(new Slot(this.craftSlots, i, i, 0));
        }
        this.addStandardInventorySlots(inventory, 8, 84);
    }

    public AmalgamationMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    protected Slot addResultSlot(final Player player, final int x, final int y) {
        return this.addSlot(new AmalgamationResultSlot(player, this.craftSlots, this.resultSlots, 0, x, y));
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
        ServerPlayer serverPlayer = (ServerPlayer)player;
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

        resultSlots.setItem(0, result);
        menu.setRemoteSlot(0, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));
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
        return stillValid(this.access, player, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int slotIndex) {
        return ItemStack.EMPTY; // FIXME
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
        return this.slots.subList(1, 1 + Volucraft.SLOTS);
    }
}
