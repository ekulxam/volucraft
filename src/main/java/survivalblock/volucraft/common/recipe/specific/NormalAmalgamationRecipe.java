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
package survivalblock.volucraft.common.recipe.specific;

import net.minecraft.world.item.crafting.*;
import org.jspecify.annotations.Nullable;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

/**
 * @see net.minecraft.world.item.crafting.NormalCraftingRecipe
 */
public abstract class NormalAmalgamationRecipe implements AmalgamationRecipe {
    protected final Recipe.CommonInfo commonInfo;
    private @Nullable PlacementInfo placementInfo;
    private boolean translatedFrom2D = false;

    protected NormalAmalgamationRecipe(final Recipe.CommonInfo commonInfo) {
        this.commonInfo = commonInfo;
    }

    @Override
    public abstract RecipeSerializer<? extends NormalAmalgamationRecipe> getSerializer();

    @Override
    public String group() {
        return "volumetric";
    }

    @Override
    public final boolean showNotification() {
        return AmalgamationRecipe.super.showNotification() && this.commonInfo.showNotification();
    }

    @Override
    public boolean isTranslatedFrom2D() {
        return this.translatedFrom2D;
    }

    public void setTranslatedFrom2D(boolean translatedFrom2D) {
        this.translatedFrom2D = translatedFrom2D;
    }

    protected abstract PlacementInfo createPlacementInfo();

    @Override
    public final PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = this.createPlacementInfo();
        }

        return this.placementInfo;
    }
}
