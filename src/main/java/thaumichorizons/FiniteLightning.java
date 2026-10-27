package thaumichorizons;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.entity.golem.GolemSnapshot;
public final class FiniteLightning extends LightningBolt {
    public static final DeferredHolder<EntityType<?>,EntityType<FiniteLightning>> TYPE=Content.ENTITIES.register("finite_lightning",()->EntityType.Builder.of(FiniteLightning::new,MobCategory.MISC).sized(.01F,.01F).clientTrackingRange(16).updateInterval(1).build("thaumichorizons:finite_lightning"));
    private static final EntityDataAccessor<Integer> LENGTH=SynchedEntityData.defineId(FiniteLightning.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANIMATE=SynchedEntityData.defineId(FiniteLightning.class,EntityDataSerializers.BOOLEAN);
    static java.util.function.Consumer<FiniteLightning> visual=bolt->{};
    public FiniteLightning(EntityType<? extends LightningBolt> type,Level level){super(type,level);}
    static void init(){}
    @Override protected void defineSynchedData(SynchedEntityData.Builder builder){super.defineSynchedData(builder);builder.define(LENGTH,1);builder.define(ANIMATE,false);}
    public int length(){return entityData.get(LENGTH);}
    public boolean animates(){return entityData.get(ANIMATE);}
    public void configure(int length,boolean animate){entityData.set(LENGTH,Math.clamp(length,1,256));entityData.set(ANIMATE,animate);}
    @Override public void tick(){super.tick();if(level().isClientSide())visual.accept(this);else if(animates())animateBlock();}
    private void animateBlock(){
        var pos=blockPosition();var state=level().getBlockState(pos);if(!AnimatedGolems.valid(state,level(),pos))return;var golem=AnimatedGolems.TYPE.get().create(level());golem.moveTo(getX(),pos.getY(),getZ(),0,0);golem.initialize(state,1200,true,false,null,GolemSnapshot.EMPTY);level().removeBlock(pos,false);if(!level().addFreshEntity(golem)){level().setBlockAndUpdate(pos,state);return;}golem.clearFire();golem.heal(100);level().playSound(null,pos,thaumcraft.registry.ModSounds.WAND.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);level().broadcastEntityEvent(golem,(byte)7);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("length",length());tag.putBoolean("animate",animates());}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);configure(tag.getInt("length"),tag.getBoolean("animate"));}
}
