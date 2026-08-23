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
package survivalblock.volucraft.client.compat.recipeviewer;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;
import survivalblock.volucraft.mixin.compat.recipeviewer.ItemFiltersAccessor;

public class VolucraftClientRRVCompat implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        //noinspection CodeBlock2Expr
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(VolucraftRecipeTypes.AMALGAMATION).forEach(recipeHolder -> {
                AmalgamationRecipe value = recipeHolder.value();
                if (value instanceof ShapedAmalgamationRecipe recipe) {
                    recipeList.add(AmalgamationClientRecipe.fromShaped(recipeHolder, recipe));
                } else if (value instanceof ShapelessAmalgamationRecipe recipe) {
                    recipeList.add(AmalgamationClientRecipe.fromShapeless(recipeHolder, recipe));
                }
            });
        });
    }

    static {
        var colorComputer = CubeOfSlotsRenderer.COLOR_COMPUTER;
        // see ItemViewOverlay
        CubeOfSlotsRenderer.COLOR_COMPUTER = (stack, anim) -> {
            if (anim < 1.0F) {
                return colorComputer.getColor(stack, anim);
            }
            if (ItemViewOverlay.INSTANCE.isItemFilterMode()) {
                var item = stack.getItem();
                //noinspection ConstantValue
                if (ItemViewOverlay.INSTANCE.availableItems().stream().noneMatch(itemStack -> itemStack.getItem() == item)
                        && ItemFiltersAccessor.volucraft$getTooltipMatch(stack, ItemViewOverlay.INSTANCE.getCurrentQuery()) == 0
                ) {
                    return 0x44888888;
                }
            }
            return colorComputer.getColor(stack, anim);
        };
    }
}
