package thaumichorizons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class VatScreen extends AbstractContainerScreen<VatMenu> {
    static final net.minecraft.resources.ResourceLocation TEXTURE=ThaumicHorizons.id("textures/gui/guivat.png");
    public VatScreen(VatMenu menu,Inventory inventory,Component title){super(menu,inventory,title);imageWidth=176;imageHeight=209;}
    @Override protected void renderBg(GuiGraphics g,float partial,int mx,int my){
        g.blit(TEXTURE,leftPos,topPos,0,0,176,209,256,256);if(!menu.cloning)g.blit(TEXTURE,leftPos+58,topPos+30,176,163,57,20,256,256);
        int health=menu.value(2),max=menu.value(4);if(health>0)for(int i=0;i<Math.min(27,max/2);i++){int x=leftPos+56+7*(i%9),y=topPos+12+7*(i/9);g.blit(TEXTURE,x,y,176,health>=2*i+2?126:120,7,6,256,256);if(health==2*i+1)g.blit(TEXTURE,x,y,176,126,4,6,256,256);}
        for(int i=0;i<12;i++){int id=menu.value(i+5);if(id>0&&id<=10)g.blit(TEXTURE,leftPos+55+16*(i%4),topPos+56+17*(i/4),(id-1)*16,menu.value(17)==1?225:209,16,16,256,256);}
    }
    @Override protected void renderLabels(GuiGraphics g,int x,int y){}
    @Override public void render(GuiGraphics g,int mx,int my,float partial){super.render(g,mx,my,partial);renderTooltip(g,mx,my);for(int i=0;i<12;i++){int id=menu.value(i+5),x=leftPos+55+16*(i%4),y=topPos+56+17*(i/4);if(id>0&&id<=10&&mx>=x&&mx<x+16&&my>=y&&my<y+16)g.renderTooltip(font,Component.translatable(VatPages.name(menu.value(17)==1,id)),mx,my);}}
}
