package survivalblock.volucraft.client.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
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
        return 344;
    }

    @Override
    public int getDisplayHeight() {
        return 166;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return AmalgamationScreen.CRAFTING_TABLE_LOCATION;
    }

    @Override
    public int getSlotCount() {
        return Volucraft.SLOTS + 1;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 0, 0);
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