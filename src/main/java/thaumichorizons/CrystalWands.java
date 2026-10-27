package thaumichorizons;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import thaumcraft.wand.*;

public final class CrystalWands {
    public static final WandParts PARTS=new WandParts("disposable","disposable",false);
    static void init(){
        Content.ALL.put("crystal_wand",Content.ITEMS.registerSimpleItem("crystal_wand",new Item.Properties().stacksTo(1)));
        Content.ALL.put("disposable_wand",Content.ITEMS.registerItem("disposable_wand",properties->new Disposable(WandKeys.VIS.apply(WandKeys.PARTS.apply(properties,PARTS),new WandVis(25000,25000,25000,25000,25000,25000))),new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    }
    static void setup(){
        WandRegistry.registerRod("disposable",250,ResourceLocation.withDefaultNamespace("bedrock"),99,null,true,false,false,ThaumicHorizons.id("textures/models/crystalrod.png"));
        WandRegistry.registerCap("disposable",.9F,java.util.List.of(),.9F,ResourceLocation.withDefaultNamespace("bedrock"),99,null,ThaumicHorizons.id("textures/models/crystalcap.png"));
    }
    public static final class Disposable extends WandItem implements AddonWand {
        Disposable(Properties properties){super(properties);}
        @Override public int getVisCapacity(ItemStack stack){return 25000;}
        @Override public boolean canReceiveVis(ItemStack stack){return false;}
        @Override public Component getName(ItemStack stack){return Component.translatable("item.thaumichorizons.disposable_wand");}
    }
}
