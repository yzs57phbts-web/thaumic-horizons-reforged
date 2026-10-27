package thaumichorizons;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.node.*;
import thaumcraft.wand.WandInteractable;

public final class RiftDevices {
    public static final DirectionProperty FACING=BlockStateProperties.FACING;
    public static final DeferredBlock<DeviceBlock> AMPLIFIER=Content.BLOCKS.register("transduction_amplifier",()->new DeviceBlock(true,BlockBehaviour.Properties.of().strength(0).noOcclusion()));
    public static final DeferredBlock<DeviceBlock> STABILIZER=Content.BLOCKS.register("vortex_stabilizer",()->new DeviceBlock(false,BlockBehaviour.Properties.of().strength(10).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Amplifier>> AMP_TYPE=Content.TILES.register("transduction_amplifier",()->BlockEntityType.Builder.of(Amplifier::new,AMPLIFIER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Stabilizer>> STAB_TYPE=Content.TILES.register("vortex_stabilizer",()->BlockEntityType.Builder.of(Stabilizer::new,STABILIZER.get()).build(null));
    static void init(){Content.ALL.put("transduction_amplifier",Content.ITEMS.registerSimpleBlockItem("transduction_amplifier",AMPLIFIER));Content.ALL.put("vortex_stabilizer",Content.ITEMS.registerSimpleBlockItem("vortex_stabilizer",STABILIZER));}
    static BlockPos ray(Level level,BlockPos pos,Direction facing){var normal=net.minecraft.world.phys.Vec3.atLowerCornerOf(facing.getNormal());var start=pos.getCenter().add(normal.scale(.51));var hit=level.clip(new ClipContext(start,pos.getCenter().add(normal.scale(10)),ClipContext.Block.OUTLINE,ClipContext.Fluid.NONE,CollisionContext.empty()));return hit.getType()==HitResult.Type.BLOCK?hit.getBlockPos():null;}
    static boolean targets(Level level,BlockPos pos,BlockPos target,BlockPos ignored){return !pos.equals(ignored)&&level.hasChunkAt(pos)&&level.getBlockEntity(pos) instanceof Stabilizer&&level.hasNeighborSignal(pos)&&target.equals(ray(level,pos,level.getBlockState(pos).getValue(FACING)));}
    static int countBeams(Level level,BlockPos target){return countBeams(level,target,null);}
    private static int countBeams(Level level,BlockPos target,BlockPos ignored){int count=0;for(var direction:Direction.values())for(int distance=1;distance<=10;distance++)if(targets(level,target.relative(direction,distance),target,ignored))count++;return Math.min(6,count);}
    static void zap(Level level,Vec3 start,Vec3 end){if(level instanceof net.minecraft.server.level.ServerLevel server)thaumcraft.platform.ModNetwork.sendToNear(server,null,start.x,start.y,start.z,32,new thaumcraft.network.NodeZapPayload((float)start.x,(float)start.y,(float)start.z,(float)end.x,(float)end.y,(float)end.z));}
    public static final class DeviceBlock extends BaseEntityBlock implements WandInteractable {
        final boolean amplifier;
        DeviceBlock(boolean amplifier,Properties properties){super(properties);this.amplifier=amplifier;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.UP));}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(properties->new DeviceBlock(amplifier,properties));}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
        @Override public BlockState getStateForPlacement(BlockPlaceContext context){var face=context.getClickedFace();if(amplifier&&(face.getAxis().isVertical()||!(context.getLevel().getBlockEntity(context.getClickedPos().relative(face.getOpposite())) instanceof EnergizedNodeBlockEntity)))return null;return defaultBlockState().setValue(FACING,face);}
        @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){if(!amplifier)return true;var entity=level.getBlockEntity(pos.relative(state.getValue(FACING).getOpposite()));return entity instanceof EnergizedNodeBlockEntity||entity instanceof Node||entity instanceof Vortex;}
        @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){if(!amplifier)return Shapes.empty();return switch(state.getValue(FACING)){case UP->Block.box(0,15,0,16,16,16);case DOWN->Block.box(0,0,0,16,1,16);case NORTH->Block.box(0,0,0,16,16,1);case SOUTH->Block.box(0,0,15,16,16,16);case WEST->Block.box(0,0,0,1,16,16);case EAST->Block.box(15,0,0,16,16,16);};}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return amplifier?new Amplifier(pos,state):new Stabilizer(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide()){if(level.getBlockEntity(pos) instanceof Amplifier amplifier)amplifier.release();else if(level.getBlockEntity(pos) instanceof Stabilizer stabilizer)stabilizer.release();}super.onRemove(state,level,pos,next,moving);}
        @Override public InteractionResult onWandUseOn(Level level,BlockPos pos,Player player,ItemStack wand,Direction side){if(amplifier)return InteractionResult.PASS;if(!level.isClientSide()){var entity=(Stabilizer)level.getBlockEntity(pos);entity.release();level.setBlockAndUpdate(pos,level.getBlockState(pos).setValue(FACING,side));level.playSound(null,pos,thaumcraft.registry.ModSounds.TOOL.get(),net.minecraft.sounds.SoundSource.BLOCKS,.5F,.9F+level.random.nextFloat()*.2F);entity.sync();}return InteractionResult.SUCCESS;}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return amplifier?createTickerHelper(type,AMP_TYPE.get(),Amplifier::tick):createTickerHelper(type,STAB_TYPE.get(),Stabilizer::tick);}
    }
    public static final class Stabilizer extends thaumcraft.blockentity.TcBlockEntity {
        public BlockPos target;
        public boolean active;
        private static final String ORIGINAL="thVortexStabilizedType";
        public Stabilizer(BlockPos pos,BlockState state){super(STAB_TYPE.get(),pos,state);}
        static void tick(Level level,BlockPos pos,BlockState state,Stabilizer device){
            if(level.isClientSide()||level.getGameTime()%5!=0)return;device.refresh();
        }
        void refresh(){
            var device=this;var pos=worldPosition;var state=getBlockState();boolean powered=level.hasNeighborSignal(pos);BlockPos hit=powered?ray(level,pos,state.getValue(FACING)):null;if(hit!=null&&!(level.getBlockEntity(hit) instanceof Node||level.getBlockEntity(hit) instanceof Vortex))hit=null;
            if(!java.util.Objects.equals(hit,device.target)){device.release();device.target=hit;if(hit!=null&&level.getBlockEntity(hit) instanceof Node node){var entity=level.getBlockEntity(hit);var data=entity.getPersistentData();if(!data.contains(ORIGINAL))data.putString(ORIGINAL,node.getNodeType().name());node.setNodeType(NodeType.NORMAL);entity.setChanged();if(entity instanceof thaumcraft.blockentity.TcBlockEntity tc)tc.sync();}device.sync();}
            if(device.active!=powered){device.active=powered;device.sync();}
        }
        void release(){if(level!=null&&target!=null&&level.hasChunkAt(target)&&level.getBlockEntity(target) instanceof Node node&&countBeams(level,target,worldPosition)==0){var entity=level.getBlockEntity(target);var data=entity.getPersistentData();if(data.contains(ORIGINAL)){try{node.setNodeType(NodeType.valueOf(data.getString(ORIGINAL)));}catch(IllegalArgumentException ignored){}data.remove(ORIGINAL);entity.setChanged();if(entity instanceof thaumcraft.blockentity.TcBlockEntity tc)tc.sync();}}target=null;}
        @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.putBoolean("active",active);if(target!=null)tag.putLong("target",target.asLong());}
        @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);active=tag.getBoolean("active");target=tag.contains("target")?BlockPos.of(tag.getLong("target")):null;}
    }
    public static final class Amplifier extends thaumcraft.blockentity.TcBlockEntity {
        public int count=-1;
        public boolean active;
        private AspectList boosted=new AspectList();
        private String nodeId="";
        public Amplifier(BlockPos pos,BlockState state){super(AMP_TYPE.get(),pos,state);}
        BlockPos nodePos(){return worldPosition.relative(getBlockState().getValue(FACING).getOpposite());}
        static void tick(Level level,BlockPos pos,BlockState state,Amplifier device){
            if(device.active)device.count++;else if(device.count>0)device.count=Math.min(device.count,50)-1;if(level.isClientSide())return;
            if(level.dimension()==PocketDimension.KEY){level.explode(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,1,Level.ExplosionInteraction.BLOCK);if(device.isRemoved())return;}
            boolean powered=level.hasNeighborSignal(pos);var node=level.getBlockEntity(device.nodePos());
            if(powered&&!device.active){if(!(node instanceof EnergizedNodeBlockEntity energized)){level.destroyBlock(pos,true);return;}var base=energized.getAuraBase().copy();device.boosted=new AspectList();for(var aspect:base.getAspects()){base.add(aspect,10);device.boosted.add(aspect,10);}device.nodeId=energized.getId();energized.setAspects(base);energized.setupNode();device.active=true;device.sync();}
            else if(!powered&&device.active){device.release();device.active=false;device.sync();}
            if(!device.active||device.count%10!=0)return;int amps=0;for(var direction:Direction.Plane.HORIZONTAL){var probe=device.nodePos().relative(direction);if(level.hasChunkAt(probe)&&level.getBlockEntity(probe) instanceof Amplifier amplifier&&amplifier.active&&amplifier.nodePos().equals(device.nodePos()))amps++;}
            if(device.count%50!=0)return;
            if(amps>3&&node instanceof EnergizedNodeBlockEntity energized){device.release();var aspects=energized.getAuraBase().copy();var target=device.nodePos();level.setBlockAndUpdate(target,Vortex.BLOCK.get().defaultBlockState());var vortex=(Vortex)level.getBlockEntity(target);vortex.aspects=aspects;vortex.sync();if(level.getBlockEntity(target.above()) instanceof NodeTransducerBlockEntity transducer)transducer.checkStatus();return;}
            if(amps>2&&level.random.nextInt(4)==2){var target=pos.offset(level.random.nextInt(16)-8,level.random.nextInt(16)-8,level.random.nextInt(16)-8);if(level.hasChunkAt(target)&&level.isEmptyBlock(target)){level.setBlockAndUpdate(target,(target.getY()>pos.getY()?thaumcraft.registry.ModBlocks.FLUX_GAS.get():thaumcraft.registry.ModBlocks.FLUX_GOO.get()).withLevel(8));zap(level,pos.getCenter(),target.getCenter());}}
            if(amps>1)for(var entity:level.getEntitiesOfClass(LivingEntity.class,new AABB(pos).inflate(10))){entity.hurt(level.damageSources().magic(),4);zap(level,pos.getCenter(),entity.position().add(0,entity.getBbHeight()/2,0));}
            if(node instanceof Node){device.release();device.active=false;device.sync();}
            device.setChanged();
        }
        void release(){if(level==null||boosted.size()==0)return;var target=nodePos();if(!level.hasChunkAt(target))level.getChunkAt(target);var entity=level.getBlockEntity(target);if(entity instanceof EnergizedNodeBlockEntity node&&node.getId().equals(nodeId)){var base=node.getAuraBase().copy();for(var aspect:boosted.getAspects())base.remove(aspect,Math.min(base.getAmount(aspect),boosted.getAmount(aspect)));node.setAspects(base);node.setupNode();}else if(entity instanceof Node node&&node.getId().equals(nodeId)){var base=node.getAspectsBase().copy();for(var aspect:boosted.getAspects())base.remove(aspect,Math.min(base.getAmount(aspect),boosted.getAmount(aspect)));node.setAspects(base);if(entity instanceof thaumcraft.blockentity.TcBlockEntity tc)tc.sync();}boosted=new AspectList();}
        @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.putBoolean("active",active);tag.putInt("count",count);tag.putString("node",nodeId);tag.put("boosted",BloodInfuser.saveAspects(boosted));}
        @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);active=tag.getBoolean("active");count=tag.getInt("count");nodeId=tag.getString("node");boosted=BloodInfuser.loadAspects(tag.getCompound("boosted"));}
    }
}
