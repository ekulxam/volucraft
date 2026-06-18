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
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;
import survivalblock.volucraft.mixin.client.AbstractContainerScreenAccessor;

import static survivalblock.volucraft.client.render.CubeOfSlotsRenderer.centerFromScale;

public class AmalgamationScreen extends AbstractContainerScreen<AmalgamationMenu> {
    public static final Identifier CRAFTING_TABLE_LOCATION = Volucraft.id("textures/gui/container/amalgamation_alt.png");
    public static final Identifier SLOT_CUBE_TEXTURE = Volucraft.id("textures/gui/container/slots.png");
    public static final Identifier TRANSLUCENT_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_translucent.png");
    private static final Identifier HIGHLIGHTED_SLOT_CUBE = Volucraft.id("textures/gui/container/slots_highlight.png");

    private static final float EXPANSION_STEP = 0.05F;

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

        // 1. Viewport Boundary Check (150x150 region)
        if (mouseX < xo || mouseX > xo + 150 || mouseY < yo || mouseY > yo + 150) {
            return -1;
        }

        // 2. Map 2D mouse directly to layout coordinates
        // Top-Left (-1, -1), Bottom-Right (1, 1)
        float ndcX = (float) (((mouseX - xo) / 150.0) * 2.0 - 1.0);
        float ndcY = (float) (((mouseY - yo) / 150.0) * 2.0 - 1.0);

        // Orthographic viewport viewing bounds for scale 11F
        float orthoScale = 11.0F / 8.0F;
        float rayX = ndcX * orthoScale;
        float rayY = ndcY * orthoScale;

        // 3. Setup the rotation matrix matching the current visual render state
        Quaternionf renderRot = new Quaternionf().rotateX(rot.y).rotateY(-rot.x);

        float expand = (Math.clamp(this.expansion, 0, 1) * 1.5F + 1) * 1.2F;
        float renderCenter = 6.5F;   // centerFromScale(11F)
        float pivotY = 9.0F / 16.0F; // 0.5625F

        int closestSlot = -1;
        float closestZ = Float.NEGATIVE_INFINITY; // We want the closest block to the screen (highest Z in view space)

        // 4. Rotate and translate every slot center point to check against mouse Ray
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            int slotX = (i % Volucraft.SIDE_LENGTH) - 1;
            int slotZ = ((i / Volucraft.SIDE_LENGTH) % Volucraft.SIDE_LENGTH) - 1;
            int slotY = (i / (Volucraft.SIDE_LENGTH * Volucraft.SIDE_LENGTH)) - 1;

            // Determine base layout position (Phanastrae's algorithm + expansion mapping)
            Vector3f slotPos = new Vector3f(slotX * expand, slotY * expand, slotZ * expand);

            // Apply Pivot transform path: translate -> rotate -> untranslate
            slotPos.y -= pivotY;
            slotPos.rotate(renderRot);
            slotPos.y += pivotY;

            // Undo center translation offset
            slotPos.y += renderCenter;

            // Apply the global LivingEntity mulPose(flip) matrix transformation: Y = -Y, Z = -Z
            slotPos.y = -slotPos.y;
            slotPos.z = -slotPos.z;

            // 5. Test if our 2D mouse coordinate falls inside this slot's projected 2D bounds
            // Radius is 0.5F per block slot cube size
            float minX = slotPos.x - 0.5F;
            float maxX = slotPos.x + 0.5F;
            float minY = slotPos.y - 0.5F;
            float maxY = slotPos.y + 0.5F;

            if (rayX >= minX && rayX <= maxX && rayY >= minY && rayY <= maxY) {
                // If the ray hits multiple overlapping cubes, pick the one closest to the viewport front (highest Z)
                if (slotPos.z > closestZ) {
                    closestZ = slotPos.z;
                    closestSlot = i;
                }
            }
        }

        return closestSlot;
    }

    // Axis-Aligned Bounding Box (AABB) intersection helper function
    private float intersectRayAABB(Vector3f origin, Vector3f dir, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        float t1 = (minX - origin.x) / dir.x;
        float t2 = (maxX - origin.x) / dir.x;
        float t3 = (minY - origin.y) / dir.y;
        float t4 = (maxY - origin.y) / dir.y;
        float t5 = (minZ - origin.z) / dir.z;
        float t6 = (maxZ - origin.z) / dir.z;

        float tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        float tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0 || tmin > tmax) {
            return -1.0F; // No intersection match
        }
        return tmin;
    }
    // end credit: AI
}
