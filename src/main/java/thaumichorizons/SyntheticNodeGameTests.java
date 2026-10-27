package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.vis.VisNet;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class SyntheticNodeGameTests {
    private static SyntheticNode node(GameTestHelper h,BlockPos relative){var pos=h.absolutePos(relative);h.getLevel().setBlockAndUpdate(pos,SyntheticNode.BLOCK.get().defaultBlockState());return (SyntheticNode)h.getLevel().getBlockEntity(pos);}
    @GameTest(template="test/platform") public static void etherealEssenceIncreasesCapacityAndReturnsOnBreak(GameTestHelper h){
        var node=node(h,new BlockPos(3,3,3));var essence=new ItemStack(thaumcraft.registry.ModItems.WISP_ESSENCE.get(),5);AspectKeys.ASPECTS.set(essence,new AspectList().add(Aspect.MAGIC,2));h.assertTrue(node.addEssence(essence)&&essence.isEmpty()&&node.getNodeVisBase(Aspect.MAGIC)==20&&node.getAspects().size()==0,"essence failed to add empty capacity");h.getLevel().destroyBlock(node.getBlockPos(),true);var drops=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(node.getBlockPos()).inflate(2));h.assertTrue(drops.stream().filter(e->e.getItem().is(thaumcraft.registry.ModItems.WISP_ESSENCE.get())).mapToInt(e->e.getItem().getCount()).sum()==5,"breaking shard duplicated or lost essence");h.succeed();
    }
    @GameTest(template="test/platform") public static void fractionalChargeAndStoredVisPersistWithoutOverfilling(GameTestHelper h){
        var node=node(h,new BlockPos(3,3,3));node.setAspects(new AspectList().add(Aspect.AIR,2));node.addFraction(Aspect.AIR,90);h.assertTrue(node.getAspects().size()==0,"partial vis rounded up");var saved=node.saveWithFullMetadata(h.getLevel().registryAccess());var loaded=new SyntheticNode(node.getBlockPos(),node.getBlockState());loaded.loadWithComponents(saved,h.getLevel().registryAccess());loaded.addFraction(Aspect.AIR,20);h.assertValueEqual(loaded.containerContains(Aspect.AIR),1,"fraction lost on save");h.assertTrue(loaded.addToContainer(Aspect.AIR,9)==8&&loaded.containerContains(Aspect.AIR)==2&&!loaded.addFraction(Aspect.AIR,10)&&!loaded.takeFromContainer(Aspect.AIR,3),"shard overfills or overdrafts");h.succeed();
    }
    @GameTest(template="test/platform") public static void syntheticShardRechargesPrimalAndCompoundThroughActualVisRelay(GameTestHelper h){
        var node=node(h,new BlockPos(3,3,3));node.setAspects(new AspectList().add(Aspect.AIR,4).add(Aspect.MAGIC,4));var sourcePos=node.getBlockPos().west(3);h.getLevel().setBlockAndUpdate(sourcePos,Content.VIS_DYNAMO.get().defaultBlockState());var source=(Dynamo)h.getLevel().getBlockEntity(sourcePos);source.ticks=100;source.provided=new AspectList().add(Aspect.AIR,20).add(Aspect.FIRE,20).add(Aspect.ENERGY,20).add(Aspect.ENTROPY,20).add(Aspect.ORDER,20);VisNet.addSource(h.getLevel(),source);
        var relayPos=node.getBlockPos().east(2);h.getLevel().setBlockAndUpdate(relayPos,thaumcraft.registry.ModBlocks.VIS_RELAY.get().defaultBlockState());var relay=(thaumcraft.vis.VisNode)h.getLevel().getBlockEntity(relayPos);relay.setParent(new java.lang.ref.WeakReference<>(source));source.getChildren().add(new java.lang.ref.WeakReference<>(relay));
        for(int i=0;i<11;i++){source.available=source.provided.copy();SyntheticNode.tick(h.getLevel(),node.getBlockPos(),node.getBlockState(),node);}h.assertTrue(node.containerContains(Aspect.AIR)==1&&node.containerContains(Aspect.MAGIC)==1,"real vis network failed to charge primal and compound");VisNet.removeSource(h.getLevel(),source);h.succeed();
    }
}
