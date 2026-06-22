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
    public static final Identifier TRANSLUCENT_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_translucent.png");
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
                rot.add((float) dx * sensitivity, (float) dy * sensitivity);
            }
        }
        return original;
    }

    @Override
    protected void init() {
        super.init();
        this.expansionButton = this.addRenderableWidget(
                CycleButton.booleanBuilder(Component.translatable("container.volucraft.amalgamating.expandbutton.shrink"), Component.translatable("container.volucraft.amalgamating.expandbutton.expand"), this.shouldExpand)
                        .displayOnlyValue()
                        .create(this.width / 2 - 65, this.height / 2 - 50, 60, 20, Component.translatable("container.volucraft.amalgamating.expandbutton"), (_, value) -> this.shouldExpand = value)
        );
    }

    @Override
    protected void containerTick() {
        if (shouldExpand) {
            if (expansion >= 1) {
                expansion = 1;
            } else {
                expansion += EXPANSION_STEP;
            }
        } else {
            if (expansion <= 0) {
                expansion = 0;
            } else {
                expansion -= EXPANSION_STEP;
            }
        }
    }

    public Quaternionfc rotation() {
        return new Quaternionf().rotateX(rot.y).rotateY(-rot.x);
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        rot.y = (float) Math.clamp(rot.y, -Math.PI / 2, Math.PI / 2); // clamp -90, 90 otherwise the turn direction becomes inverted
        rot.x = (float) (rot.x % (Math.PI * 2)); // simple mod 360 deg so the numbers don't explode

        int xo = this.leftPos;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 512);

        int cubeX0 = xo + SLOTS_X_OFFSET;
        int cubeY0 = yo + SLOTS_Y_OFFSET;
        graphics.fill(cubeX0, cubeY0, cubeX0 + SLOTS_SIDE, cubeY0 + SLOTS_SIDE, VolucraftClientConfig.INSTANCE.getCubeBackgroundColor());

        // render cube (center at 261, 83) in a 150x150 area
        int selected = this.getHovered3DSlot(mouseX, mouseY, PICTURE_IN_PICTURE_SCALE, rotation(), Volucraft.debugSlotSelector ? graphics : null);
        NonNullList<ItemStack> items = NonNullList.withSize(Volucraft.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, this.menu.getSlot(i + 1).getItem());
        }
        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        this.cubeModel,
                        this.cubeModelWithItem,
                        SLOT_CUBE_TEXTURE,
                        TRANSLUCENT_SLOT_CUBE,
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
        final double invertedMouseY = yo * 2 + SLOTS_SIDE - mouseY - 3;

        int closestSlot = -1;
        float closestZ = Float.POSITIVE_INFINITY;

        for (int i = 0; i < Volucraft.SLOTS; i++) {
            Vector3f coordinates = new Vector3f();
            CubeOfSlotsRenderer.transformByIndex(i, coordinates::set);

            // see the else branch of PictureInPictureRenderer#prepare
            Matrix4f matrix = new Matrix4f();
            matrix.translate(side / 2.0F, side, 0.0F);
            float scaleScaledToGUI = guiScale * scale;
            matrix.scale(scaleScaledToGUI, scaleScaledToGUI, -scaleScaledToGUI);

            // see CubeOfSlotsRenderer
            /*
            This scale call works better than two FLIPs because the second flip
            only undoes the first one around the pivot

            Also, PoseStack is weird and I don't have access to push and pop here
            (but I wish I did, it would save me so much time and brainpower)
             */
            matrix.scale(1, -1, -1);
            matrix.translate(0, renderCenter, 0);
            matrix.translate(0, CubeOfSlotsRenderer.CUBE_CENTER_OFFSET, 0); // pivot point
            matrix.rotate(rotation); // rotate around pivot point
            matrix.translate(0, -CubeOfSlotsRenderer.CUBE_CENTER_OFFSET, 0); // unpivot point

            // transformByIndex
            Vector4f projectedPos = new Vector4f(coordinates.x * expand, coordinates.y * expand, coordinates.z * expand, 1.0F);
            projectedPos.mul(matrix);
            float projectedMouseX = xo + (projectedPos.x / guiScale);
            float projectedMouseY = yo + (projectedPos.y / guiScale);

            // woah magic number
            float halfSlotSize = (5.1F * scale) / guiScale * 0.5F;
            if (Math.abs(mouseX - projectedMouseX) <= halfSlotSize && Math.abs(invertedMouseY - projectedMouseY) <= halfSlotSize) {
                if (projectedPos.z < closestZ) {
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
                        2000962815
                );
            }
        }

        return closestSlot;
    }
}
