package thaumichorizons;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public final class IncubatedEgg extends ThrowableItemProjectile {
    public IncubatedEgg(EntityType<? extends IncubatedEgg> type,Level level){super(type,level);}
    @Override protected Item getDefaultItem(){return Content.item("incubated_egg");}
    @Override protected void onHit(HitResult hit){
        super.onHit(hit);if(!level().isClientSide()){
            var chicken=EntityType.CHICKEN.create(level());if(chicken!=null){chicken.setAge(-24000);chicken.moveTo(getX(),getY(),getZ(),getYRot(),0);level().addFreshEntity(chicken);}
            level().broadcastEntityEvent(this,(byte)3);discard();
        }
    }
    @Override public void handleEntityEvent(byte id){if(id==3){for(int i=0;i<8;i++)level().addParticle(new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM,getItem()),getX(),getY(),getZ(),0,0,0);}else super.handleEntityEvent(id);}
}
