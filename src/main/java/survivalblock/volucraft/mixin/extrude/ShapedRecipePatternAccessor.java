package survivalblock.volucraft.mixin.extrude;

import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(ShapedRecipePattern.class)
public interface ShapedRecipePatternAccessor {
    @Accessor("data")
    Optional<ShapedRecipePattern.Data> volucraft$getData();
}
