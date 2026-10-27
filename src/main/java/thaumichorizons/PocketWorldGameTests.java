package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class PocketWorldGameTests {
    @GameTest(template="test/platform") public static void pocketDimensionHasOriginalLightingHeightAndNoNaturalSpawns(GameTestHelper h){
        var level=h.getLevel().getServer().getLevel(PocketDimension.KEY);h.assertTrue(level!=null&&level.dimensionType().height()==256&&level.dimensionType().minY()==0&&!level.dimensionType().hasSkyLight()&&level.dimensionType().ambientLight()==.3F,"pocket dimension settings differ from original");var pos=new BlockPos(0,128,0);h.assertTrue(level.getChunkSource().getGenerator() instanceof PocketChunkGenerator&&level.getChunkSource().getGenerator().getMobsAt(level.getBiome(pos),level.structureManager(),MobCategory.MONSTER,pos).isEmpty(),"pocket world has natural spawning");h.succeed();
    }
    @GameTest(template="test/platform") public static void planeMetadataAndFourCrossDimensionGatewayLinksSurviveSave(GameTestHelper h){
        var data=new PocketPlanes();var origin=GlobalPos.of(Level.OVERWORLD,h.absolutePos(new BlockPos(3,3,3)));var plane=data.create("A named plane",new AspectList().add(Aspect.AIR,20),origin,711);plane.ready=true;plane.radius=64;plane.color=0x123456;plane.potionEffects=new int[]{1,3};for(int i=0;i<4;i++)plane.portals[i]=GlobalPos.of(i==0?Level.NETHER:Level.OVERWORLD,new BlockPos(i,64,i));var tag=data.save(new CompoundTag(),h.getLevel().registryAccess());var loaded=PocketPlanes.load(tag,h.getLevel().registryAccess());var restored=loaded.get(plane.id);h.assertTrue(restored!=null&&restored.ready&&restored.name.equals("A named plane")&&restored.seed==711&&restored.origin.equals(origin)&&restored.radius==64&&restored.color==0x123456&&restored.aspects.getAmount(Aspect.AIR)==20&&restored.portals[0].equals(plane.portals[0])&&restored.portals[3].equals(plane.portals[3]),"plane metadata or endpoint at coordinate zero was lost");h.assertTrue(loaded.create("Next",new AspectList(),origin,0).id==1,"plane identifier reused after save");h.succeed();
    }
    @GameTest(template="test/platform") public static void unfinishedGenerationReplaysFromSavedSeedInsteadOfSkippingUnsavedChunks(GameTestHelper h){
        var data=new PocketPlanes();var origin=GlobalPos.of(Level.OVERWORLD,h.absolutePos(new BlockPos(3,3,3)));var plane=data.create("Interrupted",new AspectList().add(Aspect.EARTH,40),origin,899);plane.cursor=100000;plane.biomeCursor=20;plane.extrasDone=true;var loaded=PocketPlanes.load(data.save(new CompoundTag(),h.getLevel().registryAccess()),h.getLevel().registryAccess());var restored=loaded.get(plane.id);h.assertTrue(!restored.ready&&!restored.failed&&restored.cursor==0&&restored.biomeCursor==0&&!restored.extrasDone&&restored.seed==899&&restored.origin.equals(origin)&&restored.aspects.getAmount(Aspect.EARTH)==40,"pending realm cannot replay safe generation after restart");h.succeed();
    }
    @GameTest(template="test/platform",timeoutTicks=1200) public static void pearlBuildsRealRealmAndReturnRiftTeleportsBothWays(GameTestHelper h){
        var source=h.getLevel();var server=source.getServer();var pos=h.absolutePos(new BlockPos(3,3,3));source.setBlockAndUpdate(pos,Vortex.BLOCK.get().defaultBlockState());var vortex=(Vortex)source.getBlockEntity(pos);vortex.cheat=true;vortex.count=50;vortex.ateDevices=true;vortex.aspects.add(Aspect.EARTH,20).add(Aspect.AIR,20);var player=thaumcraft.test.TestSupport.mockServerPlayer(h);player.setPos(pos.getCenter());var pearl=new ItemEntity(source,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,new ItemStack(thaumcraft.registry.EldritchContent.PRIMORDIAL_PEARL.get()));pearl.setThrower(player);source.addFreshEntity(pearl);vortex.consumeItems();h.assertTrue(pearl.isRemoved()&&vortex.pendingId>=0,"primordial pearl did not start realm creation");int id=vortex.pendingId;
        h.succeedWhen(()->{
            var plane=PocketPlanes.get(server).get(id);h.assertTrue(plane!=null&&!plane.failed&&plane.ready,"realm still generating or failed");Vortex.tick(source,pos,vortex.getBlockState(),vortex);var target=server.getLevel(PocketDimension.KEY);var root=new BlockPos(0,129,id*256);h.assertTrue(target.getBlockState(root.below()).is(thaumcraft.registry.ModBlocks.ARCANE_STONE.get())&&target.getBlockEntity(root) instanceof Vortex,"generated realm has no spawn pad/return rift");var exit=(Vortex)target.getBlockEntity(root);h.assertTrue(exit.cheat&&exit.createdDimension&&exit.planeId==id&&vortex.createdDimension&&vortex.planeId==id,"return link not saved into both rifts");player.setPortalCooldown(0);vortex.travel(player);h.assertTrue(player.level().dimension()==PocketDimension.KEY&&player.blockPosition().equals(root),"vortex did not teleport into generated realm");player.setPortalCooldown(0);exit.travel(player);h.assertTrue(player.level().dimension()==source.dimension()&&player.blockPosition().equals(pos),"return rift lost original dimension or coordinates");var reload=PocketPlanes.load(PocketPlanes.get(server).save(new CompoundTag(),source.registryAccess()),source.registryAccess());h.assertTrue(reload.get(id).ready&&reload.get(id).origin.equals(plane.origin),"generated realm lost link on data reload");player.discard();
        });
    }
}
