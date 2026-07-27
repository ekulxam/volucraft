package survivalblock.volucraft.client.render.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import survivalblock.volucraft.common.networking.SelectFromMultimatchC2SPayload;
import survivalblock.volucraft.common.recipe.AmalgamationRecipe;

/**
 * @see survivalblock.volucraft.mixin.client.multimatch.AbstractSelectionListMixin
 */
public class MultimatchWidget extends AbstractSelectionList<MultimatchWidget.AssembledEntry> {
    public MultimatchWidget(Minecraft minecraft, int width, int height, int y, int defaultEntryHeight) {
        super(minecraft, width, height, y, defaultEntryHeight);
    }

    public MultimatchWidget withX(int x) {
        this.setX(x);
        return this;
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    protected int scrollBarX() {
        return super.scrollBarX() - 8;
    }

    @Override
    public int addEntry(final AssembledEntry entry) {
        boolean wasActive = this.active;
        boolean wasVisible = this.visible;
        int result = super.addEntry(entry);
        this.active = wasActive;
        this.visible = wasVisible;
        return result;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public static class AssembledEntry extends AbstractSelectionList.Entry<AssembledEntry> {
        public static final Identifier SLOT = Identifier.withDefaultNamespace("container/slot");
        public static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
        private final RecipeHolder<AmalgamationRecipe> reference;
        private final ItemStack assembled;

        public AssembledEntry(RecipeHolder<AmalgamationRecipe> reference, ItemStack assembled) {
            this.reference = reference;
            this.assembled = assembled;
            this.setWidth(18);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            int x = this.getX();
            int y = this.getY();
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT, x, y, 18, 18);
            graphics.fakeItem(
                    this.assembled,
                    x + 1,
                    y + 1
            );
            if (hovered) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK_SPRITE, x - 3, y - 3, 24, 24);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            ClientPlayNetworking.send(new SelectFromMultimatchC2SPayload(this.reference));
            return true;
        }
    }
}
