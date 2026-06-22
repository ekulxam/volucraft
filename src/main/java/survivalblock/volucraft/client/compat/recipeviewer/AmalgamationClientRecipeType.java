package survivalblock.volucraft.client.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.block.AmalgamationTableBlock;
import survivalblock.volucraft.common.init.VolucraftItems;

public class AmalgamationClientRecipeType implements ReliableClientRecipeType {

    public static final AmalgamationClientRecipeType INSTANCE = new AmalgamationClientRecipeType();
    private static final Identifier ID = Volucraft.id("amalgamation");

    @Override
    public Component getDisplayName() {
        return AmalgamationTableBlock.CONTAINER_TITLE;
    }

    @Override
    public int getDisplayWidth() {
        return 150;
    }

    @Override
    public int getDisplayHeight() {
        return 141;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return null;
    }

    @Override
    public int getSlotCount() {
        return Volucraft.SLOTS + 1;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, this.getDisplayWidth() / 2 - 8, 0);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            slotDefinition.addItemSlot(i + 1, i, 0);
        }
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ItemStack getIcon() {
        return VolucraftItems.AMALGAMATION_TABLE.getDefaultInstance();
    }
}