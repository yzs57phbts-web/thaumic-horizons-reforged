package thaumichorizons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public final class InjectorScreen extends AbstractContainerScreen<InjectorMenu>{
    public InjectorScreen(InjectorMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=175;imageHeight=191;}
    @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){g.blit(ThaumicHorizons.id("textures/gui/guiinjector.png"),leftPos,topPos,0,0,imageWidth,imageHeight);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);renderTooltip(g,x,y);}
}
