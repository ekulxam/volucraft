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

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.client.render.screen.AmalgamationScreen;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.compat.recipeviewer.AmalgamationClientRecipeType;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapedAmalgamationRecipe;
import survivalblock.volucraft.common.recipe.specific.ShapelessAmalgamationRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static survivalblock.volucraft.client.render.screen.AmalgamationScreen.*;

public class AmalgamationClientRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent result;
    private final List<SlotContent> meAndMy27Slots = NonNullList.withSize(Volucraft.SLOTS, SlotContent.of());

    public AmalgamationClientRecipe(Identifier id, ItemStackTemplate resultItem, int length, int width, int height, List<Optional<Ingredient>> inputs) {
        this.id = id;
        this.result = SlotContent.of(resultItem);
        for (int z = 0; z < height; z++) {
            for (int y = 0; y < width; y++) {
                for (int x = 0; x < length; x++) {
                    this.meAndMy27Slots.set(
                            x + y * Volucraft.SIDE_LENGTH + z * Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH,
                            inputs.get(x + y * length + z * length * width)
                                    .map(SlotContent::of)
                                    .orElse(SlotContent.of())
                    );
                }
            }
        }
    }

    public static AmalgamationClientRecipe fromShaped(RecipeHolder<AmalgamationRecipe> recipeHolder, ShapedAmalgamationRecipe recipe) {
        return new AmalgamationClientRecipe(
                recipeHolder.id().identifier(),
                recipe.getResult(),
                recipe.getLength(),
                recipe.getWidth(),
                recipe.getHeight(),
                recipe.getIngredients()
        );
    }

    public static AmalgamationClientRecipe fromShapeless(RecipeHolder<AmalgamationRecipe> recipeHolder, ShapelessAmalgamationRecipe recipe) {
        List<Ingredient> fromRecipe = recipe.getIngredients();
        int size = fromRecipe.size();
        List<Optional<Ingredient>> maybes = new ArrayList<>();
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            if (i < size) {
                maybes.add(Optional.of(fromRecipe.get(i)));
            } else {
                maybes.add(Optional.empty());
            }
        }

        return new AmalgamationClientRecipe(
                recipeHolder.id().identifier(),
                recipe.getResult(),
                Volucraft.SIDE_LENGTH,
                Volucraft.SIDE_LENGTH,
                Volucraft.SIDE_LENGTH,
                maybes
        );
    }

    @Override
    public ReliableClientRecipeType getType() {
        return AmalgamationClientRecipeType.INSTANCE;
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.result);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            slotFillContext.bindSlot(i + 1, this.meAndMy27Slots.get(i));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.meAndMy27Slots;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        final int cubeX0 = recipePosition.left();
        final int cubeY0 = recipePosition.top() + 16;
        ScreenWithCubes screenWithCubes = (ScreenWithCubes) screen;

        final NonNullList<ItemStack> items = NonNullList.withSize(Volucraft.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < this.meAndMy27Slots.size(); i++) {
            List<ItemStack> options = this.meAndMy27Slots.get(i).getValidContents();
            if (options.isEmpty()) {
                continue;
            }
            if (options.size() == 1) {
                items.set(i, options.getFirst());
            }
            items.set(i, options.get((int) ((screenWithCubes.volucraft$calculateTimeOpen() / 30) % options.size())));
        }

        final Vector2f rot = screenWithCubes.volucraft$getRotation();
        rot.y = (float) Math.clamp(rot.y, -Math.PI / 2, Math.PI / 2); // clamp -90, 90 otherwise the turn direction becomes inverted
        rot.x = (float) (rot.x % (Math.PI * 2)); // simple mod 360 deg so the numbers don't explode
        final Quaternionfc rotation = new Quaternionf().rotateX(rot.y).rotateY(-rot.x);

        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        screenWithCubes.volucraft$getCubeModel(),
                        screenWithCubes.volucraft$getCubeModelWithItem(),
                        SLOT_CUBE_TEXTURE,
                        HIGHLIGHTED_SLOT_CUBE,
                        items,
                        AmalgamationScreen.getHovered3DSlot(mouseX, mouseY, PICTURE_IN_PICTURE_SCALE, rotation, 0, 16, Minecraft.getInstance().getWindow().getGuiScale(), 1, VolucraftClient.debugSlotSelector ? graphics : null),
                        1,
                        rotation,
                        cubeX0,
                        cubeY0,
                        cubeX0 + SLOTS_SIDE,
                        cubeY0 + SLOTS_SIDE,
                        PICTURE_IN_PICTURE_SCALE,
                        graphics.scissorStack.peek()
                )
        );
    }
}