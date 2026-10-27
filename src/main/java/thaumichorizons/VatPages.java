package thaumichorizons;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import thaumcraft.client.aspect.AspectRenderer;

public final class VatPages {
    static int rendered;
    private static final String[] CREATURE={"","quicksilverlimbs","thaumclaws","awakenedblood","diamondskin","enderheart","shockskin","instilledloyalty","runichide","eldritchfangs","portability"};
    private static final String[] SELF={"","quicksilver","morphic","awakeBlood","diamondSkin","silverHeart","synthSkin","amphibious","warpedTumor","spiderClimb","chameleonSkin"};
    private static final String[] CRITTER={"","quicksilver","thaumClaws","awakeBlood","diamondSkin","enderHeart","shockSkin","instilledLoyalty","runicHide","eldritchFangs","portability"};
    static String name(boolean self,int id){return self?"selfInfusions."+SELF[id]:"critterInfusions."+CRITTER[id];}
    public static void render(GuiGraphics g,String key,int x,int y,int mx,int my){
        rendered++;
        var recipe=VatRecipes.get(key);if(recipe==null)return;var mc=Minecraft.getInstance();var font=mc.font;
        centered(g,Component.translatable("recipe.type.infusion"),x+56,y,130);
        var overlay=net.minecraft.resources.ResourceLocation.parse("thaumcraft:textures/gui/gui_researchbook_overlay.png");
        thaumcraft.client.platform.GuiBlit.blit(g,overlay,x,y+20,112,34,0,3,56,17,256,256,0xFFFFFFFF);
        thaumcraft.client.platform.GuiBlit.blit(g,overlay,x,y+58,120,88,200,77,60,44,256,256,0xFFFFFFFF);
        String subject=recipe.transformation()?(recipe.input().equals("endersteed")?"horse":recipe.input()):recipe.self()?"human":"cat";
        g.blit(ThaumicHorizons.id("textures/items/"+subject+".png"),x+48,y+94,0,0,16,16,16,16);
        if(recipe.self()||recipe.transformation())g.blit(ThaumicHorizons.id("textures/items/"+subject+".png"),x+48,y+28,0,0,16,16,16,16);
        else g.blit(VatScreen.TEXTURE,x+48,y+28,(recipe.infusion()-1)*16,209,16,16,256,256);
        if(mx>=x+48&&mx<x+64&&my>=y+28&&my<y+44)g.renderTooltip(font,Component.translatable(recipe.transformation()?(recipe.output().equals("mooshroom")?"entity.minecraft.mooshroom":"entity.thaumichorizons."+recipe.output()):name(recipe.self(),recipe.infusion())),mx,my);
        int cycle=(int)(System.currentTimeMillis()/1000);
        for(int i=0;i<recipe.ingredients().size();i++){double a=2*Math.PI*i/recipe.ingredients().size();int px=x+48+(int)(40*Math.sin(a)),py=y+94-(int)(40*Math.cos(a));var choices=recipe.ingredients().get(i).displayStacks();if(choices.isEmpty())continue;var stack=choices.get(Math.floorMod(cycle,choices.size()));g.renderItem(stack,px,py);if(mx>=px&&mx<px+16&&my>=py&&my<py+16)g.renderTooltip(font,stack,mx,my);}
        var aspects=recipe.cost().getAspectsSorted();for(int i=0;i<aspects.size();i++){int px=x+8+(i%5)*20,py=y+164+(i/5)*18;AspectRenderer.drawTag(g,px,py,aspects.get(i),recipe.cost().getAmount(aspects.get(i)));if(mx>=px&&mx<px+16&&my>=py&&my<py+16)g.renderTooltip(font,Component.literal(aspects.get(i).getName()),mx,my);}
        centered(g,Component.translatable("tc.inst").append(" "+Component.translatable("tc.inst."+Math.min(5,recipe.instability()/2)).getString()),x+56,y+194,130);
    }
    private static void centered(GuiGraphics g,Component text,int x,int y,int max){var font=Minecraft.getInstance().font;float scale=Math.min(1,max/(float)Math.max(1,font.width(text)));g.pose().pushPose();g.pose().translate(x-font.width(text)*scale/2,y,0);g.pose().scale(scale,scale,1);g.drawString(font,text,0,0,0x505050,false);g.pose().popPose();}
}
