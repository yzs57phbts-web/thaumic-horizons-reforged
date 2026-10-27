package thaumichorizons;

import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import thaumcraft.aspect.Aspect;
import thaumcraft.research.Knowledge;

public final class BloodMenu extends AbstractContainerMenu {
    public final BloodInfuser tile;
    public final List<Aspect> known;
    private final ContainerData data;
    public BloodMenu(int id,Inventory inv){this(id,inv,new BloodInfuser(BlockPos.ZERO,Content.INFUSER.get().defaultBlockState()),new SimpleContainerData(1+2*Aspect.values().length));}
    public BloodMenu(int id,Inventory inv,BloodInfuser tile){this(id,inv,tile,null);}
    private BloodMenu(int id,Inventory inv,BloodInfuser tile,ContainerData client){
        super(Content.BLOOD_MENU.get(),id);this.tile=tile;
        known=Knowledge.get(inv.player).aspects().getAspectsSorted();
        data=client!=null?client:new ContainerData(){
            public int get(int i){if(i==0)return tile.mode;int index=(i-1)/2;if(index>=Aspect.values().length)return 0;Aspect a=aspects().get(index);return (i&1)==1?tile.selected.getAmount(a):tile.acquired.getAmount(a);}
            public void set(int i,int n){}
            public int getCount(){return 1+2*Aspect.values().length;}
        };
        addDataSlots(data);
        addSlot(new SlotItemHandler(tile.inventory,0,16,37));
        for(int x=0;x<3;x++)for(int y=0;y<3;y++)addSlot(new SlotItemHandler(tile.inventory,x*3+y+1,108+x*18,19+y*18){@Override public boolean mayPlace(ItemStack s){return false;}});
        for(int r=0;r<3;r++)for(int c=0;c<9;c++)addSlot(new Slot(inv,9+r*9+c,8+c*18,137+r*18));
        for(int c=0;c<9;c++)addSlot(new Slot(inv,c,8+c*18,195));
    }
    public static List<Aspect> aspects(){return Arrays.stream(Aspect.values()).sorted(Comparator.comparing(Aspect::getTag)).toList();}
    @Override public void setData(int i,int n){super.setData(i,n);if(i==0)tile.mode=n;else if((i-1)/2<aspects().size()){Aspect a=aspects().get((i-1)/2);var target=(i&1)==1?tile.selected:tile.acquired;target.remove(a);if(n>0)target.add(a,n);}}
    @Override public boolean clickMenuButton(Player p,int button){
        if(!stillValid(p))return false;
        if(button>=0&&button<=2){tile.mode=button;tile.sync();return true;}
        // Nonnegative button IDs fit the vanilla serverbound button packet.
        int index=button>=64?button-64:button-3;
        if(index<0||index>=aspects().size())return false;
        Aspect a=aspects().get(index);if(!Knowledge.get(p).hasDiscoveredAspect(a))return false;
        return tile.select(a,button<64);
    }
    @Override public boolean stillValid(Player p){return p.level().isClientSide()||(!tile.isRemoved()&&tile.getLevel()==p.level()&&p.distanceToSqr(tile.getBlockPos().getCenter())<=64);}
    @Override public ItemStack quickMoveStack(Player p,int index){
        Slot slot=slots.get(index);if(!slot.hasItem())return ItemStack.EMPTY;ItemStack s=slot.getItem(),before=s.copy();
        if(index<10){if(!moveItemStackTo(s,10,46,true))return ItemStack.EMPTY;}else if(!moveItemStackTo(s,0,1,false))return ItemStack.EMPTY;
        if(s.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(s.getCount()==before.getCount())return ItemStack.EMPTY;slot.onTake(p,s);return before;
    }
}
