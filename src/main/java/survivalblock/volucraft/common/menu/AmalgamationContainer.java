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
package survivalblock.volucraft.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import survivalblock.volucraft.common.recipe.AmalgamationInput;

import java.util.List;

/**
 * @see net.minecraft.world.inventory.CraftingContainer
 */
public interface AmalgamationContainer extends Container, StackedContentsCompatible {
    List<ItemStack> getItems();

    default AmalgamationInput asCraftInput() {
        return this.asPositionedCraftInput().input();
    }

    default AmalgamationInput.Positioned asPositionedCraftInput() {
        return AmalgamationInput.ofPositioned(this.getLength(), this.getWidth(), this.getHeight(), this.getItems());
    }

    int getLength();

    int getWidth();

    int getHeight();

    @Override
    default int getMaxStackSize() {
        return 1;
    }
}
