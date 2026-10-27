package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.node.NodeBlockEntity;
import thaumcraft.wand.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class DeviceGameTests {
    @GameTest(template="test/platform") public static void illuminationPlacesPaidColoredLightAndSolarUpgrade(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);var focus=Illumination.focus(4);Wands.setFocus(wand,focus);player.setItemInHand(InteractionHand.MAIN_HAND,wand);
        var pos=h.absolutePos(new BlockPos(2,2,2));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());var hit=new BlockHitResult(pos.getCenter(),Direction.UP,pos,false);int air=Wands.getVis(wand,Aspect.AIR),fire=Wands.getVis(wand,Aspect.FIRE);
        ((Illumination.Focus)focus.getItem()).onFocusRightClick(wand,h.getLevel(),player,hit);
        var light=h.getLevel().getBlockState(pos.above());h.assertTrue(light.is(Illumination.LIGHT.get())&&light.getValue(Illumination.COLOR)==4&&Wands.getVis(wand,Aspect.AIR)<air&&Wands.getVis(wand,Aspect.FIRE)<fire,"colored illumination was free or lost color");
        h.getLevel().removeBlock(pos.above(),false);((Illumination.Focus)focus.getItem()).applyUpgrade(focus,Illumination.Focus.SOLAR,3);Wands.setFocus(wand,focus);((Illumination.Focus)focus.getItem()).onFocusRightClick(wand,h.getLevel(),player,hit);
        h.assertTrue(h.getLevel().getBlockState(pos.above()).is(Illumination.SOLAR_LIGHT.get()),"solar upgrade placed ordinary light");var zombie=EntityType.ZOMBIE.create(h.getLevel());var cow=EntityType.COW.create(h.getLevel());
        Illumination.SOLAR_LIGHT.get().entityInside(h.getLevel().getBlockState(pos.above()),h.getLevel(),pos.above(),zombie);Illumination.SOLAR_LIGHT.get().entityInside(h.getLevel().getBlockState(pos.above()),h.getLevel(),pos.above(),cow);
        h.assertTrue(zombie.isOnFire()&&!cow.isOnFire(),"solar light burned living creature or spared undead");h.succeed();
    }
    @GameTest(template="test/platform") public static void illuminationRefusesBlockedTargetWithoutSpendingVis(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);var focus=Illumination.focus(11);Wands.setFocus(wand,focus);
        var pos=h.absolutePos(new BlockPos(2,2,2));h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());h.getLevel().setBlockAndUpdate(pos.above(),Blocks.STONE.defaultBlockState());int before=Wands.getVis(wand,Aspect.AIR);
        ((Illumination.Focus)focus.getItem()).onFocusRightClick(wand,h.getLevel(),player,new BlockHitResult(pos.getCenter(),Direction.UP,pos,false));h.assertValueEqual(Wands.getVis(wand,Aspect.AIR),before,"blocked light consumed vis");h.succeed();
    }
    @GameTest(template="test/platform") public static void illuminationDyeKeepsUpgradesAcrossAllColors(GameTestHelper h){
        var focus=Illumination.focus(11);((Illumination.Focus)focus.getItem()).applyUpgrade(focus,Illumination.Focus.SOLAR,3);var recipe=new Illumination.DyeRecipe(CraftingBookCategory.MISC);
        for(var color:DyeColor.values()){var input=CraftingInput.of(2,1,java.util.List.of(focus,new ItemStack(DyeItem.byColor(color))));var dyed=recipe.assemble(input,h.getLevel().registryAccess());h.assertTrue(Illumination.color(dyed)==15-color.getId()&&((Illumination.Focus)dyed.getItem()).getUpgradeLevel(dyed,Illumination.Focus.SOLAR)==1,"dye changed upgrade or wrong legacy color "+color);}
        h.succeed();
    }
    @GameTest(template="test/platform") public static void nodeMonitorSignalsCriticalVisFromAllSixFaces(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));h.getLevel().setBlockAndUpdate(pos,thaumcraft.registry.ModBlocks.AURA_NODE.get().defaultBlockState());var node=(NodeBlockEntity)h.getLevel().getBlockEntity(pos);
        for(Direction face:Direction.values()){
            node.setAspects(new AspectList().add(Aspect.AIR,20).add(Aspect.FIRE,10));var monitorPos=pos.relative(face);h.getLevel().setBlockAndUpdate(monitorPos,NodeMonitor.BLOCK.get().defaultBlockState().setValue(NodeMonitor.FACING,face));var monitor=(NodeMonitor.MonitorEntity)h.getLevel().getBlockEntity(monitorPos);
            NodeMonitor.MonitorEntity.tick(h.getLevel(),monitorPos,monitor.getBlockState(),monitor);h.assertTrue(!monitor.getBlockState().getValue(NodeMonitor.ACTIVE),"healthy node signalled alarm "+face);
            node.takeFromContainer(Aspect.AIR,19);NodeMonitor.MonitorEntity.tick(h.getLevel(),monitorPos,monitor.getBlockState(),monitor);h.assertValueEqual(h.getLevel().getBlockState(monitorPos).getSignal(h.getLevel(),monitorPos,Direction.NORTH),15,"critical node has no signal "+face);
            node.addToContainer(Aspect.AIR,3);NodeMonitor.MonitorEntity.tick(h.getLevel(),monitorPos,monitor.getBlockState(),monitor);h.assertTrue(!h.getLevel().getBlockState(monitorPos).getValue(NodeMonitor.ACTIVE),"refilled node did not clear signal "+face);
            h.getLevel().removeBlock(monitorPos,false);
        }h.succeed();
    }
}
