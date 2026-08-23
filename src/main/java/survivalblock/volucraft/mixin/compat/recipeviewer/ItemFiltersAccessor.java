package survivalblock.volucraft.mixin.compat.recipeviewer;

import cc.cassian.rrv.common.overlay.itemlist.view.ItemFilters;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemFilters.class)
public interface ItemFiltersAccessor {

    @Invoker("getTooltipMatch")
    static int volucraft$getTooltipMatch(ItemStack stack, String query) {
        throw new UnsupportedOperationException();
    }
}
