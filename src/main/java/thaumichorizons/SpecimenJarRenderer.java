package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;

public final class SpecimenJarRenderer implements BlockEntityRenderer<SpecimenJarEntity> {
    public SpecimenJarRenderer(BlockEntityRendererProvider.Context ctx){}
    @Override public void render(SpecimenJarEntity jar,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){if(Souls.isSoul(jar.stack)){SoulMachineRenderer.soul(pose,buffers,.5,.4,.5,.1F,jar.getLevel().getGameTime()+partial);return;}var e=jar.occupant();if(e==null)return;ContainedPreview.prepare(e,jar.getBlockPos().getX()+.5,jar.getBlockPos().getY(),jar.getBlockPos().getZ()+.5,jar.getLevel().getGameTime());var dispatcher=Minecraft.getInstance().getEntityRenderDispatcher();pose.pushPose();pose.translate(.5,.15,.5);float scale=Math.min(.4F,.5F/Math.max(e.getBbWidth(),e.getBbHeight()));pose.scale(scale,scale,scale);dispatcher.setRenderShadow(false);try{dispatcher.render(e,0,0,0,0,partial,pose,buffers,light);}finally{dispatcher.setRenderShadow(true);pose.popPose();}}
}
