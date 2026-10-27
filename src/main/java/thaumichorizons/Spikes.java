package thaumichorizons;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;

public final class Spikes {
    public static final DirectionProperty FACING=BlockStateProperties.FACING;
    public static final java.util.Map<String,DeferredBlock<SpikeBlock>> BLOCKS=new java.util.LinkedHashMap<>();
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SpikeEntity>> TYPE=Content.TILES.register("spike",()->BlockEntityType.Builder.of(SpikeEntity::new,BLOCKS.values().stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    static void init(){for(String type:java.util.List.of("metal","wooden","tooth")){String name=type+"_spike";var block=Content.BLOCKS.register(name,()->new SpikeBlock(type,BlockBehaviour.Properties.of().strength(3,3).noOcclusion()));BLOCKS.put(type,block);Content.ALL.put(name,Content.ITEMS.registerSimpleBlockItem(name,block));}}
    public static final class SpikeBlock extends BaseEntityBlock {
        final String type;
        SpikeBlock(String type,Properties properties){super(properties);this.type=type;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.UP));}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(properties->new SpikeBlock(type,properties));}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
        @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=defaultBlockState().setValue(FACING,context.getClickedFace());return state.canSurvive(context.getLevel(),context.getClickedPos())?state:null;}
        @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){var face=state.getValue(FACING);var support=pos.relative(face.getOpposite());return level.getBlockState(support).isFaceSturdy(level,support,face);}
        @Override protected BlockState updateShape(BlockState state,Direction face,BlockState neighbour,LevelAccessor level,BlockPos pos,BlockPos neighbourPos){return !state.canSurvive(level,pos)?Blocks.AIR.defaultBlockState():super.updateShape(state,face,neighbour,level,pos,neighbourPos);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SpikeEntity(pos,state);}
        @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return switch(state.getValue(FACING)){case DOWN->Block.box(0,1,0,16,16,16);case UP->Block.box(0,0,0,16,15,16);case NORTH->Block.box(0,0,1,16,16,16);case SOUTH->Block.box(0,0,0,16,16,15);case WEST->Block.box(1,0,0,16,16,16);case EAST->Block.box(0,0,0,15,16,16);};}
        @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(!level.isClientSide())entity.hurt(level.damageSources().cactus(),1);}
    }
    public static final class SpikeEntity extends thaumcraft.blockentity.TcBlockEntity {public SpikeEntity(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}}
}
