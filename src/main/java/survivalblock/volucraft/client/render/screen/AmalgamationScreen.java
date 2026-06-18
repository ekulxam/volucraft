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
import org.joml.*;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;
import survivalblock.volucraft.mixin.client.AbstractContainerScreenAccessor;

import java.lang.Math;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static survivalblock.volucraft.client.render.CubeOfSlotsRenderer.centerFromScale;

public class AmalgamationScreen extends AbstractContainerScreen<AmalgamationMenu> {
    public static final Identifier CRAFTING_TABLE_LOCATION = Volucraft.id("textures/gui/container/amalgamation_alt.png");
    public static final Identifier SLOT_CUBE_TEXTURE = Volucraft.id("textures/gui/container/slots.png");
    public static final Identifier TRANSLUCENT_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_translucent.png");
    private static final Identifier HIGHLIGHTED_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_highlight.png");

    private static final float EXPANSION_STEP = 0.05F;

    private final CubeModel cubeModel;
    private final CubeModel cubeModelWithItem;

    public final Map<Integer, Vector3f> slotScreenPositions = new ConcurrentHashMap<>();

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
        if (((AbstractContainerScreenAccessor) this).volucraft$getClickedSlot() != null) {
            return original;
        }
        final double x = event.x();
        final int xo = this.leftPos + 186;
        if (x >= xo && x <= xo + 150) {
            final double y = event.y();
            final int yo = (this.height - this.imageHeight) / 2 + 8;
            if (y >= yo && y <= yo + 150) {
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

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        rot.y = (float) Math.clamp(rot.y, -Math.PI / 2, Math.PI / 2);
        rot.x = (float) (rot.x % (Math.PI * 2));

        int xo = this.leftPos;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 512);

        // render cube (center at 261, 83) in a 150x150 area
        //graphics. // DEATH
        //int selected = (int) ((Util.getMillis() / 100F) % Volucraft.SLOTS);
        int selected = this.getHovered3DSlot(mouseX, mouseY);
        int cubeX0 = xo + 186;
        int cubeY0 = yo + 8;
        NonNullList<ItemStack> items = NonNullList.withSize(Volucraft.SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, this.menu.getSlot(i + 1).getItem());
        }
        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        this.slotScreenPositions,
                        this.cubeModel,
                        this.cubeModelWithItem,
                        SLOT_CUBE_TEXTURE,
                        TRANSLUCENT_SLOT_CUBE,
                        HIGHLIGHTED_SLOT_CUBE,
                        items,
                        selected,
                        this.expansion,
                        new Quaternionf().rotateX(rot.y).rotateY(-rot.x),
                        cubeX0,
                        cubeY0,
                        cubeX0 + 150,
                        cubeY0 + 150,
                        11F,
                        graphics.scissorStack.peek()
                )
        );
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    // begin credit: AI (what the heck is this)
    public int getHovered3DSlot(double mouseX, double mouseY) {
        int xo = this.leftPos + 186;
        int yo = ((this.height - this.imageHeight) / 2) + 8;
        int pipWidth = 150;
        int pipHeight = 150;

        if (mouseX < xo || mouseX > xo + pipWidth || mouseY < yo || mouseY > yo + pipHeight) {
            return -1;
        }

        int guiScale = (int) this.minecraft.getWindow().getGuiScale();
        if (guiScale <= 0) guiScale = 1;

        float fboWidth = pipWidth * guiScale;
        float fboHeight = pipHeight * guiScale;

        // --- THE Y-AXIS INVERSION FIX ---
        // If the Y mapping was upside down relative to your physical mouse,
        // we mirror the mouse position around the vertical center of the PIP box.
        double pipCenterY = yo + (pipHeight / 2.0);
        double correctedMouseY = pipCenterY - (mouseY - pipCenterY);

        float expand = (Math.clamp(this.expansion, 0, 1) * 1.5F + 1) * 1.2F;
        float pivotY = 9.0F / 16.0F;
        float renderCenter = 6.5F;

        int closestSlot = -1;

        // --- THE Z-SORTING FIX ---
        // Change this to Float.POSITIVE_INFINITY if it was picking the back slots instead of the front slots!
        float closestZ = Float.POSITIVE_INFINITY;

        for (int i = 0; i < Volucraft.SLOTS; i++) {
            int slotX = (i % Volucraft.SIDE_LENGTH) - 1;
            int slotZ = ((i / Volucraft.SIDE_LENGTH) % Volucraft.SIDE_LENGTH) - 1;
            int slotY = (i / (Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH)) - 1;

            // This is the exact matrix stack from the version that was highly effective
            Matrix4f transformMatrix = new Matrix4f();
            transformMatrix.translate(fboWidth / 2.0F, fboHeight, 0.0F);

            float internalScale = guiScale * 11.0F;
            transformMatrix.scale(internalScale, internalScale, -internalScale);

            transformMatrix.scale(1.0F, -1.0F, -1.0F);
            transformMatrix.translate(0, renderCenter, 0);

            transformMatrix.translate(0, pivotY, 0);
            transformMatrix.rotate(new Quaternionf().rotateX(rot.y).rotateY(-rot.x));
            transformMatrix.translate(0, -pivotY, 0);

            Vector4f projectedPos = new Vector4f(slotX * expand, slotY * expand, slotZ * expand, 1.0F);
            projectedPos.mul(transformMatrix);

            // Convert the raw framebuffer coordinates back out to GUI mouse pixels safely
            float calculatedMouseX = xo + (projectedPos.x / guiScale);
            float calculatedMouseY = yo + (projectedPos.y / guiScale);

            // --- THE DYNAMIC HITBOX FIX ---
            // Instead of a static circle, let's calculate a dynamic half-width.
            // As 'expand' shrinks, the hitbox shrinks with it so they never overlap.
            // 11.0F is your render scale; 4.5F matches your masterScale factor.
            float halfSlotSize = (1.0F * 11.0F * 4.5F) / (2.0F * guiScale);

            // Tighten the bounds slightly to match your preference (e.g., 85% of full slot size)
            halfSlotSize *= 0.85F;

            // Define a clean bounding box around the projected slot center
            float minX = calculatedMouseX - halfSlotSize;
            float maxX = calculatedMouseX + halfSlotSize;
            float minY = calculatedMouseY - halfSlotSize;
            float maxY = calculatedMouseY + halfSlotSize;

            // Check if the mouse cursor falls precisely inside this specific slot's square footprint
            if (mouseX >= minX && mouseX <= maxX && correctedMouseY >= minY && correctedMouseY <= maxY) {
                // Z-Sorting: Always favor the slot closest to the front screen
                if (projectedPos.z < closestZ) {
                    closestZ = projectedPos.z;
                    closestSlot = i;
                }
            }
        }

        return closestSlot;
    }
    // end credit: AI
}
