package thaumichorizons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import thaumcraft.research.ResearchManager;

public final class VatMenu extends AbstractContainerMenu {
    public final VatEntity tile;
    public final boolean cloning;
    private final ContainerData data;
    public VatMenu(int id,Inventory inventory){this(id,inventory,new VatEntity(BlockPos.ZERO,Vats.SOLID.get().defaultBlockState().setValue(VatBlock.PART,7)),true);}
    public VatMenu(int id,Inventory inventory,VatEntity tile){this(id,inventory,tile,false);}
    private VatMenu(int id,Inventory inv,VatEntity vat,boolean client){
        super(Vats.MENU.get(),id);tile=vat;cloning=ResearchManager.isComplete(inv.player,"incarnationVat");
        data=client?new SimpleContainerData(18):new ContainerData(){public int getCount(){return 18;}public void set(int i,int v){}public int get(int i){boolean self=vat.mode==4||vat.mode==2&&VatRecipes.get(vat.recipeKey)!=null&&VatRecipes.get(vat.recipeKey).self();return switch(i){case 0->vat.mode;case 1->vat.progress;case 2->(int)(self?vat.shellHealth:vat.occupant()==null?0:vat.occupant().getHealth());case 3->vat.instability;case 4->vat.occupant()==null?20:(int)vat.occupant().getMaxHealth();case 17->self?1:0;default->{int[] values=!self&&vat.occupant()!=null&&Infusions.DATA.has(vat.occupant())?Infusions.DATA.get(vat.occupant()).getIntArray(vat.occupant() instanceof Player?"player":"creature"):vat.shellInfusions;yield i-5<values.length?values[i-5]:0;}};}};
        addDataSlots(data);
        for(int i=0;i<2;i++){final int slot=i;addSlot(new SlotItemHandler(vat.inventory,i,63+i*33,32){public boolean mayPlace(ItemStack stack){return cloning&&super.mayPlace(stack)&&(!stack.is(Content.item("corpse_effigy"))||ResearchManager.isComplete(inv.player,"cloneVillager"));}public boolean isActive(){return cloning;}public boolean mayPickup(Player p){return cloning&&tile.mode!=1&&tile.mode!=3;}});}
        for(int r=0;r<3;r++)for(int c=0;c<9;c++)addSlot(new Slot(inv,9+r*9+c,8+c*18,127+r*18));for(int c=0;c<9;c++)addSlot(new Slot(inv,c,8+c*18,185));
    }
    public int value(int id){return data.get(id);}
    @Override public void setData(int i,int n){super.setData(i,n);if(i==0)tile.mode=n;else if(i==1)tile.progress=n;else if(i==3)tile.instability=n;else if(i>=5&&i<17)tile.shellInfusions[i-5]=n;}
    @Override public boolean stillValid(Player p){return p.level().isClientSide()||!tile.isRemoved()&&tile.getLevel()==p.level()&&p.distanceToSqr(tile.getBlockPos().getCenter())<=64;}
    @Override public ItemStack quickMoveStack(Player p,int index){if(index<0||index>=slots.size())return ItemStack.EMPTY;Slot slot=slots.get(index);if(!slot.hasItem()||!slot.mayPickup(p))return ItemStack.EMPTY;ItemStack s=slot.getItem(),old=s.copy();if(index<2){if(!moveItemStackTo(s,2,38,true))return ItemStack.EMPTY;}else if(!cloning||!moveItemStackTo(s,0,2,false))return ItemStack.EMPTY;if(s.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();if(old.getCount()==s.getCount())return ItemStack.EMPTY;slot.onTake(p,s);return old;}
}
