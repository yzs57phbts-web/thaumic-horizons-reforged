package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;

public final class VatDeviceRenderer implements BlockEntityRenderer<VatDeviceEntity> {
    private final ModelPart beacon;
    public VatDeviceRenderer(BlockEntityRendererProvider.Context ctx){beacon=ctx.bakeLayer(SoulBeaconModel.LAYER);}
    public static void draw(boolean soul,ModelPart beacon,VatEntity vat,float ticks,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(soul){pose.pushPose();pose.translate(.5,1.5,.5);pose.mulPose(Axis.XP.rotationDegrees(180));beacon.render(pose,buffers.getBuffer(RenderType.entityCutout(ThaumicHorizons.id("textures/models/soulbeacon.png"))),light,overlay);pose.popPose();}
        else thaumcraft.client.render.RunicMatrixRendering.submit(buffers,pose,light,true,vat!=null,vat!=null&&vat.mode==2,vat!=null&&vat.mode==2?1:0,vat==null?0:vat.instability,vat==null?0:vat.cycles,ticks);
    }
    @Override public void render(VatDeviceEntity tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(tile.getBlockState().is(Vats.BEACON.get()),beacon,tile.vat(),tile.getLevel().getGameTime()+partial,pose,buffers,light,overlay);
        if(tile.getBlockState().is(Vats.BEACON.get())&&tile.getLevel().canSeeSky(tile.getBlockPos().above()))BeaconRenderer.renderBeaconBeam(pose,buffers,BeaconRenderer.BEAM_LOCATION,partial,1,tile.getLevel().getGameTime(),0,256,0xFFFFFFFF,.2F,.25F);
    }
    @Override public boolean shouldRenderOffScreen(VatDeviceEntity tile){return tile.getBlockState().is(Vats.BEACON.get());}
}
