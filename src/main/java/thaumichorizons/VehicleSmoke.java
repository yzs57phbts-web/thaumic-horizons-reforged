package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import thaumcraft.aspect.*;

final class VehicleSmoke {
    private static int ticks,woodId,metalId;
    private static Vec3 start;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1)server.execute(()->{
            var level=server.overworld();server.setDifficulty(net.minecraft.world.Difficulty.NORMAL,true);var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();for(var old:level.getEntities((Entity)null,new AABB(-35,62,-1,-7,76,25),e->!(e instanceof net.minecraft.world.entity.player.Player)))old.discard();
            for(var pos:BlockPos.betweenClosed(-35,63,-1,-8,70,24))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
            for(var pos:BlockPos.betweenClosed(-24,64,0,-8,64,8))level.setBlockAndUpdate(pos,Blocks.WATER.defaultBlockState());for(var pos:BlockPos.betweenClosed(-24,64,12,-8,64,20))level.setBlockAndUpdate(pos,Blocks.LAVA.defaultBlockState());
            var wood=MagicBoats.GREATWOOD.get().create(level);wood.moveTo(-12,65,4,0,0);level.addFreshEntity(wood);woodId=wood.getId();var metal=MagicBoats.THAUMIUM.get().create(level);metal.moveTo(-12,65,16,0,0);level.addFreshEntity(metal);metalId=metal.getId();
            int index=0;for(var form:RareCreatures.SLIMES.keySet()){int z=2+index*5;for(var pos:BlockPos.betweenClosed(-34,64,z-2,-28,65,z+2))if(pos.getX()==-34||pos.getX()==-28||pos.getZ()==z-2||pos.getZ()==z+2)level.setBlockAndUpdate(pos,Blocks.OAK_FENCE.defaultBlockState());var slime=RareCreatures.SLIMES.get(form).get().create(level);slime.setSize(2,true);slime.moveTo(-31,64,z,0,0);slime.setPersistenceRequired();level.addFreshEntity(slime);index++;}
            var soul=RareCreatures.SOUL.get().create(level);soul.moveTo(-27,67,10,0,0);level.addFreshEntity(soul);
            var shardPos=new BlockPos(-27,65,21);level.setBlockAndUpdate(shardPos,SyntheticNode.BLOCK.get().defaultBlockState());var shard=(SyntheticNode)level.getBlockEntity(shardPos);shard.setAspects(new AspectList().add(Aspect.AIR,40).add(Aspect.FIRE,20).add(Aspect.MAGIC,15));for(var aspect:shard.getAspectsBase().getAspects())shard.addToContainer(aspect,shard.getNodeVisBase(aspect));player.connection.teleport(-18,70,24,180,22);
        });
        if(ticks==55){
            int slimes=0;for(var entity:mc.level.entitiesForRendering())if(entity instanceof RareCreatures.ElementalSlime slime){slimes++;var renderer=mc.getEntityRenderDispatcher().getRenderer(slime);var texture=renderer.getTextureLocation(slime);if(!mc.getResourceManager().getResource(texture).isPresent())throw new IllegalStateException("Missing rare slime texture: "+texture);}if(slimes<4)throw new IllegalStateException("Rare slime fixture missing: "+slimes);
            shot("horizons-boats-rare-creatures.png");
        }
        if(ticks==60)server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();var boat=server.overworld().getEntity(woodId);player.connection.teleport(boat.getX(),boat.getY(),boat.getZ(),90,0);player.startRiding(boat);});
        if(ticks==85){if(!(mc.player.getVehicle() instanceof MagicBoats.LegacyBoat boat)||boat.metal)throw new IllegalStateException("Greatwood boat not mounted");start=boat.position();mc.player.setYRot(90);mc.options.keyUp.setDown(true);}
        if(ticks==120){mc.options.keyUp.setDown(false);var boat=(MagicBoats.LegacyBoat)mc.player.getVehicle();if(boat==null||boat.position().distanceToSqr(start)<1||boat.getDeltaMovement().horizontalDistance()>.46)throw new IllegalStateException("Greatwood boat steering failed: "+(boat==null?"null":boat.position().subtract(start)));System.out.println("TH_BOAT_STEERING_OK: greatwood distance="+boat.position().distanceTo(start));shot("horizons-greatwood-boat-ride.png");}
        if(ticks==125)server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();var boat=server.overworld().getEntity(metalId);player.connection.teleport(boat.getX(),boat.getY(),boat.getZ(),90,0);player.startRiding(boat);});
        if(ticks==150){if(!(mc.player.getVehicle() instanceof MagicBoats.LegacyBoat boat)||!boat.metal)throw new IllegalStateException("Thaumium boat not mounted");start=boat.position();mc.player.setYRot(90);mc.options.keyUp.setDown(true);}
        if(ticks==185){mc.options.keyUp.setDown(false);var boat=(MagicBoats.LegacyBoat)mc.player.getVehicle();if(boat==null||boat.position().distanceToSqr(start)<1||boat.getY()<64.3||mc.player.isOnFire()||!mc.player.hasEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE))throw new IllegalStateException("Thaumium boat failed lava ride: "+(boat==null?"null":boat.position()+", movement="+boat.position().subtract(start)+", burning="+mc.player.isOnFire()+", resistance="+mc.player.hasEffect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE)));shot("horizons-thaumium-boat-lava.png");System.out.println("TH_LAVA_BOAT_OK: camera steering, lava flotation, passenger fire protection");}
        if(ticks==190)server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.connection.teleport(-26,67,23,160,18);});
        if(ticks==215)shot("horizons-ethereal-shard.png");
        if(ticks==220)server.execute(()->server.getPlayerList().getPlayers().getFirst().connection.teleport(15.5,65,8.5,180,8));
        return ticks>=235;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
