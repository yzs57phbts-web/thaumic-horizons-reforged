package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.device.*;
import thaumcraft.platform.Baubles;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class MirrorAmuletGameTests {
    private static BlockPos mirror(GameTestHelper h){var pos=h.absolutePos(new BlockPos(5,3,5));h.getLevel().setBlockAndUpdate(pos.west(),Blocks.STONE.defaultBlockState());h.getLevel().setBlockAndUpdate(pos,thaumcraft.registry.ModBlocks.MAGIC_MIRROR.get().defaultBlockState().setValue(MirrorBlock.FACING,Direction.EAST));return pos;}
    @GameTest(template="test/platform") public static void mirrorAmuletLinksToWallMirrorAndPersistsFoil(GameTestHelper h){
        var pos=mirror(h);var player=h.makeMockPlayer(GameType.SURVIVAL);var stack=new ItemStack(Content.item("mirror_amulet"));player.setItemInHand(InteractionHand.MAIN_HAND,stack);var item=(MirrorAmulet)stack.getItem();item.onItemUseFirst(stack,new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(pos.getCenter(),Direction.EAST,pos,false)));h.assertTrue(MirrorKeys.LINK.get(stack).equals(GlobalPos.of(h.getLevel().dimension(),pos))&&item.isFoil(stack),"amulet failed to bind mirror");var saved=(net.minecraft.nbt.CompoundTag)stack.save(h.getLevel().registryAccess());var loaded=ItemStack.parseOptional(h.getLevel().registryAccess(),saved);h.assertTrue(MirrorKeys.LINK.get(loaded).equals(MirrorKeys.LINK.get(stack)),"amulet link lost on reload");h.succeed();
    }
    @GameTest(template="test/platform") public static void equippedMirrorAmuletRescuesInventoryAndReturnsOnePermanentPearl(GameTestHelper h){
        var pos=mirror(h);var player=h.makeMockPlayer(GameType.SURVIVAL);player.setPos(h.absolutePos(new BlockPos(1,3,1)).getCenter());var amulet=new ItemStack(Content.item("mirror_amulet"));MirrorKeys.LINK.set(amulet,GlobalPos.of(h.getLevel().dimension(),pos));Baubles.set(player,0,amulet);h.assertTrue(Baubles.get(player,0).is(Content.item("mirror_amulet")),"Curios necklace slot unavailable");player.getInventory().setItem(0,new ItemStack(Items.DIAMOND,7));player.getInventory().setItem(36,new ItemStack(Items.IRON_BOOTS));h.assertTrue(MirrorAmulet.rescue(player)&&Baubles.get(player,0).isEmpty()&&player.getInventory().getItem(0).isEmpty()&&player.getInventory().getItem(36).isEmpty(),"amulet failed to consume itself and rescue items");var drops=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2));h.assertTrue(drops.stream().filter(e->e.getItem().is(Items.DIAMOND)).mapToInt(e->e.getItem().getCount()).sum()==7&&drops.stream().filter(e->e.getItem().is(Items.IRON_BOOTS)).count()==1,"mirror duplicated or lost inventory");var pearls=h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,new AABB(player.blockPosition()).inflate(2));h.assertTrue(pearls.size()==1&&pearls.getFirst().getItem().is(thaumcraft.registry.EldritchContent.PRIMORDIAL_PEARL.get())&&!pearls.getFirst().hurt(h.getLevel().damageSources().lava(),100),"amulet did not return protected pearl");h.assertTrue(!MirrorAmulet.rescue(player),"consumed amulet worked twice");h.succeed();
    }
    @GameTest(template="test/platform") public static void brokenMirrorCannotConsumeAmuletOrLoseItems(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var amulet=new ItemStack(Content.item("mirror_amulet"));MirrorKeys.LINK.set(amulet,GlobalPos.of(h.getLevel().dimension(),h.absolutePos(new BlockPos(5,3,5))));Baubles.set(player,0,amulet);player.getInventory().setItem(0,new ItemStack(Items.DIAMOND,7));h.assertTrue(!MirrorAmulet.rescue(player)&&Baubles.get(player,0).getCount()==1&&player.getInventory().getItem(0).getCount()==7,"broken mirror consumed amulet or items");h.succeed();
    }
}
