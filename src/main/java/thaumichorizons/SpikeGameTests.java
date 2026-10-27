package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class SpikeGameTests {
    @GameTest(template="test/platform") public static void spikesMountAllSixWaysAndDropWhenSupportDisappears(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));for(var block:Spikes.BLOCKS.values())for(var facing:Direction.values()){
            var support=pos.relative(facing.getOpposite());h.getLevel().setBlockAndUpdate(support,Blocks.STONE.defaultBlockState());var state=block.get().defaultBlockState().setValue(Spikes.FACING,facing);h.assertTrue(state.canSurvive(h.getLevel(),pos),"spike rejected supported face "+facing);h.getLevel().setBlockAndUpdate(pos,state);var shape=state.getCollisionShape(h.getLevel(),pos,CollisionContext.empty()).bounds();h.assertTrue((shape.max(facing.getAxis())-shape.min(facing.getAxis()))==15/16D,"spike collision does not leave tip gap");h.getLevel().removeBlock(support,false);h.assertTrue(h.getLevel().isEmptyBlock(pos),"unsupported spike remained "+facing);
        }h.succeed();
    }
    @GameTest(template="test/platform") public static void allSpikeMaterialsDealOneCactusDamageAndReloadFacing(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos.below(),Blocks.STONE.defaultBlockState());for(var block:Spikes.BLOCKS.values()){
            var state=block.get().defaultBlockState().setValue(Spikes.FACING,Direction.UP);h.getLevel().setBlockAndUpdate(pos,state);var cow=EntityType.COW.create(h.getLevel());float before=cow.getHealth();block.get().entityInside(state,h.getLevel(),pos,cow);h.assertTrue(cow.getHealth()==before-1,"spike has wrong contact damage");var tile=h.getLevel().getBlockEntity(pos);var saved=tile.saveWithFullMetadata(h.getLevel().registryAccess());var loaded=new Spikes.SpikeEntity(pos,state);loaded.loadWithComponents(saved,h.getLevel().registryAccess());h.assertTrue(loaded.getBlockState().getValue(Spikes.FACING)==Direction.UP,"spike reload lost facing");
        }h.succeed();
    }
}
