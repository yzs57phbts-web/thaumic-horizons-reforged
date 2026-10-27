package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.research.Knowledge;
import thaumcraft.wand.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class CloudGameTests {
    private static Clouds.CloudEntity cloud(GameTestHelper h,String type){var pos=h.absolutePos(new BlockPos(3,7,3));h.getLevel().setBlockAndUpdate(pos.below(4),Blocks.STONE.defaultBlockState());h.getLevel().setBlockAndUpdate(pos,Clouds.BLOCKS.get(type).get().defaultBlockState());var cloud=(Clouds.CloudEntity)h.getLevel().getBlockEntity(pos);cloud.findFloor();return cloud;}
    @GameTest(template="test/platform") public static void allTenCloudsHaveOriginalCollisionLightAndSavedTimers(GameTestHelper h){
        for(var name:Clouds.NAMES){var cloud=cloud(h,name);var state=cloud.getBlockState();h.assertTrue(state.getCollisionShape(h.getLevel(),cloud.getBlockPos()).isEmpty()&&state.getLightEmission(h.getLevel(),cloud.getBlockPos())==(Clouds.glowing(cloud.kind())?15:0),"cloud has wrong collision/light "+name);cloud.raining=true;cloud.dropTimer=37;var tag=cloud.saveWithFullMetadata(h.getLevel().registryAccess());var restored=new Clouds.CloudEntity(cloud.getBlockPos(),state);restored.loadWithComponents(tag,h.getLevel().registryAccess());h.assertTrue(restored.raining&&restored.dropTimer==37&&restored.distance==4&&restored.kind()==cloud.kind(),"cloud timer or type lost on reload "+name);}h.succeed();
    }
    @GameTest(template="test/platform") public static void normalCloudHydratesFarmlandFillsCauldronAndExtinguishesFire(GameTestHelper h){
        var cloud=cloud(h,"rain");var pos=cloud.floor();h.getLevel().setBlockAndUpdate(pos,Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE,0));cloud.weatherFloor();h.assertValueEqual(h.getLevel().getBlockState(pos).getValue(FarmBlock.MOISTURE),7,"rain did not hydrate farmland");h.getLevel().setBlockAndUpdate(pos,Blocks.CAULDRON.defaultBlockState());cloud.weatherFloor();h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.WATER_CAULDRON)&&h.getLevel().getBlockState(pos).getValue(LayeredCauldronBlock.LEVEL)==3,"rain did not fill cauldron");h.getLevel().setBlockAndUpdate(pos,Blocks.NETHERRACK.defaultBlockState());h.getLevel().setBlockAndUpdate(pos.above(),Blocks.FIRE.defaultBlockState());cloud.weatherFloor();h.assertTrue(h.getLevel().isEmptyBlock(pos.above()),"rain failed to extinguish fire");h.succeed();
    }
    @GameTest(template="test/platform") public static void hotAndAcidCloudsAffectTerrainAndLivingCreatures(GameTestHelper h){
        var hot=cloud(h,"fire");var pos=hot.floor();h.getLevel().setBlockAndUpdate(pos,Blocks.ICE.defaultBlockState());hot.weatherFloor();h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.WATER),"hot rain did not melt ice");var cow=EntityType.COW.create(h.getLevel());cow.setPos(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5);h.getLevel().addFreshEntity(cow);hot.weatherEntities();h.assertTrue(cow.isOnFire(),"hot rain did not burn creature");var acid=cloud(h,"acid");h.getLevel().setBlockAndUpdate(acid.floor(),Blocks.OAK_LOG.defaultBlockState());acid.weatherFloor();h.assertTrue(h.getLevel().isEmptyBlock(acid.floor()),"acid did not dissolve flammable material");float before=cow.getHealth();acid.weatherEntities();h.assertTrue(cow.getHealth()<before,"acid rain did not hurt creature");h.succeed();
    }
    @GameTest(template="test/platform") public static void productiveCloudsDropProtectedItemsVisAndExperience(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,7,3));for(String name:java.util.List.of("alloy","flesh","vis","glyph")){var cloud=cloud(h,name);for(int i=0;i<5;i++)cloud.produce();}var area=new AABB(pos).inflate(2);var drops=h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,area);h.assertTrue(drops.stream().mapToInt(e->e.getItem().getCount()).sum()==10&&drops.stream().allMatch(e->e.getAge()>=0&&!e.hurt(h.getLevel().damageSources().lava(),1)),"cloud drops missing, permanent or vulnerable");h.assertValueEqual(h.getLevel().getEntitiesOfClass(thaumcraft.entity.AspectOrb.class,area).size(),5,"vis cloud did not produce five vis orbs");h.assertValueEqual(h.getLevel().getEntitiesOfClass(ExperienceOrb.class,area).size(),5,"glyph cloud did not produce experience");h.succeed();
    }
    @GameTest(template="test/platform") public static void sporeCloudChangesCowsAndGrowsPlantsAboveValidSoil(GameTestHelper h){
        var cloud=cloud(h,"spore");var pos=cloud.floor();h.getLevel().setBlockAndUpdate(pos,Blocks.DIRT.defaultBlockState());cloud.grow();h.assertTrue(h.getLevel().getBlockState(pos).is(Blocks.GRASS_BLOCK)||h.getLevel().getBlockState(pos).is(Blocks.MYCELIUM),"spores failed to colonize dirt");var cow=EntityType.COW.create(h.getLevel());cow.setPos(pos.getX()+.5,pos.getY()+1,pos.getZ()+.5);cow.setHealth(6);h.getLevel().addFreshEntity(cow);cloud.weatherEntities();var mushrooms=h.getLevel().getEntitiesOfClass(MushroomCow.class,new AABB(pos).inflate(2));h.assertTrue(cow.isRemoved()&&mushrooms.size()==1&&mushrooms.getFirst().getHealth()==6,"spores lost cow or health on conversion");h.succeed();
    }
    @GameTest(template="test/platform") public static void thunderAndAnimusCloudsCreateBoundedLightning(GameTestHelper h){
        for(String name:java.util.List.of("thunder","animus")){var cloud=cloud(h,name);cloud.produce();var bolts=h.getLevel().getEntitiesOfClass(FiniteLightning.class,new AABB(cloud.floor()).inflate(1));h.assertTrue(bolts.stream().anyMatch(bolt->bolt.length()==4&&bolt.animates()==name.equals("animus")),"wrong finite lightning type "+name);}h.succeed();
    }
    @GameTest(template="test/platform") public static void learnedCloudHarvestPaysAirAndReturnsExactlyOneProtectedCloud(GameTestHelper h){
        var cloud=cloud(h,"vis");var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);player.setItemInHand(InteractionHand.MAIN_HAND,wand);var block=(Clouds.CloudBlock)cloud.getBlockState().getBlock();h.assertTrue(block.onWandUseOn(h.getLevel(),cloud.getBlockPos(),player,wand,Direction.UP)==InteractionResult.PASS,"unresearched cloud harvest succeeded");Knowledge.get(player).addResearch("planarClouds");int before=Wands.getVis(wand,Aspect.AIR);h.assertTrue(block.onWandUseOn(h.getLevel(),cloud.getBlockPos(),player,wand,Direction.UP)==InteractionResult.SUCCESS&&h.getLevel().isEmptyBlock(cloud.getBlockPos())&&Wands.getVis(wand,Aspect.AIR)<before,"cloud harvest free or not removed");var drops=h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,new AABB(cloud.getBlockPos()).inflate(2));h.assertTrue(drops.size()==1&&drops.getFirst().getItem().is(block.asItem()),"cloud harvest duplicated or lost block");h.succeed();
    }
}
