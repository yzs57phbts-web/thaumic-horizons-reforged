package thaumichorizons;

import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import thaumcraft.aspect.Aspect;

public final class Effects {
    private static int amplifier(LivingEntity e,Holder<MobEffect> type){var effect=e.getEffect(type);return effect==null?-1:effect.getAmplifier();}
    public static void tick(EntityTickEvent.Post event){
        if(!(event.getEntity() instanceof LivingEntity e)||e.level().isClientSide()||!e.isAlive())return;
        int amp=amplifier(e,Content.SYNTHESIS);
        if(amp>=0&&e.tickCount%Math.max(1,15-2*amp)==0&&e.level().isDay()&&e.level().canSeeSky(e.blockPosition())){e.heal(.5F);if(e instanceof Player p)p.getFoodData().eat(1,.5F);}
        amp=amplifier(e,Content.VACUUM);
        if(amp>=0){
            double range=10+2*amp;
            for(ItemEntity item:e.level().getEntitiesOfClass(ItemEntity.class,e.getBoundingBox().inflate(range),i->i.isAlive()&&i.distanceToSqr(e)<=range*range)){
                Vec3 d=new Vec3(item.getX()-e.getX(),item.getY()-e.getY()+e.getBbHeight()/2,item.getZ()-e.getZ());
                if(d.lengthSqr()<.000001)continue;Vec3 v=item.getDeltaMovement().subtract(d.normalize().scale(.1));
                item.setDeltaMovement(Mth.clamp(v.x,-.35,.35),Mth.clamp(v.y,-.35,.35),Mth.clamp(v.z,-.35,.35));item.hasImpulse=true;
            }
        }
        amp=amplifier(e,Content.VIS_REGEN);
        if(amp>=0&&e.tickCount%Math.max(1,20-3*amp)==0){
            var primals=Aspect.getPrimalAspects();Aspect a=primals.get(e.getRandom().nextInt(primals.size()));
            thaumcraft.entity.AspectOrb.spawn((net.minecraft.server.level.ServerLevel)e.level(),e.blockPosition(),a,1);
        }
        amp=amplifier(e,Content.SHOCK);
        if(amp>=0){
            int chance=20*(amp+1);
            for(LivingEntity target:e.level().getEntitiesOfClass(LivingEntity.class,e.getBoundingBox().inflate(10),t->t!=e&&t.isAlive()&&t.distanceToSqr(e)<=100))
                if(e.getRandom().nextInt(1000)<chance){target.hurt(e instanceof Player p?e.damageSources().playerAttack(p):e.damageSources().magic(),1);e.level().playSound(null,e.blockPosition(),thaumcraft.registry.ModSounds.ZAP.get(),net.minecraft.sounds.SoundSource.PLAYERS,1,1);}
        }
    }
}
