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
package survivalblock.volucraft.mixin.extrude;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @ModifyVariable(method = "lambda$readAdditionalSaveData$0", at = @At("HEAD"), argsOnly = true, name = "p")
    private ServerRecipeBook.Packed addPairsFromDecoding(ServerRecipeBook.Packed p) {
        return new ServerRecipeBook.Packed(
                p.settings(),
                this.volucraft$mergeAmalPairs(p.known()),
                this.volucraft$mergeAmalPairs(p.highlight())
        );
    }

    @Unique
    private List<ResourceKey<Recipe<?>>> volucraft$mergeAmalPairs(List<ResourceKey<Recipe<?>>> original) {
        RecipeManager recipeManager = this.server.getRecipeManager();

        var pairs = recipeManager.volucraft$getRecipePairs();
        if (pairs.isEmpty()) {
            return original;
        }

        var reversed = pairs.inverse();

        List<ResourceKey<Recipe<?>>> additions = new ArrayList<>();
        List<ResourceKey<Recipe<?>>> modified = new ArrayList<>(original);

        ResourceKey<Recipe<?>> other;
        for (ResourceKey<Recipe<?>> id : modified) {
            other = null;
            if (pairs.containsKey(id)) {
                other = pairs.get(id);
            } else if (reversed.containsKey(id)) {
                other = reversed.get(id);
            }

            if (other != null && !modified.contains(other)) {
                additions.add(other);
            }
        }

        if (additions.isEmpty()) {
            return original;
        }

        modified.addAll(additions);
        return modified;
    }
}
