package thaumichorizons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.platform.GuiBlit;
final class RiftPages {
    static int rendered;
    static void repair(GuiGraphics graphics,String data,int x,int y,int mouseX,int mouseY){
        rendered++;
        var mc=Minecraft.getInstance();var title=Component.translatable("recipe.type.workbenchshapeless");graphics.drawString(mc.font,title,x+56-mc.font.width(title)/2,y,0x505050,false);var overlay=ResourceLocation.parse("thaumcraft:textures/gui/gui_researchbook_overlay.png");graphics.pose().pushPose();graphics.pose().translate(x,y,0);graphics.pose().scale(2,2,1);GuiBlit.blit(graphics,overlay,2,32,52,52,60,15,52,52,256,256,-1);GuiBlit.blit(graphics,overlay,20,12,16,16,20,3,16,16,256,256,-1);graphics.pose().popPose();
        var result=new ItemStack(thaumcraft.registry.ModItems.ELEMENTAL_SWORD.get());var damaged=result.copy();damaged.setDamageValue(Math.max(1,damaged.getMaxDamage()-1));var putty=new ItemStack(Content.item("void_putty"));draw(graphics,result,x+48,y+32,mouseX,mouseY);draw(graphics,damaged,x+16,y+76,mouseX,mouseY);draw(graphics,putty,x+48,y+76,mouseX,mouseY);
    }
    private static void draw(GuiGraphics graphics,ItemStack stack,int x,int y,int mouseX,int mouseY){var mc=Minecraft.getInstance();graphics.renderItem(stack,x,y);graphics.renderItemDecorations(mc.font,stack,x,y);if(mouseX>=x&&mouseX<x+16&&mouseY>=y&&mouseY<y+16)graphics.renderTooltip(mc.font,stack,mouseX,mouseY);}
}
