package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.node.NodeBlockEntity;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class RecombinatorGameTests {
    private static NodeBlockEntity node(GameTestHelper h,BlockPos pos,AspectList aspects){h.getLevel().setBlockAndUpdate(pos,thaumcraft.registry.ModBlocks.AURA_NODE.get().defaultBlockState());var node=(NodeBlockEntity)h.getLevel().getBlockEntity(pos);node.setAspects(aspects);return node;}
    @GameTest(template="test/platform") public static void recombinationConsumesRealComponentsAndAddsOneCompound(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,4,3));h.getLevel().setBlockAndUpdate(pos,Recombinator.BLOCK.get().defaultBlockState());var machine=(Recombinator)h.getLevel().getBlockEntity(pos);var target=node(h,pos.below(),new AspectList().add(Aspect.AIR,8));var donor=node(h,pos.east(2).below(),new AspectList().add(Aspect.FIRE,8));h.assertTrue(machine.merge(donor,target),"valid primal pair did not recombine");h.assertTrue(target.getNodeVisBase(Aspect.LIGHT)==1&&target.getAspects().getAmount(Aspect.LIGHT)==1&&target.getNodeVisBase(Aspect.AIR)==7&&target.getAspects().getAmount(Aspect.AIR)==7&&donor.getAspects().getAmount(Aspect.FIRE)==7,"merge duplicated components or lost new compound");h.succeed();
    }
    @GameTest(template="test/platform") public static void recombinationRejectsDrainedComponentsWithoutChangingCapacity(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,4,3));h.getLevel().setBlockAndUpdate(pos,Recombinator.BLOCK.get().defaultBlockState());var machine=(Recombinator)h.getLevel().getBlockEntity(pos);var target=node(h,pos.below(),new AspectList().add(Aspect.AIR,8));var donor=node(h,pos.east(2).below(),new AspectList().add(Aspect.FIRE,8));donor.takeFromContainer(Aspect.FIRE,8);h.assertTrue(!machine.merge(donor,target)&&target.getNodeVisBase(Aspect.AIR)==8&&target.getNodeVisBase(Aspect.LIGHT)==0,"empty donor created free aspects");h.succeed();
    }
    @GameTest(template="test/platform") public static void recombinatorRestoresRedstoneStateAndAnimationOnReload(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Recombinator.BLOCK.get().defaultBlockState());h.getLevel().setBlockAndUpdate(pos.east(),Blocks.REDSTONE_BLOCK.defaultBlockState());var machine=(Recombinator)h.getLevel().getBlockEntity(pos);for(int i=0;i<54;i++)Recombinator.tick(h.getLevel(),pos,machine.getBlockState(),machine);h.assertTrue(machine.active&&machine.count>50,"redstone did not start recombinator");var saved=machine.saveWithFullMetadata(h.getLevel().registryAccess());var loaded=new Recombinator(pos,machine.getBlockState());loaded.loadWithComponents(saved,h.getLevel().registryAccess());h.assertTrue(loaded.active&&loaded.count==machine.count,"recombinator lost active animation");h.getLevel().removeBlock(pos.east(),false);Recombinator.tick(h.getLevel(),pos,machine.getBlockState(),machine);h.assertTrue(!machine.active,"recombinator ignored redstone off");h.succeed();
    }
}
