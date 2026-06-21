package survivalblock.volucraft.client.compat.recipeviewer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import survivalblock.volucraft.client.render.CubeOfSlotsRenderState;
import survivalblock.volucraft.common.Volucraft;

import java.util.List;
import java.util.Optional;

import static survivalblock.volucraft.client.render.screen.AmalgamationScreen.*;

public class AmalgamationClientRecipe implements ReliableClientRecipe {

    private static final Quaternionfc ROTATION = new Quaternionf().rotateX((float) Math.PI / 4).rotateY((float) -Math.PI / 4);

    private final Identifier id;
    private final SlotContent result;
    private final List<SlotContent> meAndMy27Slots;

    public AmalgamationClientRecipe(Identifier id, ItemStackTemplate resultItem, List<Optional<Ingredient>> inputs) {
        this.id = id;
        this.result = SlotContent.of(resultItem);
        this.meAndMy27Slots = inputs.stream().map(optional -> optional.map(SlotContent::of).orElse(SlotContent.of())).toList();
        assert this.meAndMy27Slots.size() == Volucraft.SLOTS;
    }

    @Override
    public ReliableClientRecipeType getType() {
        return AmalgamationClientRecipeType.INSTANCE;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.result);
        for (int i = 0; i < Volucraft.SLOTS; i++) {
            slotFillContext.bindSlot(i + 1, this.meAndMy27Slots.get(i));
        }
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.meAndMy27Slots;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }

    // FIXME
    @Override
    public void renderRecipe(RecipeViewScreen screen, RecipePosition recipePosition, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int cubeX0 = 0;
        int cubeY0 = 0;
        ScreenWithCubes screenWithCubes = (ScreenWithCubes) screen;
        NonNullList<ItemStack> items = NonNullList.of(ItemStack.EMPTY, this.getIngredients().stream().map(slotContent -> slotContent.getValidContents().getFirst()).toArray(ItemStack[]::new));
        graphics.guiRenderState.addPicturesInPictureState(
                new CubeOfSlotsRenderState(
                        screenWithCubes.volucraft$getCubeModel(),
                        screenWithCubes.volucraft$getCubeModelWithItem(),
                        SLOT_CUBE_TEXTURE,
                        TRANSLUCENT_SLOT_CUBE,
                        HIGHLIGHTED_SLOT_CUBE,
                        items,
                        -1,
                        1,
                        ROTATION,
                        cubeX0,
                        cubeY0,
                        cubeX0 + SLOTS_SIDE,
                        cubeY0 + SLOTS_SIDE,
                        PICTURE_IN_PICTURE_SCALE,
                        graphics.scissorStack.peek()
                )
        );
    }
}