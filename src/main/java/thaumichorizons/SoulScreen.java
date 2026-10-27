package thaumichorizons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class SoulScreen extends AbstractContainerScreen<SoulMenu> {
    public SoulScreen(SoulMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=menu.count==1?166:219;}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){
        boolean sieve=menu.count==1;var texture=ThaumicHorizons.id("textures/gui/"+(sieve?"guisieve":"guiinspiratron")+".png");g.blit(texture,leftPos,topPos,0,0,imageWidth,imageHeight);
        int height=sieve?Math.clamp(menu.tile.ticksLeft*39/1200,0,39):Math.clamp(menu.tile.progress*28/100,0,28);
        if(height>0){if(sieve)g.blit(texture,leftPos+91,topPos+58-height,176,166-height,35,height);else g.blit(texture,leftPos+66,topPos+102,176,158-height,44,height);}
    }
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);renderTooltip(g,x,y);}
}
