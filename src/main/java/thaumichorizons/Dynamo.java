package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import thaumcraft.aspect.*;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.vis.*;
import thaumcraft.wand.*;

public final class Dynamo extends VisNode implements EssentiaTransport,AspectSource,WandInteractable,MenuProvider {
    public static final List<Aspect> PRIMALS=List.of(Aspect.AIR,Aspect.EARTH,Aspect.FIRE,Aspect.WATER,Aspect.ORDER,Aspect.ENTROPY);
    public AspectList provided=new AspectList(),available=new AspectList();
    public Aspect fuel;
    public int drainPlayer=-1;
    static java.util.function.Consumer<Dynamo> clientTick=dynamo->{};
    public int ticks=-1,enabled=63;
    public float rise,rotation,tilt;
    private boolean wasSource;
    public Dynamo(BlockPos pos,BlockState state){super(Content.DYNAMO.get(),pos,state);}
    public boolean isEssentia(){return ((DynamoBlock)getBlockState().getBlock()).essentia;}
    @Override public int getRange(){return 0;}
    @Override public boolean isSource(){return ticks>0;}
    @Override public int consumeVis(Aspect a,int amount){int taken=Math.min(available.getAmount(a),Math.max(0,amount));if(taken>0)available.remove(a,taken);return taken;}
    public static AspectList primals(Aspect a){var result=new AspectList();if(a.isPrimal())return result.add(a,1);for(Aspect c:a.getComponents())result.add(primals(c));return result;}
    public static void tick(Level level,BlockPos pos,BlockState state,Dynamo be){
        if(level.isClientSide()){
            clientTick.accept(be);
            if(be.ticks>0){be.rise=Math.min(.3F,be.rise+.02F);be.rotation=(be.rotation+2)%360;if(be.rise>=.3F)be.tilt=(be.tilt+2)%360;be.ticks--;}
            else if(be.tilt>0)be.tilt=Math.max(0,be.tilt-8);else be.rise=Math.max(0,be.rise-.02F);
            return;
        }
        if(be.ticks>0){
            if(be.isEssentia()&&be.fuel!=null){be.provided=new AspectList();var base=primals(be.fuel);int each=12/base.size();for(Aspect a:base.getAspects())be.provided.add(a,each);}
            be.available=be.provided.copy();be.ticks--;be.setChanged();
            if(be.ticks==0){be.provided=new AspectList();be.available=new AspectList();be.sync();}
        }else if(be.isEssentia())be.pull();
        boolean active=be.isSource();
        if(active!=be.wasSource){
            if(active){VisNet.addSource(level,be);be.requestNodeRefresh();}else{VisNet.removeSource(level,be);be.removeThisNode();}
            be.wasSource=active;be.sync();
        }
        if(active)be.tickNetwork(level);
    }
    private void pull(){
        if(!level.hasChunkAt(worldPosition.below()))return;
        if(level.getBlockEntity(worldPosition.below()) instanceof EssentiaTransport tube&&tube.isConnectable(Direction.UP)&&tube.canOutputTo(Direction.UP)&&tube.getEssentiaAmount(Direction.UP)>0&&tube.getSuctionAmount(Direction.UP)<128&&tube.getMinimumSuction()<=128){
            Aspect a=tube.getEssentiaType(Direction.UP);if(a!=null&&tube.takeEssentia(a,1,Direction.UP)==1)addEssentia(a,1,Direction.DOWN);
        }
    }
    @Override public InteractionResult onWandUseOn(Level l,BlockPos pos,Player p,ItemStack wand,Direction side){return start(l,pos,p,wand);}
    @Override public InteractionResult onWandUse(Level l,BlockPos pos,Player p,ItemStack wand){return start(l,pos,p,wand);}
    private InteractionResult start(Level l,BlockPos pos,Player p,ItemStack wand){if(isEssentia())return InteractionResult.PASS;WandKeys.TARGET.set(wand,pos);if(p.getMainHandItem()==wand)p.startUsingItem(InteractionHand.MAIN_HAND);else p.startUsingItem(InteractionHand.OFF_HAND);return InteractionResult.CONSUME;}
    @Override public void onUsingWandTick(ItemStack wand,Player p,int count){
        Vec3 from=p.getEyePosition(),to=from.add(p.getLookAngle().scale(6));
        var hit=p.level().clip(new ClipContext(from,to,ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,p));
        if(hit.getType()!=HitResult.Type.BLOCK||!hit.getBlockPos().equals(worldPosition)||p.distanceToSqr(worldPosition.getCenter())>64){p.stopUsingItem();return;}
        if(!p.level().isClientSide()&&count%5==0)charge(wand,p);
    }
    public boolean charge(ItemStack stack,Player p){
        if(!(stack.getItem() instanceof WandItem wand)||isEssentia())return false;
        provided=new AspectList();for(int i=0;i<PRIMALS.size();i++)if((enabled&(1<<i))!=0&&wand.consumeVis(stack,p,PRIMALS.get(i),40,false))provided.add(PRIMALS.get(i),5);
        if(provided.isEmpty())return false;drainPlayer=p.getId();ticks=10;available=provided.copy();VisNet.addSource(level,this);requestNodeRefresh();sync();return true;
    }
    @Override public boolean isConnectable(Direction f){return isEssentia()&&f==Direction.DOWN;}
    @Override public boolean canInputFrom(Direction f){return isConnectable(f);}
    @Override public boolean canOutputTo(Direction f){return false;}
    @Override public void setSuction(Aspect a,int n){}
    @Override public Aspect getSuctionType(Direction f){return null;}
    @Override public int getSuctionAmount(Direction f){return isConnectable(f)&&ticks<=20?128:0;}
    @Override public int takeEssentia(Aspect a,int n,Direction f){return 0;}
    @Override public int addEssentia(Aspect a,int n,Direction f){
        if(a==null||n<=0||!isConnectable(f)||ticks>20)return 0;fuel=a;ticks=Math.max(0,ticks)+21;
        VisNet.addSource(level,this);requestNodeRefresh();sync();return 1;
    }
    @Override public Aspect getEssentiaType(Direction f){return fuel;}
    @Override public int getEssentiaAmount(Direction f){return 0;}
    @Override public int getMinimumSuction(){return 0;}
    @Override public boolean renderExtendedTube(){return false;}
    @Override public AspectList getAspects(){return provided.copy();}
    @Override public void setAspects(AspectList a){}
    @Override public boolean doesContainerAccept(Aspect a){return false;}
    @Override public int addToContainer(Aspect a,int n){return n;}
    @Override public boolean takeFromContainer(Aspect a,int n){return false;}
    @Override public boolean doesContainerContainAmount(Aspect a,int n){return false;}
    @Override public int containerContains(Aspect a){return 0;}
    @Override public Component getDisplayName(){return Component.translatable("block.thaumichorizons.vis_dynamo");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player p){return new DynamoMenu(id,inv,this);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider r){super.saveAdditional(tag,r);tag.putInt("ticks",ticks);tag.putInt("enabled",enabled);tag.putInt("drainPlayer",drainPlayer);if(fuel!=null)tag.putString("fuel",fuel.getTag());tag.put("provided",BloodInfuser.saveAspects(provided));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider r){super.loadAdditional(tag,r);ticks=tag.getInt("ticks");enabled=tag.getInt("enabled")&63;drainPlayer=tag.contains("drainPlayer")?tag.getInt("drainPlayer"):-1;fuel=Aspect.getAspect(tag.getString("fuel"));provided=BloodInfuser.loadAspects(tag.getCompound("provided"));available=new AspectList();}
    @Override public void setRemoved(){if(level!=null&&!level.isClientSide())VisNet.removeSource(level,this);super.setRemoved();}
    @Override public void onChunkUnloaded(){if(level!=null&&!level.isClientSide())VisNet.removeSource(level,this);super.onChunkUnloaded();}
}
