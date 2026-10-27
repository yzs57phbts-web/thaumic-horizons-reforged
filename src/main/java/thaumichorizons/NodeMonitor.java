package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.node.Node;
import thaumcraft.research.*;

public final class NodeMonitor {
    public static final DirectionProperty FACING=BlockStateProperties.FACING;
    public static final BooleanProperty ACTIVE=BooleanProperty.create("active");
    public static final DeferredBlock<MonitorBlock> BLOCK=Content.BLOCKS.register("node_monitor",()->new MonitorBlock(BlockBehaviour.Properties.of().strength(1,1).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MonitorEntity>> TYPE=Content.TILES.register("node_monitor",()->BlockEntityType.Builder.of(MonitorEntity::new,BLOCK.get()).build(null));
    static void init(){Content.ALL.put("node_monitor",Content.ITEMS.registerSimpleBlockItem("node_monitor",BLOCK));}
    static void research(){
        TcRecipes.register(new InfusionRecipe("TH:recipeNodeMonitor","nodeMonitor",TcStack.of("thaumichorizons:node_monitor",1),2,new AspectList().add(Aspect.AURA,2).add(Aspect.SENSES,8).add(Aspect.MECHANISM,8),TcIngredient.of(TcStack.of("thaumcraft:thaumometer",1)),java.util.List.of(TcIngredient.of(TcStack.of("thaumcraft:zombie_brain",1)),TcIngredient.of(TcStack.of("minecraft:comparator",1)),TcIngredient.of(TcStack.of("thaumcraft:air_shard",1)))));
        new ResearchItem("nodeMonitor","ThaumicHorizons",new AspectList().add(Aspect.AURA,3).add(Aspect.MECHANISM,3).add(Aspect.SENSES,3),1,6,1,TcStack.of("thaumichorizons:node_monitor",1)).setParents("INFUSION").setPages(ResearchPage.text("nodeMonitor1"),ResearchPage.infusion("TH:recipeNodeMonitor")).registerResearchItem();
    }
    public static final class MonitorBlock extends BaseEntityBlock {
        MonitorBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(FACING,Direction.UP).setValue(ACTIVE,false));}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(MonitorBlock::new);}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING,ACTIVE);}
        @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=defaultBlockState().setValue(FACING,context.getClickedFace());return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;}
        @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return level.getBlockEntity(pos.relative(state.getValue(FACING).getOpposite())) instanceof Node;}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MonitorEntity(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected boolean isSignalSource(BlockState state){return true;}
        @Override protected int getSignal(BlockState state,BlockGetter level,BlockPos pos,Direction face){return state.getValue(ACTIVE)?15:0;}
        @Override protected int getDirectSignal(BlockState state,BlockGetter level,BlockPos pos,Direction face){return getSignal(state,level,pos,face);}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),MonitorEntity::tick);}
    }
    public static final class MonitorEntity extends thaumcraft.blockentity.TcBlockEntity {
        public float rotation;
        public MonitorEntity(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
        static void tick(Level level,BlockPos pos,BlockState state,MonitorEntity entity){
            if(level.isClientSide()){if(!state.getValue(ACTIVE))entity.rotation=(entity.rotation+1)%360;return;}
            var nodePos=pos.relative(state.getValue(FACING).getOpposite());if(!level.hasChunkAt(nodePos))return;
            if(!(level.getBlockEntity(nodePos) instanceof Node node)){level.destroyBlock(pos,true);return;}
            boolean critical=false;for(var aspect:node.getAspectsBase().getAspects())if(node.getAspects().getAmount(aspect)<=1){critical=true;break;}
            if(critical!=state.getValue(ACTIVE)){level.setBlockAndUpdate(pos,state.setValue(ACTIVE,critical));level.updateNeighborsAt(pos,state.getBlock());}
        }
    }
}
