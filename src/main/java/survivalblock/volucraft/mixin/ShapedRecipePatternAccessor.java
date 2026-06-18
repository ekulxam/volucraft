package survivalblock.volucraft.mixin;

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShapedRecipePattern.class)
public interface ShapedRecipePatternAccessor {
    @Invoker("firstNonEmpty")
    static int volucraft$invokeFirstNonEmpty(final String line) {
        throw new UnsupportedOperationException();
    }

    @Invoker("lastNonEmpty")
    static int volucraft$invokeLastNonEmpty(final String line) {
        throw new UnsupportedOperationException();
    }
}
