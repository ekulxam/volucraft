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
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;

public class VolucraftClientRRVCompat implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        //noinspection CodeBlock2Expr
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE.getRecipesForType(VolucraftRecipeTypes.AMALGAMATION).forEach(recipeHolder -> {
                if (!(recipeHolder.value() instanceof ShapedAmalgamationRecipe recipe)) {
                    return;
                }
                recipeList.add(new AmalgamationClientRecipe(
                        recipeHolder.id().identifier(),
                        recipe.getResult(),
                        recipe.getLength(),
                        recipe.getWidth(),
                        recipe.getHeight(),
                        recipe.getIngredients()
                ));
            });
        });
    }
}
