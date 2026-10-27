package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.*;
import thaumcraft.aspect.*;
import thaumcraft.device.BrainJarBlockEntity;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.vis.*;

public final class SoulMachine extends VisNode implements MenuProvider,EssentiaTransport,AspectSource {
    public int ticksLeft,progress,souls,essentia,forging=3,sieveMotion;
    public boolean extracting;
    public float rotation,previousRotation,targetRotation;
    private boolean loading;
    public final ItemStackHandler inventory=new ItemStackHandler(2){
        @Override protected void onContentsChanged(int i){if(!loading)sync();}
        @Override public boolean isItemValid(int i,ItemStack stack){return kind()==0?i==0&&stack.is(Items.SOUL_SAND):kind()==1&&(i==0?stack.is(Items.PAPER):stack.is(thaumcraft.registry.ModItems.KNOWLEDGE_FRAGMENT.get()));}
    };
    public SoulMachine(BlockPos p,BlockState state){super(Souls.TYPE.get(),p,state);}
    public int kind(){return ((SoulBlock)getBlockState().getBlock()).kind;}
    @Override public int getRange(){return kind()==0?8:0;}
    @Override public boolean isSource(){return false;}
    public static void tick(Level level,BlockPos pos,BlockState state,SoulMachine be){
        if(level.isClientSide()){
            if(be.kind()==0&&be.extracting){be.sieveMotion=(be.sieveMotion+1)%360;if(be.sieveMotion==0)level.playLocalSound(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,SoundEvents.SAND_BREAK,SoundSource.BLOCKS,1,0,false);}
            if(be.kind()==2){if(be.forging>0){be.rotation=(be.rotation+1)%360;be.forging--;}}
            else if(be.kind()==1){be.previousRotation=be.rotation;var player=level.getNearestPlayer(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,6,false);if(player!=null)be.targetRotation=(float)Math.atan2(player.getZ()-pos.getZ()-.5,player.getX()-pos.getX()-.5);else be.targetRotation+=.01F;be.rotation+=net.minecraft.util.Mth.wrapDegrees((be.targetRotation-be.rotation)*180/(float)Math.PI)*((float)Math.PI/180)*.04F;}
            return;
        }
        if(be.kind()==0){be.tickNetwork(level);be.extract();}
        else if(be.kind()==2){if(be.essentia<3000)be.pull();if(be.forging>0){be.rotation=(be.rotation+1)%360;be.forging--;be.setChanged();}}
    }
    public boolean canAcceptSouls(){return kind()==2?essentia>0&&souls<16:kind()==1&&!inventory.getStackInSlot(0).isEmpty()&&inventory.getStackInSlot(1).getCount()<64;}
    public int soulBitCapacity(){return !canAcceptSouls()?0:kind()==2?Math.min(essentia,(16-souls)*9600-progress):Integer.MAX_VALUE;}
    public void addSoulBits(int bits){
        bits=Math.min(bits,soulBitCapacity());if(bits<=0)return;
        if(kind()==2){progress+=bits;essentia-=bits;while(progress>=9600){progress-=9600;souls++;}forging=3;sync();}
        else if(kind()==1){
            for(int i=0;i<bits;i++)if(level.random.nextDouble()>=.97){progress++;level.playSound(null,worldPosition,thaumcraft.registry.ModSounds.WRITE.get(),SoundSource.BLOCKS,.2F,level.random.nextFloat());}
            if(progress>=100){progress-=100;var fragment=inventory.getStackInSlot(1);if(fragment.isEmpty())inventory.setStackInSlot(1,new ItemStack(thaumcraft.registry.ModItems.KNOWLEDGE_FRAGMENT.get()));else fragment.grow(1);inventory.extractItem(0,1,false);}
            sync();
        }
    }
    public void extract(){
        boolean before=extracting;extracting=false;
        if(ticksLeft<=0){if(inventory.getStackInSlot(0).isEmpty()){if(before)sync();return;}inventory.extractItem(0,1,false);ticksLeft=1200;sync();}
        var above=level.getBlockEntity(worldPosition.above());
        boolean brain=above instanceof BrainJarBlockEntity jar&&jar.getXp()<BrainJarBlockEntity.XP_MAX;
        if(brain||above instanceof SoulMachine machine&&machine.canAcceptSouls()){
            int capacity=brain?Integer.MAX_VALUE:((SoulMachine)above).soulBitCapacity();
            int limit=Math.min(ticksLeft,capacity);
            if(limit<=0){if(before)sync();return;}
            extracting=true;int bits=1+VisNet.drainVis(level,worldPosition,Aspect.AIR,Math.min(10,limit-1));ticksLeft-=bits;
            if(brain){var jar=(BrainJarBlockEntity)above;for(int i=0;i<bits;i++)if(level.random.nextDouble()>.99)jar.setXp(Math.min(BrainJarBlockEntity.XP_MAX,jar.getXp()+1));jar.sync();}
            else ((SoulMachine)above).addSoulBits(bits);
            if(ticksLeft<=0)outputSand();sync();
        }else if(before)sync();
    }
    private void outputSand(){
        ItemStack sand=new ItemStack(Items.SAND);var below=worldPosition.below();var handler=level.getCapability(Capabilities.ItemHandler.BLOCK,below,Direction.UP);
        if(handler!=null)sand=ItemHandlerHelper.insertItemStacked(handler,sand,false);
        if(!sand.isEmpty()){var entity=new ItemEntity(level,worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5,sand);level.addFreshEntity(entity);}
    }
    public boolean bottle(Player player,ItemStack empty){if(kind()!=2||souls<=0||!Specimens.isEmptyJar(empty))return false;empty.shrink(1);MedicalItem.give(player,Souls.soulJar(Souls.randomProfession(level)));souls--;sync();return true;}
    public void showStatus(Player player){
        String reason=souls>=16?"full":essentia<=0?"fuel":!(level.getBlockEntity(worldPosition.below()) instanceof SoulMachine sieve&&sieve.kind()==0)?"sieve":"working";
        player.displayClientMessage(Component.translatable("thaumichorizons.forge.status",souls,16,progress,9600,String.format(java.util.Locale.ROOT,"%.3f",essentia/1000D)).append(" — ").append(Component.translatable("thaumichorizons.forge."+reason)),true);
    }
    private void pull(){
        if(level.getBlockEntity(worldPosition.above()) instanceof EssentiaTransport tube&&tube.canOutputTo(Direction.DOWN)&&tube.isConnectable(Direction.DOWN)&&tube.getEssentiaAmount(Direction.DOWN)>0&&tube.getSuctionAmount(Direction.DOWN)<128&&tube.getMinimumSuction()<=128&&tube.getEssentiaType(Direction.DOWN)==Aspect.MIND&&tube.takeEssentia(Aspect.MIND,1,Direction.DOWN)==1)addEssentia(Aspect.MIND,1,Direction.UP);
    }
    public IItemHandler automation(){return new IItemHandler(){
        public int getSlots(){return kind()==0?1:kind()==1?2:0;}
        public ItemStack getStackInSlot(int i){return inventory.getStackInSlot(i);}
        public ItemStack insertItem(int i,ItemStack stack,boolean simulate){return i==0?inventory.insertItem(i,stack,simulate):stack;}
        public ItemStack extractItem(int i,int count,boolean simulate){return kind()==1&&i==1?inventory.extractItem(i,count,simulate):ItemStack.EMPTY;}
        public int getSlotLimit(int i){return 64;}
        public boolean isItemValid(int i,ItemStack stack){return i==0&&inventory.isItemValid(i,stack);}
    };}
    @Override public Component getDisplayName(){return Component.translatable(kind()==0?"block.thaumichorizons.soul_sieve":kind()==1?"block.thaumichorizons.inspiratron":"block.thaumichorizons.soulforge");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new SoulMenu(id,inv,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.put("inventory",inventory.serializeNBT(registry));tag.putInt("ticks",ticksLeft);tag.putInt("progress",progress);tag.putInt("souls",souls);tag.putInt("essentia",essentia);tag.putInt("forging",forging);tag.putBoolean("extracting",extracting);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);loading=true;inventory.deserializeNBT(registry,tag.getCompound("inventory"));loading=false;ticksLeft=tag.getInt("ticks");progress=tag.getInt("progress");souls=tag.getInt("souls");essentia=tag.getInt("essentia");forging=tag.getInt("forging");extracting=tag.getBoolean("extracting");}
    @Override public boolean isConnectable(Direction face){return kind()==2&&face==Direction.UP;}
    @Override public boolean canInputFrom(Direction face){return isConnectable(face);}
    @Override public boolean canOutputTo(Direction face){return false;}
    @Override public void setSuction(Aspect aspect,int amount){}
    @Override public Aspect getSuctionType(Direction face){return kind()==2?Aspect.MIND:null;}
    @Override public int getSuctionAmount(Direction face){return isConnectable(face)&&essentia<3000?128:0;}
    @Override public int takeEssentia(Aspect aspect,int n,Direction face){return 0;}
    @Override public int addEssentia(Aspect aspect,int n,Direction face){if(n<=0||!isConnectable(face)||aspect!=Aspect.MIND||essentia>=3000)return 0;essentia+=1000;sync();return 1;}
    @Override public Aspect getEssentiaType(Direction face){return kind()==2?Aspect.MIND:null;}
    @Override public int getEssentiaAmount(Direction face){return kind()==2?essentia/1000:0;}
    @Override public int getMinimumSuction(){return 0;}
    @Override public boolean renderExtendedTube(){return false;}
    @Override public AspectList getAspects(){return kind()==2&&essentia>0?new AspectList().add(Aspect.MIND,(int)Math.ceil(essentia/1000F)):new AspectList();}
    @Override public void setAspects(AspectList aspects){if(kind()==2){essentia=aspects.getAmount(Aspect.MIND)*1000;sync();}}
    @Override public boolean doesContainerAccept(Aspect aspect){return false;}
    @Override public int addToContainer(Aspect aspect,int n){return n;}
    @Override public boolean takeFromContainer(Aspect aspect,int n){return false;}
    @Override public boolean doesContainerContainAmount(Aspect aspect,int n){return kind()==2&&aspect==Aspect.MIND&&essentia/1000>=n;}
    @Override public int containerContains(Aspect aspect){return kind()==2&&aspect==Aspect.MIND?essentia/1000:0;}
}
