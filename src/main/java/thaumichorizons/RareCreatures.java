package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

public final class RareCreatures {
    public static final Map<String,DeferredHolder<EntityType<?>,EntityType<ElementalSlime>>> SLIMES=new LinkedHashMap<>();
    public static final DeferredHolder<EntityType<?>,EntityType<LostSoul>> SOUL=Content.ENTITIES.register("lost_soul",()->EntityType.Builder.of(LostSoul::new,MobCategory.MONSTER).sized(.9F,.9F).fireImmune().clientTrackingRange(8).updateInterval(3).build("thaumichorizons:lost_soul"));
    static void init(){
        for(String name:List.of("meat_slime","mercurial_slime","volt_slime","med_slime")){
            var type=Content.ENTITIES.register(name,()->EntityType.Builder.<ElementalSlime>of((t,l)->new ElementalSlime(t,l,name),MobCategory.MONSTER).sized(.52F,.52F).clientTrackingRange(8).build(ThaumicHorizons.id(name).toString()));SLIMES.put(name,type);egg(name,type);
        }egg("lost_soul",SOUL);
    }
    static void egg(String name,java.util.function.Supplier<? extends EntityType<? extends Mob>> type){var colors=EggPalette.colors(name);Content.ALL.put(name+"_spawn_egg",Content.ITEMS.registerItem(name+"_spawn_egg",p->new net.neoforged.neoforge.common.DeferredSpawnEggItem(type,colors[0],colors[1],p),new Item.Properties()));}
    static void attributes(EntityAttributeCreationEvent event){for(var type:SLIMES.values())event.put(type.get(),Slime.createMobAttributes().add(Attributes.ATTACK_DAMAGE,1).build());event.put(SOUL.get(),Mob.createMobAttributes().add(Attributes.MAX_HEALTH,1).add(Attributes.MOVEMENT_SPEED,.1).build());}
    public static final class ElementalSlime extends Slime {
        private static final EntityDataAccessor<Integer> ARC_TARGET=SynchedEntityData.defineId(ElementalSlime.class,EntityDataSerializers.INT);
        final String form;
        public ElementalSlime(EntityType<? extends Slime> type,Level level,String form){super(type,level);this.form=form;}
        @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(ARC_TARGET,-1);}
        @Override protected ParticleOptions getParticleType(){return switch(form){case "meat_slime"->new DustParticleOptions(new org.joml.Vector3f(1,0,0),1);case "volt_slime"->ParticleTypes.ENCHANTED_HIT;default->ParticleTypes.ITEM_SNOWBALL;};}
        @Override protected ResourceKey<LootTable> getDefaultLootTable(){return ResourceKey.create(Registries.LOOT_TABLE,ThaumicHorizons.id("entities/empty"));}
        @Override protected void dropCustomDeathLoot(ServerLevel level,DamageSource source,boolean hit){
            super.dropCustomDeathLoot(level,source,hit);if(getSize()!=1)return;
            if(form.equals("meat_slime"))spawnAtLocation(new ItemStack(new Item[]{Items.BEEF,Items.PORKCHOP,Items.CHICKEN,Items.COD,Items.ROTTEN_FLESH}[random.nextInt(5)]));
            else if(form.equals("mercurial_slime"))spawnAtLocation(thaumcraft.registry.ModItems.QUICKSILVER.get());
        }
        @Override public void playerTouch(Player player){
            if(isRemoved())return;
            if(!form.equals("med_slime")){super.playerTouch(player);return;}
            if(!level().isClientSide()&&player.getHealth()<player.getMaxHealth()&&hasLineOfSight(player)&&distanceToSqr(player)<.36*getSize()*getSize()){
                player.heal(getSize()+1);level().playSound(null,blockPosition(),net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE.value(),net.minecraft.sounds.SoundSource.NEUTRAL,1,1);if(level() instanceof ServerLevel server)server.sendParticles(ParticleTypes.EXPLOSION,getX(),getY()+getBbHeight()*.5,getZ(),1,0,0,0,0);discard();
            }
        }
        @Override public void aiStep(){super.aiStep();if(!form.equals("volt_slime")||tickCount%2!=0)return;
            if(!level().isClientSide()){
                var target=getLastHurtByMob();entityData.set(ARC_TARGET,target!=null&&target.isAlive()?target.getId():-1);if(target!=null&&target.isAlive()){target.hurt(damageSources().magic(),.5F);level().playSound(null,blockPosition(),thaumcraft.registry.ModSounds.ZAP.get(),net.minecraft.sounds.SoundSource.HOSTILE,1,1+(random.nextFloat()-random.nextFloat())*.2F);}
            }else{var target=level().getEntity(entityData.get(ARC_TARGET));if(target!=null&&target.isAlive())thaumcraft.fx.Fx.get().beamPulse(level(),this,target,0x33CCCC);}
        }
    }
    public static final class LostSoul extends FlyingMob implements Enemy {
        private Vec3 destination=Vec3.ZERO;private int courseCooldown;
        public LostSoul(EntityType<? extends LostSoul> type,Level level){super(type,level);xpReward=5;setPersistenceRequired();}
        @Override public boolean hurt(DamageSource source,float amount){return false;}
        @Override public boolean removeWhenFarAway(double distance){return false;}
        @Override public boolean canDrownInFluidType(net.neoforged.neoforge.fluids.FluidType type){return false;}
        @Override protected void customServerAiStep(){
            double distance=position().distanceToSqr(destination);if(distance<1||distance>3600)destination=position().add((random.nextFloat()*2-1)*16,(random.nextFloat()*2-1)*16,(random.nextFloat()*2-1)*16);
            if(courseCooldown--<=0){courseCooldown+=random.nextInt(5)+2;Vec3 delta=destination.subtract(position());double length=delta.length();if(length>0&&traversable(delta,length))setDeltaMovement(getDeltaMovement().add(delta.scale(.1/length)));else destination=position();}
            float yaw=(float)(-Math.atan2(getDeltaMovement().x,getDeltaMovement().z)*180/Math.PI);setYRot(yaw);yBodyRot=yaw;
        }
        private boolean traversable(Vec3 delta,double length){var step=delta.scale(1/length);var box=getBoundingBox();for(int i=1;i<length;i++){box=box.move(step);if(!level().noCollision(this,box))return false;}var pos=BlockPos.containing(destination);if(!level().getFluidState(pos).isEmpty())return false;for(int i=0;i<11;i++)if(!level().isEmptyBlock(pos.below(i)))return true;return false;}
        @Override public void aiStep(){super.aiStep();if(level().isClientSide()&&random.nextBoolean())thaumcraft.fx.Fx.get().sparkle(level(),(float)getX()+(random.nextFloat()-random.nextFloat())*.7F,(float)getY()+(random.nextFloat()-random.nextFloat())*.7F,(float)getZ()+(random.nextFloat()-random.nextFloat())*.7F,.1F,0x00FFFF,0);}
        @Override protected net.minecraft.sounds.SoundEvent getAmbientSound(){return thaumcraft.registry.ModSounds.WISPLIVE.get();}
        @Override protected net.minecraft.sounds.SoundEvent getHurtSound(DamageSource source){return net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH;}
        @Override protected net.minecraft.sounds.SoundEvent getDeathSound(){return thaumcraft.registry.ModSounds.WISPDEAD.get();}
        @Override protected float getSoundVolume(){return .25F;}
        @Override protected ResourceKey<LootTable> getDefaultLootTable(){return ResourceKey.create(Registries.LOOT_TABLE,ThaumicHorizons.id("entities/empty"));}
    }
}
