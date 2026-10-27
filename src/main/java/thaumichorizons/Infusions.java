package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import thaumcraft.aspect.AspectList;
import thaumcraft.platform.*;
import thaumcraft.research.*;

/** Saved creature/player infusion properties, including the original beacon reincarnation path. */
public final class Infusions {
    public static final DataKey<CompoundTag> DATA=DataKey.builder("th_infusions",CompoundTag::new).codec(CompoundTag.CODEC).syncToAll(ByteBufCodecs.COMPOUND_TAG).build();
    public static final DataKey<CompoundTag> BEACON=DataKey.builder("th_soul_beacon",CompoundTag::new).codec(CompoundTag.CODEC).copyOnDeath().syncToSelf(ByteBufCodecs.COMPOUND_TAG).build();
    private static final ResourceLocation SPEED=ThaumicHorizons.id("quick_limbs"),CLAWS=ThaumicHorizons.id("thaum_claws"),FANGS=ThaumicHorizons.id("eldritch_fangs");
    private static final Set<Mob> LOYAL_GOALS=Collections.newSetFromMap(new WeakHashMap<>());
    public static void init(){ModNetwork.toServer(InfusionAction.TYPE,InfusionAction.CODEC,(action,p)->action.run(p));}
    public static boolean has(LivingEntity e,int id){return e!=null&&DATA.has(e)&&Arrays.stream(DATA.get(e).getIntArray(e instanceof Player?"player":"creature")).anyMatch(n->n==id);}
    public static boolean add(LivingEntity e,int id,UUID owner,AspectList cost){
        if(id<1||id>10||has(e,id))return false;var tag=DATA.get(e);String key=e instanceof Player?"player":"creature";int[] old=tag.getIntArray(key);if(old.length>=12)return false;
        int[] next=Arrays.copyOf(old,old.length+1);next[old.length]=id;tag.putIntArray(key,next);
        if(owner!=null&&id==7&&!(e instanceof Player))tag.putUUID("owner",owner);
        var paid=BloodInfuser.loadAspects(tag.getCompound("cost"));paid.add(cost);tag.put("cost",BloodInfuser.saveAspects(paid));
        if(id==8&&!(e instanceof Player))tag.putInt("runic",6);
        DATA.set(e,tag);apply(e);return true;
    }
    private static void modifier(LivingEntity e,Holder<Attribute> type,ResourceLocation id,double amount){
        var attribute=e.getAttribute(type);if(attribute!=null&&!attribute.hasModifier(id))attribute.addPermanentModifier(new AttributeModifier(id,amount,AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }
    public static void apply(LivingEntity e){
        if(has(e,1)){if(!(e instanceof Player))modifier(e,Attributes.MOVEMENT_SPEED,SPEED,.3);effect(e,MobEffects.JUMP);if(e instanceof Player)effect(e,MobEffects.MOVEMENT_SPEED);}
        if(!(e instanceof Player)){if(has(e,2))modifier(e,Attributes.ATTACK_DAMAGE,CLAWS,2);if(has(e,9))modifier(e,Attributes.ATTACK_DAMAGE,FANGS,1);}
        if(has(e,3))effect(e,MobEffects.REGENERATION);if(has(e,4))effect(e,MobEffects.DAMAGE_RESISTANCE);
        if(e instanceof Player&&has(e,10)&&!DATA.get(e).getBoolean("invisibilityDisabled"))effect(e,MobEffects.INVISIBILITY);
        if(e instanceof Mob mob&&has(e,7)&&!e.level().isClientSide()&&LOYAL_GOALS.add(mob)){
            mob.setPersistenceRequired();mob.goalSelector.removeAllGoals(g->true);mob.targetSelector.removeAllGoals(g->true);
            mob.goalSelector.addGoal(0,new net.minecraft.world.entity.ai.goal.FloatGoal(mob));mob.goalSelector.addGoal(1,new LoyaltyGoal(mob));
            if(mob instanceof net.minecraft.world.entity.animal.Animal animal)mob.goalSelector.addGoal(2,new net.minecraft.world.entity.ai.goal.BreedGoal(animal,1));
            if(mob instanceof PathfinderMob pathfinder)mob.goalSelector.addGoal(3,new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(pathfinder,1));
            mob.goalSelector.addGoal(4,new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(mob,Player.class,8));mob.goalSelector.addGoal(5,new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(mob));
        }
    }
    private static void effect(LivingEntity e,Holder<MobEffect> type){if(!e.hasEffect(type)||e.getEffect(type).getDuration()<100)e.addEffect(new MobEffectInstance(type,-1,0,true,false));}
    private static boolean permanent(LivingEntity e,Holder<MobEffect> type){return (type==MobEffects.JUMP&&has(e,1))||(type==MobEffects.MOVEMENT_SPEED&&e instanceof Player&&has(e,1))||(type==MobEffects.REGENERATION&&has(e,3))||(type==MobEffects.DAMAGE_RESISTANCE&&has(e,4))||(type==MobEffects.INVISIBILITY&&e instanceof Player&&has(e,10)&&!DATA.get(e).getBoolean("invisibilityDisabled"));}
    @SubscribeEvent public static void remove(MobEffectEvent.Remove event){if(permanent(event.getEntity(),event.getEffect()))event.setCanceled(true);}
    @SubscribeEvent public static void tick(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof LivingEntity e)||!DATA.has(e)||!e.isAlive())return;
        if(e.tickCount%30==0&&!e.level().isClientSide())apply(e);
        if(e instanceof Player p){
            if(has(e,7)&&e.isInWater())e.setAirSupply(e.getMaxAirSupply());
            if(has(e,9)&&!DATA.get(e).getBoolean("climbDisabled")&&!p.getAbilities().flying){
                Vec3 v=e.getDeltaMovement();if(e.horizontalCollision){e.setDeltaMovement(v.x,e.isShiftKeyDown()?0:.2,v.z);e.fallDistance=0;}
                else if(e.isShiftKeyDown()&&v.y<-.15){e.setDeltaMovement(v.x,-.15,v.z);e.fallDistance=0;}
            }
            if(!(p instanceof ServerPlayer sp))return;
            if(has(e,6)&&e.tickCount%200==0&&e.level().isDay()&&e.level().canSeeSky(e.blockPosition()))p.getFoodData().eat(1,0);
            if(has(e,5)&&e.tickCount%10==0)for(var type:List.of(MobEffects.POISON,MobEffects.WITHER,thaumcraft.registry.ModEffects.TAINT_POISON,thaumcraft.registry.ModEffects.VIS_EXHAUST)){
                var effect=e.getEffect(type);if(effect!=null){e.removeEffect(type);if(effect.getDuration()>10)e.addEffect(new MobEffectInstance(type,effect.getDuration()-10,0,effect.isAmbient(),effect.isVisible()));}
            }
            if(has(e,8)&&e.tickCount%30==0)absorbWarp(sp);
        }else{
            if(has(e,6)&&e.tickCount%30==0){LivingEntity target=e.getLastHurtByMob();if(target==null&&e instanceof Mob mob)target=mob.getTarget();if(target!=null&&target.isAlive()){target.hurt(e.damageSources().indirectMagic(e,e),1);sparks(e);e.playSound(thaumcraft.registry.ModSounds.ZAP.get());}}
            if(has(e,8)&&e.tickCount%100==0){DATA.get(e).putInt("runic",Math.min(6,DATA.get(e).getInt("runic")+1));DATA.sync(e);}
        }
    }
    public static void absorbWarp(ServerPlayer player){
        var tag=DATA.get(player);int held=tag.getInt("tumorPermanent")+tag.getInt("tumorSticky")+tag.getInt("tumorTemporary");int room=Math.max(0,HorizonsConfig.WARPED_TUMOR.get()-held);
        for(var pair:List.of(Map.entry(WarpType.PERMANENT,"tumorPermanent"),Map.entry(WarpType.STICKY,"tumorSticky"),Map.entry(WarpType.TEMPORARY,"tumorTemporary"))){int taken=Math.min(room,Warp.get(player,pair.getKey()));if(taken>0){Warp.set(player,Warp.get(player,pair.getKey())-taken,pair.getKey());tag.putInt(pair.getValue(),tag.getInt(pair.getValue())+taken);room-=taken;}}
        DATA.sync(player);
    }
    public static void returnWarp(ServerPlayer player){if(!DATA.has(player))return;var tag=DATA.get(player);for(var pair:List.of(Map.entry(WarpType.PERMANENT,"tumorPermanent"),Map.entry(WarpType.STICKY,"tumorSticky"),Map.entry(WarpType.TEMPORARY,"tumorTemporary"))){int value=tag.getInt(pair.getValue());if(value>0){Warp.set(player,Warp.get(player,pair.getKey())+value,pair.getKey());tag.remove(pair.getValue());}}DATA.sync(player);}
    private static void sparks(LivingEntity e){if(e.level() instanceof ServerLevel l)l.sendParticles(ParticleTypes.ELECTRIC_SPARK,e.getX(),e.getY()+e.getBbHeight()/2,e.getZ(),20,.5,.5,.5,.05);}
    @SubscribeEvent public static void damage(LivingIncomingDamageEvent event){
        LivingEntity e=event.getEntity();if(e.level().isClientSide())return;
        if(has(e,5)&&!(e instanceof Player)&&event.getSource().is(DamageTypeTags.IS_PROJECTILE)){
            event.setCanceled(true);for(int i=0;i<16;i++)if(e.randomTeleport(e.getX()+(e.getRandom().nextDouble()-.5)*64,e.getY()+e.getRandom().nextInt(64)-32,e.getZ()+(e.getRandom().nextDouble()-.5)*64,true))break;return;
        }
        if(e instanceof Player&&has(e,5)&&event.getSource().getMsgId().contains("taint")){event.setCanceled(true);return;}
        if(!(e instanceof Player)&&has(e,8)&&!event.getSource().is(DamageTypes.DROWN)&&!event.getSource().is(DamageTypes.WITHER)&&!event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)&&!event.getSource().is(DamageTypes.STARVE)){
            var tag=DATA.get(e);int shield=tag.getInt("runic");float absorbed=Math.min(shield,event.getAmount());if(absorbed>0){event.setAmount(event.getAmount()-absorbed);tag.putInt("runic",Math.max(0,shield-(int)Math.ceil(absorbed)));DATA.sync(e);sparks(e);}
        }
        // The legacy listener accidentally checked the victim's fangs; apply them to the attacker.
        if(event.getSource().getEntity() instanceof LivingEntity attacker&&has(attacker,9)&&!(attacker instanceof Player))e.addEffect(new MobEffectInstance(MobEffects.POISON,40));
    }
    public static boolean bindBeacon(Player player,BlockPos pos){
        if(!(player.level().getBlockEntity(pos.below()) instanceof VatEntity vat)||!vat.isController())return false;
        if(!player.level().isClientSide()){var tag=BEACON.get(player);tag.putLong("beacon",pos.asLong());tag.putString("beaconDimension",player.level().dimension().location().toString());BEACON.set(player,tag);player.displayClientMessage(net.minecraft.network.chat.Component.translatable("thaumichorizons.beacon.bound"),true);}return true;
    }
    public static VatEntity reincarnationVat(ServerPlayer player){
        var tag=BEACON.has(player)?BEACON.get(player):DATA.has(player)?DATA.get(player):new CompoundTag();if(!tag.contains("beacon"))return null;ResourceLocation dimension=ResourceLocation.tryParse(tag.getString("beaconDimension"));if(dimension==null)return null;
        ServerLevel level=player.server.getLevel(ResourceKey.create(Registries.DIMENSION,dimension));if(level==null)return null;BlockPos pos=BlockPos.of(tag.getLong("beacon"));
        // Loading the bound chunk is intentional; no unrelated chunks are loaded by vat automation.
        level.getChunkAt(pos);return level.getBlockState(pos).is(Vats.BEACON.get())&&level.getBlockEntity(pos.below()) instanceof VatEntity vat&&vat.mode==4&&vat.shellHealth>0?vat:null;
    }
    public static boolean reincarnate(ServerPlayer player){
        VatEntity vat=reincarnationVat(player);if(vat==null)return false;int[] infusions=vat.shellInfusions.clone();returnWarp(player);
        // Reincarnation leaves the inventory at the death location, as in 1.7.10, even with keepInventory.
        player.getInventory().dropAll();top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).ifPresent(inventory->inventory.getCurios().values().forEach(handler->{
            for(var stacks:List.of(handler.getStacks(),handler.getCosmeticStacks()))for(int i=0;i<stacks.getSlots();i++){ItemStack stack=stacks.getStackInSlot(i);if(!stack.isEmpty()){player.drop(stack.copy(),true);stacks.setStackInSlot(i,ItemStack.EMPTY);}}
        }));
        DATA.set(player,new CompoundTag());
        player.removeAllEffects();player.clearFire();player.setHealth(player.getMaxHealth());player.getFoodData().setFoodLevel(20);player.fallDistance=0;player.setAirSupply(player.getMaxAirSupply());player.deathTime=0;
        BlockPos pos=vat.getBlockPos();player.teleportTo((ServerLevel)vat.getLevel(),pos.getX()+.5,pos.getY()-2,pos.getZ()+.5,player.getYRot(),player.getXRot());
        for(int id:infusions)if(id>0)add(player,id,null,new AspectList());vat.shellInfusions=new int[12];vat.shellHealth=20;vat.mode=0;vat.playerSubject=player.getUUID();vat.sync();apply(player);return true;
    }
    @SubscribeEvent public static void death(LivingDeathEvent event){if(event.getEntity() instanceof ServerPlayer player){MirrorAmulet.rescue(player);if(reincarnate(player))event.setCanceled(true);else returnWarp(player);}LOYAL_GOALS.remove(event.getEntity());}
    @SubscribeEvent public static void interact(PlayerInteractEvent.EntityInteract event){
        if(!(event.getTarget() instanceof LivingEntity e)||e instanceof Player||!DATA.has(e))return;Player p=event.getEntity();ItemStack held=event.getItemStack();
        if(has(e,10)&&held.getItem() instanceof thaumcraft.wand.WandItem){if(!p.level().isClientSide()){MedicalItem.give(p,Specimens.write(e));e.discard();}event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);event.setCanceled(true);return;}
        if(has(e,7)&&held.isEmpty()&&DATA.get(e).hasUUID("owner")&&DATA.get(e).getUUID("owner").equals(p.getUUID())){if(!p.level().isClientSide()){var tag=DATA.get(e);tag.putBoolean("sitting",!tag.getBoolean("sitting"));DATA.sync(e);if(e instanceof Mob m)m.getNavigation().stop();}event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);event.setCanceled(true);}
    }
    private static final class LoyaltyGoal extends Goal {
        final Mob mob;LivingEntity owner,target;
        LoyaltyGoal(Mob mob){this.mob=mob;setFlags(EnumSet.of(Flag.MOVE,Flag.JUMP,Flag.TARGET));}
        private void targets(){var tag=DATA.get(mob);owner=tag.hasUUID("owner")?mob.level().getPlayerByUUID(tag.getUUID("owner")):null;target=owner==null?null:owner.getLastHurtByMob();if(target==null||!target.isAlive())target=owner==null?null:owner.getLastHurtMob();if(target==null||!target.isAlive())target=mob.getLastHurtByMob();if(target==owner||target!=null&&DATA.has(target)&&DATA.get(target).hasUUID("owner")&&tag.hasUUID("owner")&&DATA.get(target).getUUID("owner").equals(tag.getUUID("owner")))target=null;}
        public boolean canUse(){targets();return DATA.get(mob).getBoolean("sitting")||target!=null&&target.isAlive()||owner!=null&&mob.distanceToSqr(owner)>100;}
        public boolean canContinueToUse(){targets();return DATA.get(mob).getBoolean("sitting")||target!=null&&target.isAlive()||owner!=null&&mob.distanceToSqr(owner)>4;}
        public boolean requiresUpdateEveryTick(){return true;}
        public void tick(){var tag=DATA.get(mob);if(tag.getBoolean("sitting")){mob.getNavigation().stop();mob.setTarget(null);return;}if(owner==null&&target==null)return;
            if(target!=null&&target.isAlive()&&mob.canAttack(target)){mob.setTarget(target);mob.getNavigation().moveTo(target,1);if(mob.tickCount%20==0&&mob.distanceToSqr(target)<Math.pow(mob.getBbWidth()*2,2)+target.getBbWidth()){var attack=mob.getAttribute(Attributes.ATTACK_DAMAGE);target.hurt(mob.damageSources().mobAttack(mob),3+(attack==null?0:(float)attack.getValue()));}}
            else if(owner!=null&&mob.distanceToSqr(owner)>4){mob.setTarget(null);mob.getNavigation().moveTo(owner,1);if(mob.distanceToSqr(owner)>144)for(int i=0;i<10;i++)if(mob.randomTeleport(owner.getX()+mob.getRandom().nextInt(7)-3,owner.getY(),owner.getZ()+mob.getRandom().nextInt(7)-3,false))break;}
        }
    }
}
