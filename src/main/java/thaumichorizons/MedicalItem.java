package thaumichorizons;

import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class MedicalItem extends Item {
    public final String kind;
    public MedicalItem(String kind,Properties p){super(p);this.kind=kind;}
    public static void give(Player p,ItemStack result){if(!p.getInventory().add(result))p.drop(result,false);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player p,InteractionHand hand){
        ItemStack stack=p.getItemInHand(hand);
        if(kind.equals("injection")){p.startUsingItem(hand);return InteractionResultHolder.consume(stack);}
        if(kind.equals("incubated_egg")||kind.equals("blast_phial")){
            if(!level.isClientSide()){
                Projectile projectile;
                if(kind.equals("incubated_egg")){IncubatedEgg egg=new IncubatedEgg(Content.EGG.get(),level);egg.setOwner(p);egg.setPos(p.getX(),p.getEyeY()-.1,p.getZ());projectile=egg;}
                else{BlastPhial phial=new BlastPhial(Content.PHIAL.get(),level);phial.setOwner(p);phial.setPos(p.getX(),p.getEyeY()-.1,p.getZ());phial.setItem(stack.copyWithCount(1));projectile=phial;}
                projectile.shootFromRotation(p,p.getXRot(),p.getYRot(),0,kind.equals("incubated_egg")?1.5F:.75F,1);
                level.addFreshEntity(projectile);if(!p.hasInfiniteMaterials())stack.shrink(1);
                level.playSound(null,p.blockPosition(),net.minecraft.sounds.SoundEvents.SNOWBALL_THROW,net.minecraft.sounds.SoundSource.PLAYERS,.5F,.4F/(level.random.nextFloat()*.4F+.8F));
            }
            return InteractionResultHolder.sidedSuccess(stack,level.isClientSide());
        }
        if(kind.equals("empty_syringe")){
            if(!level.isClientSide()){
                Vec3 from=p.getEyePosition(),to=from.add(p.getLookAngle().scale(1.5));
                double max=level.clip(new net.minecraft.world.level.ClipContext(from,to,net.minecraft.world.level.ClipContext.Block.COLLIDER,net.minecraft.world.level.ClipContext.Fluid.NONE,p)).getLocation().distanceToSqr(from);
                LivingEntity target=null;double best=max;
                for(LivingEntity entity:level.getEntitiesOfClass(LivingEntity.class,p.getBoundingBox().expandTowards(p.getLookAngle().scale(1.5)).inflate(.3),e->e!=p&&e.isAlive())){
                    var hit=entity.getBoundingBox().inflate(.1).clip(from,to);
                    if(hit.isPresent()&&from.distanceToSqr(hit.get())<=best){target=entity;best=from.distanceToSqr(hit.get());}
                }
                ItemStack result;
                if(target instanceof Mob mob){
                    if(target.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)||target instanceof net.minecraft.world.entity.npc.AbstractVillager||!Set.of(MobCategory.CREATURE,MobCategory.AMBIENT,MobCategory.WATER_CREATURE,MobCategory.WATER_AMBIENT,MobCategory.AXOLOTLS).contains(target.getType().getCategory()))return InteractionResultHolder.fail(stack);
                    CompoundTag tag=Vats.genetics(target);
                    result=new ItemStack(Content.item("blood_sample"));CompoundTag genetic=new CompoundTag();genetic.put("critter",tag);genetic.putString("critterName",target.getName().getString());result.set(DataComponents.CUSTOM_DATA,CustomData.of(genetic));
                }else result=new ItemStack(Content.item("blood_syringe"));
                if(p.getInventory().add(result)){if(!p.hasInfiniteMaterials())stack.shrink(1);}
            }
            return InteractionResultHolder.sidedSuccess(stack,level.isClientSide());
        }
        return super.use(level,p,hand);
    }
    @Override public int getUseDuration(ItemStack stack,LivingEntity e){return kind.equals("injection")?8:0;}
    @Override public UseAnim getUseAnimation(ItemStack s){return kind.equals("injection")?UseAnim.BOW:UseAnim.NONE;}
    @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity e){
        if(!kind.equals("injection"))return stack;
        if(!level.isClientSide()){
            apply(stack,e,e);
            if(!(e instanceof Player p)||!p.hasInfiniteMaterials())stack.shrink(1);
        }
        return stack;
    }
    static void apply(ItemStack stack,LivingEntity target,Entity source){
        for(var effect:stack.getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY).getAllEffects()){
            if(effect.getEffect().value().isInstantenous())effect.getEffect().value().applyInstantenousEffect(source,source,target,effect.getAmplifier(),1);
            else target.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect),source);
        }
    }
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity target,LivingEntity hitter){
        if(kind.equals("injection")&&!target.level().isClientSide()){apply(stack,target,hitter);stack.shrink(1);}return super.hurtEnemy(stack,target,hitter);
    }
    @Override public Component getName(ItemStack stack){
        if(kind.equals("blood_sample"))return super.getName(stack).copy().append(": "+stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("critterName"));
        return super.getName(stack);
    }
    @Override public void appendHoverText(ItemStack s,TooltipContext c,List<Component> lines,TooltipFlag flag){
        if(s.has(DataComponents.POTION_CONTENTS))s.get(DataComponents.POTION_CONTENTS).addPotionTooltip(lines::add,1,c.tickRate());
        if(kind.equals("blood_sample"))lines.add(Component.literal(s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("critterName")));
    }
}
