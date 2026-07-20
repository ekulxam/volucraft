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
package survivalblock.volucraft.mixin.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeSlot;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.common.compat.recipeviewer.AmalgamationClientRecipeType;

@Mixin(RecipeViewMenu.class)
public abstract class RecipeViewMenuMixin {
    @Shadow
    public abstract ReliableClientRecipeType getClientRecipeType();

    @ModifyReturnValue(method = "calculateRecipesPerPage", at = @At("RETURN"))
    private int limitToOneAmalgamationRecipe(int original) {
        if (this.getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE) {
            return Math.min(original, 1);
        }
        return original;
    }

    @WrapOperation(method = "updateByPage", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;IIIZ)Lcc/cassian/rrv/common/recipe/inventory/RecipeSlot;", ordinal = 0))
    private RecipeSlot getShovedIntoTheCorner(Container viewContainer, int index, int x, int y, boolean highlightWithoutContents, Operation<RecipeSlot> original, @Local(name = "slot") Slot slot) {
        if (this.getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE && slot instanceof AmalgamationClientRecipeType.RecipeSlotShovedIntoACorner) {
            return AmalgamationClientRecipeType.INSTANCE.new RecipeSlotShovedIntoACorner(viewContainer, index, x, y, highlightWithoutContents);
        }
        return original.call(viewContainer, index, x, y, highlightWithoutContents);
    }

    @SuppressWarnings("unused")
    @Mixin(RecipeViewMenu.SlotDefinition.class)
    public abstract static class SlotDefinitionMixin {
        @Shadow
        @Final
        RecipeViewMenu this$0;

        @WrapOperation(method = "addItemSlot", at = @At(value = "NEW", target = "(Lnet/minecraft/world/Container;IIIZ)Lcc/cassian/rrv/common/recipe/inventory/RecipeSlot;"))
        private RecipeSlot getShovedIntoTheCorner(Container viewContainer, int index, int x, int y, boolean highlightWithoutContents, Operation<RecipeSlot> original) {
            if (this.this$0.getClientRecipeType() == AmalgamationClientRecipeType.INSTANCE) {
                return AmalgamationClientRecipeType.INSTANCE.new RecipeSlotShovedIntoACorner(viewContainer, index, x, y, highlightWithoutContents);
            }
            return original.call(viewContainer, index, x, y, highlightWithoutContents);
        }
    }
}
