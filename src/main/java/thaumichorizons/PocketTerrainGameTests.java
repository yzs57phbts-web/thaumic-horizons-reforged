package thaumichorizons;
import java.util.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumichorizons.legacy.NoiseGeneratorOctaves;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class PocketTerrainGameTests {
    @GameTest(template="test/platform") public static void legacyNoiseMatchesOriginalReferenceInTwoAndThreeDimensions(GameTestHelper h){
        long[] seeds={0,1,42,-1};int[] xs={0,-97,1680,-30000000},zs={0,388,33,29999997},flatHash={744153731,-1099133040,322219518,-2083029680},volumeHash={1449461206,-757175603,552096804,-763047931};
        for(int i=0;i<seeds.length;i++){var generator=new NoiseGeneratorOctaves(new Random(seeds[i]),10);var flat=generator.generateNoiseOctaves(null,xs[i],128,zs[i],3,1,5,2,1,2);var volume=generator.generateNoiseOctaves(null,xs[i],64,zs[i],3,4,5,.4,.6,.8);h.assertTrue(Arrays.hashCode(flat)==flatHash[i]&&Arrays.hashCode(volume)==volumeHash[i],"legacy noise diverged from 1.7.10 reference for seed "+seeds[i]);}h.succeed();
    }
    @GameTest(template="test/platform") public static void originalLayersReserveSpawnPadAndUnbreakableSphere(GameTestHelper h){
        var plan=PocketTerrain.generate(new AspectList().add(Aspect.EARTH,32).add(Aspect.ORDER,32),42,0);h.assertTrue(plan.radius==48&&plan.biome==TerrainBiome.forest&&plan.solidCount()>10000,"wrong plane size, biome or empty terrain");for(int x=-2;x<=2;x++)for(int z=-2;z<=2;z++)h.assertTrue(plan.get(x,128,z).state().is(thaumcraft.registry.ModBlocks.ARCANE_STONE.get())&&plan.get(x,129,z).block()==TerrainBlocks.air&&plan.get(x,130,z).block()==TerrainBlocks.air,"spawn pad obstructed");
        h.assertTrue(plan.get(48,128,0).block()==TerrainBlocks.blockVoid&&plan.get(-48,128,0).block()==TerrainBlocks.blockVoid&&plan.get(0,128,48).block()==TerrainBlocks.blockVoid&&plan.get(0,128,-48).block()==TerrainBlocks.blockVoid,"sphere shell has axial holes");h.assertTrue(plan.get(10,128,0).state().is(Blocks.DIRT)&&plan.get(10,122,0).state().is(Blocks.STONE),"original earth/stone layers missing");h.succeed();
    }
    @GameTest(template="test/platform") public static void generationIsRepeatableAndPlaneCoordinatesRemainSeparated(GameTestHelper h){
        var aspects=new AspectList().add(Aspect.AIR,12).add(Aspect.WATER,12).add(Aspect.METAL,8).add(Aspect.CRYSTAL,8);var first=PocketTerrain.generate(aspects,721,3);var again=PocketTerrain.generate(aspects,721,3);h.assertTrue(Arrays.equals(first.voxels,again.voxels)&&first.palette.equals(again.palette)&&first.extras.equals(again.extras),"saved seed cannot reproduce generation");h.assertTrue(first.zCenter==768&&first.position(0).getZ()==768-first.radius&&first.position(first.voxels.length-1).getZ()<1024-32,"planes overlap or coordinate transform wrong");for(var cell:first.palette)h.assertTrue(cell.state()!=null,"terrain contains unmapped block variant");h.succeed();
    }
    @GameTest(template="test/platform",timeoutTicks=400) public static void largestAndEmptyPlanesStayBoundedAndKeepCreatureCap(GameTestHelper h){
        var aspects=new AspectList().add(Aspect.WATER,32).add(Aspect.LIFE,100).add(Aspect.BEAST,100).add(Aspect.MOTION,1).add(Aspect.HEAL,1).add(Aspect.TOOL,1).add(Aspect.WEAPON,1).add(Aspect.ARMOR,1);var plan=PocketTerrain.generate(aspects,861,0);h.assertTrue(plan.radius==127&&plan.width==255&&plan.creatures.size()<=100&&plan.creatures.size()>0,"maximum radius or creature limit wrong");var effects=Arrays.stream(plan.potionEffects).boxed().toList();h.assertTrue(effects.containsAll(List.of(1,10,3,5,11)),"original aspect effects missing");var empty=PocketTerrain.generate(new AspectList(),0,1);h.assertTrue(empty.radius==32&&empty.solidCount()>0&&empty.get(0,129,256).block()==TerrainBlocks.air,"empty-aspect plane invalid or spawn obstructed");h.succeed();
    }
}
