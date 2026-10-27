package thaumichorizons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.client.aspect.AspectRenderer;

public final class DynamoScreen extends AbstractContainerScreen<DynamoMenu> {
    private static final int[][] POS={{11,12},{83,11},{11,45},{83,45},{11,78},{83,78}};
    private final GuiPulse pulse=new GuiPulse();
    public DynamoScreen(DynamoMenu m,Inventory inv,Component title){super(m,inv,title);imageWidth=111;imageHeight=104;}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){g.blit(ThaumicHorizons.id("textures/gui/guidynamo.png"),leftPos,topPos,0,0,111,104,256,256);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){for(int i=0;i<6;i++)AspectRenderer.drawTag(g,POS[i][0],POS[i][1],Dynamo.PRIMALS.get(i),0,0,1,(menu.tile.enabled&(1<<i))==0);pulse.draw(g);}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0)for(int i=0;i<6;i++)if(x>=leftPos+POS[i][0]&&x<leftPos+POS[i][0]+16&&y>=topPos+POS[i][1]&&y<topPos+POS[i][1]+16){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,i+1);pulse.start((int)x-leftPos,(int)y-topPos,Dynamo.PRIMALS.get(i).getColor());return true;}return super.mouseClicked(x,y,button);}
}
