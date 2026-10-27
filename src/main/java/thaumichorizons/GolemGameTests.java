package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.entity.golem.*;
import thaumcraft.wand.*;
import thaumcraft.aspect.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class GolemGameTests {
    @GameTest(template="test/platform") public static void animationPaysMaterialVisAndRestoresOneBlockAtExpiry(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);Wands.setFocus(wand,new ItemStack(Content.item("focus_animation")));player.setItemInHand(InteractionHand.MAIN_HAND,wand);var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());int before=Wands.getVis(wand,Aspect.AIR);
        var golem=AnimatedGolems.animate(h.getLevel(),pos,player,600,false,false,wand);h.assertTrue(golem!=null&&h.getLevel().isEmptyBlock(pos)&&golem.materialType()==GolemMaterial.ROCK&&golem.getMaxHealth()==30&&golem.getCarryLimit()==16,"stone animation has wrong block or stats");h.assertTrue(Wands.getVis(wand,Aspect.AIR)<before&&player.getUUID().equals(golem.getOwnerUUID()),"animation free or owner lost");golem.lifetime=1;golem.aiStep();h.assertTrue(golem.isRemoved()&&h.getLevel().getBlockState(pos).is(Blocks.STONE),"temporary golem did not restore source block");h.succeed();
    }
    @GameTest(template="test/platform") public static void animationRefusesTileEntitiesAndInsufficientVisWithoutLoss(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);player.setItemInHand(InteractionHand.MAIN_HAND,wand);var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());h.assertTrue(AnimatedGolems.animate(h.getLevel(),pos,player,600,false,false,wand)==null&&h.getLevel().getBlockState(pos).is(Blocks.STONE),"unpaid animation lost block");Wands.fill(wand);h.getLevel().setBlockAndUpdate(pos,Blocks.CHEST.defaultBlockState());h.assertTrue(AnimatedGolems.animate(h.getLevel(),pos,player,600,false,false,wand)==null&&h.getLevel().getBlockState(pos).is(Blocks.CHEST),"container was animated");h.succeed();
    }
    @GameTest(template="test/platform") public static void installingCoreStopsExpiryAndKeepsMaterialCapacity(GameTestHelper h){
        var golem=AnimatedGolems.TYPE.get().create(h.getLevel());golem.moveTo(h.absolutePos(new BlockPos(3,3,3)).getCenter());golem.initialize(Blocks.IRON_BLOCK.defaultBlockState(),20,false,false,null,GolemSnapshot.EMPTY);golem.configure(GolemType.WOOD,new GolemSnapshot(4,false,java.util.List.of(),"",java.util.List.of(),net.minecraft.world.item.component.ItemContainerContents.EMPTY,java.util.List.of(),0));for(int i=0;i<30;i++)golem.aiStep();h.assertTrue(!golem.isRemoved()&&golem.lifetime==20&&golem.getCarryLimit()==32&&golem.fireImmune()&&golem.getMaxHealth()==35,"core golem expires or uses wood stats");h.succeed();
    }
    @GameTest(template="test/platform") public static void golemItemRoundTripPreservesBlockCoreAndUpgrades(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var golem=AnimatedGolems.TYPE.get().create(h.getLevel());golem.moveTo(h.absolutePos(new BlockPos(3,3,3)).getCenter());var block=Blocks.OAK_LOG.defaultBlockState().setValue(RotatedPillarBlock.AXIS,Direction.Axis.X);var state=new GolemSnapshot(4,true,java.util.List.of(0,1,2),"H",java.util.List.of(),net.minecraft.world.item.component.ItemContainerContents.EMPTY,java.util.List.of(),0);golem.initialize(block,400,true,false,player.getUUID(),state);golem.setCustomName(Component.literal("Oak"));var item=golem.toItem(false);h.assertTrue(item.is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ThaumicHorizons.id("golem_placer"))),"pickup became ordinary wood golem");
        var saved=(CompoundTag)item.save(h.getLevel().registryAccess());item=ItemStack.parseOptional(h.getLevel().registryAccess(),saved);player.setItemInHand(InteractionHand.MAIN_HAND,item);var base=h.absolutePos(new BlockPos(5,2,5));h.getLevel().setBlockAndUpdate(base,Blocks.STONE.defaultBlockState());var context=new UseOnContext(player,InteractionHand.MAIN_HAND,new BlockHitResult(base.getCenter(),Direction.UP,base,false));item.getItem().useOn(context);var placed=h.getLevel().getEntitiesOfClass(AnimatedGolem.class,new AABB(base.above()).inflate(1)).getFirst();h.assertTrue(placed.material().equals(block)&&placed.getCore()==4&&placed.isAdvanced()&&placed.getUpgrade(2)==2&&placed.berserk()&&placed.getName().getString().equals("Oak")&&player.getMainHandItem().isEmpty(),"placement lost material, name, upgrade or consumed wrong count");placed.discard();h.succeed();
    }
    @GameTest(template="test/platform") public static void golemEntityReloadPreservesLifetimeAndMaterialAttributes(GameTestHelper h){
        var golem=AnimatedGolems.TYPE.get().create(h.getLevel());golem.initialize(Blocks.CLAY.defaultBlockState(),311,false,true,null,GolemSnapshot.EMPTY);var saved=new CompoundTag();golem.save(saved);var loaded=(AnimatedGolem)EntityType.loadEntityRecursive(saved,h.getLevel(),e->e);h.assertTrue(loaded!=null&&loaded.lifetime==311&&loaded.materialType()==GolemMaterial.CLAY&&loaded.explosive()&&loaded.getMaxHealth()==25&&loaded.getGolemStats().upgrades()==1,"saved golem lost material attributes or lifetime");h.succeed();
    }
    @GameTest(template="test/platform") public static void vanillaGolemStatsRemainUnchangedByAddonSeam(GameTestHelper h){
        for(var type:GolemType.values()){var golem=new GolemBase(thaumcraft.registry.ModEntities.GOLEM.get(),h.getLevel());golem.configure(type,GolemSnapshot.EMPTY);h.assertTrue(golem.getMaxHealth()==type.health&&golem.getCarryLimit()==type.carry&&golem.fireImmune()==type.fireResist&&golem.snapshot().upgrades().size()==type.upgrades,"vanilla golem changed: "+type);}h.succeed();
    }
    @GameTest(template="test/platform") public static void disposableWandSpendsVisButCannotRechargeFromAnyVisApi(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=new ItemStack(Content.item("disposable_wand"));h.assertTrue(Wands.isWand(wand)&&Wands.getMaxVis(wand)==25000,"disposable wand not recognized");for(var aspect:Aspect.getPrimalAspects())h.assertValueEqual(Wands.getVis(wand,aspect),25000,"initial primal charge");var item=(WandItem)wand.getItem();h.assertTrue(item.consumeAllVis(wand,player,new AspectList().add(Aspect.AIR,100),true,false),"disposable wand cannot cast");int left=Wands.getVis(wand,Aspect.AIR);h.assertTrue(left<25000&&Wands.getAspectsWithRoom(wand).isEmpty()&&Wands.addRealVis(wand,Aspect.AIR,100,true)==100&&Wands.getVis(wand,Aspect.AIR)==left,"disposable wand recharged");var saved=(CompoundTag)wand.save(h.getLevel().registryAccess());var restored=ItemStack.parseOptional(h.getLevel().registryAccess(),saved);h.assertValueEqual(Wands.getVis(restored,Aspect.AIR),left,"reload refilled disposable wand");h.succeed();
    }
    @GameTest(template="test/platform") public static void normalWandStillRechargesAndSmeltingBonusRecognizesMeat(GameTestHelper h){
        var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.storeVis(wand,Aspect.AIR,0);h.assertValueEqual(Wands.addRealVis(wand,Aspect.AIR,100,true),0,"normal wand rejected vis");h.assertValueEqual(Wands.getVis(wand,Aspect.AIR),100,"normal wand did not recharge");h.assertTrue(thaumcraft.device.SmeltingBonuses.get(new ItemStack(Content.item("meat")))==Content.item("meat_nugget"),"infernal furnace did not inherit original meat bonus");h.succeed();
    }
}
