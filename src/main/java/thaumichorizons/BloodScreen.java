package thaumichorizons;

import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.aspect.*;
import thaumcraft.client.aspect.AspectRenderer;

public final class BloodScreen extends AbstractContainerScreen<BloodMenu> {
    private static final net.minecraft.resources.ResourceLocation TEXTURE=ThaumicHorizons.id("textures/gui/guibloodinfuser.png");
    private int offset;
    private int topOut,bottomOut;
    private Aspect moused;
    private final GuiPulse pulse=new GuiPulse();
    public BloodScreen(BloodMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=219;}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){g.blit(TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight,256,256);}
    private List<Aspect> selected(){var list=new ArrayList<Aspect>();for(Aspect a:menu.tile.selected.getAspects())for(int i=0;i<menu.tile.selected.getAmount(a);i++)list.add(a);return list;}
    @Override protected void renderLabels(GuiGraphics g,int mouseX,int mouseY){
        var values=selected();AspectList used=new AspectList();
        for(int i=0;i<values.size()&&i<8;i++){
            Aspect a=values.get(i);used.add(a,1);AspectRenderer.drawTag(g,54+i%2*17,12+i/2*17,a,0,0,1,menu.tile.acquired.getAmount(a)<used.getAmount(a));
        }
        for(int i=0;i<16&&offset+i<menu.known.size();i++)AspectRenderer.drawTag(g,14+18*(i/2),83+(i%2==0?18:0),menu.known.get(offset+i),0,0,1,false);
        g.blit(TEXTURE,111,75,177,offset>0?136:144,24,8,256,256);
        g.blit(TEXTURE,135,75,201,offset+16<menu.known.size()?136:144,24,8,256,256);
        if(menu.tile.mode>0)g.blit(TEXTURE,38,menu.tile.mode==1?57:70,178,130,6,6,256,256);
        int hoverX=mouseX-leftPos-15,hoverY=mouseY-topPos-83;
        if(hoverX>=0&&hoverY>=0&&hoverX<=144&&hoverY<=34){int index=hoverX/18*2-(hoverY/18-1)+offset;if(index>=0&&index<menu.known.size())moused=menu.known.get(index);}
        else if(inside(mouseX-leftPos,mouseY-topPos,110,74,49,9))moused=null;
        bottomOut=moused==null?Math.max(2,bottomOut-2):Math.min(38,bottomOut+2);
        g.blit(TEXTURE,171,69,215-bottomOut,194,bottomOut,59,256,256);
        if(bottomOut==38&&moused!=null){var weights=BloodFormula.weights.get(moused);if(weights!=null){int i=0;for(var entry:weights.entrySet())if(entry.getKey()>=0){var icon=minecraft.getMobEffectTextures().get(BloodFormula.effect(entry.getKey()));int x=171+i%2*18,y=72+i/2*18;g.blit(x,y,0,18,18,icon);g.drawString(font,Integer.toString(entry.getValue()),x,y,0xFF808080,false);i++;}}}
        var effects=BloodFormula.calculate(menu.tile.selected).effects();
        topOut=effects.isEmpty()?Math.max(2,topOut-2):Math.min(38,topOut+2);
        g.blit(TEXTURE,171,25,215-topOut,152,topOut,41,256,256);
        if(topOut==38&&!effects.isEmpty()){
            for(int i=0;i<effects.size();i++){
                var effect=effects.get(i);var icon=minecraft.getMobEffectTextures().get(effect.getEffect());
                int x=171+i%2*18,y=28+i/2*18;g.blit(x,y,0,18,18,icon);g.drawString(font,Integer.toString(effect.getAmplifier()+1),x,y,0xFF808080,false);
            }
        }
        pulse.draw(g);
    }
    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partial){
        super.render(g,mouseX,mouseY,partial);renderTooltip(g,mouseX,mouseY);
        int x=mouseX-leftPos,y=mouseY-topPos;
        for(int i=0;i<16&&offset+i<menu.known.size();i++)if(inside(x,y,14+18*(i/2),83+(i%2==0?18:0),16,16)){
            Aspect a=menu.known.get(offset+i);var lines=new ArrayList<Component>();lines.add(Component.literal(a.getName()));lines.add(a.getDescription());
            var weights=BloodFormula.weights.get(a);if(weights!=null)for(var entry:weights.entrySet())lines.add(Component.translatable(BloodFormula.effect(entry.getKey()).value().getDescriptionId()).append(" "+entry.getValue()));
            g.renderComponentTooltip(font,lines,mouseX,mouseY);
        }
        if(inside(x,y,38,57,6,6))g.renderTooltip(font,Component.translatable("th.mode.once"),mouseX,mouseY);
        if(inside(x,y,38,70,6,6))g.renderTooltip(font,Component.translatable("th.mode.repeat"),mouseX,mouseY);
        var effects=BloodFormula.calculate(menu.tile.selected).effects();
        for(int i=0;topOut==38&&i<effects.size();i++)if(inside(x,y,171+i%2*18,28+i/2*18,18,18)){
            var e=effects.get(i);g.renderTooltip(font,Component.translatable(e.getEffect().value().getDescriptionId()).append(" "+(e.getAmplifier()+1)+" ("+(e.getDuration()/20.0)+" s)"),mouseX,mouseY);
        }
    }
    private static boolean inside(double x,double y,int left,int top,int w,int h){return x>=left&&y>=top&&x<left+w&&y<top+h;}
    private void button(int id){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,id);}
    @Override public boolean mouseClicked(double mouseX,double mouseY,int button){
        double x=mouseX-leftPos,y=mouseY-topPos;if(button!=0)return super.mouseClicked(mouseX,mouseY,button);
        if(inside(x,y,38,57,6,6)){button(menu.tile.mode==1?0:1);return true;}
        if(inside(x,y,38,70,6,6)){button(menu.tile.mode==2?0:2);return true;}
        if(inside(x,y,111,75,24,8)&&offset>0){offset-=2;return true;}
        if(inside(x,y,135,75,24,8)&&offset+16<menu.known.size()){offset+=2;return true;}
        for(int i=0;i<16&&offset+i<menu.known.size();i++)if(inside(x,y,14+18*(i/2),83+(i%2==0?18:0),16,16)){var aspect=menu.known.get(offset+i);if(selected().size()<8){button(3+BloodMenu.aspects().indexOf(aspect));pulse.start((int)x,(int)y,aspect.getColor());}return true;}
        var values=selected();for(int i=0;i<values.size()&&i<8;i++)if(inside(x,y,54+i%2*17,12+i/2*17,16,16)){var aspect=values.get(i);button(64+BloodMenu.aspects().indexOf(aspect));pulse.start((int)x,(int)y,aspect.getColor());return true;}
        return super.mouseClicked(mouseX,mouseY,button);
    }
}
