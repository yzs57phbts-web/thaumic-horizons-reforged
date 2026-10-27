package thaumichorizons;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** One damaged item plus at least one putty; preserve components, restore all durability. */
public final class VoidPuttyRecipe extends CustomRecipe {
    public VoidPuttyRecipe(CraftingBookCategory category){super(category);}
    private ItemStack target(CraftingInput input){ItemStack target=ItemStack.EMPTY;int putty=0;for(int i=0;i<input.size();i++){var stack=input.getItem(i);if(stack.isEmpty())continue;if(stack.is(Content.item("void_putty"))){putty++;continue;}if(!stack.isDamageableItem()||!stack.isDamaged()||!target.isEmpty())return ItemStack.EMPTY;target=stack;}return putty>0?target:ItemStack.EMPTY;}
    @Override public boolean matches(CraftingInput input,Level level){return !target(input).isEmpty();}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider registry){var result=target(input).copyWithCount(1);if(!result.isEmpty())result.setDamageValue(0);return result;}
    @Override public boolean canCraftInDimensions(int width,int height){return width*height>=2;}
    @Override public RecipeSerializer<?> getSerializer(){return Content.VOID_PUTTY_RECIPE.get();}
}
