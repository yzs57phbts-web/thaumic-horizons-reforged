package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.Direction;
import thaumcraft.client.render.model.ObjModel;
public final class SpikeRenderer implements BlockEntityRenderer<Spikes.SpikeEntity> {
    public SpikeRenderer(BlockEntityRendererProvider.Context context){}
    static void draw(String type,Direction facing,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.translate(.5,.5,.5);pose.scale(.35F,.35F,.35F);switch(facing){case UP->pose.translate(0,-1.45,0);case DOWN->{pose.translate(0,1.45,0);pose.mulPose(Axis.ZP.rotationDegrees(180));}case NORTH->{pose.translate(0,0,1.45);pose.mulPose(Axis.XP.rotationDegrees(-90));}case SOUTH->{pose.translate(0,0,-1.45);pose.mulPose(Axis.XP.rotationDegrees(90));}case WEST->{pose.translate(1.45,0,0);pose.mulPose(Axis.ZP.rotationDegrees(90));}case EAST->{pose.translate(-1.45,0,0);pose.mulPose(Axis.ZP.rotationDegrees(-90));}}ObjModel.get(ThaumicHorizons.id("textures/models/spike.obj")).renderAll(pose.last(),buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/"+type+"spike.png"))),-1,light);pose.popPose();}
    @Override public void render(Spikes.SpikeEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){draw(((Spikes.SpikeBlock)entity.getBlockState().getBlock()).type,entity.getBlockState().getValue(Spikes.FACING),pose,buffers,light);}
}
