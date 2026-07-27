package survivalblock.volucraft.mixin.client.multimatch;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import survivalblock.volucraft.client.render.screen.MultimatchWidget;

@Mixin(AbstractSelectionList.class)
public abstract class AbstractSelectionListMixin extends AbstractContainerWidget {
    public AbstractSelectionListMixin(int x, int y, int width, int height, Component message, ScrollbarSettings scrollbarSettings) {
        super(x, y, width, height, message, scrollbarSettings);
    }

    @ModifyReturnValue(method = "getFirstEntryY", at = @At("RETURN"))
    private int flushAgainstTheTop(int original) {
        if ((AbstractSelectionList) (Object) this instanceof MultimatchWidget) {
            return this.getY();
        }
        return original;
    }
}
