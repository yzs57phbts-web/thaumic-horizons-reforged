package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.node.*;
import thaumcraft.research.*;
import thaumcraft.vis.*;
import thaumcraft.wand.*;

public final class SyntheticNode extends VisNode implements Node,WandInteractable {
    public static final DeferredBlock<ShardBlock> BLOCK=Content.BLOCKS.register("synthetic_node",()->new ShardBlock(BlockBehaviour.Properties.of().strength(.7F,1).noOcclusion().lightLevel(s->7)));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SyntheticNode>> TYPE=Content.TILES.register("synthetic_node",()->BlockEntityType.Builder.of(SyntheticNode::new,BLOCK.get()).build(null));
    private AspectList capacity=new AspectList(),stored=new AspectList(),fraction=new AspectList();
    public float rotation;
    public int drainer=-1,drainColor=0xFFFFFF;
    public SyntheticNode(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
    static void init(){Content.ALL.put("synthetic_node",Content.ITEMS.registerSimpleBlockItem("synthetic_node",BLOCK));}
    static void research(){
        TcRecipes.register(new InfusionRecipe("TH:recipeEtherealShard","etherealShard",TcStack.of("thaumichorizons:synthetic_node",1),7,new AspectList().add(Aspect.AURA,8).add(Aspect.ENERGY,32).add(Aspect.VOID,32),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),java.util.Collections.nCopies(4,TcIngredient.of(TcStack.of("thaumcraft:nitor",1)))));
        new ResearchItem("etherealShard","ThaumicHorizons",new AspectList().add(Aspect.AURA,8).add(Aspect.ENERGY,16).add(Aspect.VOID,16).add(Aspect.MAGIC,8),1,4,3,TcStack.of("thaumichorizons:synthetic_node",1)).setParents("shardTrans").setParentsHidden("INFUSION","VISPOWER").setConcealed().setPages(ResearchPage.text("etherealShard1"),ResearchPage.infusion("TH:recipeEtherealShard"),ResearchPage.text("etherealShard2")).registerResearchItem();
    }
    @Override public int getRange(){return 8;}
    @Override public boolean isSource(){return false;}
    @Override public String getId(){return "SYNTHETIC";}
    @Override public NodeType getNodeType(){return NodeType.NORMAL;}
    @Override public void setNodeType(NodeType type){}
    @Override public NodeModifier getNodeModifier(){return null;}
    @Override public void setNodeModifier(NodeModifier modifier){}
    @Override public int getNodeVisBase(Aspect aspect){return capacity.getAmount(aspect);}
    @Override public void setNodeVisBase(Aspect aspect,int amount){}
    @Override public AspectList getAspectsBase(){return capacity.copy();}
    @Override public AspectList getAspects(){return stored.copy();}
    @Override public void setAspects(AspectList aspects){capacity=aspects.copy();stored=new AspectList();fraction=new AspectList();sync();}
    @Override public boolean doesContainerAccept(Aspect aspect){return aspect!=null&&stored.getAmount(aspect)<capacity.getAmount(aspect);}
    @Override public int addToContainer(Aspect aspect,int amount){if(aspect==null||amount<=0)return Math.max(0,amount);int added=Math.min(amount,Math.max(0,capacity.getAmount(aspect)-stored.getAmount(aspect)));if(added>0){stored.add(aspect,added);sync();}return amount-added;}
    @Override public boolean takeFromContainer(Aspect aspect,int amount){if(amount<0||stored.getAmount(aspect)<amount)return false;stored.remove(aspect,amount);if(amount>0)sync();return true;}
    @Override public boolean doesContainerContainAmount(Aspect aspect,int amount){return amount>=0&&stored.getAmount(aspect)>=amount;}
    @Override public int containerContains(Aspect aspect){return stored.getAmount(aspect);}
    public boolean addFraction(Aspect aspect,int amount){if(!doesContainerAccept(aspect)||amount<=0)return false;fraction.add(aspect,amount);while(fraction.getAmount(aspect)>100&&doesContainerAccept(aspect)){addToContainer(aspect,1);fraction.remove(aspect,100);}setChanged();return true;}
    public boolean addEssence(ItemStack stack){if(!stack.is(thaumcraft.registry.ModItems.WISP_ESSENCE.get()))return false;var aspects=AspectKeys.ASPECTS.getOrDefault(stack,new AspectList()).getAspects();if(aspects.isEmpty())return false;capacity.add(aspects.getFirst(),Math.multiplyExact(4,stack.getCount()));stack.setCount(0);sync();return true;}
    public static void tick(Level level,BlockPos pos,BlockState state,SyntheticNode node){
        if(level.isClientSide()){node.rotation=(node.rotation+1)%360;return;}node.tickNetwork(level);
        if(node.drainer>=0&&(!(level.getEntity(node.drainer) instanceof Player player)||!player.isUsingItem())){node.drainer=-1;node.sync();}
        for(var aspect:node.capacity.getAspects())if(node.doesContainerAccept(aspect)){
            int drained;if(aspect.isPrimal())drained=VisNet.drainVis(level,pos,aspect,10);else{drained=100;for(var primal:AspectHelper.reduceToPrimals(new AspectList().add(aspect,1)).getAspects())drained=Math.min(drained,VisNet.drainVis(level,pos,primal,10));}if(drained>0)node.addFraction(aspect,drained);
        }
    }
    @Override public InteractionResult onWandUse(Level level,BlockPos pos,Player player,ItemStack wand){WandKeys.TARGET.set(wand,worldPosition);return InteractionResult.CONSUME;}
    @Override public void onUsingWandTick(ItemStack wand,Player player,int remaining){
        var hit=player.pick(player.blockInteractionRange(),1,true);if(!(hit instanceof BlockHitResult block)||hit.getType()!=HitResult.Type.BLOCK||!block.getBlockPos().equals(worldPosition)){player.stopUsingItem();onWandStoppedUsing(wand,player.level(),player,remaining);return;}
        if(level.isClientSide()||remaining%5!=0)return;int tap=1+(ResearchGate.isComplete(player,"NODETAPPER1")?1:0)+(ResearchGate.isComplete(player,"NODETAPPER2")?1:0);boolean preserve=!player.isShiftKeyDown()&&ResearchGate.isComplete(player,"NODEPRESERVE")&&!Wands.getRod(wand).tag().equals("wood")&&!Wands.getCap(wand).tag().equals("iron");
        var room=Wands.getAspectsWithRoom(wand);var options=stored.getAspects().stream().filter(a->room.getAmount(a)>0&&stored.getAmount(a)>(preserve?1:0)).toList();int previous=drainer;drainer=-1;
        if(!options.isEmpty()){var aspect=options.get(level.random.nextInt(options.size()));int amount=Math.min(tap,stored.getAmount(aspect)-(preserve?1:0));int accepted=amount-Wands.addVis(wand,aspect,amount,true);if(accepted>0){takeFromContainer(aspect,accepted);drainer=player.getId();drainColor=aspect.getColor();}}
        if(previous!=drainer||drainer>=0)sync();
    }
    @Override public void onWandStoppedUsing(ItemStack wand,Level level,Player player,int remaining){if(drainer==player.getId()){drainer=-1;if(!level.isClientSide())sync();}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.put("capacity",BloodInfuser.saveAspects(capacity));tag.put("stored",BloodInfuser.saveAspects(stored));tag.put("fraction",BloodInfuser.saveAspects(fraction));tag.putInt("drainer",drainer);tag.putInt("drainColor",drainColor);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);capacity=BloodInfuser.loadAspects(tag.getCompound("capacity"));stored=BloodInfuser.loadAspects(tag.getCompound("stored"));fraction=BloodInfuser.loadAspects(tag.getCompound("fraction"));for(var aspect:java.util.List.copyOf(stored.getAspects())){int excess=stored.getAmount(aspect)-capacity.getAmount(aspect);if(excess>0)stored.remove(aspect,excess);}drainer=tag.contains("drainer")?tag.getInt("drainer"):-1;drainColor=tag.getInt("drainColor");}
    public static final class ShardBlock extends BaseEntityBlock {
        ShardBlock(Properties properties){super(properties);}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(ShardBlock::new);}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SyntheticNode(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(4.8,0,4.8,11.2,16,11.2);}
        @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!stack.is(thaumcraft.registry.ModItems.WISP_ESSENCE.get()))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;if(level.isClientSide())return ItemInteractionResult.SUCCESS;return level.getBlockEntity(pos) instanceof SyntheticNode node&&node.addEssence(stack)?ItemInteractionResult.SUCCESS:ItemInteractionResult.FAIL;}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide()&&level.getBlockEntity(pos) instanceof SyntheticNode node)for(var aspect:node.capacity.getAspects()){int count=node.capacity.getAmount(aspect)/4;while(count>0){var essence=new ItemStack(thaumcraft.registry.ModItems.WISP_ESSENCE.get(),Math.min(64,count));AspectKeys.ASPECTS.set(essence,new AspectList().add(aspect,2));popResource(level,pos,essence);count-=essence.getCount();}}super.onRemove(state,level,pos,next,moving);}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),SyntheticNode::tick);}
    }
}
