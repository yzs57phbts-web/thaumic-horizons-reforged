package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.node.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;

/** TNT-style priming with the original destructive aspect-crystallising explosion. */
public final class Alchemite {
    public static final DeferredBlock<AlchemiteBlock> BLOCK=Content.BLOCKS.register("alchemite",()->new AlchemiteBlock(BlockBehaviour.Properties.of().strength(.7F,1).sound(SoundType.GRASS).lightLevel(s->7)));
    public static final DeferredHolder<EntityType<?>,EntityType<Primed>> ENTITY=Content.ENTITIES.register("primed_alchemite",()->EntityType.Builder.<Primed>of(Primed::new,MobCategory.MISC).sized(.98F,.98F).fireImmune().clientTrackingRange(10).updateInterval(10).build("thaumichorizons:primed_alchemite"));
    public static final DeferredHolder<EntityType<?>,EntityType<InvulnerableDrop>> DROPS=Content.ENTITIES.register("invulnerable_item",()->EntityType.Builder.<InvulnerableDrop>of(InvulnerableDrop::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(6).updateInterval(20).build("thaumichorizons:invulnerable_item"));
    static void init(){Content.ALL.put("alchemite",Content.ITEMS.registerSimpleBlockItem("alchemite",BLOCK));}
    static void research(){
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlchemite","alchemite",TcStack.of("thaumichorizons:alchemite",1),TcIngredient.of(TcStack.of("minecraft:tnt",1)),new AspectList().add(Aspect.ENERGY,4).add(Aspect.CRYSTAL,4).add(Aspect.EXCHANGE,4)));
        new ResearchItem("alchemite","ThaumicHorizons",new AspectList().add(Aspect.FIRE,2).add(Aspect.ENTROPY,2).add(Aspect.ENERGY,4).add(Aspect.CRYSTAL,4).add(Aspect.EXCHANGE,2),3,6,2,TcStack.of("thaumichorizons:alchemite",1)).setParentsHidden("ESSENTIACRYSTAL").setPages(ResearchPage.text("alchemite1"),ResearchPage.crucible("TH:recipeAlchemite")).registerResearchItem();
    }
    static AspectList aspects(ItemStack stack){return stack.isEmpty()?new AspectList():AspectHelper.getBonusTags(stack,AspectHelper.getObjectTags(stack));}
    static void crystals(Level level,double x,double y,double z,AspectList aspects,int multiplier){
        if(level.isClientSide()||multiplier<=0)return;
        for(var aspect:aspects.getAspects())for(int count=aspects.getAmount(aspect)*multiplier;count>0;count-=64){
            var stack=new ItemStack(thaumcraft.registry.ModItems.CRYSTALLIZED_ESSENCE.get(),Math.min(64,count));AspectKeys.ASPECTS.set(stack,new AspectList().add(aspect,1));var item=new InvulnerableDrop(DROPS.get(),level);item.setPos(x,y,z);item.setItem(stack);item.setDeltaMovement((level.random.nextDouble()-.5)*.2,.2,(level.random.nextDouble()-.5)*.2);level.addFreshEntity(item);
        }
    }
    public static final class InvulnerableDrop extends ItemEntity {
        public InvulnerableDrop(EntityType<? extends ItemEntity> type,Level level){super(type,level);}
        @Override public boolean hurt(DamageSource source,float amount){return false;}
    }
    public static final class AlchemiteBlock extends TntBlock {
        AlchemiteBlock(Properties properties){super(properties);}
        @Override public void onCaughtFire(BlockState state,Level level,BlockPos pos,Direction face,LivingEntity igniter){if(!level.isClientSide()){prime(level,pos,igniter,false);level.playSound(null,pos,SoundEvents.TNT_PRIMED,SoundSource.BLOCKS,1,1);}}
        @Override public void wasExploded(Level level,BlockPos pos,Explosion explosion){if(!level.isClientSide())prime(level,pos,explosion.getIndirectSourceEntity(),true);}
        static Primed prime(Level level,BlockPos pos,LivingEntity owner,boolean chain){var entity=new Primed(ENTITY.get(),level);entity.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);double angle=level.random.nextDouble()*Math.PI*2;entity.setDeltaMovement(-Math.sin(angle)*.02,.2,-Math.cos(angle)*.02);entity.owner=owner;entity.ownerId=owner==null?null:owner.getUUID();if(chain)entity.setFuse(level.random.nextInt(20)+10);level.addFreshEntity(entity);return entity;}
    }
    public static final class Primed extends PrimedTnt {
        private LivingEntity owner;private java.util.UUID ownerId;
        public Primed(EntityType<? extends Primed> type,Level level){super(type,level);setBlockState(BLOCK.get().defaultBlockState());}
        @Override public LivingEntity getOwner(){if(owner==null&&ownerId!=null&&level() instanceof ServerLevel server&&server.getEntity(ownerId) instanceof LivingEntity living)owner=living;return owner;}
        @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);if(ownerId!=null)tag.putUUID("placer",ownerId);}
        @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);ownerId=tag.hasUUID("placer")?tag.getUUID("placer"):null;}
        @Override protected void explode(){
            if(!(level() instanceof ServerLevel server))return;var explosion=new TransmutingExplosion(server,this,getX(),getY()+.0625,getZ(),5);
            if(net.neoforged.neoforge.event.EventHooks.onExplosionStart(server,explosion))return;explosion.explode();explosion.finalizeExplosion(false);
            for(var player:server.players())if(player.distanceToSqr(position())<4096)player.connection.send(new ClientboundExplodePacket(getX(),getY()+.0625,getZ(),5,explosion.getToBlow(),explosion.getHitPlayers().get(player),explosion.getBlockInteraction(),ParticleTypes.EXPLOSION,ParticleTypes.EXPLOSION_EMITTER,SoundEvents.GENERIC_EXPLODE));
        }
    }
    public static final class TransmutingExplosion extends Explosion {
        final ServerLevel level;
        TransmutingExplosion(ServerLevel level,Entity owner,double x,double y,double z,float radius){
            super(level,owner,new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(thaumcraft.damage.ModDamageTypes.DISSOLVE),owner,owner instanceof Primed primed?primed.getOwner():owner),new ExplosionDamageCalculator(){
                @Override public float getEntityDamageAmount(Explosion explosion,Entity entity){double strength=(1-Math.sqrt(entity.distanceToSqr(explosion.center()))/(explosion.radius()*2))*Explosion.getSeenPercent(explosion.center(),entity);return (float)(int)((strength*strength+strength)/2*16*(explosion.radius()*2)+1);}
            },x,y,z,radius,false,Explosion.BlockInteraction.DESTROY,ParticleTypes.EXPLOSION,ParticleTypes.EXPLOSION_EMITTER,SoundEvents.GENERIC_EXPLODE);this.level=level;
        }
        @Override public void finalizeExplosion(boolean particles){
            var destroyed=new java.util.ArrayList<BlockPos>();
            for(var pos:java.util.List.copyOf(getToBlow())){
                var state=level.getBlockState(pos);if(level.getBlockEntity(pos) instanceof NodeBlockEntity node){
                    double roll=level.random.nextDouble();if(roll<.25){crystals(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,node.getAspects(),1);level.removeBlock(pos,false);destroyed.add(pos);}else{if(roll<.5)node.setNodeModifier(NodeModifier.FADING);else{node.setNodeModifier(null);node.setNodeType(NodeType.UNSTABLE);}node.sync();}continue;
                }
                if(state.isAir())continue;var aspects=aspects(new ItemStack(state.getBlock()));
                if(aspects.isEmpty())state.onExplosionHit(level,pos,this,(stack,where)->Block.popResource(level,where,stack));
                else{crystals(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,aspects,1);state.onBlockExploded(level,pos,this);}
                if(level.getBlockState(pos).isAir())destroyed.add(pos);
            }
            getToBlow().clear();getToBlow().addAll(destroyed);
        }
    }
}
