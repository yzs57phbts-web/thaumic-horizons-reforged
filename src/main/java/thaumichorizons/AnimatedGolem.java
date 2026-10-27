package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.entity.golem.*;

public final class AnimatedGolem extends GolemBase {
    private static final EntityDataAccessor<BlockState> MATERIAL=SynchedEntityData.defineId(AnimatedGolem.class,EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> BERSERK=SynchedEntityData.defineId(AnimatedGolem.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EXPLOSIVE=SynchedEntityData.defineId(AnimatedGolem.class,EntityDataSerializers.BOOLEAN);
    int lifetime=600,voidWarnings;
    public AnimatedGolem(EntityType<? extends AnimatedGolem> type,Level level){super(type,level);}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(MATERIAL,Blocks.SAND.defaultBlockState());builder.define(BERSERK,false);builder.define(EXPLOSIVE,false);}
    public BlockState material(){return entityData.get(MATERIAL);}
    public GolemMaterial materialType(){return GolemMaterial.of(material());}
    public boolean berserk(){return entityData.get(BERSERK);}
    public boolean explosive(){return entityData.get(EXPLOSIVE);}
    @Override public GolemStats getGolemStats(){return materialType().stats;}
    public void initialize(BlockState block,int life,boolean berserk,boolean explosive,UUID owner,GolemSnapshot state){
        entityData.set(MATERIAL,block);entityData.set(BERSERK,berserk);entityData.set(EXPLOSIVE,explosive);lifetime=life;setOwnerUUID(owner);setHome(blockPosition(),Direction.UP);configure(GolemType.WOOD,state);setHealth(getMaxHealth());bootup=0;
    }
    @Override public boolean isWorking(){return getCore()<0?isAlive()&&!inactive:super.isWorking();}
    @Override public boolean isWithinHome(BlockPos pos){return getCore()<0||super.isWithinHome(pos);}
    @Override public Component getName(){return hasCustomName()?getCustomName():material().isAir()?Component.translatable("entity.thaumichorizons.voidling"):Component.translatable("entity.thaumichorizons.material_golem",new ItemStack(material().getBlock()).getHoverName());}
    @Override public void rebuildGoals(){
        super.rebuildGoals();if(getCore()>=0)return;
        goalSelector.addGoal(1,new AvoidEntityGoal<>(this,Creeper.class,6,1.2,1.5,c->c instanceof Creeper creeper&&creeper.getSwellDir()>0));goalSelector.addGoal(3,new MeleeAttackGoal(this,1,true));goalSelector.addGoal(5,new OpenDoorGoal(this,true));goalSelector.addGoal(6,new FollowOwner());goalSelector.addGoal(7,new LookAtPlayerGoal(this,Player.class,6));goalSelector.addGoal(8,new RandomLookAroundGoal(this));
        targetSelector.addGoal(1,new HurtByTargetGoal(this));targetSelector.addGoal(2,new NearestAttackableTargetGoal<>(this,LivingEntity.class,0,true,false,this::validTarget));
    }
    private boolean validTarget(LivingEntity target){if(target==this||!target.isAlive())return false;if(berserk())return !(target instanceof Player player&&player.getUUID().equals(getOwnerUUID()))&&!target.getName().getString().equals(getName().getString());return target instanceof Enemy;}
    @Override public void aiStep(){
        super.aiStep();if(level().isClientSide()||!isAlive()||getCore()>=0)return;
        if(getOwnerUUID()!=null){var owner=level().getPlayerByUUID(getOwnerUUID());if(owner!=null)setHome(owner.blockPosition(),Direction.UP);}
        if(lifetime>0){if(--lifetime==0)expire();}
        else if(lifetime!=-420)expire();
        else if(tickCount%10==0&&random.nextInt(500)==0){
            var owner=getOwnerUUID()==null?null:level().getPlayerByUUID(getOwnerUUID());if(voidWarnings<3&&owner!=null)owner.displayClientMessage(Component.translatable("thaumichorizons.golemWarning"+(voidWarnings+1)).withStyle(net.minecraft.ChatFormatting.DARK_PURPLE,net.minecraft.ChatFormatting.ITALIC),false);
            if(voidWarnings++>=3){var guardian=thaumcraft.registry.ModEntities.ELDRITCH_GUARDIAN.get().create(level());guardian.moveTo(position());level().addFreshEntity(guardian);expire();}
        }
    }
    @Override public boolean hurt(DamageSource source,float amount){
        if(source.is(DamageTypes.CACTUS))return false;
        if(material().is(thaumcraft.registry.ModBlocks.ARCANE_STONE.get())&&source.is(DamageTypes.MAGIC))amount*=.5F;
        var attacker=source.getDirectEntity();if(material().is(Blocks.CACTUS)&&getUpgradeAmount(5)==0&&attacker!=null&&attacker!=this&&!source.is(DamageTypes.THORNS))attacker.hurt(damageSources().thorns(this),random.nextInt(2));
        return super.hurt(source,amount);
    }
    @Override public boolean doHurtTarget(Entity target){if(material().is(Blocks.COBWEB)&&target instanceof LivingEntity living)living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,20));return super.doHurtTarget(target);}
    void expire(){
        if(isRemoved())return;dropCarried();
        if(materialType()==GolemMaterial.TNT||explosive()){level().explode(this,getX(),getY(),getZ(),materialType()==GolemMaterial.TNT?4:3,true,Level.ExplosionInteraction.MOB);discard();return;}
        if(!material().isAir()){
            var origin=new BlockPos((int)Math.floor(getX()),Math.round((float)getY()),(int)Math.floor(getZ()));
            if(restore(origin)){discard();return;}
            for(int y:new int[]{0,-1,1})for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)if(restore(origin.offset(x,y,z))){discard();return;}
            Block.popResource(level(),origin,new ItemStack(material().getBlock()));
        }
        level().playSound(null,blockPosition(),thaumcraft.registry.ModSounds.CRAFTFAIL.get(),net.minecraft.sounds.SoundSource.NEUTRAL,1,1);discard();
    }
    private boolean restore(BlockPos pos){if(!level().isEmptyBlock(pos))return false;level().setBlockAndUpdate(pos,material());level().levelEvent(2001,pos,Block.getId(material()));return true;}
    @Override public ItemStack toItem(boolean dismantle){
        var stack=super.toItem(dismantle);var saved=(CompoundTag)stack.save(registryAccess());saved.putString("id",ThaumicHorizons.id("golem_placer").toString());stack=ItemStack.parseOptional(registryAccess(),saved);
        var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.put("thBlock",NbtUtils.writeBlockState(material()));tag.putInt("thLife",lifetime);tag.putBoolean("thBerserk",berserk());tag.putBoolean("thExplosive",explosive());tag.putInt("thVoidWarnings",voidWarnings);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;
    }
    @Override public ItemStack getPickResult(){return toItem(false);}
    @Override public void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.put("thBlock",NbtUtils.writeBlockState(material()));tag.putInt("thLife",lifetime);tag.putInt("thVoidWarnings",voidWarnings);tag.putBoolean("thBerserk",berserk());tag.putBoolean("thExplosive",explosive());}
    @Override public void readAdditionalSaveData(CompoundTag tag){
        entityData.set(MATERIAL,NbtUtils.readBlockState(level().holderLookup(Registries.BLOCK),tag.getCompound("thBlock")));entityData.set(BERSERK,tag.getBoolean("thBerserk"));entityData.set(EXPLOSIVE,tag.getBoolean("thExplosive"));lifetime=tag.getInt("thLife");voidWarnings=tag.getInt("thVoidWarnings");super.readAdditionalSaveData(tag);
    }
    private final class FollowOwner extends Goal {
        Player owner;
        FollowOwner(){setFlags(EnumSet.of(Flag.MOVE));}
        @Override public boolean canUse(){owner=getOwnerUUID()==null?null:level().getPlayerByUUID(getOwnerUUID());return getCore()<0&&owner!=null&&owner.isAlive()&&distanceToSqr(owner)>144;}
        @Override public boolean canContinueToUse(){return getCore()<0&&owner!=null&&owner.isAlive()&&distanceToSqr(owner)>(explosive()?64:4);}
        @Override public void tick(){getLookControl().setLookAt(owner,10,30);getNavigation().moveTo(owner,3);}
        @Override public void stop(){getNavigation().stop();owner=null;}
    }
}
