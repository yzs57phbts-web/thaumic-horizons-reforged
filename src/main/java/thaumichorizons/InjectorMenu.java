package thaumichorizons;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class InjectorMenu extends AbstractContainerMenu {
    final InteractionHand hand;
    final ItemStack weapon;
    final Player owner;
    final int lockedSlot;
    final SimpleContainer ammo=new SimpleContainer(7);
    public InjectorMenu(int id,Inventory inventory,InteractionHand hand){
        super(Content.INJECTOR_MENU.get(),id);this.hand=hand;owner=inventory.player;weapon=owner.getItemInHand(hand);lockedSlot=hand==InteractionHand.MAIN_HAND?inventory.selected:40;
        var loaded=InjectorItem.magazine(weapon);for(int i=0;i<7;i++)ammo.setItem(i,loaded.get(i));
        ammo.addListener(c->{if(!owner.level().isClientSide()&&owner.getItemInHand(hand)==weapon){var saved=net.minecraft.core.NonNullList.withSize(7,ItemStack.EMPTY);for(int i=0;i<7;i++)saved.set(i,ammo.getItem(i));InjectorItem.saveMagazine(weapon,saved);}});
        int[][] positions={{73,10},{99,20},{107,47},{92,70},{64,72},{45,51},{49,24}};
        for(int i=0;i<7;i++)addSlot(new Slot(ammo,i,positions[i][0],positions[i][1]){@Override public boolean mayPlace(ItemStack stack){return InjectorItem.isAmmo(stack);}@Override public int getMaxStackSize(){return 1;}});
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inventory,9+row*9+col,8+col*18,108+row*18));
        for(int col=0;col<9;col++){final int hotbarIndex=col;addSlot(new Slot(inventory,col,8+col*18,166){@Override public boolean mayPickup(Player p){return hotbarIndex!=lockedSlot;}});}
    }
    @Override public boolean stillValid(Player player){return player==owner&&player.isAlive()&&player.getItemInHand(hand)==weapon&&weapon.getItem() instanceof InjectorItem;}
    @Override public void clicked(int slot,int button,ClickType type,Player player){if(type==ClickType.SWAP&&button==lockedSlot)return;super.clicked(slot,button,type,player);}
    @Override public ItemStack quickMoveStack(Player player,int index){var slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(player))return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();if(index<7){if(!moveItemStackTo(stack,7,43,true))return ItemStack.EMPTY;}else if(!InjectorItem.isAmmo(stack)||!moveItemStackTo(stack,0,7,false))return ItemStack.EMPTY;if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();slot.onTake(player,stack);return before;}
}
