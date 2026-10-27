package thaumichorizons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.platform.GuiBlit;

public final class LensCaseScreen extends AbstractContainerScreen<LensCaseMenu> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("thaumcraft:textures/gui/gui_focuspouch.png");
    public LensCaseScreen(LensCaseMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=176;imageHeight=233;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){GuiBlit.blit(graphics,TEXTURE,leftPos,topPos,0,0,imageWidth,imageHeight,256,256);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(menu.lockedSlot<9)GuiBlit.blit(graphics,TEXTURE,leftPos+8+menu.lockedSlot*18,topPos+209,240,0,16,16,256,256);renderTooltip(graphics,x,y);}
}
