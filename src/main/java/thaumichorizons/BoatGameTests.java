package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class BoatGameTests {
    @GameTest(template="test/platform") public static void thaumiumBoatCanBeStudiedWithoutBaseBoatResearch(GameTestHelper h){
        h.assertTrue(thaumcraft.research.ResearchCategories.getResearch("greatwoodBoat")==null,"redundant base-boat icon remains");
        var entry=thaumcraft.research.ResearchCategories.getResearch("thaumiumBoat");
        h.assertTrue(entry!=null&&entry.parents().isEmpty()&&!entry.isAutoUnlock(),"thaumium boat must be available to study, not granted automatically");
        var r=(thaumcraft.crafting.ShapedArcaneRecipe)thaumcraft.crafting.TcRecipes.get("TH:recipeThaumiumBoat");
        h.assertTrue(r.grid().get(4).test(new net.minecraft.world.item.ItemStack(thaumcraft.modern.ModernContent.BOATS.get("greatwood").get())),"upgrade no longer uses TC greatwood boat");h.succeed();
    }
    @GameTest(template="test/platform") public static void boatsKeepOwnItemAndFluidTypeOnReload(GameTestHelper h){
        for(var type:java.util.List.of(MagicBoats.GREATWOOD,MagicBoats.THAUMIUM)){var boat=type.get().create(h.getLevel());var saved=new CompoundTag();boat.save(saved);var loaded=(MagicBoats.LegacyBoat)EntityType.loadEntityRecursive(saved,h.getLevel(),e->e);h.assertTrue(loaded!=null&&loaded.getType()==boat.getType()&&loaded.getPickResult().is(boat.getDropItem()),"boat saved as vanilla type");h.assertTrue(loaded.canBoatInFluid(Blocks.WATER.defaultBlockState().getFluidState())&&loaded.canBoatInFluid(Blocks.LAVA.defaultBlockState().getFluidState())==boat.metal&&loaded.fireImmune()==boat.metal,"boat lost water/lava capability");}h.succeed();
    }
    @GameTest(template="test/platform") public static void thaumiumBoatProtectsModernPassengersFromLava(GameTestHelper h){
        var boat=MagicBoats.THAUMIUM.get().create(h.getLevel());boat.moveTo(h.absolutePos(new BlockPos(3,3,3)).getCenter());h.getLevel().addFreshEntity(boat);var rider=EntityType.PIG.create(h.getLevel());var second=EntityType.PIG.create(h.getLevel());rider.moveTo(boat.position());h.getLevel().addFreshEntity(rider);h.assertTrue(rider.startRiding(boat)&&second.startRiding(boat),"boat accepts wrong number of riders");rider.igniteForSeconds(20);boat.tick();h.assertTrue(!rider.isOnFire()&&rider.hasEffect(MobEffects.FIRE_RESISTANCE),"thaumium boat failed to protect rider");boat.ejectPassengers();rider.discard();boat.discard();h.succeed();
    }
    @GameTest(template="test/platform") public static void dispenserPlacesThaumiumBoatOnLavaAndConsumesOne(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Blocks.DISPENSER.defaultBlockState().setValue(net.minecraft.world.level.block.DispenserBlock.FACING,Direction.EAST));h.getLevel().setBlockAndUpdate(pos.east(),Blocks.LAVA.defaultBlockState());var item=new net.minecraft.world.item.ItemStack(Content.item("thaumium_boat"));var source=new net.minecraft.core.dispenser.BlockSource(h.getLevel(),pos,h.getLevel().getBlockState(pos),(net.minecraft.world.level.block.entity.DispenserBlockEntity)h.getLevel().getBlockEntity(pos));net.minecraft.world.level.block.DispenserBlock.DISPENSER_REGISTRY.get(item.getItem()).dispense(source,item);var boats=h.getLevel().getEntitiesOfClass(MagicBoats.LegacyBoat.class,new AABB(pos.east()).inflate(2));h.assertTrue(item.isEmpty()&&boats.size()==1&&boats.getFirst().metal,"dispenser did not launch boat into lava");boats.forEach(Entity::discard);h.succeed();
    }
    @GameTest(template="test/platform") public static void breakingBoatDropsItselfAndNeverVanillaBoat(GameTestHelper h){
        var boat=MagicBoats.GREATWOOD.get().create(h.getLevel());var pos=h.absolutePos(new BlockPos(3,3,3));boat.moveTo(pos.getCenter());h.getLevel().addFreshEntity(boat);boat.hurt(h.getLevel().damageSources().playerAttack(h.makeMockPlayer(GameType.SURVIVAL)),5);var drops=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(pos).inflate(2));h.assertTrue(boat.isRemoved()&&drops.size()==1&&drops.getFirst().getItem().is(thaumcraft.modern.ModernContent.BOATS.get("greatwood").get()),"breaking legacy boat did not migrate its item");h.succeed();
    }
}
