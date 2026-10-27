package thaumichorizons;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class LensCaseMenu extends AbstractContainerMenu {
    final Player owner;
    final InteractionHand hand;
    final ItemStack pouch;
    final int lockedSlot;
    final SimpleContainer lenses=new SimpleContainer(18);
    public LensCaseMenu(int id,Inventory inventory,InteractionHand hand){
        super(Content.LENS_CASE_MENU.get(),id);owner=inventory.player;this.hand=hand;pouch=owner.getItemInHand(hand);lockedSlot=hand==InteractionHand.MAIN_HAND?inventory.selected:40;var contents=Lenses.contents(pouch);for(int i=0;i<18;i++)lenses.setItem(i,contents.get(i));
        lenses.addListener(container->{if(!owner.level().isClientSide()&&owner.getItemInHand(hand)==pouch){var updated=Lenses.contents(pouch);for(int i=0;i<18;i++)updated.set(i,lenses.getItem(i));Lenses.contents(pouch,updated);}});
        for(int i=0;i<18;i++)addSlot(new Slot(lenses,i,37+i%6*18,51+i/6*18){@Override public boolean mayPlace(ItemStack stack){return stack.getItem() instanceof Lenses.Lens;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,151+row*18));for(int col=0;col<9;col++){final int hotbarIndex=col;addSlot(new Slot(inventory,col,8+col*18,209){@Override public boolean mayPickup(Player player){return hotbarIndex!=lockedSlot;}});}
    }
    @Override public boolean stillValid(Player player){return player==owner&&player.isAlive()&&player.getItemInHand(hand)==pouch&&pouch.getItem() instanceof Lenses.Case;}
    @Override public void clicked(int slot,int button,ClickType type,Player player){if(type==ClickType.SWAP&&button==lockedSlot)return;super.clicked(slot,button,type,player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(index<18){if(!moveItemStackTo(stack,18,54,true))return ItemStack.EMPTY;}else if(!(stack.getItem() instanceof Lenses.Lens)||!moveItemStackTo(stack,0,18,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return before;}
}
