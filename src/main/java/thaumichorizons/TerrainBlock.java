package thaumichorizons;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
final class TerrainBlock {
    private final BlockState[] states;
    private final boolean opaque;
    TerrainBlock(boolean opaque,String... variants){this.opaque=opaque;states=new BlockState[variants.length];for(int i=0;i<variants.length;i++)states[i]=BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(variants[i])).orElseThrow(()->new IllegalStateException("Missing original pocket terrain block")).defaultBlockState();}
    BlockState state(int metadata){return states[metadata<states.length?metadata:0];}
    boolean isNormalCube(){return opaque;}
}
