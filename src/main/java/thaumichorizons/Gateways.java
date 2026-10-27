package thaumichorizons;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.wand.*;

public final class Gateways {
    public static final EnumProperty<Direction.Axis> AXIS=BlockStateProperties.HORIZONTAL_AXIS;
    public static final IntegerProperty PART=IntegerProperty.create("part",0,14),SIDE=IntegerProperty.create("side",0,3);
    public static final BooleanProperty RETURNING=BooleanProperty.create("returning");
    public static final DeferredBlock<SlotBlock> SLOT=Content.BLOCKS.register("keystone_receptacle",()->new SlotBlock(BlockBehaviour.Properties.of().strength(2.5F,2.5F).noOcclusion()));
    public static final DeferredBlock<FrameBlock> FRAME=Content.BLOCKS.register("gateway_frame",()->new FrameBlock(BlockBehaviour.Properties.of().strength(2.5F,2.5F).noLootTable()));
    public static final DeferredBlock<PortalBlock> PORTAL=Content.BLOCKS.register("gateway_portal",()->new PortalBlock(BlockBehaviour.Properties.of().strength(-1,0).noOcclusion().noCollission().noLootTable()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SlotEntity>> TYPE=Content.TILES.register("keystone_receptacle",()->BlockEntityType.Builder.of(SlotEntity::new,SLOT.get()).build(null));
    private static final List<BlockPos> FRAME_PARTS=frameParts();
    static void init(){Content.ALL.put("keystone",Content.ITEMS.registerItem("keystone",Keystone::new,new Item.Properties().stacksTo(1)));Content.ALL.put("keystone_receptacle",Content.ITEMS.registerSimpleBlockItem("keystone_receptacle",SLOT));}
    private static List<BlockPos> frameParts(){var result=new ArrayList<BlockPos>();for(int t=-2;t<=2;t++)for(int y=0;y>=-4;y--)if((Math.abs(t)==2||y==0||y==-4)&&(t!=0||y!=0))result.add(new BlockPos(t,y,0));return List.copyOf(result);}
    private static BlockPos offset(BlockPos pos,Direction.Axis axis,int t,int y){return axis==Direction.Axis.X?pos.offset(t,y,0):pos.offset(0,y,t);}
    static int planeId(ItemStack stack){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();return data.contains("thPlaneId",Tag.TAG_INT)?data.getInt("thPlaneId"):-1;}
    static int color(ItemStack stack){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();return data.contains("thPlaneColor")?data.getInt("thPlaneColor"):0xFFFFFF;}
    static ItemStack key(PocketPlanes.Plane plane){var stack=new ItemStack(Content.item("keystone"));bind(stack,plane);return stack;}
    static void bind(ItemStack stack,PocketPlanes.Plane plane){var data=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();data.putInt("thPlaneId",plane.id);data.putInt("thPlaneColor",plane.color);data.putString("thPlaneName",plane.name);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    private static SlotEntity slotAt(Level level,BlockPos portal,Direction.Axis axis){for(int y=1;y<=3;y++)for(int t=-1;t<=1;t++){var pos=offset(portal,axis,t,y);if(level.getBlockEntity(pos) instanceof SlotEntity slot&&slot.open&&slot.axis==axis)return slot;}return null;}
    static BlockPos inside(PocketPlanes.Plane plane,int side){return switch(side){case 0->new BlockPos(0,128,plane.id*256+plane.radius);case 1->new BlockPos(0,128,plane.id*256-plane.radius);case 2->new BlockPos(plane.radius,128,plane.id*256);default->new BlockPos(-plane.radius,128,plane.id*256);};}
    private static Direction outward(int side){return switch(side){case 0->Direction.SOUTH;case 1->Direction.NORTH;case 2->Direction.EAST;default->Direction.WEST;};}
    private static void insideBlocks(ServerLevel level,PocketPlanes.Plane plane,int side,boolean open){
        if(plane.gatewayChanging[side])return;plane.gatewayChanging[side]=true;try{var center=inside(plane,side);var axis=side<2?Direction.Axis.X:Direction.Axis.Z;for(int t=-1;t<=1;t++)for(int y=126;y<=128;y++){var pos=offset(center,axis,t,y-128);level.setBlock(pos,open?PORTAL.get().defaultBlockState().setValue(AXIS,axis).setValue(RETURNING,true).setValue(SIDE,side):Materials.BLOCKS.get("void").get().defaultBlockState(),18);if(open)level.setBlock(pos.relative(outward(side)),Materials.BLOCKS.get("void").get().defaultBlockState(),18);}}finally{plane.gatewayChanging[side]=false;}
    }
    private static void closeInside(ServerLevel level,PocketPlanes.Plane plane,int side){insideBlocks(level,plane,side,false);plane.portals[side]=null;PocketPlanes.get(level.getServer()).setDirty();}
    private static void prepareArrival(ServerLevel level,PocketPlanes.Plane plane,int side){
        // Keep the original portal skin/coordinates; make room for a modern player's head and exit.
        var center=inside(plane,side);var inward=outward(side).getOpposite();var tangent=side<2?Direction.EAST:Direction.SOUTH;
        for(int depth=1;depth<=2;depth++)for(int width=-1;width<=1;width++){var feet=center.relative(inward,depth).relative(tangent,width).below();for(int y=0;y<3;y++)level.setBlock(feet.above(y),Blocks.AIR.defaultBlockState(),18);if(level.getBlockState(feet.below()).getCollisionShape(level,feet.below()).isEmpty())level.setBlock(feet.below(),thaumcraft.registry.ModBlocks.ARCANE_STONE.get().defaultBlockState(),18);}
    }
    static void teleport(ServerPlayer player,BlockPos pos,BlockState state){
        if(player.isPassenger()||player.isVehicle())return;if(player.isOnPortalCooldown()){player.setPortalCooldown(10);return;}var level=player.serverLevel();var data=PocketPlanes.get(player.server);ServerLevel target;BlockPos destination;float yaw;
        if(state.getValue(RETURNING)){
            var plane=data.get(Math.floorDiv(pos.getZ()+128,256));int side=state.getValue(SIDE);if(plane==null||!plane.ready||plane.portals[side]==null)return;var link=plane.portals[side];target=player.server.getLevel(link.dimension());if(target==null)return;target.getChunkAt(link.pos());if(!(target.getBlockEntity(link.pos()) instanceof SlotEntity slot)||!slot.open||slot.planeId()!=plane.id){closeInside(level,plane,side);return;}destination=link.pos().below(2);yaw=slot.axis==Direction.Axis.X?0:270;
        }else{
            var slot=slotAt(level,pos,state.getValue(AXIS));if(slot==null)return;var plane=data.get(slot.planeId());if(plane==null||!plane.ready)return;target=player.server.getLevel(PocketDimension.KEY);if(target==null)return;destination=inside(plane,slot.side);yaw=switch(slot.side){case 0->180;case 1->0;case 2->90;default->270;};
        }
        if(!state.getValue(RETURNING)){var slot=slotAt(level,pos,state.getValue(AXIS));var plane=data.get(slot.planeId());prepareArrival(target,plane,slot.side);destination=destination.below();}
        player.setPortalCooldown(10);player.teleportTo(target,destination.getX()+.5,destination.getY(),destination.getZ()+.5,yaw,0);
    }
    public static final class Keystone extends Item {
        Keystone(Properties properties){super(properties);}
        @Override public Component getName(ItemStack stack){return Component.translatable(planeId(stack)>=0?"item.thaumichorizons.keystone_bound":"item.thaumichorizons.keystone");}
        @Override public InteractionResult onItemUseFirst(ItemStack stack,UseOnContext context){if(planeId(stack)>=0||context.getLevel().dimension()!=PocketDimension.KEY||!(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof Vortex node)||!node.createdDimension)return InteractionResult.PASS;if(context.getLevel().isClientSide())return InteractionResult.SUCCESS;var plane=PocketPlanes.get(context.getLevel().getServer()).get(node.planeId);if(plane==null||!plane.ready)return InteractionResult.FAIL;bind(stack,plane);if(context.getPlayer()!=null)context.getPlayer().inventoryMenu.broadcastChanges();return InteractionResult.SUCCESS;}
        @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){if(planeId(stack)>=0){String name=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("thPlaneName");if(!name.isEmpty())tooltip.add(Component.literal(name));}super.appendHoverText(stack,context,tooltip,flag);}
    }
    public static final class SlotEntity extends thaumcraft.blockentity.TcBlockEntity {
        public ItemStack contained=ItemStack.EMPTY;
        public boolean open,changing;
        public Direction.Axis axis=Direction.Axis.X;
        public int side;
        public SlotEntity(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
        int planeId(){return Gateways.planeId(contained);}
        boolean insert(ItemStack stack){if(!contained.isEmpty()||!stack.is(Content.item("keystone"))||Gateways.planeId(stack)<0)return false;var plane=PocketPlanes.get(level.getServer()).get(Gateways.planeId(stack));if(plane==null||!plane.ready)return false;contained=stack.copyWithCount(1);bind(contained,plane);stack.shrink(1);sync();return true;}
        ItemStack take(){close();var key=contained;contained=ItemStack.EMPTY;sync();return key;}
        private boolean matches(Direction.Axis axis){for(int t=-2;t<=2;t++)for(int y=0;y>=-4;y--){if(t==0&&y==0)continue;var state=level.getBlockState(offset(worldPosition,axis,t,y));if(Math.abs(t)==2||y==0||y==-4){boolean corner=Math.abs(t)==2&&(y==0||y==-4);if(!state.is(corner?thaumcraft.registry.ModBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT.get():thaumcraft.registry.ModBlocks.ANCIENT_STONE.get()))return false;}else if(!state.isAir())return false;}return true;}
        boolean activate(Player player,ItemStack wand){
            if(open||changing||contained.isEmpty()||!(wand.getItem() instanceof WandItem caster)||!(level instanceof ServerLevel source))return false;var data=PocketPlanes.get(source.getServer());var plane=data.get(planeId());if(plane==null||!plane.ready)return false;var realm=source.getServer().getLevel(PocketDimension.KEY);if(realm==null)return false;int free=-1;for(int i=0;i<4;i++)if(plane.portals[i]==null){free=i;break;}if(free<0)return false;Direction.Axis alignment=matches(Direction.Axis.X)?Direction.Axis.X:matches(Direction.Axis.Z)?Direction.Axis.Z:null;if(alignment==null)return false;
            var cost=new AspectList();for(var aspect:Aspect.getPrimalAspects())cost.add(aspect,250);if(!caster.consumeAllVis(wand,player,cost,false,true))return false;for(int t=-2;t<=2;t++)for(int y=0;y>=-4;y--)if(!level.mayInteract(player,offset(worldPosition,alignment,t,y)))return false;
            changing=true;axis=alignment;side=free;try{
                for(int part=0;part<FRAME_PARTS.size();part++){var off=FRAME_PARTS.get(part);level.setBlock(offset(worldPosition,axis,off.getX(),off.getY()),FRAME.get().defaultBlockState().setValue(AXIS,axis).setValue(PART,part),18);}for(int t=-1;t<=1;t++)for(int y=-1;y>=-3;y--)level.setBlock(offset(worldPosition,axis,t,y),PORTAL.get().defaultBlockState().setValue(AXIS,axis).setValue(SIDE,side),18);
                if(!caster.consumeAllVis(wand,player,cost,true,true)){restoreFrame();return false;}plane.portals[side]=GlobalPos.of(level.dimension(),worldPosition);insideBlocks(realm,plane,side,true);data.setDirty();open=true;sync();level.playSound(null,worldPosition,thaumcraft.registry.ModSounds.WAND.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);return true;
            }finally{changing=false;}
        }
        private void restoreFrame(){for(var off:FRAME_PARTS){boolean corner=Math.abs(off.getX())==2&&(off.getY()==0||off.getY()==-4);level.setBlock(offset(worldPosition,axis,off.getX(),off.getY()),(corner?thaumcraft.registry.ModBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT.get():thaumcraft.registry.ModBlocks.ANCIENT_STONE.get()).defaultBlockState(),18);}for(int t=-1;t<=1;t++)for(int y=-1;y>=-3;y--)level.removeBlock(offset(worldPosition,axis,t,y),false);}
        void close(){if(!open||changing||level==null||level.isClientSide())return;changing=true;open=false;try{restoreFrame();var data=PocketPlanes.get(level.getServer());var plane=data.get(planeId());var realm=level.getServer().getLevel(PocketDimension.KEY);if(plane!=null&&realm!=null&&plane.portals[side]!=null&&plane.portals[side].equals(GlobalPos.of(level.dimension(),worldPosition)))closeInside(realm,plane,side);sync();level.playSound(null,worldPosition,thaumcraft.registry.ModSounds.CRAFTFAIL.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);}finally{changing=false;}}
        @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);if(!contained.isEmpty())tag.put("keystone",contained.save(registry));tag.putBoolean("open",open);tag.putBoolean("xAligned",axis==Direction.Axis.X);tag.putInt("side",side);}
        @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);contained=ItemStack.parseOptional(registry,tag.getCompound("keystone"));open=tag.getBoolean("open");axis=tag.getBoolean("xAligned")?Direction.Axis.X:Direction.Axis.Z;side=Math.clamp(tag.getInt("side"),0,3);}
    }
    public static final class SlotBlock extends BaseEntityBlock implements WandInteractable {
        SlotBlock(Properties properties){super(properties);}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(SlotBlock::new);}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new SlotEntity(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(level.getBlockEntity(pos) instanceof SlotEntity slot){if(stack.is(Content.item("keystone"))){if(level.isClientSide())return ItemInteractionResult.SUCCESS;return slot.insert(stack)?ItemInteractionResult.SUCCESS:ItemInteractionResult.FAIL;}if(stack.getItem() instanceof WandItem&&!slot.contained.isEmpty()){if(level.isClientSide())return ItemInteractionResult.SUCCESS;return slot.activate(player,stack)?ItemInteractionResult.SUCCESS:ItemInteractionResult.FAIL;}}return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
        @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(!player.getMainHandItem().isEmpty()||!(level.getBlockEntity(pos) instanceof SlotEntity slot)||slot.contained.isEmpty())return InteractionResult.PASS;if(!level.isClientSide())MedicalItem.give(player,slot.take());return InteractionResult.SUCCESS;}
        @Override public InteractionResult onWandUseOn(Level level,BlockPos pos,Player player,ItemStack wand,Direction side){if(level.isClientSide())return InteractionResult.SUCCESS;return level.getBlockEntity(pos) instanceof SlotEntity slot&&slot.activate(player,wand)?InteractionResult.SUCCESS:InteractionResult.PASS;}
        @Override protected VoxelShape getBlockSupportShape(BlockState state,BlockGetter level,BlockPos pos){return Shapes.empty();}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide()&&level.getBlockEntity(pos) instanceof SlotEntity slot){slot.close();if(!slot.contained.isEmpty())popResource(level,pos,slot.contained);}super.onRemove(state,level,pos,next,moving);}
    }
    public static final class FrameBlock extends Block {
        FrameBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(AXIS,Direction.Axis.X).setValue(PART,0));}
        @Override protected MapCodec<? extends Block> codec(){return simpleCodec(FrameBlock::new);}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(AXIS,PART);}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide()){var off=FRAME_PARTS.get(state.getValue(PART));var parent=offset(pos,state.getValue(AXIS),-off.getX(),-off.getY());if(level.getBlockEntity(parent) instanceof SlotEntity slot)slot.close();}super.onRemove(state,level,pos,next,moving);}
        @Override public ItemStack getCloneItemStack(BlockState state,HitResult hit,LevelReader level,BlockPos pos,Player player){return ItemStack.EMPTY;}
    }
    public static final class PortalBlock extends Block {
        PortalBlock(Properties properties){super(properties);registerDefaultState(stateDefinition.any().setValue(AXIS,Direction.Axis.X).setValue(RETURNING,false).setValue(SIDE,0));}
        @Override protected MapCodec<? extends Block> codec(){return simpleCodec(PortalBlock::new);}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(AXIS,RETURNING,SIDE);}
        @Override protected boolean skipRendering(BlockState state,BlockState next,Direction face){return next.is(this)||super.skipRendering(state,next,face);}
        @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(entity instanceof ServerPlayer player)teleport(player,pos,state);}
        @Override public void animateTick(BlockState state,Level level,BlockPos pos,net.minecraft.util.RandomSource random){for(int i=0;i<4;i++){int sign=random.nextBoolean()?1:-1;double x=pos.getX()+random.nextFloat(),y=pos.getY()+random.nextFloat(),z=pos.getZ()+random.nextFloat(),dx=(random.nextFloat()-.5)*.5,dy=(random.nextFloat()-.5)*.5,dz=(random.nextFloat()-.5)*.5;if(state.getValue(AXIS)==Direction.Axis.X){z=pos.getZ()+.5+.25*sign;dz=random.nextFloat()*2*sign;}else{x=pos.getX()+.5+.25*sign;dx=random.nextFloat()*2*sign;}level.addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL,x,y,z,dx,dy,dz);}}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level instanceof ServerLevel server){if(state.getValue(RETURNING)){var plane=PocketPlanes.get(server.getServer()).get(Math.floorDiv(pos.getZ()+128,256));int side=state.getValue(SIDE);if(plane!=null&&!plane.gatewayChanging[side]){var link=plane.portals[side];var origin=link==null?null:server.getServer().getLevel(link.dimension());if(origin!=null){origin.getChunkAt(link.pos());if(origin.getBlockEntity(link.pos()) instanceof SlotEntity slot)slot.close();}if(plane.portals[side]!=null)closeInside(server,plane,side);}}else{var slot=slotAt(level,pos,state.getValue(AXIS));if(slot!=null)slot.close();}}super.onRemove(state,level,pos,next,moving);}
        @Override public ItemStack getCloneItemStack(BlockState state,HitResult hit,LevelReader level,BlockPos pos,Player player){return ItemStack.EMPTY;}
    }
}
