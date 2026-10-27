package thaumichorizons;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.*;
import thaumcraft.aspect.*;

@EventBusSubscriber(modid=ThaumicHorizons.ID)
public final class PocketPlanes extends SavedData {
    private static final Factory<PocketPlanes> FACTORY=new Factory<>(PocketPlanes::new,PocketPlanes::load);
    final Map<Integer,Plane> planes=new LinkedHashMap<>();
    private int nextId;
    public static PocketPlanes get(MinecraftServer server){return server.overworld().getDataStorage().computeIfAbsent(FACTORY,"thaumichorizons_pocket_planes");}
    Plane create(String name,AspectList aspects,GlobalPos origin,long seed){
        if(nextId>117000)throw new IllegalStateException("Pocket plane limit reached");var plane=new Plane(nextId++,name,aspects.copy(),origin,seed);planes.put(plane.id,plane);setDirty();return plane;
    }
    Plane get(int id){return planes.get(id);}
    Plane at(BlockPos pos){var plane=get(Math.floorDiv(pos.getZ()+128,256));return plane!=null&&plane.ready&&pos.distSqr(new BlockPos(0,128,plane.id*256))<=(double)plane.radius*plane.radius?plane:null;}
    @SubscribeEvent public static void serverTick(ServerTickEvent.Post event){
        var server=event.getServer();var level=server.getLevel(PocketDimension.KEY);if(level==null)return;var data=get(server);int active=0;
        for(var plane:data.planes.values())if(plane.work!=null&&!plane.work.isDone())active++;
        for(var plane:data.planes.values())if(!plane.ready&&!plane.failed&&plane.work==null&&active<2){plane.work=CompletableFuture.supplyAsync(()->PocketTerrain.generate(plane.aspects,plane.seed,plane.id));active++;}
        for(var plane:data.planes.values())if(!plane.ready&&!plane.failed&&plane.work!=null&&plane.work.isDone()){
            if(plane.plan==null)try{plane.plan=plane.work.join();}catch(RuntimeException failure){plane.failed=true;plane.work=null;data.setDirty();com.mojang.logging.LogUtils.getLogger().error("Cannot generate Thaumic Horizons pocket plane {}",plane.id,failure);continue;}
            data.apply(level,plane);break;
        }
    }
    private void apply(ServerLevel level,Plane plane){
        var plan=plane.plan;long stop=System.nanoTime()+8_000_000L;int writes=0,scanned=0;
        while(plane.cursor<plan.voxels.length&&writes<16384&&scanned<262144&&System.nanoTime()<stop){int index=plane.cursor++;short value=plan.voxels[index];scanned++;if(value==0)continue;var pos=plan.position(index);level.setBlock(pos,plan.palette.get(Short.toUnsignedInt(value)).state(),18);writes++;}
        if(plane.cursor<plan.voxels.length){setDirty();return;}
        if(!plane.extrasDone){for(var entry:plan.extras.long2ShortEntrySet())if(entry.getShortValue()!=0)level.setBlock(BlockPos.of(entry.getLongKey()),plan.palette.get(Short.toUnsignedInt(entry.getShortValue())).state(),18);plane.extrasDone=true;}
        int first=Math.floorDiv(-plan.radius,4)*4,count=Math.floorDiv(plan.radius,4)-Math.floorDiv(-plan.radius,4)+1;var biome=level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ResourceKey.create(Registries.BIOME,ResourceLocation.parse(plan.biome.key)));
        while(plane.biomeCursor<count*count&&System.nanoTime()<stop){int index=plane.biomeCursor++;var pos=new BlockPos(first+index%count*4,128,plan.zCenter+first+index/count*4);level.getChunkAt(pos);thaumcraft.platform.BiomeEdit.setColumn(level,pos,biome);}
        if(plane.biomeCursor<count*count){setDirty();return;}
        for(int i=0;i<plan.creatures.size();i++){
            var spawn=plan.creatures.get(i);var uuid=UUID.nameUUIDFromBytes((ThaumicHorizons.ID+"/plane/"+plane.id+"/"+plane.seed+"/"+i).getBytes(java.nio.charset.StandardCharsets.UTF_8));if(level.getEntity(uuid)!=null)continue;var type=BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(spawn.type)).orElseThrow();var entity=type.create(level);if(entity==null)continue;entity.setUUID(uuid);entity.moveTo(spawn.x,spawn.y,spawn.z,0,0);if(spawn.persistent&&entity instanceof Mob mob)mob.setPersistenceRequired();level.addFreshEntity(entity);
        }
        plane.radius=plan.radius;plane.color=plan.color;plane.potionEffects=plan.potionEffects.clone();plane.biome=plan.biome.key;Vortex.makeReturn(level,plane);plane.ready=true;plane.plan=null;plane.work=null;setDirty();
    }
    @SubscribeEvent public static void playerTick(PlayerTickEvent.Post event){
        if(!(event.getEntity() instanceof ServerPlayer player)||player.level().dimension()!=PocketDimension.KEY||player.tickCount%20!=0)return;var plane=get(player.server).at(player.blockPosition());if(plane==null)return;for(int id:plane.potionEffects){var effect=switch(id){case 1->MobEffects.MOVEMENT_SPEED;case 3->MobEffects.DIG_SPEED;case 5->MobEffects.DAMAGE_BOOST;case 8->MobEffects.JUMP;case 10->MobEffects.REGENERATION;case 11->MobEffects.DAMAGE_RESISTANCE;default->null;};if(effect!=null)player.addEffect(new MobEffectInstance(effect,60,0,true,false));}
    }
    static PocketPlanes load(CompoundTag tag,HolderLookup.Provider registry){
        var data=new PocketPlanes();data.nextId=Math.max(0,tag.getInt("nextId"));var list=tag.getList("planes",Tag.TAG_COMPOUND);for(int i=0;i<list.size();i++){
            var entry=list.getCompound(i);int id=entry.getInt("id");if(id<0||id>117000)continue;var plane=new Plane(id,entry.getString("name"),BloodInfuser.loadAspects(entry.getCompound("aspects")),readPos(entry.getCompound("origin")),entry.getLong("seed"));plane.ready=entry.getBoolean("ready");plane.failed=entry.getBoolean("failed");plane.radius=Math.clamp(entry.getInt("radius"),32,127);plane.color=entry.getInt("color");plane.potionEffects=entry.getIntArray("effects");plane.biome=entry.getString("biome");for(int p=0;p<4;p++)if(entry.contains("portal"+p))plane.portals[p]=readPos(entry.getCompound("portal"+p));data.planes.put(id,plane);data.nextId=Math.max(data.nextId,id+1);
        }return data;
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider registry){
        tag.putInt("nextId",nextId);var list=new ListTag();for(var plane:planes.values()){var entry=new CompoundTag();entry.putInt("id",plane.id);entry.putString("name",plane.name);entry.put("aspects",BloodInfuser.saveAspects(plane.aspects));entry.put("origin",savePos(plane.origin));entry.putLong("seed",plane.seed);entry.putBoolean("ready",plane.ready);entry.putBoolean("failed",plane.failed);entry.putInt("radius",plane.radius);entry.putInt("color",plane.color);entry.putIntArray("effects",plane.potionEffects);entry.putString("biome",plane.biome);for(int p=0;p<4;p++)if(plane.portals[p]!=null)entry.put("portal"+p,savePos(plane.portals[p]));list.add(entry);}tag.put("planes",list);return tag;
    }
    static CompoundTag savePos(GlobalPos pos){var tag=new CompoundTag();tag.putString("dimension",pos.dimension().location().toString());tag.putLong("pos",pos.pos().asLong());return tag;}
    static GlobalPos readPos(CompoundTag tag){var id=ResourceLocation.tryParse(tag.getString("dimension"));return GlobalPos.of(id==null?Level.OVERWORLD:ResourceKey.create(Registries.DIMENSION,id),BlockPos.of(tag.getLong("pos")));}
    static final class Plane {
        final int id;
        final String name;
        final AspectList aspects;
        final GlobalPos origin;
        final long seed;
        final GlobalPos[] portals=new GlobalPos[4];
        final boolean[] gatewayChanging=new boolean[4];
        int radius=32,color=0xFFFFFF;
        int[] potionEffects=new int[0];
        String biome="minecraft:the_void";
        boolean ready,failed,extrasDone;
        int cursor,biomeCursor;
        CompletableFuture<PocketTerrain.Plan> work;
        PocketTerrain.Plan plan;
        Plane(int id,String name,AspectList aspects,GlobalPos origin,long seed){this.id=id;this.name=name;this.aspects=aspects;this.origin=origin;this.seed=seed;}
    }
}
