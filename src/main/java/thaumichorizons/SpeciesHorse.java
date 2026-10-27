package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.*;
import net.minecraft.sounds.*;
import net.minecraft.tags.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class SpeciesHorse extends Horse implements Species.Form {
    public static final DeferredHolder<EntityType<?>,EntityType<SpeciesHorse>> ENDERSTEED=Species.register("endersteed",(t,l)->new SpeciesHorse(t,l,"endersteed"),1.4F,1.6F,false),NIGHTMARE=Species.register("nightmare",(t,l)->new SpeciesHorse(t,l,"nightmare"),1.4F,1.6F,true),ZOMBIE=Species.register("zombie_horse",(t,l)->new SpeciesHorse(t,l,"zombie_horse"),1.4F,1.6F,false),SKELETON=Species.register("skeleton_horse",(t,l)->new SpeciesHorse(t,l,"skeleton_horse"),1.4F,1.6F,false);
    private final String form;
    public SpeciesHorse(EntityType<? extends Horse> type,Level level,String form){super(type,level);this.form=form;}
    public String form(){return form;}
    static void init(){}
    static void attributes(EntityAttributeCreationEvent event){for(var type:List.of(ENDERSTEED,NIGHTMARE,ZOMBIE,SKELETON))event.put(type.get(),Horse.createBaseHorseAttributes().build());}
    private boolean ender(){return form.equals("endersteed")||form.equals("nightmare");}
    @Override public void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);if(ender()){
        bonus(Attributes.MOVEMENT_SPEED,"ender_speed",.1,AttributeModifier.Operation.ADD_MULTIPLIED_BASE);bonus(Attributes.JUMP_STRENGTH,"ender_jump",.25,AttributeModifier.Operation.ADD_MULTIPLIED_BASE);bonus(Attributes.MAX_HEALTH,"ender_health",4,AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }}
    private void bonus(Holder<Attribute> type,String name,double n,AttributeModifier.Operation operation){var a=getAttribute(type);ResourceLocation id=ThaumicHorizons.id(name);if(a!=null&&!a.hasModifier(id))a.addPermanentModifier(new AttributeModifier(id,n,operation));}
    @Override public void onPlayerJump(int power){}
    @Override public void handleStartJump(int power){
        if(!ender()||level().isClientSide()||!(getControllingPassenger() instanceof ServerPlayer))return;
        if(form.equals("nightmare")&&power>=90&&(level().dimension()==Level.OVERWORLD||level().dimension()==Level.NETHER))netherport();
        else{double blocks=power/7D;teleportStride(getX()-blocks*Math.sin(Math.toRadians(getYRot())),getY(),getZ()+blocks*Math.cos(Math.toRadians(getYRot())));}
    }
    public boolean teleportStride(double x,double y,double z){
        if(!(level() instanceof ServerLevel level)||!level.hasChunkAt(BlockPos.containing(x,y,z)))return false;
        var event=new net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity(this,x,y,z);if(net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event).isCanceled())return false;
        Vec3 from=position();double targetX=event.getTargetX(),targetZ=event.getTargetZ();int targetY=(int)Math.floor(event.getTargetY())-3;
        for(int yy=targetY;yy<event.getTargetY()+4;yy++){
            BlockPos below=BlockPos.containing(targetX,yy-1,targetZ);if(!level.getBlockState(below).blocksMotion())continue;
            setPos(targetX,yy,targetZ);if(level.noCollision(this)&&!level.containsAnyLiquid(getBoundingBox())){
                for(int i=0;i<128;i++){double f=i/127D;Vec3 p=from.lerp(position(),f);level.sendParticles(ParticleTypes.PORTAL,p.x,p.y+random.nextDouble()*getBbHeight(),p.z,1,getBbWidth()*.3,.1,getBbWidth()*.3,.05);}
                level.playSound(null,BlockPos.containing(from),SoundEvents.ENDERMAN_TELEPORT,SoundSource.NEUTRAL,1,1);playSound(SoundEvents.ENDERMAN_TELEPORT,1,1);teleportTo(targetX,yy,targetZ);return true;
            }
        }setPos(from);return false;
    }
    public boolean netherport(){
        if(!(level() instanceof ServerLevel from)||!(getControllingPassenger() instanceof ServerPlayer))return false;ServerLevel destination=from.getServer().getLevel(from.dimension()==Level.NETHER?Level.OVERWORLD:Level.NETHER);if(destination==null)return false;
        double factor=from.dimension()==Level.NETHER?8:1/8D;BlockPos initial=destination.getWorldBorder().clampToBounds(getX()*factor,getY(),getZ()*factor);BlockPos landing=findLanding(destination,initial);if(landing==null)return false;
        from.explode(this,getX(),getY()+getBbHeight()/2,getZ(),2,true,Level.ExplosionInteraction.MOB);
        return changeDimension(new DimensionTransition(destination,landing.getBottomCenter(),Vec3.ZERO,getYRot(),getXRot(),DimensionTransition.DO_NOTHING))!=null;
    }
    /** Search the original sixteen-block radius for a solid floor and a 3×4×4 clear landing. */
    public static BlockPos findLanding(ServerLevel level,BlockPos initial){
        double best=Double.MAX_VALUE;BlockPos result=null;int max=level.dimension()==Level.NETHER?Math.min(127,level.getMaxBuildHeight()-1):level.getMaxBuildHeight()-1;
        for(int x=-16;x<=16;x++)for(int z=-16;z<=16;z++){
            int px=initial.getX()+x,pz=initial.getZ()+z;level.getChunkAt(new BlockPos(px,initial.getY(),pz));
            for(int y=max;y>level.getMinBuildHeight();y--){BlockPos p=new BlockPos(px,y,pz);if(!level.isEmptyBlock(p)||!level.getBlockState(p.below()).blocksMotion())continue;
                boolean fits=true;for(BlockPos q:BlockPos.betweenClosed(p.offset(-1,-1,-1),p.offset(1,3,2))){if(q.getY()==y-1?!level.getBlockState(q).blocksMotion():!level.isEmptyBlock(q)){fits=false;break;}}
                if(fits){double d=p.distSqr(initial);if(d<best){best=d;result=p;}}}
        }return result;
    }
    @Override public void tick(){
        if(form.equals("nightmare")){
            if(isInLava()){setDeltaMovement(getDeltaMovement().add(0,.1,0));if(getDeltaMovement().y>.25)setDeltaMovement(getDeltaMovement().x,.25,getDeltaMovement().z);setOnGround(true);fallDistance=0;}
            else if(level().getFluidState(blockPosition().below()).is(FluidTags.LAVA)){setOnGround(true);fallDistance=0;if(getDeltaMovement().y<0)setDeltaMovement(getDeltaMovement().x,0,getDeltaMovement().z);}
        }
        super.tick();if(form.equals("nightmare")&&!level().isClientSide()){
            BlockPos below=blockPosition().below();if(level().getBlockState(below).is(Blocks.GRASS_BLOCK))level().setBlockAndUpdate(below,Blocks.DIRT.defaultBlockState());
            for(BlockPos p:List.of(blockPosition(),blockPosition().above())){var state=level().getBlockState(p);if(state.is(BlockTags.LEAVES)||state.is(BlockTags.FLOWERS)||state.is(BlockTags.SAPLINGS)||state.is(Blocks.COBWEB)||state.is(Blocks.VINE)||state.is(Blocks.SHORT_GRASS)||state.is(Blocks.TALL_GRASS)){level().levelEvent(2001,p,Block.getId(state));level().setBlockAndUpdate(p,Blocks.AIR.defaultBlockState());}}
        }
    }
    @Override public void makeStuckInBlock(BlockState state,Vec3 motion){if(form.equals("nightmare")&&state.is(Blocks.COBWEB)){fallDistance=0;return;}super.makeStuckInBlock(state,motion);}
    @Override public boolean isFood(ItemStack stack){return ender()&&super.isFood(stack);}
    @Override public boolean canMate(net.minecraft.world.entity.animal.Animal other){return ender()&&super.canMate(other);}
    @Override protected SoundEvent getAmbientSound(){return switch(form){case "zombie_horse"->SoundEvents.ZOMBIE_HORSE_AMBIENT;case "skeleton_horse"->SoundEvents.SKELETON_HORSE_AMBIENT;default->super.getAmbientSound();};}
    @Override protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source){return switch(form){case "zombie_horse"->SoundEvents.ZOMBIE_HORSE_HURT;case "skeleton_horse"->SoundEvents.SKELETON_HORSE_HURT;default->super.getHurtSound(source);};}
    @Override protected SoundEvent getDeathSound(){return switch(form){case "zombie_horse"->SoundEvents.ZOMBIE_HORSE_DEATH;case "skeleton_horse"->SoundEvents.SKELETON_HORSE_DEATH;default->super.getDeathSound();};}
    @Override public net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> getDefaultLootTable(){return Species.loot(form.equals("zombie_horse")?"zombie_horse":form.equals("skeleton_horse")?"skeleton_horse":"horse");}
}
