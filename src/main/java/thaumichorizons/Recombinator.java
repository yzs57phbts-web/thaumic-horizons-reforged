package thaumichorizons;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.node.NodeBlockEntity;
import thaumcraft.research.*;

public final class Recombinator extends thaumcraft.blockentity.TcBlockEntity {
    public static final DeferredBlock<RecombBlock> BLOCK=Content.BLOCKS.register("recombinator",()->new RecombBlock(BlockBehaviour.Properties.of().strength(0).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Recombinator>> TYPE=Content.TILES.register("recombinator",()->BlockEntityType.Builder.of(Recombinator::new,BLOCK.get()).build(null));
    public int count=-1;
    public boolean active;
    public Recombinator(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
    static void init(){Content.ALL.put("planar_conduit",Content.ITEMS.registerSimpleItem("planar_conduit"));Content.ALL.put("recombinator",Content.ITEMS.registerSimpleBlockItem("recombinator",BLOCK));}
    static void research(){
        var cost=new AspectList();for(var primal:Aspect.getPrimalAspects())cost.add(primal,50);
        MagicData.arcane("recipeConduit","planarTheory",TcStack.of("thaumichorizons:planar_conduit",1),cost,new String[]{"VEV","EPE","VEV"},'V',TcIngredient.item("thaumcraft:void_metal_ingot"),'E',TcIngredient.item("thaumcraft:wisp_essence"),'P',TcIngredient.item("minecraft:ender_pearl"));
        new ResearchItem("planarTheory","ThaumicHorizons",new AspectList().add(Aspect.VOID,5).add(Aspect.MAGIC,5).add(Aspect.ELDRITCH,5),10,8,3,TcStack.of("thaumichorizons:planar_conduit",1)).setParents("VISPOWER","VOIDMETAL").setConcealed().setPages(ResearchPage.text("planarTheory1"),ResearchPage.arcane("TH:recipeConduit"),ResearchPage.text("planarTheory2")).registerResearchItem();
        TcRecipes.register(new InfusionRecipe("TH:recipeRecombinator","recombinator",TcStack.of("thaumichorizons:recombinator",1),9,new AspectList().add(Aspect.EXCHANGE,32).add(Aspect.AURA,20).add(Aspect.CRAFT,32).add(Aspect.ENERGY,32),TcIngredient.item("thaumcraft:primordial_pearl"),List.of(TcIngredient.item("thaumcraft:vis_filter"),TcIngredient.item("thaumichorizons:planar_conduit"),TcIngredient.item("thaumcraft:vis_filter"),TcIngredient.item("thaumcraft:node_transducer"),TcIngredient.item("thaumcraft:vis_filter"),TcIngredient.item("thaumichorizons:planar_conduit"),TcIngredient.item("thaumcraft:vis_filter"),TcIngredient.item("thaumcraft:node_stabilizer"))));
        new ResearchItem("recombinator","ThaumicHorizons",new AspectList().add(Aspect.AURA,4).add(Aspect.EXCHANGE,4).add(Aspect.CRAFT,4),12,8,3,TcStack.of("thaumichorizons:recombinator",1)).setParents("planarTheory","PRIMPEARL").setConcealed().setPages(ResearchPage.text("recombinator1"),ResearchPage.infusion("TH:recipeRecombinator"),ResearchPage.text("recombinator2"),ResearchPage.text("recombinator3")).registerResearchItem();
    }
    public static void tick(Level level,BlockPos pos,BlockState state,Recombinator machine){
        if(machine.active)machine.count++;else if(machine.count>0)machine.count=Math.min(machine.count,50)-1;
        if(level.isClientSide())return;boolean signal=level.hasNeighborSignal(pos);if(machine.active!=signal){machine.active=signal;machine.sync();}
        if(machine.active&&machine.count>50&&level.getBlockEntity(pos.below()) instanceof NodeBlockEntity target){var random=level.random;var donorPos=pos.offset(random.nextInt(5)-random.nextInt(5),random.nextInt(5)-random.nextInt(5)-1,random.nextInt(5)-random.nextInt(5));if(!donorPos.equals(pos.below())&&level.hasChunkAt(donorPos)&&level.getBlockState(donorPos).is(thaumcraft.registry.ModBlocks.AURA_NODE.get())&&level.getBlockEntity(donorPos) instanceof NodeBlockEntity donor&&donor.getLock()==0)machine.merge(donor,target);}
    }
    private record Combination(Aspect result,Aspect own,Aspect donor){}
    public boolean merge(NodeBlockEntity donor,NodeBlockEntity target){
        if(donor==target||donor.getLock()>0)return false;
        for(int priority=0;priority<4;priority++){
            var candidates=new LinkedHashMap<Aspect,Combination>();for(var own:target.getAspectsBase().getAspects())for(var other:donor.getAspectsBase().getAspects()){
                if(priority==0&&(!own.isPrimal()||!other.isPrimal())||priority==1&&!own.isPrimal()||priority==2&&!other.isPrimal()||target.getAspects().getAmount(own)<1||donor.getAspects().getAmount(other)<1)continue;
                var result=Aspect.combine(own,other);if(result!=null)candidates.putIfAbsent(result,new Combination(result,own,other));
            }
            if(candidates.isEmpty())continue;var options=List.copyOf(candidates.values());var combo=options.get(level.random.nextInt(options.size()));target.setNodeVisBase(combo.result,target.getNodeVisBase(combo.result)+1);target.getAspects().add(combo.result,1);target.setNodeVisBase(combo.own,target.getNodeVisBase(combo.own)-1);target.takeFromContainer(combo.own,1);donor.takeFromContainer(combo.donor,1);if(level.random.nextInt(3)==0)donor.setNodeVisBase(combo.donor,Math.max(0,donor.getNodeVisBase(combo.donor)-1));target.sync();donor.sync();
            if(level instanceof ServerLevel server){var from=donor.getBlockPos().getCenter();var to=worldPosition.getCenter();thaumcraft.platform.ModNetwork.sendToNear(server,null,to.x,to.y,to.z,32,new thaumcraft.network.NodeZapPayload((float)from.x,(float)from.y,(float)from.z,(float)to.x,(float)to.y,(float)to.z));}return true;
        }return false;
    }
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.putBoolean("active",active);tag.putInt("count",count);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);active=tag.getBoolean("active");count=tag.getInt("count");}
    public static final class RecombBlock extends BaseEntityBlock {
        RecombBlock(Properties properties){super(properties);}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(RecombBlock::new);}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new Recombinator(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected net.minecraft.world.phys.shapes.VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Block.box(0,15,0,16,16,16);}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),Recombinator::tick);}
    }
}
