package thaumichorizons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.GuiBlit;
final class GuiPulse {
    private int timer,x,y,color;
    void start(int x,int y,int color){this.x=x-8;this.y=y-8;this.color=color;timer=8;var mc=Minecraft.getInstance();if(mc.player!=null)mc.player.playSound(thaumcraft.registry.ModSounds.HHOFF.get(),.2F,1+mc.player.getRandom().nextFloat()*.1F);}
    void draw(GuiGraphics graphics){if(timer<=0)return;int part=--timer,u=Math.floorMod(128+part*32,256);graphics.pose().pushPose();graphics.pose().translate(x+8,y+8,0);graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90));GuiBlit.blit(graphics,ResourceLocation.parse("thaumcraft:textures/misc/particles.png"),-8,-8,16,16,u,128,16,16,256,256,0xFF000000|(color&0xFFFFFF));graphics.pose().popPose();}
}
