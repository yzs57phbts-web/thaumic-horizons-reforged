package thaumichorizons;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import thaumcraft.aspect.AspectList;

/** The original twenty creative infusion tools and hidden research-page icons. */
public final class CreativeTools {
    static final String[] CREATURE={"quicksilver","thaumClaws","awakeBlood","diamondSkin","enderHeart","shockSkin","instilledLoyalty","runicHide","eldritchFangs","portability"};
    static final String[] SELF={"quicksilver","morphic","awakeBlood","diamondSkin","silverHeart","synthSkin","amphibious","warpedTumor","spiderClimb","chameleonSkin"};
    static void init(){
        for(boolean self:new boolean[]{false,true})for(int id=1;id<=10;id++){String name=(self?"self_infusion_":"creature_infusion_")+id;final int value=id;Content.ALL.put(name,Content.ITEMS.registerItem(name,p->new InfusionTool(self,value,p),new Item.Properties()));}
        Content.ITEMS.registerItem("creature_icon",Dummy::new,new Item.Properties());Content.ITEMS.registerSimpleItem("vat_icon",new Item.Properties());
    }
    static final class InfusionTool extends Item {
        final boolean self;final int id;
        InfusionTool(boolean self,int id,Properties properties){super(properties);this.self=self;this.id=id;}
        @Override public Component getName(ItemStack stack){return Component.translatable((self?"selfInfusions.":"critterInfusions.")+(self?SELF:CREATURE)[id-1]);}
        @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
            if(level.isClientSide())return InteractionResultHolder.success(player.getItemInHand(hand));
            if(self)Infusions.add(player,id,null,new AspectList());
            else if(ThermalFoci.pointed(player,1.5) instanceof Mob target&&Vats.validSubject(target)){
                if(id!=7||!target.goalSelector.getAvailableGoals().isEmpty()){
                    if(Infusions.add(target,id,player.getUUID(),VatRecipes.get("creature_"+id).cost()))target.setPersistenceRequired();
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
    }
    static int dummyType(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("thSpecies");}
    static final class Dummy extends Item {
        Dummy(Properties properties){super(properties);}
        @Override public Component getName(ItemStack stack){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(tag.contains("infName"))return Component.translatable(tag.getString("infName"));return Component.translatable(switch(dummyType(stack)){case 0->"entity.minecraft.cow";case 1->"entity.minecraft.pig";case 2->"entity.minecraft.sheep";case 3->"entity.minecraft.chicken";case 5->"entity.minecraft.wolf";case 6->"entity.minecraft.horse";case 8->"entity.minecraft.spider";case 15->"thaumichorizons.revived";default->"entity.minecraft.cat";});}
    }
}
