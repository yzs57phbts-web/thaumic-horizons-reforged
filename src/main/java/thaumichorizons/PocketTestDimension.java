package thaumichorizons;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

/** Vanilla GameTestServer deliberately bakes an empty dimension registry. Opt-in test fixture only. */
@EventBusSubscriber(modid=ThaumicHorizons.ID)
public final class PocketTestDimension {
    @SubscribeEvent public static void stopping(ServerStoppingEvent event){
        var server=event.getServer();if(!Boolean.getBoolean("th.pocketGameTest")||!(server instanceof GameTestServer))return;
        try{
            var fields=new java.util.ArrayList<java.lang.reflect.Field>();for(String name:List.of("updatingChunkMap","pendingUnloads")){var field=net.minecraft.server.level.ChunkMap.class.getDeclaredField(name);if(!field.trySetAccessible())throw new IllegalStateException("GameTest chunk holders inaccessible");fields.add(field);}
            long deadline=System.nanoTime()+30_000_000_000L;boolean ready=false;while(!ready){ready=true;for(var level:server.getAllLevels()){level.getChunkSource().pollTask();var chunks=level.getChunkSource().chunkMap;for(var field:fields)for(Object value:((java.util.Map<?,?>)field.get(chunks)).values())if(value instanceof net.minecraft.server.level.ChunkHolder holder&&!holder.isReadyForSaving())ready=false;}if(ready)break;server.pollTask();if(System.nanoTime()>deadline)throw new IllegalStateException("GameTest chunk generation did not settle before shutdown");java.util.concurrent.locks.LockSupport.parkNanos(100_000L);}System.out.println("TH_GAMETEST_CHUNKS_SETTLED: asynchronous portal chunk work drained before vanilla shutdown");
        }catch(ReflectiveOperationException error){throw new IllegalStateException(error);}
    }
    @SubscribeEvent public static void started(ServerStartedEvent event){
        var server=event.getServer();if(!Boolean.getBoolean("th.pocketGameTest")||!(server instanceof GameTestServer)||server.getLevel(PocketDimension.KEY)!=null)return;
        LevelStorageSource.LevelStorageAccess storage=null;
        for(var field:MinecraftServer.class.getDeclaredFields())if(field.getType()==LevelStorageSource.LevelStorageAccess.class){try{if(!field.trySetAccessible())throw new IllegalStateException("GameTest storage inaccessible");storage=(LevelStorageSource.LevelStorageAccess)field.get(server);}catch(ReflectiveOperationException error){throw new IllegalStateException(error);}break;}
        if(storage==null)throw new IllegalStateException("GameTest storage not found");
        var registry=server.registryAccess();var type=registry.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(net.minecraft.resources.ResourceKey.create(Registries.DIMENSION_TYPE,ThaumicHorizons.id("pocket_plane")));var biome=registry.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.THE_VOID);var stem=new LevelStem(type,new PocketChunkGenerator(new FixedBiomeSource(biome)));
        var progress=new ChunkProgressListener(){public void updateSpawnPos(ChunkPos pos){}public void onStatusChange(ChunkPos pos,ChunkStatus status){}public void start(){}public void stop(){}};
        var data=new DerivedLevelData(server.getWorldData(),(ServerLevelData)server.overworld().getLevelData());var level=new ServerLevel(server,Util.backgroundExecutor(),storage,data,PocketDimension.KEY,stem,progress,false,BiomeManager.obfuscateSeed(server.overworld().getSeed()),List.of(),false,server.overworld().getRandomSequences());server.forgeGetWorldMap().put(PocketDimension.KEY,level);server.markWorldsDirty();NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
    }
}
