package thaumichorizons;
import java.util.*;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.AspectList;

final class PocketTerrain {
    static Plan generate(AspectList aspects,long seed,int id){if(id<0||id>117000)throw new IllegalArgumentException("Pocket plane outside world bounds");return PocketGenerator.generate(aspects.copy(),seed,id);}
    record Cell(TerrainBlock block,int metadata){BlockState state(){return block.state(metadata);}}
    static final class Plan {
        final int id,radius,width,zCenter;
        final long seed;
        final short[] voxels;
        final List<Cell> palette=new ArrayList<>();
        private final Map<Cell,Short> paletteIndex=new HashMap<>();
        final Long2ShortOpenHashMap extras=new Long2ShortOpenHashMap();
        final List<TerrainWorld.TerrainMob> creatures=new ArrayList<>();
        int color;
        int[] potionEffects;
        TerrainBiome biome;
        Plan(int id,int radius,long seed){this.id=id;this.radius=radius;this.seed=seed;width=radius*2+1;zCenter=id*256;voxels=new short[width*width*256];palette.add(new Cell(TerrainBlocks.air,0));paletteIndex.put(palette.getFirst(),(short)0);}
        private int index(int x,int y,int z){int px=x+radius,pz=z-zCenter+radius;return y<0||y>255||px<0||pz<0||px>=width||pz>=width?-1:(y*width+pz)*width+px;}
        Cell get(int x,int y,int z){int index=index(x,y,z);return palette.get(Short.toUnsignedInt(index<0?extras.get(BlockPos.asLong(x,y,z)):voxels[index]));}
        void set(int x,int y,int z,TerrainBlock block,int metadata){if(y<0||y>255||block==null)return;var cell=new Cell(block,metadata&15);short value=paletteIndex.computeIfAbsent(cell,key->{if(palette.size()>=65536)throw new IllegalStateException("Pocket terrain palette overflow");short next=(short)palette.size();palette.add(key);return next;});int index=index(x,y,z);if(index<0)extras.put(BlockPos.asLong(x,y,z),value);else voxels[index]=value;}
        BlockPos position(int index){return new BlockPos(index%width-radius,index/(width*width),index/width%width-radius+zCenter);}
        long solidCount(){long count=0;for(short voxel:voxels)if(voxel!=0)count++;for(short voxel:extras.values())if(voxel!=0)count++;return count;}
    }
}
