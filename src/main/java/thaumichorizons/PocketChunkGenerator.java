package thaumichorizons;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.*;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
final class PocketChunkGenerator extends ChunkGenerator {
    static final MapCodec<PocketChunkGenerator> CODEC=BiomeSource.CODEC.fieldOf("biome_source").xmap(PocketChunkGenerator::new,generator->generator.biomeSource).stable();
    PocketChunkGenerator(BiomeSource biomeSource){super(biomeSource);}
    @Override protected MapCodec<? extends ChunkGenerator> codec(){return CODEC;}
    @Override public void spawnOriginalMobs(WorldGenRegion region){}
    @Override public void applyBiomeDecoration(WorldGenLevel level,ChunkAccess chunk,StructureManager structures){}
    @Override public WeightedRandomList<MobSpawnSettings.SpawnerData> getMobsAt(Holder<Biome> biome,StructureManager structures,MobCategory category,BlockPos pos){return WeightedRandomList.create();}
    @Override public int getGenDepth(){return 256;}
    @Override public int getMinY(){return 0;}
    @Override public int getSeaLevel(){return 0;}
    @Override public int getSpawnHeight(LevelHeightAccessor height){return 128;}
    @Override public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender,RandomState state,StructureManager structures,ChunkAccess chunk){return CompletableFuture.completedFuture(chunk);}
    @Override public void applyCarvers(WorldGenRegion region,long seed,RandomState state,BiomeManager biomes,StructureManager structures,ChunkAccess chunk,GenerationStep.Carving step){}
    @Override public void buildSurface(WorldGenRegion region,StructureManager structures,RandomState state,ChunkAccess chunk){}
    @Override public int getBaseHeight(int x,int z,Heightmap.Types type,LevelHeightAccessor height,RandomState state){return height.getMinBuildHeight();}
    @Override public NoiseColumn getBaseColumn(int x,int z,LevelHeightAccessor height,RandomState state){var blocks=new BlockState[height.getHeight()];java.util.Arrays.fill(blocks,Blocks.AIR.defaultBlockState());return new NoiseColumn(height.getMinBuildHeight(),blocks);}
    @Override public void addDebugScreenInfo(List<String> result,RandomState state,BlockPos feet){}
}
