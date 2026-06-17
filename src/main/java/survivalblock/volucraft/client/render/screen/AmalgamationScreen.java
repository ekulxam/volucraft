package survivalblock.volucraft.client.render.screen;

import com.mojang.math.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import survivalblock.volucraft.client.VolucraftClient;
import survivalblock.volucraft.client.render.CubeModel;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.menu.AmalgamationMenu;
import survivalblock.volucraft.mixin.client.AbstractContainerScreenAccessor;

public class AmalgamationScreen extends AbstractContainerScreen<AmalgamationMenu> {
    public static final Identifier CRAFTING_TABLE_LOCATION = Volucraft.id("textures/gui/container/amalgamation_alt.png");
    public static final Identifier SLOT_SPRITE = Volucraft.id("textures/gui/container/slots.png");
    private static final Identifier SLOT_HIGHLIGHT = Volucraft.id("textures/gui/container/slots_highlight.png");

    private static final float EXPANSION_STEP = 0.05F;

    private final CubeModel cubeModel;

    @SuppressWarnings({"FieldCanBeLocal", "unused", "NotNullFieldNotInitialized"})
    private CycleButton<Boolean> expansionButton;
    private boolean shouldExpand = false;
    private float expansion = 0F;
    private final Vector2f rot = new Vector2f((float) (Math.PI / 4), (float) (Math.PI / 4));

    public AmalgamationScreen(AmalgamationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 344, 166);
        this.cubeModel = new CubeModel(this.minecraft.getEntityModels().bakeLayer(VolucraftClient.CUBE));
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
        rot.x = (float) (rot.x % Math.PI);

        int xo = this.leftPos;
        int yo = (this.height - this.imageHeight) / 2;
        //graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 512);

        // render cube (center at 261, 83) in a 150x150 area
        //graphics. // DEATH
        int selected = (int) ((Util.getMillis() / 100F) % Volucraft.SLOTS);
        int cubeX0 = xo + 186;
        int cubeY0 = yo + 8;
        NonNullList<ItemStack> items = NonNullList.withSize(Volucraft.SLOTS, Items.COBBLESTONE.getDefaultInstance());
        /*for (int i = 0; i < items.size(); i++) {
            items.set(i, this.menu.getSlot(i + 1).getItem());
        }*/
        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        this.cubeModel,
                        SLOT_SPRITE,
                        SLOT_HIGHLIGHT,
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
}
