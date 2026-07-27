package survivalblock.volucraft.client.render.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.world.item.ItemStack;

public class MultimatchWidget extends AbstractSelectionList<MultimatchWidget.AssembledEntry> {
    public MultimatchWidget(Minecraft minecraft, int width, int height, int y, int defaultEntryHeight) {
        super(minecraft, width, height, y, defaultEntryHeight);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    public int addEntry(final AssembledEntry entry) {
        return super.addEntry(entry);
    }

        @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public static class AssembledEntry extends AbstractSelectionList.Entry<AssembledEntry> {
        private final ItemStack assembled;

        public AssembledEntry(ItemStack assembled) {
            this.assembled = assembled;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.fakeItem(this.assembled, getContentX(), getContentY());
        }
    }
}
