package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class HorizonsGameTests {
    static BloodInfuser place(GameTestHelper h){var p=new BlockPos(2,2,2);h.setBlock(p,Content.INFUSER.get());return h.getBlockEntity(p);}
    static void tick(BloodInfuser be){BloodInfuser.tick(be.getLevel(),be.getBlockPos(),be.getBlockState(),be);}
    @GameTest(template="test/platform") public static void originalEffectWeightsAndDuration(GameTestHelper h){
        h.assertValueEqual(BloodFormula.weights.size(),48,"legacy aspect tables");
        var r=BloodFormula.calculate(new AspectList().add(Aspect.MINE,8));h.assertValueEqual(r.effects().size(),1,"single supported effect must not create invalid potion zero");
        var effect=r.effects().getFirst();h.assertTrue(effect.getEffect().equals(MobEffects.DIG_SPEED),"mining must produce haste");h.assertValueEqual(effect.getAmplifier(),2,"80 / 30 amplifier");h.assertValueEqual(effect.getDuration(),9600,"100 * 80 * 1.2 duration");h.assertValueEqual(r.color(),Aspect.MINE.getColor(),"pure aspect color");h.succeed();
    }
    @GameTest(template="test/platform") public static void mixedAspectFormulaMatchesOriginal(GameTestHelper h){
        var r=BloodFormula.calculate(new AspectList().add(Aspect.AIR,1).add(Aspect.FIRE,1));h.assertValueEqual(r.effects().size(),1,"one effect per two units rounded up");
        h.assertTrue(r.effects().getFirst().getEffect().equals(MobEffects.FIRE_RESISTANCE),"highest weighted effect");h.assertValueEqual(r.effects().getFirst().getDuration(),720,"original duration");
        h.assertTrue(BloodFormula.calculate(new AspectList()).effects().isEmpty(),"empty formula");h.succeed();
    }
    @GameTest(template="test/platform") public static void formulaLimitAndInputValidation(GameTestHelper h){
        var be=place(h);for(int i=0;i<8;i++)h.assertTrue(be.select(Aspect.AIR,true),"valid selection");h.assertTrue(!be.select(Aspect.FIRE,true),"ninth aspect accepted");
        h.assertTrue(be.inventory.insertItem(0,new ItemStack(Items.DIAMOND),false).is(Items.DIAMOND),"invalid input accepted");
        h.assertTrue(be.inventory.insertItem(1,new ItemStack(Content.item("blood_syringe")),false).is(Content.item("blood_syringe")),"output slot allowed input");h.succeed();
    }
    @GameTest(template="test/platform") public static void oneShotConsumesOneSyringeAndExactEssentia(GameTestHelper h){
        var be=place(h);be.select(Aspect.AIR,true);be.select(Aspect.FIRE,true);be.inventory.setStackInSlot(0,new ItemStack(Content.item("blood_syringe"),3));be.mode=1;
        h.assertValueEqual(be.addEssentia(Aspect.WATER,1,Direction.DOWN),0,"unrequested aspect consumed");h.assertValueEqual(be.addEssentia(Aspect.AIR,5,Direction.UP),0,"top port accepted essentia");
        h.assertValueEqual(be.addEssentia(Aspect.AIR,5,Direction.DOWN),1,"overfill consumed");h.assertValueEqual(be.addEssentia(Aspect.FIRE,1,Direction.NORTH),1,"second ingredient");tick(be);
        h.assertValueEqual(be.inventory.getStackInSlot(0).getCount(),2,"input count");h.assertTrue(be.inventory.getStackInSlot(1).is(Content.item("injection")),"output missing");h.assertValueEqual(be.mode,0,"one-shot did not pause");h.assertTrue(be.acquired.isEmpty(),"essentia not consumed");tick(be);h.assertTrue(be.inventory.getStackInSlot(2).isEmpty(),"extra output duplicated");h.succeed();
    }
    @GameTest(template="test/platform") public static void fullOutputsPreventEssentiaLoss(GameTestHelper h){
        var be=place(h);be.select(Aspect.MINE,true);be.inventory.setStackInSlot(0,new ItemStack(Content.item("blood_phial"),10));be.mode=2;
        for(int i=0;i<9;i++){h.assertValueEqual(be.addEssentia(Aspect.MINE,1,Direction.DOWN),1,"needed essentia rejected");tick(be);}
        h.assertValueEqual(be.inventory.getStackInSlot(0).getCount(),1,"nine outputs must consume nine inputs");h.assertValueEqual(be.addEssentia(Aspect.MINE,1,Direction.DOWN),0,"full output consumed essentia");
        for(int i=1;i<10;i++)h.assertTrue(be.inventory.getStackInSlot(i).is(Content.item("blast_phial")),"phial output type");tick(be);h.assertValueEqual(be.inventory.getStackInSlot(0).getCount(),1,"blocked machine consumed input");h.succeed();
    }
    @GameTest(template="test/platform") public static void essentiaPullWorksWithThaumcraftJar(GameTestHelper h){
        var be=place(h);h.setBlock(new BlockPos(2,1,2),thaumcraft.registry.ModBlocks.WARDED_JAR.get());thaumcraft.essentia.JarBlockEntity jar=h.getBlockEntity(new BlockPos(2,1,2));jar.addToContainer(Aspect.AIR,8);
        be.select(Aspect.AIR,true);be.select(Aspect.AIR,true);be.inventory.setStackInSlot(0,new ItemStack(Content.item("blood_syringe")));be.mode=1;
        tick(be);tick(be);tick(be);h.assertValueEqual(jar.getAmount(),6,"two essentia must leave jar");h.assertTrue(be.inventory.getStackInSlot(1).is(Content.item("injection")),"direct compatible transport failed");h.succeed();
    }
    @GameTest(template="test/platform") public static void persistentInventoryFormulaAndProgress(GameTestHelper h){
        var be=place(h);be.select(Aspect.AIR,true);be.select(Aspect.AIR,true);be.inventory.setStackInSlot(0,new ItemStack(Content.item("blood_syringe"),2));be.mode=2;be.addEssentia(Aspect.AIR,1,Direction.DOWN);
        be.inventory.setStackInSlot(4,BloodFormula.create(new AspectList().add(Aspect.FIRE,2),true));
        var saved=be.saveWithFullMetadata(h.getLevel().registryAccess());var restored=new BloodInfuser(be.getBlockPos(),be.getBlockState());restored.loadWithComponents(saved,h.getLevel().registryAccess());
        h.assertValueEqual(restored.mode,2,"mode lost");h.assertValueEqual(restored.selected.getAmount(Aspect.AIR),2,"formula lost");h.assertValueEqual(restored.acquired.getAmount(Aspect.AIR),1,"progress lost");h.assertValueEqual(restored.inventory.getStackInSlot(0).getCount(),2,"input lost");h.assertTrue(ItemStack.isSameItemSameComponents(be.inventory.getStackInSlot(4),restored.inventory.getStackInSlot(4)),"effect components lost");h.succeed();
    }
    @GameTest(template="test/platform") public static void automationCannotExtractBloodOrInsertOutput(GameTestHelper h){
        var be=place(h);var io=be.automation(Direction.DOWN);io.insertItem(0,new ItemStack(Content.item("blood_syringe")),false);
        h.assertTrue(io.extractItem(0,1,false).isEmpty(),"automation extracted input");h.assertTrue(!io.insertItem(1,new ItemStack(Items.DIAMOND),false).isEmpty(),"automation inserted output");
        be.inventory.setStackInSlot(1,BloodFormula.create(new AspectList().add(Aspect.AIR,1),false));h.assertTrue(io.extractItem(1,1,false).is(Content.item("injection")),"automation could not extract output");h.succeed();
    }
    @GameTest(template="test/platform") public static void menuRejectsUnknownAspectsAndInvalidButtons(GameTestHelper h){
        var be=place(h);var p=h.makeMockPlayer(GameType.SURVIVAL);p.setPos(be.getBlockPos().getCenter());var menu=new BloodMenu(7,p.getInventory(),be);Knowledge.get(p).discoverAspect(Aspect.AIR);
        h.assertTrue(menu.clickMenuButton(p,3+BloodMenu.aspects().indexOf(Aspect.AIR)),"known aspect rejected");
        h.assertTrue(!menu.clickMenuButton(p,3+BloodMenu.aspects().indexOf(Aspect.ELDRITCH)),"undiscovered aspect accepted");h.assertTrue(!menu.clickMenuButton(p,250),"invalid index accepted");
        h.assertTrue(menu.clickMenuButton(p,64+BloodMenu.aspects().indexOf(Aspect.AIR)),"remove selection");h.assertTrue(be.selected.isEmpty(),"selection not removed");h.succeed();
    }
    @GameTest(template="test/platform") public static void injectionAppliesEffectsAndIsConsumed(GameTestHelper h){
        Player p=h.makeMockPlayer(GameType.SURVIVAL);var stack=BloodFormula.create(new AspectList().add(Aspect.AIR,2),false);
        var result=stack.getItem().finishUsingItem(stack,h.getLevel(),p);h.assertTrue(result.isEmpty(),"legacy injection must be consumed entirely");
        h.assertTrue(p.hasEffect(MobEffects.JUMP),"formula effect not applied");h.succeed();
    }
    @GameTest(template="test/platform") public static void meleeInjectionAppliesToTarget(GameTestHelper h){
        var p=h.makeMockPlayer(GameType.SURVIVAL);var cow=EntityType.COW.create(h.getLevel());var stack=BloodFormula.create(new AspectList().add(Aspect.AIR,2),false);
        stack.getItem().hurtEnemy(stack,cow,p);h.assertTrue(cow.hasEffect(MobEffects.JUMP),"melee syringe did not inject target");h.assertTrue(stack.isEmpty(),"melee injection not consumed");h.succeed();
    }
    @GameTest(template="test/platform") public static void recipesAndResearchResolve(GameTestHelper h){
        var category=ResearchCategories.get("ThaumicHorizons");h.assertTrue(category!=null,"missing category");h.assertValueEqual(category.research().size(),(HorizonsConfig.ALTERNATE_BELL.get()?73:72)-(HorizonsConfig.POCKET_PLANES.get()?0:4),"research coverage after removing base-boat node");
        for(var entry:category.research()){
            if(entry.iconItem()!=null)h.assertTrue(entry.iconItem().exists(),"missing icon "+entry.key());
            for(String parent:entry.parents())h.assertTrue(ResearchCategories.getResearch(parent)!=null,"missing parent "+parent);
            for(String parent:entry.parentsHidden())h.assertTrue(ResearchCategories.getResearch(parent)!=null,"missing hidden parent "+parent);
        }
        int count=0;for(var recipe:TcRecipes.all())if(recipe.key().startsWith("TH:")){
            count++;h.assertTrue(ResearchCategories.getResearch(recipe.research())!=null,"missing research "+recipe.key());
            var ingredients=recipe instanceof InfusionRecipe r?java.util.stream.Stream.concat(java.util.stream.Stream.of(r.central()),r.components().stream()).toList():recipe instanceof ShapedArcaneRecipe r?r.grid():recipe instanceof ShapelessArcaneRecipe r?r.ingredients():recipe instanceof CrucibleRecipe r?List.of(r.catalyst()):List.<TcIngredient>of();
            for(var i:ingredients)h.assertTrue(i.exists(),"missing ingredient "+recipe.key()+" "+i);
            var output=recipe instanceof InfusionRecipe r?r.result():recipe instanceof ShapedArcaneRecipe r?r.result():recipe instanceof ShapelessArcaneRecipe r?r.result():recipe instanceof CrucibleRecipe r?r.result():null;
            if(output!=null)h.assertTrue(output.exists(),"missing result "+recipe.key());
        }
        h.assertValueEqual(count,(HorizonsConfig.ALTERNATE_BELL.get()?64:63)-(HorizonsConfig.POCKET_PLANES.get()?0:4),"recipe coverage");RuntimeAudit.export();h.succeed();
    }
    @GameTest(template="test/platform") public static void essentiaDynamoResetsBudgetEachTick(GameTestHelper h){
        var pos=new BlockPos(2,2,2);h.setBlock(pos,Content.ESSENTIA_DYNAMO.get());Dynamo be=h.getBlockEntity(pos);
        h.assertValueEqual(be.addEssentia(Aspect.LIFE,100,Direction.NORTH),0,"side port accepted fuel");h.assertValueEqual(be.addEssentia(Aspect.LIFE,100,Direction.DOWN),1,"fuel must consume exactly one unit");h.assertValueEqual(be.ticks,21,"legacy burn time");
        Dynamo.tick(h.getLevel(),be.getBlockPos(),be.getBlockState(),be);h.assertValueEqual(be.provided.visSize(),12,"12 centivis total per tick");h.assertValueEqual(be.consumeVis(Aspect.WATER,100),6,"water half of victus");h.assertValueEqual(be.consumeVis(Aspect.WATER,1),0,"per-tick budget exceeded");h.assertValueEqual(be.consumeVis(Aspect.EARTH,100),6,"earth half of victus");
        Dynamo.tick(h.getLevel(),be.getBlockPos(),be.getBlockState(),be);h.assertValueEqual(be.consumeVis(Aspect.WATER,100),6,"budget did not reset");h.succeed();
    }
    @GameTest(template="test/platform") public static void wandDynamoPaysOnlyEnabledAspects(GameTestHelper h){
        var pos=new BlockPos(2,2,2);h.setBlock(pos,Content.VIS_DYNAMO.get());Dynamo be=h.getBlockEntity(pos);
        var p=h.makeMockPlayer(GameType.SURVIVAL);ItemStack wand=thaumcraft.wand.Wands.create(thaumcraft.wand.WandRegistry.CAP_GOLD,thaumcraft.wand.WandRegistry.ROD_GREATWOOD,false);thaumcraft.wand.Wands.fill(wand);int before=thaumcraft.wand.Wands.getVis(wand,Aspect.AIR);be.enabled=1;
        h.assertTrue(be.charge(wand,p),"charged wand did not start dynamo");h.assertValueEqual(thaumcraft.wand.Wands.getVis(wand,Aspect.AIR),before-40,"40 centivis activation cost");h.assertValueEqual(thaumcraft.wand.Wands.getVis(wand,Aspect.FIRE),before,"disabled aspect consumed");h.assertValueEqual(be.consumeVis(Aspect.AIR,100),5,"5 centivis output");h.assertValueEqual(be.consumeVis(Aspect.AIR,1),0,"output duplication");h.assertValueEqual(be.consumeVis(Aspect.FIRE,10),0,"disabled aspect generated");h.succeed();
    }
    @GameTest(template="test/platform") public static void dynamosPowerExistingVisRelays(GameTestHelper h){
        var pos=new BlockPos(2,2,2);h.setBlock(pos,Content.ESSENTIA_DYNAMO.get());Dynamo be=h.getBlockEntity(pos);be.addEssentia(Aspect.AIR,1,Direction.DOWN);Dynamo.tick(h.getLevel(),be.getBlockPos(),be.getBlockState(),be);
        var relayPos=new BlockPos(4,2,2);h.setBlock(relayPos,thaumcraft.registry.ModBlocks.VIS_RELAY.get());thaumcraft.vis.VisRelayBlockEntity relay=h.getBlockEntity(relayPos);thaumcraft.vis.VisRelayBlockEntity.tick(h.getLevel(),relay.getBlockPos(),relay.getBlockState(),relay);
        h.assertTrue(relay.getParent()!=null&&relay.getParent().get()==be,"Thaumcraft relay did not discover source");h.assertValueEqual(relay.consumeVis(Aspect.AIR,5),5,"relay did not forward centivis");h.assertValueEqual(be.consumeVis(Aspect.AIR,20),7,"relay draw not charged to same budget");
        h.getLevel().destroyBlock(be.getBlockPos(),false);h.assertValueEqual(relay.consumeVis(Aspect.AIR,1),0,"destroyed dynamo still provides energy");h.succeed();
    }
}
