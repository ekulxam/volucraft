package survivalblock.volucraft.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeProvider.class)
public interface RecipeProviderAccessor {
    @Accessor("items")
    HolderGetter<Item> volucraft$getItems();
}
