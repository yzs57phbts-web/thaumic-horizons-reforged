package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import thaumcraft.aspect.*;
import thaumcraft.wand.*;
import thaumcraft.wand.focus.*;

/** Lossless entity transport; cloning uses a deliberately separate, sanitised genetic snapshot. */
public final class Specimens {
    public static final net.neoforged.neoforge.registries.DeferredBlock<SpecimenJarBlock> JAR=Content.BLOCKS.register("specimen_jar",()->new SpecimenJarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(.3F,1).lightLevel(s->9).noOcclusion().noLootTable()));
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.level.block.entity.BlockEntityType<?>,net.minecraft.world.level.block.entity.BlockEntityType<SpecimenJarEntity>> TYPE=Content.TILES.register("specimen_jar",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(SpecimenJarEntity::new,JAR.get()).build(null));
    public static void init(){
        thaumcraft.platform.ModNetwork.toClient(ContainmentFx.TYPE,ContainmentFx.CODEC);
        Content.ALL.put("specimen_jar",Content.ITEMS.registerItem("specimen_jar",Jar::new,new Item.Properties().stacksTo(1)));
        Content.ALL.put("focus_containment",Content.ITEMS.registerItem("focus_containment",Containment::new,FocusItem.properties(new Item.Properties())));
    }
    public static ItemStack write(LivingEntity entity){var tag=new CompoundTag();entity.save(tag);tag.remove("Passengers");var data=new CompoundTag();data.put("critter",tag);data.putString("name",entity.getName().getString());ItemStack stack=new ItemStack(Content.item("specimen_jar"));stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));return stack;}
    public static LivingEntity read(ItemStack stack,Level level){if(Souls.isSoul(stack))return null;var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompound("critter").copy();if(!tag.contains("id"))return null;tag.remove("Passengers");var e=EntityType.loadEntityRecursive(tag,level,x->x);return e instanceof LivingEntity living&&!(e instanceof Player)?living:null;}
    public static boolean isEmptyJar(ItemStack stack){var aspects=AspectKeys.ASPECTS.get(stack);return stack.is(thaumcraft.registry.ModBlocks.WARDED_JAR.get().asItem())&&(aspects==null||aspects.isEmpty());}
    public static int emptyJar(Player player){for(int i=0;i<player.getInventory().getContainerSize();i++)if(isEmptyJar(player.getInventory().getItem(i)))return i;return -1;}
    public static boolean capture(Player player,LivingEntity entity){int slot=emptyJar(player);if(slot<0||entity instanceof Player||!entity.isAlive())return false;ItemStack jar=entity instanceof RareCreatures.LostSoul?Souls.soulJar(net.minecraft.world.entity.npc.VillagerProfession.FARMER):write(entity);player.getInventory().getItem(slot).shrink(1);MedicalItem.give(player,jar);if(entity.level() instanceof ServerLevel level)ContainmentFx.send(level,entity.position());entity.discard();return true;}
    static ThermalFoci.Beam clientBeam=(level,player,end,color,impact)->{};
    public static final class Jar extends BlockItem {
        Jar(Properties p){super(JAR.get(),p);}
        @Override public Component getName(ItemStack stack){if(Souls.isSoul(stack))return Component.translatable("item.thaumichorizons.soul_jar").append(" (").append(Component.translatable("entity.minecraft.villager."+net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.getKey(Souls.profession(stack)).getPath())).append(")");String name=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("name");return Component.translatable("item.thaumichorizons.specimen_jar").append(name.isEmpty()?"":" ("+name+")");}
        @Override public InteractionResult useOn(UseOnContext c){
            if(c.getPlayer()==null||!c.getPlayer().isShiftKeyDown())return super.useOn(c);
            BlockPos pos=c.getClickedPos().relative(c.getClickedFace());if(!c.getLevel().mayInteract(c.getPlayer(),pos)||!c.getPlayer().mayUseItemAt(pos,c.getClickedFace(),c.getItemInHand()))return InteractionResult.FAIL;
            if(c.getLevel() instanceof ServerLevel level){LivingEntity e=read(c.getItemInHand(),level);if(e==null)return InteractionResult.FAIL;e.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,e.getYRot(),e.getXRot());if(!level.noCollision(e)||level.getEntity(e.getUUID())!=null)return InteractionResult.FAIL;if(!level.addFreshEntity(e))return InteractionResult.FAIL;c.getItemInHand().shrink(1);MedicalItem.give(c.getPlayer(),new ItemStack(thaumcraft.registry.ModBlocks.WARDED_JAR.get()));}
            return InteractionResult.sidedSuccess(c.getLevel().isClientSide());
        }
    }
    public static final class Containment extends FocusItem {
        public static final FocusUpgradeType SLOW=new FocusUpgradeType(101,"slow",ThaumicHorizons.id("textures/foci/slow.png"),new AspectList().add(Aspect.TRAP,8));
        private static final Map<UUID,Progress> PROGRESS=new HashMap<>();
        private record Progress(UUID target,float amount){}
        Containment(Properties p){super(p);}
        @Override public int getFocusColor(ItemStack focus){return 29631;}
        @Override public net.minecraft.resources.ResourceLocation getFocusTexture(ItemStack s){return ThaumicHorizons.id("items/focus_containment");}
        @Override public net.minecraft.resources.ResourceLocation getDepthLayerTexture(ItemStack s){return ThaumicHorizons.id("items/focus_containment_depth");}
        @Override public AspectList getVisCost(ItemStack focus){return new AspectList().add(Aspect.AIR,10).add(Aspect.ENTROPY,10);}
        @Override public boolean isVisCostPerTick(ItemStack s){return true;}
        @Override public WandFocusAnimation getAnimation(ItemStack s){return WandFocusAnimation.CHARGE;}
        @Override public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus,int rank){return rank==2||rank==4?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY,SLOW}:rank>=1&&rank<=5?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY}:null;}
        @Override public InteractionResult onFocusRightClick(ItemStack s,Level l,Player p,HitResult hit){return InteractionResult.CONSUME;}
        @Override public void onPlayerStoppedUsingFocus(ItemStack s,Level l,Player p,int count){PROGRESS.remove(p.getUUID());}
        @Override public void onUsingFocusTick(ItemStack stack,Player player,int count){
            if(emptyJar(player)<0){if(!player.level().isClientSide())player.displayClientMessage(Component.translatable("thaumichorizons.noJar"),true);player.stopUsingItem();return;}
            WandItem wand=(WandItem)stack.getItem();if(!wand.consumeAllVis(stack,player,getVisCost(stack),false,false)){player.stopUsingItem();return;}
            Vec3 from=player.getEyePosition(),to=from.add(player.getLookAngle().scale(10));var block=player.level().clip(new net.minecraft.world.level.ClipContext(from,to,net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,player));double distance=from.distanceToSqr(block.getLocation());Mob target=null;
            for(Mob candidate:player.level().getEntitiesOfClass(Mob.class,player.getBoundingBox().expandTowards(player.getLookAngle().scale(10)).inflate(1))){if(!candidate.isAlive()||candidate instanceof net.minecraft.world.entity.animal.AbstractGolem||candidate instanceof net.minecraft.world.entity.boss.wither.WitherBoss||candidate instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon)continue;var hit=candidate.getBoundingBox().inflate(.3).clip(from,to);if(hit.isPresent()&&from.distanceToSqr(hit.get())<distance){target=candidate;distance=from.distanceToSqr(hit.get());}}
            if(player.level().isClientSide())clientBeam.draw(player.level(),player,target==null?block.getLocation():target.getBoundingBox().getCenter(),4482815,target==null?0:2);
            if(target==null){PROGRESS.remove(player.getUUID());return;}
            if(player.level() instanceof ServerLevel level&&wand.consumeAllVis(stack,player,getVisCost(stack),true,false)){
                int potency=Wands.getFocusPotency(stack),slow=getUpgradeLevel(Wands.getFocusItem(stack),SLOW);if(slow>0)target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,40,slow-1));
                Progress old=PROGRESS.get(player.getUUID());float value=old!=null&&old.target.equals(target.getUUID())?old.amount+2+potency/3:2+potency/2F;PROGRESS.put(player.getUUID(),new Progress(target.getUUID(),value));
                level.sendParticles(ParticleTypes.ENCHANT,target.getX(),target.getY()+target.getBbHeight()/2,target.getZ(),3,.3,.4,.3,.02);if(player.tickCount%24==0)target.playSound(thaumcraft.registry.ModSounds.JACOBS.get(),.3F,1);
                if(value>target.getHealth()*20&&capture(player,target))PROGRESS.remove(player.getUUID());
            }
        }
    }
}
