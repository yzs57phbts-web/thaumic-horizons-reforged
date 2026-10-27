package thaumichorizons;
import java.util.Random;

/** A planning buffer for the original generator; it never reads or changes a live world. */
final class TerrainWorld {
    final PocketTerrain.Plan plan;
    final Random rand;
    TerrainWorld(PocketTerrain.Plan plan,long seed){this.plan=plan;rand=new Random(seed);}
    void setBlock(int x,int y,int z,TerrainBlock block,int metadata){plan.set(x,y,z,block,metadata);}
    TerrainBlock getBlock(int x,int y,int z){return plan.get(x,y,z).block();}
    boolean isAirBlock(int x,int y,int z){return getBlock(x,y,z)==TerrainBlocks.air;}
    void spawnEntityInWorld(TerrainMob entity){plan.creatures.add(entity);}
    void setBiome(TerrainBiome biome){plan.biome=biome;}
    static final class TerrainMob {
        final String type;
        double x,y,z;
        boolean persistent;
        TerrainMob(String type){this.type=type;}
        void setPosition(double x,double y,double z){this.x=x;this.y=y;this.z=z;}
        void func_110163_bv(){persistent=true;}
    }
}
