package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thaumcraft.infusion.PedestalBlockEntity;
import thaumcraft.research.*;

final class VatInstability {
    static void event(VatEntity vat){
        Level l=vat.getLevel();int roll=l.random.nextInt(21);boolean all=roll==12||roll==18;
        if(roll==20){var players=l.getEntitiesOfClass(ServerPlayer.class,new AABB(vat.getBlockPos()).inflate(10));if(!players.isEmpty()){var p=players.get(l.random.nextInt(players.size()));boolean sticky=l.random.nextFloat()<.25F;Warp.add(p,sticky?1:1+l.random.nextInt(5),sticky?WarpType.STICKY:WarpType.TEMPORARY);}return;}
        if(roll==9){var p=vat.getBlockPos();l.explode(null,p.getX()+.5,p.getY()+.5,p.getZ()+.5,1.5F+l.random.nextFloat(),Level.ExplosionInteraction.NONE);return;}
        if(roll==3||roll==8||roll==14||roll==12||roll==5||roll==16||roll==18){for(LivingEntity e:l.getEntitiesOfClass(LivingEntity.class,new AABB(vat.getBlockPos()).inflate(10))){if(roll==5||roll==16||roll==18)e.addEffect(l.random.nextBoolean()?new MobEffectInstance(thaumcraft.registry.ModEffects.TAINT_POISON,120):new MobEffectInstance(thaumcraft.registry.ModEffects.VIS_EXHAUST,2400));else e.hurt(e.damageSources().magic(),4+l.random.nextInt(4));if(!all)break;}return;}
        int type=switch(roll){case 6,17->1;case 1,11->2;case 19->3;case 7->4;case 4,15->5;default->0;};
        for(int attempt=0;attempt<50&&!vat.pedestals.isEmpty();attempt++){BlockPos p=vat.pedestals.get(l.random.nextInt(vat.pedestals.size()));if(!l.hasChunkAt(p)||!(l.getBlockEntity(p) instanceof PedestalBlockEntity pedestal)||pedestal.getStack().isEmpty())continue;
            var stack=pedestal.getStack().copy();pedestal.setStackFromInfusion(net.minecraft.world.item.ItemStack.EMPTY);if(type<3||type==5)Containers.dropItemStack(l,p.getX()+.5,p.getY()+1,p.getZ()+.5,stack);
            if(type==1||type==3)l.setBlockAndUpdate(p.above(),thaumcraft.registry.ModBlocks.FLUX_GOO.get().defaultBlockState());else if(type==2||type==4)l.setBlockAndUpdate(p.above(),thaumcraft.registry.ModBlocks.FLUX_GAS.get().defaultBlockState());else if(type==5)l.explode(null,p.getX()+.5,p.getY()+.5,p.getZ()+.5,1,Level.ExplosionInteraction.NONE);break;
        }
    }
}
