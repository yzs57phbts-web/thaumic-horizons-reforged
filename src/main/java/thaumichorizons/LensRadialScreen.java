package thaumichorizons;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

final class LensRadialScreen extends Screen {
    private java.util.List<String> choices=java.util.List.of();
    private String hovered;
    LensRadialScreen(){super(Component.translatable("thaumichorizons.lenses"));}
    @Override protected void init(){var list=new java.util.ArrayList<>(Lenses.available(minecraft.player));if(!Lenses.installed(Lenses.goggles(minecraft.player)).isEmpty())list.add("REMOVE");choices=java.util.List.copyOf(list);}
    @Override public boolean isPauseScreen(){return false;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){
        hovered=null;int cx=width/2,cy=height/2;for(int i=0;i<choices.size();i++){String type=choices.get(i);double angle=2*Math.PI*i/choices.size()-Math.PI/2;int x=cx+(int)(Math.cos(angle)*60),y=cy+(int)(Math.sin(angle)*60);boolean near=Math.hypot(mouseX-x,mouseY-y)<22;if(near){hovered=type;graphics.fill(x-12,y-12,x+12,y+12,0x775A346F);}if(type.equals("REMOVE"))graphics.drawCenteredString(font,Component.literal("×"),x,y-4,0xFFFFFFFF);else graphics.renderItem(new ItemStack(Content.item("lens_"+type)),x-8,y-8);}
        Component label=choices.isEmpty()?Component.translatable("thaumichorizons.lenses.none"):hovered==null?getTitle():hovered.equals("REMOVE")?Component.translatable("thaumichorizons.lenses.remove"):new ItemStack(Content.item("lens_"+hovered)).getHoverName();graphics.drawCenteredString(font,label,cx,cy-4,0xFFFFFFFF);
    }
    private void select(){if(hovered!=null)PacketDistributor.sendToServer(new Lenses.Change(hovered));onClose();}
    @Override public boolean mouseClicked(double x,double y,int button){if(button==0){hovered=null;for(int i=0;i<choices.size();i++){double angle=2*Math.PI*i/choices.size()-Math.PI/2;if(Math.hypot(x-width/2-Math.cos(angle)*60,y-height/2-Math.sin(angle)*60)<22){hovered=choices.get(i);break;}}select();return true;}return super.mouseClicked(x,y,button);}
    @Override public boolean keyReleased(int key,int scan,int modifiers){if(LensClient.CHANGE.matches(key,scan)){select();return true;}return super.keyReleased(key,scan,modifiers);}
}
