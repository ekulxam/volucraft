/*
 * Copyright (c) 2026-present ekulxam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package survivalblock.volucraft.common.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeSlot;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
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

    /**
     * @see survivalblock.volucraft.common.menu.AmalgamationMenu.SlotShovedIntoACorner
     */
    @SuppressWarnings("InnerClassMayBeStatic")
    public class RecipeSlotShovedIntoACorner extends RecipeSlot {
        public RecipeSlotShovedIntoACorner(Container viewContainer, int index, int x, int y, boolean highlightWithoutContents) {
            super(viewContainer, index, x, y, highlightWithoutContents);
        }

        @Override
        public boolean isHighlightable() {
            return false;
        }
    }
}