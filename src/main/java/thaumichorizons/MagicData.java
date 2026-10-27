package thaumichorizons;
import java.util.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;
/** Recipe quantities, research positions and page order converted from the pinned upstream. */
public final class MagicData {
    public static void init(){
        ResearchCategories.registerCategory("ThaumicHorizons",ThaumicHorizons.id("textures/misc/vat.png"),ResourceLocation.parse("thaumcraft:textures/gui/gui_researchback.png"));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlcheponicsWheat","alcheponics",TcStack.of("minecraft:wheat",2),TcIngredient.of(TcStack.of("minecraft:wheat_seeds",1)),(new AspectList()).add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlcheponicsCarrot","alcheponics",TcStack.of("minecraft:carrot",2),TcIngredient.of(TcStack.of("minecraft:carrot",1)),(new AspectList()).add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlcheponicsPotato","alcheponics",TcStack.of("minecraft:potato",2),TcIngredient.of(TcStack.of("minecraft:potato",1)),(new AspectList()).add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlcheponicsMelon","alcheponics",TcStack.of("minecraft:melon",1),TcIngredient.of(TcStack.of("minecraft:melon_seeds",1)),(new AspectList()).add(Aspect.LIGHT, 3).add(Aspect.EARTH, 3).add(Aspect.WATER, 3)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlcheponicsPumpkin","alcheponics",TcStack.of("minecraft:pumpkin",1),TcIngredient.of(TcStack.of("minecraft:pumpkin_seeds",1)),(new AspectList()).add(Aspect.LIGHT, 3).add(Aspect.EARTH, 3).add(Aspect.WATER, 3)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeAlchIncubation","alchIncubation",TcStack.of("thaumichorizons:incubated_egg",1),TcIngredient.of(TcStack.of("minecraft:egg",1)),(new AspectList()).add(Aspect.FIRE, 2).add(Aspect.LIFE, 2)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeMeatGrowthPork","meatGrowth",TcStack.of("minecraft:porkchop",2),TcIngredient.of(TcStack.of("minecraft:porkchop",1)),(new AspectList()).add(Aspect.LIFE, 3)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeMeatGrowthBeef","meatGrowth",TcStack.of("minecraft:beef",2),TcIngredient.of(TcStack.of("minecraft:beef",1)),(new AspectList()).add(Aspect.LIFE, 3)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeMeatGrowthChicken","meatGrowth",TcStack.of("minecraft:chicken",2),TcIngredient.of(TcStack.of("minecraft:chicken",1)),(new AspectList()).add(Aspect.LIFE, 3)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeLeatherBeef","fleshLeather",TcStack.of("minecraft:leather",1),TcIngredient.of(TcStack.of("minecraft:beef",1)),(new AspectList()).add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeLeatherPork","fleshLeather",TcStack.of("minecraft:leather",1),TcIngredient.of(TcStack.of("minecraft:porkchop",1)),(new AspectList()).add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:recipeLeatherZombie","fleshLeather",TcStack.of("minecraft:leather",1),TcIngredient.of(TcStack.of("minecraft:rotten_flesh",1)),(new AspectList()).add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1).add(Aspect.ORDER, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransFire","shardTrans",TcStack.of("thaumcraft:fire_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.FIRE, 2).add(Aspect.EXCHANGE, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransWater","shardTrans",TcStack.of("thaumcraft:water_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.WATER, 2).add(Aspect.EXCHANGE, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransAir","shardTrans",TcStack.of("thaumcraft:air_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.AIR, 2).add(Aspect.EXCHANGE, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransEarth","shardTrans",TcStack.of("thaumcraft:earth_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.EARTH, 2).add(Aspect.EXCHANGE, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransOrder","shardTrans",TcStack.of("thaumcraft:order_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 1)));
        TcRecipes.register(new CrucibleRecipe("TH:shardTransEntropy","shardTrans",TcStack.of("thaumcraft:entropy_shard",1),TcIngredient.of(TcStack.of("thaumcraft:balanced_shard",1)),(new AspectList()).add(Aspect.ENTROPY, 2).add(Aspect.EXCHANGE, 1)));
        arcane("recipeSyringe","essentiaInjection",TcStack.of("thaumichorizons:empty_syringe",1),(new AspectList()).add(Aspect.WATER, 1).add(Aspect.ORDER, 1),new String[]{"P","N"},'P',TcIngredient.of(TcStack.of("thaumcraft:glass_phial",1)),'N',TcIngredient.tag("c:nuggets/iron"));
        TcRecipes.register(new ShapelessArcaneRecipe("TH:recipeBlastPhial","blastPhial",TcStack.of("thaumichorizons:blood_phial",4),(new AspectList()).add(Aspect.FIRE, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1),List.of(TcIngredient.of(TcStack.of("thaumcraft:glass_phial",1)),TcIngredient.of(TcStack.of("thaumcraft:glass_phial",1)),TcIngredient.of(TcStack.of("minecraft:gunpowder",1)),TcIngredient.of(TcStack.of("thaumcraft:glass_phial",1)),TcIngredient.of(TcStack.of("thaumcraft:glass_phial",1)),TcIngredient.of(TcStack.of("thaumichorizons:blood_syringe",1)))));
        TcRecipes.register(new InfusionRecipe("TH:recipeVisDynamo","visDynamo",TcStack.of("thaumichorizons:vis_dynamo",1),5,(new AspectList()).add(Aspect.AURA, 4).add(Aspect.ENERGY, 16).add(Aspect.EXCHANGE, 16) .add(Aspect.MECHANISM, 16),TcIngredient.of(TcStack.of("minecraft:piston",1)),List.of(TcIngredient.of(TcStack.of("thaumcraft:primal_charm",1)),TcIngredient.of(TcStack.of("minecraft:gold_ingot",1)),TcIngredient.of(TcStack.of("thaumcraft:greatwood_planks",1)),TcIngredient.of(TcStack.of("minecraft:gold_ingot",1)))));
        TcRecipes.register(new InfusionRecipe("TH:recipeEssentiaDynamo","essentiaDynamo",TcStack.of("thaumichorizons:essentia_dynamo",1),5,(new AspectList()).add(Aspect.AURA, 4).add(Aspect.ENERGY, 16).add(Aspect.EXCHANGE, 16) .add(Aspect.MECHANISM, 16).add(Aspect.WATER, 32),TcIngredient.of(TcStack.of("minecraft:piston",1)),List.of(TcIngredient.of(TcStack.of("thaumcraft:thaumium_ingot",1)),TcIngredient.of(TcStack.of("thaumcraft:alchemical_construct",1)),TcIngredient.of(TcStack.of("thaumcraft:greatwood_planks",1)),TcIngredient.of(TcStack.of("thaumcraft:thaumium_ingot",1)),TcIngredient.of(TcStack.of("thaumcraft:alchemical_centrifuge",1)))));
        TcRecipes.register(new InfusionRecipe("TH:recipeBloodInfuser","essentiaInjection",TcStack.of("thaumichorizons:blood_infuser",1),4,(new AspectList()).add(Aspect.MAN, 8).add(Aspect.EXCHANGE, 8).add(Aspect.LIFE, 16) .add(Aspect.MAGIC, 16),TcIngredient.of(TcStack.of("thaumcraft:crucible",1)),List.of(TcIngredient.of(TcStack.of("minecraft:comparator",1)),TcIngredient.of(TcStack.of("thaumcraft:alchemical_construct",1)),TcIngredient.of(TcStack.of("thaumcraft:essentia_buffer",1)),TcIngredient.of(TcStack.of("thaumcraft:alchemical_construct",1)))));
        new ResearchItem("alcheponics","ThaumicHorizons",(new AspectList()).add(Aspect.CROP, 5).add(Aspect.PLANT, 5).add(Aspect.LIGHT, 5),0,0,1,TcStack.of("minecraft:wheat",1)).setPages(ResearchPage.text("alcheponics1"),ResearchPage.crucible("TH:recipeAlcheponicsWheat"),ResearchPage.text("alcheponics2"),ResearchPage.crucible("TH:recipeAlcheponicsCarrot"),ResearchPage.crucible("TH:recipeAlcheponicsPotato"),ResearchPage.crucible("TH:recipeAlcheponicsMelon"),ResearchPage.crucible("TH:recipeAlcheponicsPumpkin")).registerResearchItem();
        new ResearchItem("alchIncubation","ThaumicHorizons",(new AspectList()).add(Aspect.BEAST, 4).add(Aspect.LIFE, 2).add(Aspect.FIRE, 2),2,0,1,TcStack.of("thaumichorizons:incubated_egg",1)).setParents("alcheponics").setSecondary().setPages(ResearchPage.text("alchIncubation1"),ResearchPage.crucible("TH:recipeAlchIncubation")).registerResearchItem();
        new ResearchItem("meatGrowth","ThaumicHorizons",(new AspectList()).add(Aspect.FLESH, 6).add(Aspect.LIFE, 4).add(Aspect.BEAST, 4).add(Aspect.CRAFT, 4),4,0,2,TcStack.of("minecraft:beef",1)).setParents("alchIncubation").setPages(ResearchPage.text("meatGrowth1"),ResearchPage.crucible("TH:recipeMeatGrowthPork","TH:recipeMeatGrowthBeef","TH:recipeMeatGrowthChicken")).registerResearchItem();
        new ResearchItem("fleshLeather","ThaumicHorizons",(new AspectList()).add(Aspect.FLESH, 4).add(Aspect.CLOTH, 6).add(Aspect.CRAFT, 4),6,-1,1,TcStack.of("minecraft:leather",1)).setParents("meatGrowth").setSecondary().setPages(ResearchPage.text("fleshLeather1"),ResearchPage.crucible("TH:recipeLeatherBeef","TH:recipeLeatherPork","TH:recipeLeatherZombie")).registerResearchItem();
        new ResearchItem("shardTrans","ThaumicHorizons",(new AspectList()).add(Aspect.EXCHANGE, 4).add(Aspect.CRYSTAL, 4).add(Aspect.FIRE, 2) .add(Aspect.WATER, 2).add(Aspect.EARTH, 2).add(Aspect.AIR, 2).add(Aspect.ORDER, 2) .add(Aspect.ENTROPY, 2),1,2,2,TcStack.of("thaumcraft:balanced_shard",1)).setSecondary().setPages(ResearchPage.text("shardTrans1"),ResearchPage.crucible("TH:shardTransFire","TH:shardTransWater","TH:shardTransAir","TH:shardTransEarth","TH:shardTransOrder","TH:shardTransEntropy")).registerResearchItem();
        new ResearchItem("visDynamo","ThaumicHorizons",(new AspectList()).add(Aspect.AURA, 5).add(Aspect.MECHANISM, 5).add(Aspect.ENERGY, 5),3,2,2,TcStack.of("thaumichorizons:vis_dynamo",1)).setParentsHidden("INFUSION","VISPOWER").setPages(ResearchPage.text("visDynamo1"),ResearchPage.infusion("TH:recipeVisDynamo")).registerResearchItem();
        new ResearchItem("essentiaDynamo","ThaumicHorizons",(new AspectList()).add(Aspect.AURA, 5).add(Aspect.MECHANISM, 5).add(Aspect.ENERGY, 5) .add(Aspect.WATER, 5),3,4,2,TcStack.of("thaumichorizons:essentia_dynamo",1)).setParents("visDynamo").setParentsHidden("INFUSION","VISPOWER","THAUMIUM").setSecondary().setConcealed().setPages(ResearchPage.text("essentiaDynamo1"),ResearchPage.infusion("TH:recipeEssentiaDynamo"),ResearchPage.text("essentiaDynamo2")).registerResearchItem();
        new ResearchItem("essentiaInjection","ThaumicHorizons",(new AspectList()).add(Aspect.LIFE, 4).add(Aspect.MAN, 2).add(Aspect.EXCHANGE, 2).add(Aspect.MAGIC, 2),6,1,3,TcStack.of("thaumichorizons:blood_syringe",1)).setParents("meatGrowth").setParentsHidden("THAUMATORIUM","INFUSION").setWarp(1).setPages(ResearchPage.text("essentiaInjection1"),ResearchPage.arcane("TH:recipeSyringe"),ResearchPage.text("essentiaInjection2"),ResearchPage.infusion("TH:recipeBloodInfuser"),ResearchPage.text("essentiaInjection3"),ResearchPage.text("essentiaInjection4")).registerResearchItem();
        new ResearchItem("blastPhial","ThaumicHorizons",(new AspectList()).add(Aspect.LIFE, 2).add(Aspect.FIRE, 2).add(Aspect.ENTROPY, 2),6,3,1,TcStack.of("thaumichorizons:blood_phial",1)).setParents("essentiaInjection").setSecondary().setConcealed().setPages(ResearchPage.text("blastPhial1"),ResearchPage.arcane("TH:recipeBlastPhial")).registerResearchItem();
        ThaumicCrossbow.research();
        Illumination.research();
        NodeMonitor.research();
        Alchemite.research();
        ThermalFoci.research();
        AnimatedGolems.research();
        MagicBoats.research();
        SyntheticNode.research();
        Recombinator.research();
        MirrorAmulet.research();
        LensResearch.register();
        RiftResearch.register();
    }
    static void arcane(String key,String research,TcStack result,AspectList cost,String[] pattern,Object... symbols){
        var grid=new ArrayList<TcIngredient>();
        for(String row:pattern)for(char c:row.toCharArray()){
            TcIngredient item=TcIngredient.EMPTY;
            if(c!=' ')for(int n=0;n<symbols.length;n+=2)if((Character)symbols[n]==c)item=(TcIngredient)symbols[n+1];
            grid.add(item);
        }
        TcRecipes.register(new ShapedArcaneRecipe("TH:"+key,research,result,cost,pattern[0].length(),pattern.length,grid));
    }
}
