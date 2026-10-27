package thaumichorizons;

import net.minecraft.world.item.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;

/** Registers the injector and preserves the old crossbow registry ID for existing saves. */
public final class ThaumicCrossbow {
    static void init(){
        for(String id:java.util.List.of("injector","thaumic_crossbow"))Content.ALL.put(id,Content.ITEMS.registerItem(id,InjectorItem::new,new Item.Properties().durability(1000).rarity(Rarity.UNCOMMON).component(net.minecraft.core.component.DataComponents.CONTAINER,net.minecraft.world.item.component.ItemContainerContents.EMPTY)));
        thaumcraft.platform.ModNetwork.toServer(InjectorAction.TYPE,InjectorAction.CODEC,(action,p)->action.run(p));
    }
    static void research(){
        var cost=new AspectList().add(Aspect.WEAPON,8).add(Aspect.MECHANISM,8).add(Aspect.FLIGHT,8);
        TcRecipes.register(new InfusionRecipe("TH:recipeInjector","injector",TcStack.of("thaumichorizons:injector",1),2,cost,TcIngredient.of(TcStack.of("thaumcraft:bone_bow",1)),java.util.List.of(TcIngredient.of(TcStack.of("thaumcraft:arcane_bellows",1)),TcIngredient.of(TcStack.of("minecraft:feather",1)),TcIngredient.of(TcStack.of("thaumcraft:thaumium_ingot",1)),TcIngredient.of(TcStack.of("thaumcraft:air_shard",1)))));
        new ResearchItem("injector","ThaumicHorizons",new AspectList().add(Aspect.WEAPON,3).add(Aspect.MECHANISM,3).add(Aspect.FLIGHT,3),8,3,1,TcStack.of("thaumichorizons:injector",1)).setParents("blastPhial","BONEBOW").setConcealed().setPages(ResearchPage.text("injector1"),ResearchPage.infusion("TH:recipeInjector")).registerResearchItem();
    }
}
