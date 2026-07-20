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
package survivalblock.volucraft.client.render.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.compat.config.VolucraftClientConfig;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderer;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;
import survivalblock.volucraft.mixin.client.AbstractContainerScreenAccessor;

import java.lang.Math;

public class AmalgamationScreen extends AbstractContainerScreen<AmalgamationMenu> {
    public static final Identifier CRAFTING_TABLE_LOCATION = Volucraft.id("textures/gui/container/amalgamation.png");
    public static final Identifier SLOT_CUBE_TEXTURE = Volucraft.id("textures/gui/container/slots.png");
    public static final Identifier HIGHLIGHTED_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_highlight.png");

    public static final float PICTURE_IN_PICTURE_SCALE = 11F;

    private static final float EXPANSION_STEP = 0.05F;
    public static final int SLOTS_SIDE = 150;
    private static final int SLOTS_X_OFFSET = 186;
    private static final int SLOTS_Y_OFFSET = 8;

    private final CubeModel cubeModel;
    private final CubeModel cubeModelWithItem;

    @SuppressWarnings({"FieldCanBeLocal", "unused", "NotNullFieldNotInitialized"})
    private CycleButton<Boolean> expansionButton;
    private boolean shouldExpand = false;
    private float expansion = 0F;
    private final Vector2f rot = new Vector2f((float) (Math.PI / 4), (float) (Math.PI / 4));

    public AmalgamationScreen(AmalgamationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 344, 166);
        this.cubeModel = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE));
        this.cubeModelWithItem = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE), RenderTypes::entityTranslucentEmissive);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        boolean original = super.mouseDragged(event, dx, dy);
        // handle rotation if within bounds of cube area
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        if (accessor.volucraft$getClickedSlot() != null || !accessor.volucraft$getDraggingItem().isEmpty()) {
            return original;
        }
        final double x = event.x();
        final int xo = this.leftPos + SLOTS_X_OFFSET;
        if (x >= xo && x <= xo + SLOTS_SIDE) {
            final double y = event.y();
            final int yo = (this.height - this.imageHeight) / 2 + SLOTS_Y_OFFSET;
            if (y >= yo && y <= yo + SLOTS_SIDE) {
                float sensitivity = 0.05F;
                this.rot.add((float) dx * sensitivity, (float) dy * sensitivity);
                this.onRotationChanged();
            }
        }
        return original;
    }

    protected void onRotationChanged() {
        this.onDisplayTouched();
    }

    protected void onExpandToggled() {
        this.onDisplayTouched();
    }

    protected void onDisplayTouched() {
    }

    @Override
    protected void init() {
        super.init();
        this.expansionButton = this.addRenderableWidget(
                CycleButton.booleanBuilder(Component.translatable("container.volucraft.amalgamating.expandbutton.shrink"), Component.translatable("container.volucraft.amalgamating.expandbutton.expand"), this.shouldExpand)
                        .displayOnlyValue()
                        .create(this.width / 2 - 65, this.height / 2 - 50, 60, 20, Component.translatable("container.volucraft.amalgamating.expandbutton"), (_, value) -> {
                            this.shouldExpand = value;
                            this.onExpandToggled();
                        })
        );
    }

    @Override
    protected void containerTick() {
        if (this.shouldExpand) {
            if (this.expansion >= 1) {
                this.expansion = 1;
            } else {
                this.expansion += EXPANSION_STEP;
            }
        } else {
            if (this.expansion <= 0) {
                this.expansion = 0;
            } else {
                this.expansion -= EXPANSION_STEP;
            }
        }
    }

    public Quaternionfc rotation() {
        return new Quaternionf().rotateX(this.rot.y).rotateY(-this.rot.x);
    }

    protected float gameCubeAnimationProgress() {
        return 1.0F;
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        this.rot.y = (float) Math.clamp(this.rot.y, -Math.PI / 2, Math.PI / 2); // clamp -90, 90 otherwise the turn direction becomes inverted
        this.rot.x = (float) (this.rot.x % (Math.PI * 2)); // simple mod 360 deg so the numbers don't explode

        int xo = this.leftPos;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 512);

        int cubeX0 = xo + SLOTS_X_OFFSET;
        int cubeY0 = yo + SLOTS_Y_OFFSET;
        graphics.fill(cubeX0, cubeY0, cubeX0 + SLOTS_SIDE, cubeY0 + SLOTS_SIDE, VolucraftClientConfig.INSTANCE.getCubeBackgroundColor());

        // render cube (center at 261, 83) in a 150x150 area
        int selected = this.getHovered3DSlot(mouseX, mouseY, PICTURE_IN_PICTURE_SCALE, rotation(), VolucraftClient.debugSlotSelector ? graphics : null);
        NonNullList<ItemStack> items = NonNullList.withSize(Volucraft.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, this.menu.getSlot(i + 1).getItem());
        }

        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        this.cubeModel,
                        this.cubeModelWithItem,
                        SLOT_CUBE_TEXTURE,
                        HIGHLIGHTED_SLOT_CUBE,
                        items,
                        selected,
                        this.expansion,
                        rotation(),
                        cubeX0,
                        cubeY0,
                        cubeX0 + SLOTS_SIDE,
                        cubeY0 + SLOTS_SIDE,
                        PICTURE_IN_PICTURE_SCALE,
                        this.gameCubeAnimationProgress(),
                        graphics.scissorStack.peek()
                )
        );
    }

    public int getHovered3DSlot(double mouseX, double mouseY, final float scale, final Quaternionfc rotation, @Nullable GuiGraphicsExtractor graphics) {
        int xo = this.leftPos + SLOTS_X_OFFSET;
        int yo = ((this.height - this.imageHeight) / 2) + SLOTS_Y_OFFSET;

        final int guiScale = this.minecraft.getWindow().getGuiScale();

        return getHovered3DSlot(mouseX, mouseY, scale, rotation, xo, yo, guiScale, this.expansion, graphics);
    }

    /**
     * We basically use the {@link com.mojang.blaze3d.vertex.PoseStack} and reverse the transforms
     * @see net.minecraft.client.gui.render.pip.PictureInPictureRenderer
     * @see CubeOfSlotsRenderer
     */
    public static int getHovered3DSlot(double mouseX, double mouseY, float scale, Quaternionfc rotation, int xo, int yo, int guiScale, float lerpExpansion, @Nullable GuiGraphicsExtractor graphics) {
        if (mouseX < xo || mouseX > xo + SLOTS_SIDE || mouseY < yo || mouseY > yo + SLOTS_SIDE) {
            return -1;
        }

        final int side = SLOTS_SIDE * guiScale; // can be one variable because square
        final float renderCenter = CubeOfSlotsRenderer.centerFromScale(scale);
        final float expand = CubeOfSlotsRenderer.calculateExpansion(lerpExpansion);

        int closestSlot = -1;
        float closestZ = Float.NEGATIVE_INFINITY;

        Matrix4fStack poseStack = new Matrix4fStack(2);
        final CubeOfSlotsRenderer.Translator translator = (x, y, z) -> poseStack.translate(x * expand, y * expand, z * expand);

        // see the else branch of PictureInPictureRenderer#prepare
        poseStack.translate(side / 2.0F, side, 0.0F);
        float scaleScaledToGUI = guiScale * scale;
        poseStack.scale(scaleScaledToGUI, scaleScaledToGUI, -scaleScaledToGUI);

        // see CubeOfSlotsRenderer#renderToTexture
        poseStack.rotate(CubeOfSlotsRenderer.FLIP);
        poseStack.translate(0, renderCenter, 0);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            poseStack.pushMatrix();

            poseStack.translate(0, CubeOfSlotsRenderer.CUBE_CENTER_OFFSET, 0); // pivot point
            poseStack.rotate(CubeOfSlotsRenderer.FLIP);
            poseStack.rotate(rotation); // rotate around pivot point
            poseStack.translate(0, -CubeOfSlotsRenderer.CUBE_CENTER_OFFSET, 0); // unpivot point
            CubeOfSlotsRenderer.transformByIndex(i, translator);

            // 2D time
            Vector4f projectedPos = new Vector4f(0, 0, 0, 1.0F);
            projectedPos.mul(poseStack);
            float projectedMouseX = xo + (projectedPos.x / guiScale);
            float projectedMouseY = yo + (projectedPos.y / guiScale) + 3; // small offset because it's too high somehow

            poseStack.popMatrix();

            // woah magic number
            float halfSlotSize = (5.1F * scale) / guiScale * 0.5F;
            if (Math.abs(mouseX - projectedMouseX) <= halfSlotSize && Math.abs(mouseY - projectedMouseY) <= halfSlotSize) {
                if (projectedPos.z > closestZ) {
                    closestZ = projectedPos.z;
                    closestSlot = i;
                }
            }

            if (graphics != null) {
                graphics.fill(
                        (int) (projectedMouseX - halfSlotSize),
                        (int) (projectedMouseY - halfSlotSize),
                        (int) (projectedMouseX + halfSlotSize),
                        (int) (projectedMouseY + halfSlotSize),
                        0x774444FF
                );
            }
        }

        return closestSlot;
    }
}
