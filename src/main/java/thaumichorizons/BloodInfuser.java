package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.*;
import thaumcraft.aspect.*;
import thaumcraft.essentia.EssentiaTransport;

public final class BloodInfuser extends thaumcraft.blockentity.TcBlockEntity implements MenuProvider,EssentiaTransport,AspectSource {
    public AspectList selected=new AspectList(),acquired=new AspectList();
    public int mode;
    public final ItemStackHandler inventory=new ItemStackHandler(10){
        @Override protected void onContentsChanged(int slot){sync();}
        @Override public boolean isItemValid(int slot,ItemStack stack){return slot==0&&(stack.is(Content.item("blood_syringe"))||stack.is(Content.item("blood_phial")));}
        @Override public int getSlotLimit(int slot){return slot==0?64:1;}
    };
    public BloodInfuser(BlockPos p,BlockState s){super(Content.BLOOD.get(),p,s);}
    public boolean hasBlood(){return !inventory.getStackInSlot(0).isEmpty();}
    public int freeOutput(){for(int i=1;i<10;i++)if(inventory.getStackInSlot(i).isEmpty())return i;return -1;}
    public Aspect needed(){if(mode==0||!hasBlood()||freeOutput()<0)return null;for(Aspect a:selected.getAspects())if(acquired.getAmount(a)<selected.getAmount(a))return a;return null;}
    public static void tick(Level level,BlockPos pos,BlockState state,BloodInfuser be){
        if(be.mode==0||!be.hasBlood()||be.freeOutput()<0||be.selected.visSize()==0)return;
        Aspect need=be.needed();
        if(need==null){
            boolean phial=be.inventory.getStackInSlot(0).is(Content.item("blood_phial"));
            be.inventory.setStackInSlot(be.freeOutput(),BloodFormula.create(be.selected,phial));
            be.inventory.extractItem(0,1,false);be.acquired=new AspectList();if(be.mode==1)be.mode=0;be.sync();return;
        }
        for(Direction face:Direction.values()){
            if(!be.isConnectable(face)||!level.hasChunkAt(pos.relative(face)))continue;
            BlockEntity other=level.getBlockEntity(pos.relative(face));Direction out=face.getOpposite();
            if(other instanceof EssentiaTransport tube&&tube.isConnectable(out)&&tube.canOutputTo(out)&&tube.getEssentiaAmount(out)>0&&tube.getSuctionAmount(out)<128&&tube.getMinimumSuction()<=128){
                int n=tube.takeEssentia(need,1,out);if(n>0){be.addToContainer(need,n);return;}
            }
        }
    }
    public boolean select(Aspect aspect,boolean add){
        if(add){if(selected.visSize()>=8)return false;selected.add(aspect,1);}else{if(selected.getAmount(aspect)==0)return false;selected.remove(aspect,1);}
        // Keep already consumed essentia credited, as in 1.7.10; changing a formula cannot produce extra outputs.
        sync();return true;
    }
    public IItemHandler automation(Direction side){
        return new IItemHandler(){
            public int getSlots(){return 10;}
            public ItemStack getStackInSlot(int slot){return inventory.getStackInSlot(slot);}
            public ItemStack insertItem(int slot,ItemStack s,boolean simulate){return slot==0?inventory.insertItem(slot,s,simulate):s;}
            public ItemStack extractItem(int slot,int count,boolean simulate){return slot>0?inventory.extractItem(slot,count,simulate):ItemStack.EMPTY;}
            public int getSlotLimit(int slot){return inventory.getSlotLimit(slot);}
            public boolean isItemValid(int slot,ItemStack s){return inventory.isItemValid(slot,s);}
        };
    }
    @Override public Component getDisplayName(){return Component.translatable("block.thaumichorizons.blood_infuser");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player p){return new BloodMenu(id,inv,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);tag.putInt("mode",mode);tag.put("inventory",inventory.serializeNBT(registries));tag.put("selected",saveAspects(selected));tag.put("acquired",saveAspects(acquired));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);mode=Math.clamp(tag.getInt("mode"),0,2);inventory.deserializeNBT(registries,tag.getCompound("inventory"));selected=loadAspects(tag.getCompound("selected"));acquired=loadAspects(tag.getCompound("acquired"));}
    static CompoundTag saveAspects(AspectList list){var tag=new CompoundTag();for(Aspect a:list.getAspects())tag.putInt(a.getTag(),list.getAmount(a));return tag;}
    static AspectList loadAspects(CompoundTag tag){var list=new AspectList();for(String k:tag.getAllKeys()){Aspect a=Aspect.getAspect(k);if(a!=null&&tag.getInt(k)>0)list.add(a,tag.getInt(k));}return list;}
    @Override public boolean isConnectable(Direction f){return f!=Direction.UP;}
    @Override public boolean canInputFrom(Direction f){return isConnectable(f);}
    @Override public boolean canOutputTo(Direction f){return false;}
    @Override public void setSuction(Aspect a,int n){}
    @Override public Aspect getSuctionType(Direction f){return needed();}
    @Override public int getSuctionAmount(Direction f){return needed()!=null?128:0;}
    @Override public int takeEssentia(Aspect a,int n,Direction f){return 0;}
    @Override public int addEssentia(Aspect a,int n,Direction f){if(!isConnectable(f))return 0;return n-addToContainer(a,n);}
    @Override public Aspect getEssentiaType(Direction f){return null;}
    @Override public int getEssentiaAmount(Direction f){return 0;}
    @Override public int getMinimumSuction(){return 0;}
    @Override public boolean renderExtendedTube(){return false;}
    @Override public AspectList getAspects(){return acquired.copy();}
    @Override public void setAspects(AspectList a){}
    @Override public boolean doesContainerAccept(Aspect a){return a!=null&&a==needed();}
    @Override public int addToContainer(Aspect a,int n){if(n<=0||!doesContainerAccept(a))return Math.max(0,n);int accepted=Math.min(n,selected.getAmount(a)-acquired.getAmount(a));acquired.add(a,accepted);sync();return n-accepted;}
    @Override public boolean takeFromContainer(Aspect a,int n){return false;}
    public boolean takeFromContainer(AspectList a){return false;}
    @Override public boolean doesContainerContainAmount(Aspect a,int n){return acquired.getAmount(a)>=n;}
    public boolean doesContainerContain(AspectList list){for(Aspect a:list.getAspects())if(!doesContainerContainAmount(a,list.getAmount(a)))return false;return true;}
    @Override public int containerContains(Aspect a){return acquired.getAmount(a);}
}
