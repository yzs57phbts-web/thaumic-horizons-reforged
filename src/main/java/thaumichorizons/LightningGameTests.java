package thaumichorizons;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class LightningGameTests {
    @GameTest(template="test/platform") public static void finiteLightningReloadPreservesLengthAndAnimationFlag(GameTestHelper h){var bolt=FiniteLightning.TYPE.get().create(h.getLevel());bolt.configure(17,true);var saved=new CompoundTag();bolt.save(saved);var loaded=(FiniteLightning)EntityType.loadEntityRecursive(saved,h.getLevel(),e->e);h.assertTrue(loaded!=null&&loaded.length()==17&&loaded.animates(),"lightning length/animation lost on reload");h.succeed();}
    @GameTest(template="test/platform") public static void animusLightningTurnsExactlyOneBlockIntoBerserkGolem(GameTestHelper h){var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());var bolt=FiniteLightning.TYPE.get().create(h.getLevel());bolt.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);bolt.configure(12,true);bolt.tick();bolt.tick();var golems=h.getLevel().getEntitiesOfClass(AnimatedGolem.class,new AABB(pos).inflate(2));h.assertTrue(golems.size()==1&&golems.getFirst().material().is(Blocks.STONE)&&golems.getFirst().berserk()&&golems.getFirst().lifetime==1200&&golems.getFirst().getOwnerUUID()==null,"animus strike lost block, doubled golem or retained owner");h.succeed();}
}
