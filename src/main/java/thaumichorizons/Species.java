package thaumichorizons;

import java.util.*;
import java.util.function.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.platform.*;
import thaumcraft.wand.*;

/** Distinct entity types for the original species infusions; offspring remain ordinary animals. */
public final class Species {
    public interface Form {String form();}
    public static final Map<String,DeferredHolder<EntityType<?>,? extends EntityType<? extends LivingEntity>>> TYPES=new LinkedHashMap<>();
    static <T extends LivingEntity> DeferredHolder<EntityType<?>,EntityType<T>> register(String name,EntityType.EntityFactory<T> factory,float w,float h,boolean fireproof){
        var holder=Content.ENTITIES.register(name,()->{var builder=EntityType.Builder.of(factory,MobCategory.CREATURE).sized(w,h).clientTrackingRange(10);if(fireproof)builder.fireImmune();return builder.build(ThaumicHorizons.id(name).toString());});TYPES.put(name,holder);return holder;
    }
    public static final DeferredHolder<EntityType<?>,EntityType<InfusedSheep>> CHROMATIC=register("chromatic_sheep",(t,l)->new InfusedSheep(t,l,"chromatic_sheep"),.9F,1.3F,false),SHEARING=register("self_shearing_sheep",(t,l)->new InfusedSheep(t,l,"self_shearing_sheep"),.9F,1.3F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<InfusedChicken>> SCHOLAR=register("scholar_chicken",(t,l)->new InfusedChicken(t,l,"scholar_chicken"),.3F,.7F,false),GOLD=register("gold_chicken",(t,l)->new InfusedChicken(t,l,"gold_chicken"),.3F,.7F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<ChocolateCow>> CHOCOLATE=register("chocolate_cow",ChocolateCow::new,.9F,1.4F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<InfusedCat>> PANTHER=register("guardian_panther",(t,l)->new InfusedCat(t,l,"guardian_panther"),1.2F,1.6F,false),GRAVEKEEPER=register("gravekeeper",(t,l)->new InfusedCat(t,l,"gravekeeper"),.6F,.8F,false),FAMILIAR=register("familiar",(t,l)->new InfusedCat(t,l,"familiar"),.6F,.8F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<InfusedWolf>> SEA=register("seawolf",(t,l)->new InfusedWolf(t,l,"seawolf"),.6F,.8F,false),LUNAR=register("lunar_wolf",(t,l)->new InfusedWolf(t,l,"lunar_wolf"),.6F,.8F,false),NETHER=register("nether_hound",(t,l)->new InfusedWolf(t,l,"nether_hound"),.6F,.8F,true);
    public static final DeferredHolder<EntityType<?>,EntityType<InfusedPig>> ORE=register("ore_pig",(t,l)->new InfusedPig(t,l,"ore_pig"),.9F,.9F,false),TAINT_EATER=register("taint_eater_pig",(t,l)->new InfusedPig(t,l,"taint_eater_pig"),.9F,.9F,false);
    public static final DeferredHolder<EntityType<?>,EntityType<Sheeder>> SHEEDER=register("sheeder",Sheeder::new,1.4F,.9F,false);
    public static final DeferredBlock<Block> CHOCOLATE_BLOCK=Content.BLOCKS.register("chocolate",()->new Block(BlockBehaviour.Properties.of().strength(.5F,.5F)));
    public static void init(){
        SpeciesHorse.init();
        for(var entry:TYPES.entrySet()){
            String name=entry.getKey()+"_spawn_egg";
            var colors=EggPalette.colors(entry.getKey());Content.ALL.put(name,Content.ITEMS.registerItem(name,p->new net.neoforged.neoforge.common.DeferredSpawnEggItem(()->(EntityType<? extends Mob>)entry.getValue().get(),colors[0],colors[1],p),new Item.Properties()));
        }
        for(String name:List.of("ink_egg","gold_egg"))Content.ALL.put(name,Content.ITEMS.registerSimpleItem(name,new Item.Properties().stacksTo(16)));
        Content.ALL.put("chocolate_milk",Content.ITEMS.registerItem("chocolate_milk",MilkBucketItem::new,new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        Content.ALL.put("chocolate_bar",Content.ITEMS.registerSimpleItem("chocolate_bar",new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationModifier(2).build())));
        Content.ALL.put("ice_cream",Content.ITEMS.registerSimpleItem("ice_cream",new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(4).build())));
        Content.ALL.put("chocolate",Content.ITEMS.registerSimpleBlockItem("chocolate",CHOCOLATE_BLOCK));
    }
    public static void restoreShowcaseAi(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event){
        if(event.getLevel().isClientSide()||!(event.getEntity() instanceof Mob mob)||!(mob instanceof Form form))return;
        // Older validation saves used NoAI to keep the exhibit in place; it also suppresses motion.
        boolean namedExhibit=mob.hasCustomName()&&mob.getCustomName().getString().equals(form.form());
        boolean oldExhibit=event.getLevel() instanceof ServerLevel server&&server.getServer().getWorldData().getLevelName().equals("th-validation")&&mob.getX()>=34&&mob.getX()<=76&&mob.getZ()>=-5&&mob.getZ()<=25;
        if(mob.isNoAi()&&(namedExhibit||oldExhibit))mob.setNoAi(false);
    }
    public static void attributes(EntityAttributeCreationEvent event){
        for(var type:List.of(CHROMATIC,SHEARING))event.put(type.get(),Sheep.createAttributes().build());
        for(var type:List.of(SCHOLAR,GOLD))event.put(type.get(),Chicken.createAttributes().build());event.put(CHOCOLATE.get(),Cow.createAttributes().build());
        event.put(PANTHER.get(),Cat.createAttributes().add(Attributes.MAX_HEALTH,24).add(Attributes.MOVEMENT_SPEED,.5).add(Attributes.ATTACK_DAMAGE,6).build());
        for(var type:List.of(GRAVEKEEPER,FAMILIAR))event.put(type.get(),Cat.createAttributes().build());
        for(var type:List.of(SEA,LUNAR,NETHER))event.put(type.get(),Wolf.createAttributes().add(Attributes.MAX_HEALTH,8).build());
        for(var type:List.of(ORE,TAINT_EATER))event.put(type.get(),Pig.createAttributes().build());event.put(SHEEDER.get(),Spider.createAttributes().build());SpeciesHorse.attributes(event);
    }
    static ResourceKey<LootTable> loot(String entity){return ResourceKey.create(Registries.LOOT_TABLE,ResourceLocation.withDefaultNamespace("entities/"+entity));}
    public static boolean accepts(String input,LivingEntity entity){
        if(entity==null)return false;EntityType<?> type=entity.getType();return switch(input){case "sheep"->type==EntityType.SHEEP;case "chicken"->type==EntityType.CHICKEN;case "cow"->type==EntityType.COW;case "pig"->type==EntityType.PIG;case "cat"->type==EntityType.CAT||type==EntityType.OCELOT;case "wolf"->type==EntityType.WOLF;case "horse"->type==EntityType.HORSE;case "spider"->type==EntityType.SPIDER;case "endersteed"->type==SpeciesHorse.ENDERSTEED.get();default->false;};
    }
    public static LivingEntity transform(LivingEntity donor,String output,AspectList cost){
        EntityType<?> type=output.equals("mooshroom")?EntityType.MOOSHROOM:TYPES.containsKey(output)?TYPES.get(output).get():null;if(type==null)return null;
        CompoundTag tag=new CompoundTag();donor.save(tag);tag.remove("Passengers");tag.putString("id",BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        Entity loaded=EntityType.loadEntityRecursive(tag,donor.level(),e->e);if(!(loaded instanceof LivingEntity result))return null;
        if(result instanceof InfusedCat cat&&cat.form().equals("guardian_panther")){cat.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24);cat.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.5);cat.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);}
        if(result instanceof Mob mob)mob.setPersistenceRequired();var data=Infusions.DATA.get(result);if(result instanceof InfusedCat)data.putBoolean("wildOcelot",donor instanceof Ocelot);var paid=BloodInfuser.loadAspects(data.getCompound("cost"));paid.add(cost);data.put("cost",BloodInfuser.saveAspects(paid));Infusions.DATA.set(result,data);Infusions.apply(result);return result;
    }
    public static final class InfusedSheep extends Sheep implements Form {
        private final String form;
        public InfusedSheep(EntityType<? extends Sheep> t,Level l,String form){super(t,l);this.form=form;}
        public String form(){return form;}
        @Override public void aiStep(){super.aiStep();if(!level().isClientSide()){if(form.equals("chromatic_sheep")&&tickCount%30==0)setColor(DyeColor.byId((getColor().getId()+1)%16));else if(form.equals("self_shearing_sheep")&&tickCount%100==0&&readyForShearing())shear(SoundSource.NEUTRAL);}}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return loot(isSheared()?"sheep":"sheep/"+getColor().getName());}
    }
    public static final class InfusedChicken extends Chicken implements Form {
        private final String form;public int featherTime;
        public InfusedChicken(EntityType<? extends Chicken> t,Level l,String form){super(t,l);this.form=form;featherTime=4000+random.nextInt(4000);}
        public String form(){return form;}
        @Override public void aiStep(){int timer=eggTime;eggTime=Integer.MAX_VALUE;super.aiStep();eggTime=timer;if(!level().isClientSide()&&!isBaby()&&!isChickenJockey()){
            if(--eggTime<=0){spawnAtLocation(Content.item(form.equals("gold_chicken")?"gold_egg":"ink_egg"));eggTime=form.equals("gold_chicken")?8000+random.nextInt(8000):6000+random.nextInt(6000);playSound(SoundEvents.CHICKEN_EGG,1,1+(random.nextFloat()-random.nextFloat())*.2F);}
            if(form.equals("scholar_chicken")&&--featherTime<=0){spawnAtLocation(Items.FEATHER);featherTime=4000+random.nextInt(4000);playSound(SoundEvents.CHICKEN_EGG,1,1+(random.nextFloat()-random.nextFloat())*.2F);}
        }}
        @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("THFeather",featherTime);}
        @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);if(tag.contains("THFeather"))featherTime=tag.getInt("THFeather");}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return loot("chicken");}
    }
    public static final class ChocolateCow extends Cow implements Form {
        public ChocolateCow(EntityType<? extends Cow> t,Level l){super(t,l);}
        public String form(){return "chocolate_cow";}
        @Override public InteractionResult mobInteract(Player player,InteractionHand hand){var held=player.getItemInHand(hand);if(held.is(Items.BUCKET)){if(!level().isClientSide()){held.shrink(1);var milk=new ItemStack(Content.item("chocolate_milk"));if(held.isEmpty())player.setItemInHand(hand,milk);else MedicalItem.give(player,milk);playSound(SoundEvents.COW_MILK,1,1);}return InteractionResult.sidedSuccess(level().isClientSide());}return super.mobInteract(player,hand);}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return loot("cow");}
    }
    static void chargeNearby(Mob pet){
        for(Player player:pet.level().getEntitiesOfClass(Player.class,pet.getBoundingBox().inflate(5))){var wand=player.getMainHandItem();if(Wands.isWand(wand))for(Aspect a:Wands.getAspectsWithRoom(wand).getAspects())Wands.storeVis(wand,a,Wands.getVis(wand,a)+1);
            var amulet=Baubles.get(player,0);if(amulet.is(thaumcraft.registry.ModItems.VIS_AMULET.get()))for(Aspect a:VisStorage.getAspectsWithRoom(amulet).getAspects())VisStorage.addRealVis(amulet,a,1,true);
        }
    }
    public static final class InfusedCat extends Cat implements Form {
        private final String form;
        public InfusedCat(EntityType<? extends Cat> t,Level l,String form){super(t,l);this.form=form;setTame(true,false);
            if(form.equals("guardian_panther")){goalSelector.removeAllGoals(g->true);targetSelector.removeAllGoals(g->true);goalSelector.addGoal(1,new FloatGoal(this));goalSelector.addGoal(2,new SitWhenOrderedToGoal(this));goalSelector.addGoal(3,new LeapAtTargetGoal(this,.5F));goalSelector.addGoal(4,new MeleeAttackGoal(this,1,true));goalSelector.addGoal(5,new FollowOwnerGoal(this,1,10,5));goalSelector.addGoal(6,new BreedGoal(this,.8));goalSelector.addGoal(7,new WaterAvoidingRandomStrollGoal(this,.8));goalSelector.addGoal(8,new LookAtPlayerGoal(this,Player.class,10));targetSelector.addGoal(1,new OwnerHurtByTargetGoal(this));targetSelector.addGoal(2,new OwnerHurtTargetGoal(this));targetSelector.addGoal(3,new HurtByTargetGoal(this));}
            else if(form.equals("gravekeeper")){targetSelector.removeAllGoals(g->true);targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,LivingEntity.class,0,false,false,e->e.getType().is(EntityTypeTags.UNDEAD)||BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString().equals("thaumcraft:eldritch_guardian")));}
        }
        public String form(){return form;}
        @Override public boolean isTame(){return true;}
        @Override public void customServerAiStep(){super.customServerAiStep();if(form.equals("familiar")&&tickCount%10==0)chargeNearby(this);else if(form.equals("gravekeeper"))for(LivingEntity e:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(5),e->e.getType().is(EntityTypeTags.UNDEAD)))e.igniteForSeconds(1);}
        @Override public boolean doHurtTarget(Entity target){if(form.equals("gravekeeper")&&target.getType().is(EntityTypeTags.UNDEAD))return true;return target.hurt(damageSources().mobAttack(this),form.equals("guardian_panther")?6:2);}
        @Override public InteractionResult mobInteract(Player player,InteractionHand hand){var stack=player.getItemInHand(hand);var food=stack.get(DataComponents.FOOD);if(form.equals("guardian_panther")&&food!=null&&getHealth()<getMaxHealth()){if(!level().isClientSide()){stack.consume(1,player);heal(food.nutrition());}return InteractionResult.sidedSuccess(level().isClientSide());}return super.mobInteract(player,hand);}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return loot("ocelot");}
    }
    public static final class InfusedWolf extends Wolf implements Form {
        private final String form;
        public InfusedWolf(EntityType<? extends Wolf> t,Level l,String form){super(t,l);this.form=form;if(form.equals("seawolf"))setPathfindingMalus(PathType.WATER,0);}
        public String form(){return form;}
        @Override protected void applyTamingSideEffects(){super.applyTamingSideEffects();getAttribute(Attributes.MAX_HEALTH).setBaseValue(isTame()?20:8);}
        @Override public void aiStep(){super.aiStep();if(form.equals("seawolf")&&isInWater()){setAirSupply(300);moveRelative(.025F,new Vec3(0,0,1));}
            if(!level().isClientSide()&&form.equals("lunar_wolf")&&!level().isDay()&&tickCount%Math.max(1,(int)(7-level().getMoonBrightness()*4))==0)chargeNearby(this);
            if(!level().isClientSide()&&form.equals("nether_hound")){var target=getTarget()!=null?getTarget():getLastHurtByMob();if(target!=null&&target.isAlive()&&distanceToSqr(target)<=49&&hasLineOfSight(target)&&getLookAngle().dot(target.getEyePosition().subtract(getEyePosition()).normalize())>.95){var ember=thaumcraft.entity.projectile.Ember.create(level(),this,8);ember.damage=1;ember.firey=1;ember.setPos(ember.position().add(ember.getDeltaMovement()));level().addFreshEntity(ember);if(tickCount%10==0)playSound(thaumcraft.registry.ModSounds.FIRELOOP.get(),.33F,2);}}
        }
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return loot("wolf");}
    }
    public static final class InfusedPig extends Pig implements Form {
        private final String form;public int nuggetPercent;
        public InfusedPig(EntityType<? extends Pig> t,Level l,String form){super(t,l);this.form=form;goalSelector.addGoal(9,new PigFeedingGoal(this,form.equals("ore_pig")));}
        public String form(){return form;}
        @Override protected void customServerAiStep(){super.customServerAiStep();if(form.equals("taint_eater_pig"))for(var effect:List.of(thaumcraft.registry.ModEffects.TAINT_POISON,thaumcraft.registry.ModEffects.INFECTIOUS_VIS_EXHAUST,thaumcraft.registry.ModEffects.VIS_EXHAUST))removeEffect(effect);}
        public void eatStone(){nuggetPercent+=1+random.nextInt(15);if(nuggetPercent>=100){nuggetPercent-=100;spawnAtLocation(nugget(),.3F);}}
        private ItemStack nugget(){String[] metals={"gold","silver","copper","tin","lead","quicksilver","iron"};int[] weights={6,6,8,10,10,10,25};List<Integer> eligible=new ArrayList<>();int total=0;for(int i=0;i<metals.length;i++)if(i==0||i>=5||BuiltInRegistries.ITEM.getTagOrEmpty(TagKey.create(Registries.ITEM,ResourceLocation.parse("c:ingots/"+metals[i]))).iterator().hasNext()){eligible.add(i);total+=weights[i];}int roll=random.nextInt(total);for(int i:eligible){roll-=weights[i];if(roll<0)return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(i==0?"minecraft:gold_nugget":i==6?"minecraft:iron_nugget":i==5?"thaumcraft:quicksilver_drop":"thaumcraft:"+metals[i]+"_nugget")));}return new ItemStack(Items.IRON_NUGGET);}
        @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("THNuggetPercent",nuggetPercent);}
        @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);nuggetPercent=tag.getInt("THNuggetPercent");}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return form.equals("ore_pig")?ResourceKey.create(Registries.LOOT_TABLE,ThaumicHorizons.id("entities/ore_pig")):loot("pig");}
    }
    static final class PigFeedingGoal extends Goal {
        final InfusedPig pig;final boolean stone;ItemEntity item;BlockPos taint;int cooldown,count;
        PigFeedingGoal(InfusedPig pig,boolean stone){this.pig=pig;this.stone=stone;setFlags(EnumSet.of(Flag.MOVE,Flag.LOOK));}
        boolean edible(BlockPos p){var state=pig.level().getBlockState(p);return state.getBlock() instanceof thaumcraft.taint.TaintBlock||state.getBlock() instanceof thaumcraft.taint.TaintFibresBlock||state.is(Blocks.GRASS_BLOCK)&&pig.level().getBiome(p).is(ResourceLocation.parse("thaumcraft:tainted_land"));}
        @Override public boolean canUse(){if(cooldown>0){cooldown--;return false;}if(stone){item=pig.level().getEntitiesOfClass(ItemEntity.class,pig.getBoundingBox().inflate(16),e->e.isAlive()&&!e.hasPickUpDelay()&&e.getItem().is(Items.COBBLESTONE)&&e.distanceToSqr(pig)<128).stream().min(Comparator.comparingDouble(e->e.distanceToSqr(pig))).orElse(null);return item!=null;}
            BlockPos base=pig.blockPosition();for(BlockPos p:BlockPos.betweenClosed(base.offset(-2,-2,-2),base.offset(2,2,2)))if(pig.level().hasChunkAt(p)&&edible(p)){taint=p.immutable();return true;}for(int i=0;i<30;i++){BlockPos p=base.offset(pig.getRandom().nextInt(17)-8,pig.getRandom().nextInt(5)-2,pig.getRandom().nextInt(17)-8);if(pig.level().hasChunkAt(p)&&pig.level().isEmptyBlock(p.above())&&edible(p)){taint=p;return true;}}return false;
        }
        @Override public void start(){count=500;if(stone)pig.getNavigation().moveTo(item,1);else pig.getNavigation().moveTo(taint.getX()+.5,taint.getY()+.5,taint.getZ()+.5,1);}
        @Override public boolean canContinueToUse(){return count-->0&&cooldown<=0&&(stone?item!=null&&item.isAlive():taint!=null&&edible(taint));}
        @Override public boolean requiresUpdateEveryTick(){return true;}
        @Override public void tick(){if(stone){pig.getLookControl().setLookAt(item,30,30);if(pig.distanceToSqr(item)<=2){pig.eatStone();item.getItem().shrink(1);if(item.getItem().isEmpty())item.discard();pig.playSound(SoundEvents.PLAYER_BURP,.2F,2+(pig.getRandom().nextFloat()-pig.getRandom().nextFloat())*1.4F);}}
            else if(pig.distanceToSqr(taint.getCenter())<=4&&pig.level() instanceof ServerLevel level){boolean grass=level.getBlockState(taint).is(Blocks.GRASS_BLOCK);level.levelEvent(2001,taint,Block.getId(level.getBlockState(taint)));level.setBlockAndUpdate(taint,grass?Blocks.DIRT.defaultBlockState():Blocks.AIR.defaultBlockState());BiomeEdit.setColumn(level,taint,level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));pig.heal(1);cooldown=grass?10:20;pig.playSound(SoundEvents.PLAYER_BURP,.2F,2+(pig.getRandom().nextFloat()-pig.getRandom().nextFloat())*1.4F);}}
        @Override public void stop(){pig.getNavigation().stop();item=null;taint=null;}
    }
    public static final class Sheeder extends Spider implements Form,net.neoforged.neoforge.common.IShearable {
        private static final EntityDataAccessor<Boolean> SHEARED=SynchedEntityData.defineId(Sheeder.class,EntityDataSerializers.BOOLEAN);
        public Sheeder(EntityType<? extends Spider> t,Level l){super(t,l);goalSelector.removeAllGoals(g->true);targetSelector.removeAllGoals(g->true);goalSelector.addGoal(0,new FloatGoal(this));goalSelector.addGoal(1,new PanicGoal(this,.5));goalSelector.addGoal(3,new TemptGoal(this,.44,Ingredient.of(Items.WHEAT),false));goalSelector.addGoal(5,new EatBlockGoal(this));goalSelector.addGoal(6,new WaterAvoidingRandomStrollGoal(this,.4));goalSelector.addGoal(7,new LookAtPlayerGoal(this,Player.class,6));goalSelector.addGoal(8,new RandomLookAroundGoal(this));}
        public String form(){return "sheeder";}
        @Override protected void defineSynchedData(SynchedEntityData.Builder b){super.defineSynchedData(b);b.define(SHEARED,false);}
        public boolean isSheared(){return entityData.get(SHEARED);}public void setSheared(boolean value){entityData.set(SHEARED,value);}
        @Override public boolean isShearable(Player p,ItemStack tool,Level level,BlockPos pos){return isAlive()&&!isSheared()&&!isBaby();}
        @Override public List<ItemStack> onSheared(Player p,ItemStack tool,Level level,BlockPos pos){playSound(SoundEvents.SHEEP_SHEAR,1,1);if(level.isClientSide())return List.of();setSheared(true);List<ItemStack> drops=new ArrayList<>();for(int i=2+random.nextInt(4);i>0;i--)drops.add(new ItemStack(Items.STRING));return drops;}
        @Override public void ate(){super.ate();setSheared(false);}
        @Override protected boolean shouldDespawnInPeaceful(){return false;}
        @Override protected SoundEvent getAmbientSound(){return SoundEvents.SHEEP_AMBIENT;}
        @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putBoolean("Sheared",isSheared());}
        @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);setSheared(tag.getBoolean("Sheared"));}
        @Override public ResourceKey<LootTable> getDefaultLootTable(){return ResourceKey.create(Registries.LOOT_TABLE,ThaumicHorizons.id("entities/sheeder"));}
    }
}
