package thaumichorizons;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.research.ResearchGate;
import thaumcraft.wand.*;

public final class Clouds {
    public static final List<String> NAMES=List.of("rain","fire","thunder","acid","alloy","flesh","vis","glyph","spore","animus");
    public static final int[] PERIODS={-1,-1,120,-1,480,480,80,360,280,550};
    public static final Map<String,DeferredBlock<CloudBlock>> BLOCKS=new LinkedHashMap<>();
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CloudEntity>> TYPE=Content.TILES.register("cloud",()->BlockEntityType.Builder.of(CloudEntity::new,BLOCKS.values().stream().map(DeferredBlock::get).toArray(Block[]::new)).build(null));
    static boolean glowing(int type){return type==1||type==4||type==6||type==7||type==9;}
    static void init(){for(int i=0;i<NAMES.size();i++){final int type=i;String name=NAMES.get(i)+"_cloud";var block=Content.BLOCKS.register(name,()->new CloudBlock(type,BlockBehaviour.Properties.of().strength(Float.MAX_VALUE,Float.MAX_VALUE).noOcclusion().noCollission().noLootTable().lightLevel(s->glowing(type)?15:0)));BLOCKS.put(NAMES.get(i),block);Content.ALL.put(name,Content.ITEMS.registerSimpleBlockItem(name,block));}}
    static net.minecraft.world.damagesource.DamageSource acid(Level level){return new net.minecraft.world.damagesource.DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(thaumcraft.damage.ModDamageTypes.DISSOLVE));}
    public static final class CloudBlock extends BaseEntityBlock implements WandInteractable {
        final int type;
        CloudBlock(int type,Properties properties){super(properties);this.type=type;}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(properties->new CloudBlock(type,properties));}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new CloudEntity(pos,state);}
        @Override protected boolean skipRendering(BlockState state,BlockState adjacent,Direction direction){return adjacent.is(this)||super.skipRendering(state,adjacent,direction);}
        @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(type==1||type==4)entity.igniteForSeconds(6);else if(type==3&&!level.isClientSide())entity.hurt(acid(level),1);}
        @Override public InteractionResult onWandUseOn(Level level,BlockPos pos,Player player,ItemStack wand,Direction side){
            if(!ResearchGate.isComplete(player,"planarClouds"))return InteractionResult.PASS;if(level.isClientSide())return InteractionResult.SUCCESS;
            if(!((WandItem)wand.getItem()).consumeAllVis(wand,player,new AspectList().add(Aspect.AIR,100),false,false))return InteractionResult.FAIL;
            var drop=new Alchemite.InvulnerableDrop(Alchemite.DROPS.get(),level);drop.setPos(pos.getCenter());drop.setItem(new ItemStack(this));if(!level.addFreshEntity(drop))return InteractionResult.FAIL;((WandItem)wand.getItem()).consumeAllVis(wand,player,new AspectList().add(Aspect.AIR,100),true,false);level.removeBlock(pos,false);return InteractionResult.SUCCESS;
        }
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),CloudEntity::tick);}
    }
    public static final class CloudEntity extends thaumcraft.blockentity.TcBlockEntity {
        public boolean raining;
        public int distance=256,dropTimer=-2;
        private int timer;
        public CloudEntity(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
        public int kind(){return ((CloudBlock)getBlockState().getBlock()).type;}
        public BlockPos floor(){return worldPosition.below(distance);}
        public void findFloor(){var start=worldPosition.getCenter().add(0,-.501,0);var end=start.add(0,-Math.min(256,start.y-level.getMinBuildHeight()),0);var hit=level.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,net.minecraft.world.phys.shapes.CollisionContext.empty()));int previous=distance;distance=hit.getType()==HitResult.Type.BLOCK?worldPosition.getY()-hit.getBlockPos().getY():256;if(previous!=distance&&!level.isClientSide())sync();}
        public static void tick(Level level,BlockPos pos,BlockState state,CloudEntity cloud){
            if(level.isClientSide())return;cloud.timer++;if(cloud.dropTimer==-2){cloud.dropTimer=PERIODS[cloud.kind()];cloud.findFloor();}
            if(cloud.timer==1||cloud.timer%100==0){boolean raining=level.getServer().overworld().isRaining();if(raining!=cloud.raining){cloud.raining=raining;cloud.sync();}}
            if(!cloud.raining)return;if(level.getGameTime()%10==0){cloud.findFloor();cloud.weatherFloor();}cloud.weatherEntities();int period=PERIODS[cloud.kind()];if(period<0){cloud.dropTimer=-1;return;}if(cloud.dropTimer>0){cloud.dropTimer--;cloud.setChanged();if(cloud.dropTimer==0){cloud.dropTimer=period/2+level.random.nextInt(period/2);cloud.produce();}}
        }
        void weatherFloor(){
            var pos=floor();if(!level.hasChunkAt(pos)||level.isOutsideBuildHeight(pos))return;var state=level.getBlockState(pos);int type=kind(),melt=ThermalFoci.meltable(state);
            if(type==1||type==4){if(melt==1||state.isFlammable(level,pos,Direction.UP)){level.setBlockAndUpdate(pos,BaseFireBlock.getState(level,pos));}else if(melt==2){level.setBlockAndUpdate(pos,(level.dimension()==Level.NETHER?Blocks.AIR:Blocks.WATER).defaultBlockState());}else if(melt==3)level.setBlockAndUpdate(pos,Blocks.DIRT.defaultBlockState());else if(melt==4){state.onCaughtFire(level,pos,Direction.UP,null);level.removeBlock(pos,false);}}
            else if(type==3){if(melt==1||state.isFlammable(level,pos,Direction.UP))level.removeBlock(pos,false);else if(melt==3)level.setBlockAndUpdate(pos,Blocks.DIRT.defaultBlockState());}
            else if(!cold()){if(state.is(Blocks.FARMLAND))level.setBlockAndUpdate(pos,state.setValue(FarmBlock.MOISTURE,7));else if(level.getBlockState(pos.above()).is(Blocks.FIRE))level.removeBlock(pos.above(),false);else if(state.is(Blocks.CAULDRON))level.setBlockAndUpdate(pos,Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,3));}
            else if(state.isSolidRender(level,pos)&&level.isEmptyBlock(pos.above()))level.setBlockAndUpdate(pos.above(),Blocks.SNOW.defaultBlockState());
        }
        boolean cold(){return level.getBiome(worldPosition).value().coldEnoughToSnow(worldPosition);}
        void weatherEntities(){
            for(var entity:level.getEntities((Entity)null,new AABB(worldPosition.getX(),worldPosition.getY()-distance,worldPosition.getZ(),worldPosition.getX()+1,worldPosition.getY(),worldPosition.getZ()+1))){
                if(kind()==1||kind()==4)entity.igniteForSeconds(6);else if(kind()==3)entity.hurt(acid(level),1);else if(!cold()){
                    if(entity instanceof net.minecraft.world.entity.monster.EnderMan||entity instanceof net.minecraft.world.entity.monster.Blaze||entity.getType()==thaumcraft.registry.ModEntities.FIREBAT.get())entity.hurt(level.damageSources().drown(),1);entity.clearFire();
                    if(kind()==8&&entity instanceof Cow cow&&!(entity instanceof MushroomCow)){var mushroom=EntityType.MOOSHROOM.create(level);mushroom.moveTo(cow.getX(),cow.getY(),cow.getZ(),cow.getYRot(),cow.getXRot());mushroom.setHealth(cow.getHealth());mushroom.yBodyRot=cow.yBodyRot;if(level.addFreshEntity(mushroom))cow.discard();}
                }
            }
        }
        void produce(){
            var random=level.random;switch(kind()){
                case 2,9->{findFloor();var bolt=FiniteLightning.TYPE.get().create(level);bolt.setPos(worldPosition.getX()+.5,floor().getY(),worldPosition.getZ()+.5);bolt.configure(distance,kind()==9);level.addFreshEntity(bolt);}
                case 4->{int roll=random.nextInt(75);Item item;if(roll<6)item=Items.GOLD_NUGGET;else if(roll<12)item=nugget("silver");else if(roll<20)item=nugget("copper");else if(roll<30)item=nugget("tin");else if(roll<40)item=nugget("lead");else if(roll<50)item=thaumcraft.registry.ModItems.THAUMIUM_NUGGET.get();else item=Items.IRON_NUGGET;drop(new ItemStack(item));}
                case 5->{int roll=random.nextInt(10);Item item=switch(roll){case 0->Items.BEEF;case 1->Items.PORKCHOP;case 2->Items.CHICKEN;case 3->switch(random.nextInt(3)){case 0->Items.COD;case 1->Items.SALMON;default->Items.TROPICAL_FISH;};default->Content.item("meat");};drop(new ItemStack(item));}
                case 6->{var aspects=List.of(Aspect.WATER,Aspect.FIRE,Aspect.ORDER,Aspect.ENTROPY,Aspect.AIR,Aspect.EARTH);level.addFreshEntity(thaumcraft.entity.AspectOrb.create(level,worldPosition.getX()+random.nextDouble(),worldPosition.getY()+.5,worldPosition.getZ()+random.nextDouble(),aspects.get(random.nextInt(6)),1));}
                case 7->level.addFreshEntity(new ExperienceOrb(level,worldPosition.getX()+random.nextDouble(),worldPosition.getY()+.5,worldPosition.getZ()+random.nextDouble(),random.nextInt(4)));
                case 8->grow();
            }
        }
        private Item nugget(String metal){var ingots=BuiltInRegistries.ITEM.getTag(TagKey.create(Registries.ITEM,ResourceLocation.parse("c:ingots/"+metal)));return ingots.isPresent()&&ingots.get().size()>0?BuiltInRegistries.ITEM.get(ResourceLocation.parse("thaumcraft:"+metal+"_nugget")):Items.IRON_NUGGET;}
        private void drop(ItemStack stack){var entity=new Alchemite.InvulnerableDrop(Alchemite.DROPS.get(),level);entity.setPos(worldPosition.getX()+level.random.nextDouble(),worldPosition.getY()+.5,worldPosition.getZ()+level.random.nextDouble());entity.setItem(stack);entity.setDefaultPickUpDelay();level.addFreshEntity(entity);}
        void grow(){
            findFloor();var floor=floor();if(!level.isEmptyBlock(floor.above()))return;var state=level.getBlockState(floor);var random=level.random;Block plant=null;
            if(state.is(Blocks.FARMLAND))plant=switch(random.nextInt(8)){case 1->Blocks.MELON_STEM;case 2->Blocks.PUMPKIN_STEM;case 3->Blocks.CARROTS;case 4->Blocks.POTATOES;default->Blocks.WHEAT;};
            else if(state.is(Blocks.DIRT)){level.setBlockAndUpdate(floor,(random.nextInt(10)==4?Blocks.MYCELIUM:Blocks.GRASS_BLOCK).defaultBlockState());return;}
            else if(state.is(Blocks.STONE)||state.is(Blocks.MYCELIUM))plant=random.nextInt(3)==1?Blocks.BROWN_MUSHROOM:Blocks.RED_MUSHROOM;
            else if(state.is(Blocks.GRASS_BLOCK)){
                int roll=random.nextInt(1000);if(roll==666)plant=thaumcraft.registry.ModBlocks.SILVERWOOD_SAPLING.get();else if(roll<750){var plants=List.of(Blocks.SHORT_GRASS,Blocks.FERN,Blocks.DANDELION,Blocks.POPPY,Blocks.POPPY,Blocks.BLUE_ORCHID,Blocks.ALLIUM,Blocks.AZURE_BLUET,Blocks.RED_TULIP,Blocks.ORANGE_TULIP,Blocks.WHITE_TULIP,Blocks.PINK_TULIP,Blocks.OXEYE_DAISY,Blocks.SUGAR_CANE);plant=plants.get(random.nextInt(14));}
                else if(roll<950){int sapling=random.nextInt(100);plant=sapling<10?thaumcraft.registry.ModBlocks.GREATWOOD_SAPLING.get():sapling<25?Blocks.OAK_SAPLING:sapling<40?Blocks.SPRUCE_SAPLING:sapling<55?Blocks.BIRCH_SAPLING:sapling<70?Blocks.JUNGLE_SAPLING:sapling<85?Blocks.ACACIA_SAPLING:Blocks.DARK_OAK_SAPLING;}
                else plant=roll<975?thaumcraft.registry.ModBlocks.SHIMMERLEAF.get():thaumcraft.registry.ModBlocks.VISHROOM.get();
            }else if(state.is(Blocks.SAND))plant=random.nextInt(10)==4?thaumcraft.registry.ModBlocks.CINDERPEARL.get():Blocks.CACTUS;else if(state.is(Blocks.SOUL_SAND))plant=Blocks.NETHER_WART;
            if(plant!=null)level.setBlockAndUpdate(floor.above(),plant.defaultBlockState());
        }
        @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);tag.putBoolean("raining",raining);tag.putInt("distance",distance);tag.putInt("dropTimer",dropTimer);}
        @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);raining=tag.getBoolean("raining");distance=tag.contains("distance")?Math.clamp(tag.getInt("distance"),1,256):256;dropTimer=tag.contains("dropTimer")?tag.getInt("dropTimer"):-2;}
    }
}
