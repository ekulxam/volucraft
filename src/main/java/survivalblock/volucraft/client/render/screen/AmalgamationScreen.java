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
        int pipSize = 150;

        // 1. Core window bounds check
        if (mouseX < xo || mouseX > xo + pipSize || mouseY < yo || mouseY > yo + pipSize) {
            return -1;
        }

        float expand = (Math.clamp(this.expansion, 0, 1) * 1.5F + 1) * 1.2F;
        float pivotY = 9.0F / 16.0F; // 0.5625F

        // 2. Generate the view transformation matrix matching your render pipeline exactly
        Matrix4f transformMatrix = new Matrix4f();

        // Step A: Base Entity Flip (Y = -Y, Z = -Z)
        transformMatrix.scale(1.0F, -1.0F, -1.0F);

        // Step B: Pivot manipulation and Rotation
        transformMatrix.translate(0, pivotY, 0);
        transformMatrix.rotate(new Quaternionf().rotateX(rot.y).rotateY(-rot.x));
        transformMatrix.translate(0, -pivotY, 0);

        int closestSlot = -1;
        double closestDistanceSq = Double.MAX_VALUE;
        float closestZ = Float.NEGATIVE_INFINITY;

        // Find the visual pixel center of your 150x150 PIP area
        float renderCenterX = xo + (pipSize / 2.0F);
        float renderCenterY = yo + (pipSize / 2.0F);

        // --- THE TUNING LEVERS ---
        // Since 11F * 4.5F gave massive progress on X, let's keep the baseline scale,
        // but separate them into X and Y multipliers to fix the vertical compression/inversion.
        float baseScale = 11.0F * 4.5F;
        float scaleX = baseScale;
        float scaleY = baseScale; // Adjust this if the rows feel too squished or too far apart!

        // 3. Loop through every slot to project its 3D space out into 2D UI coordinates
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            int slotX = (i % Volucraft.SIDE_LENGTH) - 1;
            int slotZ = ((i / Volucraft.SIDE_LENGTH) % Volucraft.SIDE_LENGTH) - 1;
            int slotY = (i / (Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH)) - 1;

            // Position determined via transformByIndex(i, translator)
            Vector4f projectedPos = new Vector4f(slotX * expand, slotY * expand, slotZ * expand, 1.0F);

            // Transform our local point using the compiled view matrix state
            projectedPos.mul(transformMatrix);

            // Convert the transformed positions directly into UI Screen Pixels
            float screenX = renderCenterX + (projectedPos.x * scaleX);
            float screenY = renderCenterY + (projectedPos.y * scaleY); // Fllipped in matrix step A

            // Calculate proximity to the mouse cursor pointer
            double dx = mouseX - screenX;
            double dy = mouseY - screenY;
            double distanceSq = (dx * dx) + (dy * dy);

            // Increased hit detection threshold radius to be more forgiving for 3D bounds
            if (distanceSq < 400.0) { // 20-pixel radius bounds
                // Prioritize slots physically closest to the foreground screen
                if (projectedPos.z > closestZ) {
                    closestZ = projectedPos.z;
                    closestSlot = i;
                }
            }
        }

        return closestSlot;
    }
    // end credit: AI
}
