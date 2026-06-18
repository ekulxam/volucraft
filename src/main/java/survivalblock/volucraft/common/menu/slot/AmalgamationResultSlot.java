package survivalblock.volucraft.common.menu.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import survivalblock.volucraft.common.Volucraft;
import survivalblock.volucraft.common.init.VolucraftRecipeTypes;
import survivalblock.volucraft.common.menu.AmalgamationContainer;
import survivalblock.volucraft.common.menu.recipe.AmalgamationInput;
import survivalblock.volucraft.common.menu.recipe.AmalgamationRecipe;

/**
 * @see net.minecraft.world.inventory.ResultSlot
 */
public class AmalgamationResultSlot extends Slot {
	private final AmalgamationContainer craftSlots;
	private final Player player;
	private int removeCount;

	public AmalgamationResultSlot(final Player player, final AmalgamationContainer craftSlots, final Container container, final int id, final int x, final int y) {
		super(container, id, x, y);
		this.player = player;
		this.craftSlots = craftSlots;
	}

	@Override
	public boolean mayPlace(final ItemStack itemStack) {
		return false;
	}

	@Override
	public ItemStack remove(final int amount) {
		if (this.hasItem()) {
			this.removeCount = this.removeCount + Math.min(amount, this.getItem().getCount());
		}

		return super.remove(amount);
	}

	@Override
	protected void onQuickCraft(final ItemStack picked, final int count) {
		this.removeCount += count;
		this.checkTakeAchievements(picked);
	}

	@Override
	protected void onSwapCraft(final int count) {
		this.removeCount += count;
	}

	@Override
	protected void checkTakeAchievements(final ItemStack carried) {
		if (this.removeCount > 0) {
			carried.onCraftedBy(this.player, this.removeCount);
		}

		if (this.container instanceof RecipeCraftingHolder recipeCraftingHolder) {
			recipeCraftingHolder.awardUsedRecipes(this.player, this.craftSlots.getItems());
		}

		this.removeCount = 0;
	}

	private static NonNullList<ItemStack> copyAllInputItems(final AmalgamationInput input) {
		NonNullList<ItemStack> result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

		for (int slot = 0; slot < result.size(); slot++) {
			result.set(slot, input.getItem(slot));
		}

		return result;
	}

	private NonNullList<ItemStack> getRemainingItems(final AmalgamationInput input, final Level level) {
		return level instanceof ServerLevel serverLevel
			? serverLevel.recipeAccess()
				.getRecipeFor(VolucraftRecipeTypes.AMALGAMATION, input, serverLevel)
				.map(recipe -> recipe.value().getRemainingItems(input))
				.orElseGet(() -> copyAllInputItems(input))
			: AmalgamationRecipe.defaultCraftingReminder(input);
	}

	@Override
	public void onTake(final Player player, final ItemStack carried) {
		this.checkTakeAchievements(carried);
        AmalgamationInput.Positioned positionedRecipe = this.craftSlots.asPositionedCraftInput();
		AmalgamationInput input = positionedRecipe.input();
		int recipeLeft = positionedRecipe.left();
        int recipeBack = positionedRecipe.back();
		int recipeTop = positionedRecipe.top();
		NonNullList<ItemStack> remaining = this.getRemainingItems(input, player.level());

		for (int z = 0; z < input.height(); z++) {
			for (int y = 0; y < input.width(); y++) {
				for (int x = 0; x < input.length(); x++) {
                    int slot = (x + recipeLeft) + (y + recipeBack) * this.craftSlots.getLength() + (z + recipeTop) * this.craftSlots.getLength() * this.craftSlots.getWidth();
                    ItemStack itemStack = this.craftSlots.getItem(slot);
                    ItemStack replacement = remaining.get(x + y * input.width());
                    if (!itemStack.isEmpty()) {
                        this.craftSlots.removeItem(slot, 1);
                        itemStack = this.craftSlots.getItem(slot);
                    }

                    if (!replacement.isEmpty()) {
                        if (itemStack.isEmpty()) {
                            this.craftSlots.setItem(slot, replacement);
                        } else if (ItemStack.isSameItemSameComponents(itemStack, replacement)) {
                            replacement.grow(itemStack.getCount());
                            this.craftSlots.setItem(slot, replacement);
                        } else if (!this.player.getInventory().add(replacement)) {
                            this.player.drop(replacement, false);
                        }
                    }
                }
			}
		}
	}

	@Override
	public boolean isFake() {
		return true;
	}
}
