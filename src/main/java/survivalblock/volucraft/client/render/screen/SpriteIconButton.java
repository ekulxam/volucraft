package survivalblock.volucraft.client.render.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public abstract class SpriteIconButton extends AbstractButton {
    private static final Identifier BUTTON_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_selected");
    private final Identifier sprite;
    private boolean selected;

    @SuppressWarnings("unused")
    protected SpriteIconButton(final int x, final int y, int width, int height, Identifier sprite) {
        this(x, y, width, height, sprite, CommonComponents.EMPTY);
    }

    protected SpriteIconButton(final int x, final int y, int width, int height, Identifier sprite, final Component component) {
        super(x, y, width, height, component);
        this.setTooltip(Tooltip.create(component));
        this.sprite = sprite;
    }

    @Override
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }

    @Override
    public void updateWidgetNarration(final NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}