package thaumichorizons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class SoulMenu extends AbstractContainerMenu {
    public final SoulMachine tile;
    final int count;
    public SoulMenu(int id,Inventory inv,int kind){this(id,inv,new SoulMachine(BlockPos.ZERO,(kind==0?Souls.SIEVE:Souls.INSPIRATRON).get().defaultBlockState()),new SimpleContainerData(1));}
    public SoulMenu(int id,Inventory inv,SoulMachine tile){this(id,inv,tile,null);}
    private SoulMenu(int id,Inventory inv,SoulMachine tile,ContainerData client){
        super(tile.kind()==0?Souls.SIEVE_MENU.get():Souls.INSPIRATRON_MENU.get(),id);this.tile=tile;count=tile.kind()==0?1:2;
        addDataSlots(client!=null?client:new ContainerData(){public int get(int i){return tile.kind()==0?tile.ticksLeft:tile.progress;}public void set(int i,int n){}public int getCount(){return 1;}});
        addSlot(new SlotItemHandler(tile.inventory,0,count==1?64:15,count==1?30:42));
        if(count==2)addSlot(new SlotItemHandler(tile.inventory,1,146,42));
        for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new Slot(inv,9+row*9+col,8+col*18,(count==1?84:137)+row*18));
        for(int col=0;col<9;col++)addSlot(new Slot(inv,col,8+col*18,count==1?142:195));
    }
    @Override public void setData(int i,int n){super.setData(i,n);if(count==1)tile.ticksLeft=n;else tile.progress=n;}
    @Override public boolean stillValid(Player p){return p.level().isClientSide()||(!tile.isRemoved()&&tile.getLevel()==p.level()&&p.distanceToSqr(tile.getBlockPos().getCenter())<=64);}
    @Override public ItemStack quickMoveStack(Player p,int index){
        var slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;var stack=slot.getItem();var before=stack.copy();
        if(index<count){if(!moveItemStackTo(stack,count,count+36,true))return ItemStack.EMPTY;}
        else if(!moveItemStackTo(stack,0,count,false))return ItemStack.EMPTY;
        if(stack.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(stack.getCount()==before.getCount())return ItemStack.EMPTY;slot.onTake(p,stack);return before;
    }
}
