package thaumichorizons;

import net.minecraft.world.entity.LivingEntity;

/** Contained specimens are display entities, not entities ticked by the client world. */
final class ContainedPreview {
    static void prepare(LivingEntity entity,double x,double y,double z,long time){
        entity.setPos(x,y,z);
        entity.xo=entity.xOld=x;entity.yo=entity.yOld=y;entity.zo=entity.zOld=z;
        entity.setYRot(0);entity.yRotO=0;entity.setXRot(0);entity.xRotO=0;
        entity.yBodyRot=entity.yBodyRotO=entity.yHeadRot=entity.yHeadRotO=0;
        entity.walkAnimation.setSpeed(0);entity.walkAnimation.update(0,1);
        entity.tickCount=(int)(time&0x7fffffff);entity.hurtTime=0;entity.deathTime=0;
    }
}
