package thaumichorizons;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

public final class BlastPhial extends ThrowableItemProjectile {
    public BlastPhial(EntityType<? extends BlastPhial> type,Level level){super(type,level);}
    @Override protected Item getDefaultItem(){return Content.item("blast_phial");}
    @Override protected double getDefaultGravity(){return .05;}
    @Override protected void onHit(HitResult hit){
        super.onHit(hit);if(level().isClientSide())return;
        PotionContents contents=getItem().getOrDefault(DataComponents.POTION_CONTENTS,PotionContents.EMPTY);
        for(LivingEntity target:level().getEntitiesOfClass(LivingEntity.class,getBoundingBox().inflate(4,2,4))){
            double distance=distanceToSqr(target);if(distance>=16)continue;
            double factor=hit instanceof EntityHitResult direct&&direct.getEntity()==target?1:1-Math.sqrt(distance)/4;
            for(var effect:contents.getAllEffects()){
                var type=effect.getEffect().value();
                if(type.isInstantenous())type.applyInstantenousEffect(this,getOwner(),target,effect.getAmplifier(),factor);
                else{int ticks=(int)(factor*effect.getDuration()+.5);if(ticks>20)target.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect.getEffect(),ticks,effect.getAmplifier()),getEffectSource());}
            }
        }
        level().levelEvent(2002,blockPosition(),contents.getColor());discard();
    }
}
