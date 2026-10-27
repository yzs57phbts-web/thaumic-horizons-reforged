package thaumichorizons;

import java.util.UUID;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.aspect.AspectList;

/** Only called by the explicit disposable-world client smoke test. */
final class SpeciesSmoke {
    private static int ticks,stage,started;
    private static volatile UUID mount;
    private static volatile boolean ready,nether,returned;
    @SuppressWarnings({"unchecked","rawtypes"})
    static boolean tick(Minecraft mc,MinecraftServer server){
        ++ticks;
        if(ticks==1){
            for(var entry:Species.TYPES.entrySet()){
                var entity=entry.getValue().get().create(mc.level);
                var renderer=(net.minecraft.client.renderer.entity.EntityRenderer)mc.getEntityRenderDispatcher().getRenderer(entity);
                var texture=renderer.getTextureLocation(entity);
                if(mc.getResourceManager().getResource(texture).isEmpty())throw new IllegalStateException("Missing species texture "+entry.getKey()+": "+texture);
                if(entity instanceof Horse horse){
                    for(var armor:java.util.List.of(Items.IRON_HORSE_ARMOR,Items.GOLDEN_HORSE_ARMOR,Items.DIAMOND_HORSE_ARMOR)){
                        horse.setItemSlot(EquipmentSlot.BODY,new ItemStack(armor));
                        if(mc.getResourceManager().getResource(renderer.getTextureLocation(horse)).isEmpty())throw new IllegalStateException("Missing steed armor texture");
                    }
                }
            }
            System.out.println("TH_SPECIES_RENDERERS_OK: eighteen entity renderers and all horse armor textures");
            mc.options.hideGui=true;
            server.execute(()->{
                var level=server.overworld();var p=server.getPlayerList().getPlayers().getFirst();
                var body=(VatEntity)level.getBlockEntity(new BlockPos(17,67,1));body.playerSubject=null;body.sync();
                for(var old:level.getEntities((Entity)null,new net.minecraft.world.phys.AABB(-6,62,-10,100,90,40),e->e instanceof Species.Form))old.discard();
                for(BlockPos pos:BlockPos.betweenClosed(34,63,-5,76,72,25))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
                int i=0;for(var entry:Species.TYPES.entrySet()){
                    var entity=(Mob)entry.getValue().get().create(level);entity.setPersistenceRequired();entity.setCustomName(net.minecraft.network.chat.Component.literal(entry.getKey()));entity.setCustomNameVisible(true);
                    int x=39+(i%6)*6,z=(i/6)*6;
                    for(BlockPos fence:BlockPos.betweenClosed(x-2,64,z-2,x+2,64,z+2))if(fence.getX()==x-2||fence.getX()==x+2||fence.getZ()==z-2||fence.getZ()==z+2)level.setBlockAndUpdate(fence,Blocks.OAK_FENCE.defaultBlockState());
                    level.setBlockAndUpdate(new BlockPos(x,64,z+2),Blocks.OAK_FENCE_GATE.defaultBlockState());
                    entity.moveTo(x+.5,64,z+.5,0,0);if(entity instanceof Horse horse){horse.setTamed(true);horse.equipSaddle(new ItemStack(Items.SADDLE),SoundSource.NEUTRAL);}level.addFreshEntity(entity);i++;
                }
                p.connection.teleport(54,70,24,180,18);
            });
        }
        if(ticks==70)shot("horizons-species.png");
        if(ticks==80)server.execute(()->{
            var level=server.overworld();var horse=SpeciesHorse.ENDERSTEED.get().create(level);horse.moveTo(70,64,17,40,0);horse.setNoAi(true);horse.setTamed(true);horse.equipSaddle(new ItemStack(Items.SADDLE),SoundSource.NEUTRAL);horse.setItemSlot(EquipmentSlot.BODY,new ItemStack(Items.DIAMOND_HORSE_ARMOR));level.addFreshEntity(horse);
            var familiar=Species.FAMILIAR.get().create(level);familiar.moveTo(68,64,17,0,0);familiar.setNoAi(true);level.addFreshEntity(familiar);
            server.getPlayerList().getPlayers().getFirst().connection.teleport(68,66,21,160,20);
        });
        if(ticks==120)shot("horizons-familiar-legacy-steed.png");
        if(ticks==130)server.execute(()->{
            var level=server.overworld();var destination=server.getLevel(Level.NETHER);var p=server.getPlayerList().getPlayers().getFirst();
            for(BlockPos pos:BlockPos.betweenClosed(108,79,108,117,86,117))level.setBlockAndUpdate(pos,pos.getY()==79?Blocks.OBSIDIAN.defaultBlockState():Blocks.AIR.defaultBlockState());
            for(BlockPos pos:BlockPos.betweenClosed(10,79,10,19,86,19))destination.setBlockAndUpdate(pos,pos.getY()==79?Blocks.OBSIDIAN.defaultBlockState():Blocks.AIR.defaultBlockState());
            var donor=EntityType.HORSE.create(level);donor.setTamed(true);donor.equipSaddle(new ItemStack(Items.SADDLE),SoundSource.NEUTRAL);donor.moveTo(112,80,112,0,0);
            var horse=(SpeciesHorse)Species.transform(donor,"nightmare",new AspectList());horse.setNoAi(true);level.addFreshEntity(horse);mount=horse.getUUID();
            p.getAbilities().flying=false;p.onUpdateAbilities();p.connection.teleport(112,80,112,0,0);p.startRiding(horse,true);ready=true;
        });
        if(ready&&stage==0&&ticks>150&&mc.player.getVehicle() instanceof SpeciesHorse){jump(mc);stage=1;started=ticks;}
        if(stage==1&&mc.level.dimension()==Level.NETHER&&mc.player.getVehicle() instanceof SpeciesHorse){
            server.execute(()->{
                var p=server.getPlayerList().getPlayers().getFirst();
                if(p.level().dimension()!=Level.NETHER||p.getVehicle()==null||!p.getVehicle().getUUID().equals(mount))throw new IllegalStateException("Mounted Nightmare transfer lost passenger or UUID");nether=true;
            });stage=2;started=ticks;
        }
        if(stage==2&&nether&&ticks-started>40){shot("horizons-nightmare-nether.png");jump(mc);stage=3;started=ticks;}
        if(stage==3&&mc.level.dimension()==Level.OVERWORLD&&mc.player.getVehicle() instanceof SpeciesHorse){
            server.execute(()->{
                var p=server.getPlayerList().getPlayers().getFirst();
                if(p.level().dimension()!=Level.OVERWORLD||p.getVehicle()==null||!p.getVehicle().getUUID().equals(mount))throw new IllegalStateException("Nightmare return lost passenger or UUID");returned=true;
                System.out.println("TH_NIGHTMARE_TRAVEL_OK: native riding-jump packets, Nether and Overworld, passenger remount and entity UUID preserved");
            });stage=4;started=ticks;
        }
        if(ticks>1800||(stage>0&&stage<4&&ticks-started>500))throw new IllegalStateException("Species smoke timed out at stage "+stage+" dimension "+mc.level.dimension());
        return returned&&ticks-started>160;
    }
    private static void jump(Minecraft mc){mc.getConnection().send(new ServerboundPlayerCommandPacket(mc.player,ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP,100));}
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
