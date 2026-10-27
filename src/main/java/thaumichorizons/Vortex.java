package thaumichorizons;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.entity.golem.GolemSnapshot;
import thaumcraft.wand.WandInteractable;

public final class Vortex extends thaumcraft.blockentity.TcBlockEntity implements AspectContainer,WandInteractable {
    public static final DeferredBlock<RiftBlock> BLOCK=Content.BLOCKS.register("vortex",()->new RiftBlock(BlockBehaviour.Properties.of().strength(-1,20000).noOcclusion().noCollission().noLootTable().lightLevel(s->15)));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Vortex>> TYPE=Content.TILES.register("vortex",()->BlockEntityType.Builder.of(Vortex::new,BLOCK.get()).build(null));
    public static final DeferredHolder<SoundEvent,SoundEvent> SOUND=Content.SOUNDS.register("vortex",()->SoundEvent.createVariableRangeEvent(ThaumicHorizons.id("vortex")));
    public AspectList aspects=new AspectList();
    public int count,beams,planeId=-1,pendingId=-1;
    public boolean ateDevices,collapsing,createdDimension,cheat;
    private boolean removing;
    private ItemStack pearl=ItemStack.EMPTY;
    final List<ItemStack> outputs=new ArrayList<>();
    static java.util.function.Consumer<Vortex> clientTick=node->{};
    public Vortex(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
    static void init(){Content.ALL.put("vortex",Content.ITEMS.registerItem("vortex",p->new RiftItem(BLOCK.get(),p),new Item.Properties()));}
    static ItemStack stableItem(){var stack=new ItemStack(Content.item("vortex"));var tag=new CompoundTag();tag.putBoolean("thStableVortex",true);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;}
    public static void tick(Level level,BlockPos pos,BlockState state,Vortex node){
        if(level.isClientSide()){clientTick.accept(node);if(node.collapsing)node.count++;else if(node.count<50)node.count++;else if(!node.cheat)node.count+=Math.max(0,6-node.beams);return;}
        if(node.pendingId>=0){var plane=PocketPlanes.get(level.getServer()).get(node.pendingId);if(plane!=null&&plane.ready){node.planeId=plane.id;node.createdDimension=true;node.pendingId=-1;node.pearl=ItemStack.EMPTY;node.sync();}else if(plane==null||plane.failed){if(!node.pearl.isEmpty())node.queue(node.pearl);node.pearl=ItemStack.EMPTY;node.pendingId=-1;node.sync();}}
        int beams=RiftDevices.countBeams(level,pos);if(beams!=node.beams){node.beams=beams;node.sync();}
        if(node.collapsing){if(++node.count>25){node.removeReturn();level.removeBlock(pos,false);explode(level,pos);}node.setChanged();return;}
        if(node.count<50){node.count++;if(node.count==50)node.sync();node.setChanged();return;}
        if(!node.ateDevices){if(!node.cheat&&level.dimension()!=PocketDimension.KEY)for(var block:BlockPos.betweenClosed(pos.offset(-2,-2,-2),pos.offset(2,2,2)))if(!block.equals(pos))level.removeBlock(block,false);node.ateDevices=true;node.sync();}
        if(node.beams<6&&!node.cheat&&level.dimension()!=PocketDimension.KEY)node.hunger();else if(!node.createdDimension)node.consumeItems();
        if(!node.cheat)node.count+=Math.max(0,6-node.beams);
        if(node.count>2400&&!node.cheat&&level.dimension()!=PocketDimension.KEY){node.collapsing=true;node.count=0;node.sync();}
        if(node.createdDimension)for(var player:level.getEntitiesOfClass(ServerPlayer.class,new AABB(pos)))node.travel(player);
        node.setChanged();if(level.getGameTime()%40==0)node.sync();
    }
    static void makeReturn(ServerLevel level,PocketPlanes.Plane plane){
        var pos=new BlockPos(0,129,plane.id*256);level.setBlockAndUpdate(pos,BLOCK.get().defaultBlockState());var node=(Vortex)level.getBlockEntity(pos);node.aspects=plane.aspects.copy();if(node.aspects.size()==0)node.aspects.add(Aspect.AIR,1);node.planeId=plane.id;node.cheat=true;node.createdDimension=true;node.ateDevices=true;node.count=50;node.sync();
    }
    void travel(ServerPlayer player){
        if(player.isPassenger()||player.isVehicle())return;if(player.isOnPortalCooldown()){player.setPortalCooldown(10);return;}var plane=PocketPlanes.get(player.server).get(planeId);if(plane==null||!plane.ready)return;
        ServerLevel target;Vec3 destination;if(level.dimension()!=PocketDimension.KEY){target=player.server.getLevel(PocketDimension.KEY);destination=new Vec3(.5,129,plane.id*256+.5);}else{target=player.server.getLevel(plane.origin.dimension());destination=Vec3.atCenterOf(plane.origin.pos()).add(0,-.5,0);}if(target==null)return;player.setPortalCooldown(200);player.teleportTo(target,destination.x,destination.y,destination.z,player.getYRot(),player.getXRot());
    }
    void consumeItems(){
        for(var entity:level.getEntitiesOfClass(ItemEntity.class,new AABB(worldPosition).inflate(1))){var stack=entity.getItem();if(stack.isEmpty())continue;
            if(stack.is(thaumcraft.registry.EldritchContent.PRIMORDIAL_PEARL.get())&&HorizonsConfig.POCKET_PLANES.get()&&pendingId<0){String name=stack.has(DataComponents.CUSTOM_NAME)?stack.getHoverName().getString():entity.getOwner() instanceof Player player?player.getName().getString()+" "+net.minecraft.network.chat.Component.translatable("thaumichorizons.pocketplane").getString():net.minecraft.network.chat.Component.translatable("thaumichorizons.pocketplane").getString();var plane=PocketPlanes.get(level.getServer()).create(name,aspects,GlobalPos.of(level.dimension(),worldPosition),level.random.nextLong());pendingId=plane.id;pearl=stack.copyWithCount(1);stack.shrink(1);if(stack.isEmpty())entity.discard();else entity.setItem(stack);sync();}
            else voidCraft(entity);
        }
    }
    boolean voidCraft(ItemEntity entity){
        var stack=entity.getItem();if(stack.is(thaumcraft.registry.ModItems.VOID_METAL_INGOT.get()))queue(new ItemStack(Content.item("void_putty"),stack.getCount()));
        else if(stack.is(thaumcraft.registry.ModItems.SALIS_MUNDUS.get())){for(int i=0;i<stack.getCount();i++)for(int n=level.random.nextInt(4)+1;n>0;n--){var wisp=thaumcraft.registry.ModEntities.WISP.get().create(level);wisp.setPos(worldPosition.getCenter());if(aspects.size()>0)wisp.setAspect(aspects.getAspects().get(level.random.nextInt(aspects.size())));level.addFreshEntity(wisp);}}
        else if(stack.is(Content.item("crystal_wand")))for(int i=0;i<stack.getCount();i++)queue(new ItemStack(Content.item("disposable_wand")));
        else if(stack.is(Content.item("golem_powder"))){var tag=new CompoundTag();entity.saveWithoutId(tag);UUID owner=entity.getOwner()!=null?entity.getOwner().getUUID():tag.hasUUID("Thrower")?tag.getUUID("Thrower"):null;if(owner==null)return false;for(int i=0;i<stack.getCount();i++){var golem=AnimatedGolems.TYPE.get().create(level);golem.moveTo(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5,0,0);golem.initialize(Blocks.AIR.defaultBlockState(),-420,false,false,owner,GolemSnapshot.EMPTY);level.addFreshEntity(golem);level.broadcastEntityEvent(golem,(byte)7);}}
        else return false;entity.discard();sync();return true;
    }
    private void queue(ItemStack stack){for(int left=stack.getCount();left>0;left-=stack.getMaxStackSize())outputs.add(stack.copyWithCount(Math.min(left,stack.getMaxStackSize())));}
    @Override public InteractionResult onWandUseOn(Level level,BlockPos pos,Player player,ItemStack wand,Direction side){if(level.isClientSide())return InteractionResult.SUCCESS;if(!outputs.isEmpty()){var item=new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,outputs.getFirst().copy());if(level.addFreshEntity(item))outputs.removeFirst();sync();}level.playSound(null,pos,thaumcraft.registry.ModSounds.WAND.get(),SoundSource.BLOCKS,.5F,.9F+level.random.nextFloat()*.2F);return InteractionResult.SUCCESS;}
    private void hunger(){
        var center=worldPosition.getCenter();for(var entity:level.getEntities((Entity)null,new AABB(worldPosition).inflate(15))){if(entity instanceof Player player&&player.getAbilities().invulnerable)continue;double distance=center.distanceToSqr(entity.position());if(entity.isAlive()&&!entity.isInvulnerable()&&distance<2){if(entity instanceof FallingBlockEntity)entity.discard();else entity.hurt(level.damageSources().fellOutOfWorld(),3-beams/2F);}var pull=center.subtract(entity.position()).scale(1/15D);double length=pull.length();if(length>1.0E-8&&length<1){double strength=(1-length)*(1-length)*(2-beams/3D);entity.setDeltaMovement(entity.getDeltaMovement().add(pull.x/length*strength*.15,pull.y/length*strength*.25,pull.z/length*strength*.15));entity.hurtMarked=true;}}
        for(int i=0;i<3;i++)if(beams<=0||level.random.nextInt(beams)==0){int x=worldPosition.getX()+level.random.nextInt(16)-level.random.nextInt(16),y=worldPosition.getY()+level.random.nextInt(16)-level.random.nextInt(16),z=worldPosition.getZ()+level.random.nextInt(16)-level.random.nextInt(16);if(!level.hasChunkAt(new BlockPos(x,y,z)))continue;y=Math.min(y,level.getHeight(Heightmap.Types.MOTION_BLOCKING,x,z));var hit=level.clip(new ClipContext(center,new Vec3(x+.5,y+.5,z+.5),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,CollisionContext.empty()));if(hit.getType()==HitResult.Type.BLOCK&&worldPosition.distSqr(hit.getBlockPos())<256){var state=level.getBlockState(hit.getBlockPos());float hardness=state.getDestroySpeed(level,hit.getBlockPos());if(hardness>=0&&hardness<10)level.destroyBlock(hit.getBlockPos(),true);}}
    }
    static void explode(Level level,BlockPos pos){
        level.explode(null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,3,false,Level.ExplosionInteraction.BLOCK);for(int i=0;i<50;i++){var target=pos.offset(level.random.nextInt(8)-level.random.nextInt(8),level.random.nextInt(8)-level.random.nextInt(8),level.random.nextInt(8)-level.random.nextInt(8));if(level.hasChunkAt(target)&&level.isEmptyBlock(target))level.setBlockAndUpdate(target,(target.getY()<pos.getY()?thaumcraft.registry.ModBlocks.FLUX_GOO.get():thaumcraft.registry.ModBlocks.FLUX_GAS.get()).withLevel(8));}
    }
    private void removeReturn(){if(cheat||planeId<0||level==null)return;var target=level.getServer().getLevel(PocketDimension.KEY);var pos=new BlockPos(0,129,planeId*256);if(target!=null&&target.getBlockEntity(pos) instanceof Vortex exit&&exit.planeId==planeId){exit.removing=true;target.removeBlock(pos,false);}}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.put("aspects",BloodInfuser.saveAspects(aspects));tag.putInt("count",count);tag.putInt("beams",beams);tag.putInt("planeId",planeId);tag.putInt("pendingId",pendingId);tag.putBoolean("ateDevices",ateDevices);tag.putBoolean("collapsing",collapsing);tag.putBoolean("createdDimension",createdDimension);tag.putBoolean("cheat",cheat);if(!pearl.isEmpty())tag.put("pearl",pearl.save(registry));var list=new ListTag();for(var stack:outputs)list.add(stack.save(registry));tag.put("outputs",list);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);aspects=BloodInfuser.loadAspects(tag.getCompound("aspects"));count=tag.getInt("count");beams=Math.clamp(tag.getInt("beams"),0,6);planeId=tag.contains("planeId")?tag.getInt("planeId"):-1;pendingId=tag.contains("pendingId")?tag.getInt("pendingId"):-1;ateDevices=tag.getBoolean("ateDevices");collapsing=tag.getBoolean("collapsing");createdDimension=tag.getBoolean("createdDimension");cheat=tag.getBoolean("cheat");pearl=ItemStack.parseOptional(registry,tag.getCompound("pearl"));outputs.clear();var list=tag.getList("outputs",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){var stack=ItemStack.parseOptional(registry,list.getCompound(i));if(!stack.isEmpty())queue(stack);}}
    @Override public AspectList getAspects(){return aspects.copy();}
    @Override public void setAspects(AspectList list){}
    @Override public boolean doesContainerAccept(Aspect aspect){return false;}
    @Override public int addToContainer(Aspect aspect,int amount){return Math.max(0,amount);}
    @Override public boolean takeFromContainer(Aspect aspect,int amount){return false;}
    @Override public boolean doesContainerContainAmount(Aspect aspect,int amount){return aspects.getAmount(aspect)>=amount;}
    @Override public int containerContains(Aspect aspect){return aspects.getAmount(aspect);}
    public static final class RiftItem extends BlockItem {
        RiftItem(Block block,Properties properties){super(block,properties);}
        @Override public net.minecraft.network.chat.Component getName(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("thStableVortex")?net.minecraft.network.chat.Component.translatable("block.thaumichorizons.stable_vortex"):super.getName(stack);}
    }
    public static final class RiftBlock extends BaseEntityBlock {
        RiftBlock(Properties properties){super(properties);}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(RiftBlock::new);}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new Vortex(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
        @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(4.8,4.8,4.8,11.2,11.2,11.2);}
        @Override public ItemStack getCloneItemStack(BlockState state,HitResult hit,LevelReader level,BlockPos pos,Player player){return level.getBlockEntity(pos) instanceof Vortex node&&node.cheat?stableItem():new ItemStack(this);}
        @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){if(!level.isClientSide()&&level.getBlockEntity(pos) instanceof Vortex node){for(int i=level.random.nextInt(4)+1;i>0;i--){var available=level.random.nextInt(3)==0?Aspect.getCompoundAspects():Aspect.getPrimalAspects();node.aspects.add(available.get(level.random.nextInt(available.size())),level.random.nextInt(30));}if(node.aspects.size()==0)node.aspects.add(Aspect.AIR,1);node.cheat=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("thStableVortex");node.sync();}}
        @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide()&&level.getBlockEntity(pos) instanceof Vortex node&&!node.removing){node.removing=true;node.removeReturn();for(var stack:node.outputs)popResource(level,pos,stack);if(!node.pearl.isEmpty())popResource(level,pos,node.pearl);}super.onRemove(state,level,pos,next,moving);}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),Vortex::tick);}
    }
}
