package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.wand.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class AlchemyGameTests {
    static ItemStack wand(Player player,String focus){var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);Wands.setFocus(wand,new ItemStack(Content.item(focus)));player.setItemInHand(InteractionHand.MAIN_HAND,wand);return wand;}
    static int crystals(GameTestHelper h,BlockPos pos){return h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,new AABB(pos).inflate(2)).stream().mapToInt(e->e.getItem().getCount()).sum();}
    @GameTest(template="test/platform") public static void alchemiteCrystallisesBlockAspectsWithoutOrdinaryDrops(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Blocks.BOOKSHELF.defaultBlockState());var aspects=Alchemite.aspects(new ItemStack(Blocks.BOOKSHELF));h.assertTrue(!aspects.isEmpty(),"bookshelf has no test aspects");
        var explosion=new Alchemite.TransmutingExplosion(h.getLevel(),null,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,5);explosion.getToBlow().add(pos);explosion.finalizeExplosion(false);
        h.assertTrue(h.getLevel().isEmptyBlock(pos)&&crystals(h,pos)==aspects.visSize(),"explosion lost crystallised aspects");var drops=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2));h.assertTrue(drops.stream().noneMatch(e->e.getItem().is(Items.BOOK)||e.getItem().is(Blocks.BOOKSHELF.asItem())),"explosion duplicated ordinary drops");h.succeed();
    }
    @GameTest(template="test/platform") public static void primedAlchemitePersistsFuseAndChainReactionUsesOwnEntity(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(2,3,2));var primed=Alchemite.AlchemiteBlock.prime(h.getLevel(),pos,null,true);h.assertTrue(primed.getFuse()>=10&&primed.getFuse()<30,"wrong chain fuse");primed.setFuse(53);var nbt=new CompoundTag();primed.save(nbt);var restored=(Alchemite.Primed)EntityType.loadEntityRecursive(nbt,h.getLevel(),e->e);h.assertTrue(restored!=null&&restored.getFuse()==53&&restored.getBlockState().is(Alchemite.BLOCK.get()),"primed alchemite lost fuse or skin");h.succeed();
    }
    @GameTest(template="test/platform") public static void crystallisedDropsSurviveFireAndSecondaryExplosions(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(2,3,2));Alchemite.crystals(h.getLevel(),pos.getX(),pos.getY(),pos.getZ(),new AspectList().add(Aspect.FIRE,130),1);var drops=h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,new AABB(pos).inflate(2));h.assertTrue(drops.size()==3&&drops.stream().mapToInt(e->e.getItem().getCount()).sum()==130,"crystals overstack or disappear");for(var drop:drops){h.assertTrue(!drop.hurt(h.getLevel().damageSources().lava(),100)&&!drop.isRemoved(),"crystal destroyed by heat");h.assertTrue(AspectKeys.ASPECTS.get(drop.getItem()).getAmount(Aspect.FIRE)==1,"wrong crystal unit value");}h.succeed();
    }
    @GameTest(template="test/platform") public static void liquefactionMeltsStoneBeforeVanillaSmoothStoneRecipe(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=wand(player,"focus_liquefaction");var focus=(ThermalFoci.Focus)Content.item("focus_liquefaction");var pos=h.absolutePos(new BlockPos(2,3,2));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());int before=Wands.getVis(wand,Aspect.FIRE);
        h.assertTrue(focus.processBlock(player,wand,pos)&&h.getLevel().getBlockState(pos).is(Blocks.LAVA)&&Wands.getVis(wand,Aspect.FIRE)<before,"stone did not melt to paid lava");h.succeed();
    }
    @GameTest(template="test/platform") public static void liquefactionSmeltsWholeItemStackWithOutputMultiplier(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=wand(player,"focus_liquefaction");var pos=h.absolutePos(new BlockPos(2,3,2));var entity=new ItemEntity(h.getLevel(),pos.getX(),pos.getY(),pos.getZ(),new ItemStack(Items.RAW_IRON,7));h.getLevel().addFreshEntity(entity);
        h.assertTrue(((ThermalFoci.Focus)Content.item("focus_liquefaction")).processItem(player,wand,entity)&&entity.getItem().is(Items.IRON_INGOT)&&entity.getItem().getCount()==7,"item stack smelting lost output");h.succeed();
    }
    @GameTest(template="test/platform") public static void liquefactionPurityUsesNativeClustersAndCannotMeltBedrock(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=wand(player,"focus_liquefaction");var stack=Wands.getFocusItem(wand);var focus=(ThermalFoci.Focus)stack.getItem();focus.applyUpgrade(stack,ThermalFoci.Focus.PURITY,5);Wands.setFocus(wand,stack);var pos=h.absolutePos(new BlockPos(2,3,2));h.getLevel().setBlockAndUpdate(pos,Blocks.IRON_ORE.defaultBlockState());
        h.assertTrue(focus.processBlock(player,wand,pos),"ore smelting failed");var drops=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2));h.assertTrue(drops.stream().filter(e->e.getItem().is(Items.IRON_INGOT)).mapToInt(e->e.getItem().getCount()).sum()>=2,"purity did not double native ore result");
        h.getLevel().setBlockAndUpdate(pos,Blocks.BEDROCK.defaultBlockState());int before=Wands.getVis(wand,Aspect.FIRE);h.assertTrue(!focus.processBlock(player,wand,pos)&&Wands.getVis(wand,Aspect.FIRE)==before,"bedrock melted or consumed vis");h.succeed();
    }
    @GameTest(template="test/platform") public static void disintegrationCrystallisesBatchAndRejectsExistingCrystals(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=wand(player,"focus_disintegration");var focus=(ThermalFoci.Focus)Content.item("focus_disintegration");var pos=h.absolutePos(new BlockPos(2,3,2));var source=new ItemStack(Items.DIAMOND,3);var aspects=Alchemite.aspects(source);var item=new ItemEntity(h.getLevel(),pos.getX(),pos.getY(),pos.getZ(),source);h.getLevel().addFreshEntity(item);
        h.assertTrue(focus.processItem(player,wand,item)&&item.isRemoved()&&crystals(h,pos)==aspects.visSize()*3,"disintegration lost batch aspects");var crystal=h.getLevel().getEntitiesOfClass(Alchemite.InvulnerableDrop.class,new AABB(pos).inflate(2)).getFirst();int before=Wands.getVis(wand,Aspect.EARTH);h.assertTrue(!focus.processItem(player,wand,crystal)&&Wands.getVis(wand,Aspect.EARTH)==before,"crystals recursively disintegrate");h.succeed();
    }
    @GameTest(template="test/platform") public static void disintegrationRefusesUnbreakableBlocksWithoutSpending(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=wand(player,"focus_disintegration");var pos=h.absolutePos(new BlockPos(2,3,2));h.getLevel().setBlockAndUpdate(pos,Blocks.BEDROCK.defaultBlockState());int before=Wands.getVis(wand,Aspect.EARTH);
        h.assertTrue(!((ThermalFoci.Focus)Content.item("focus_disintegration")).processBlock(player,wand,pos)&&h.getLevel().getBlockState(pos).is(Blocks.BEDROCK)&&before==Wands.getVis(wand,Aspect.EARTH),"unbreakable disintegration consumed vis");h.succeed();
    }
}
