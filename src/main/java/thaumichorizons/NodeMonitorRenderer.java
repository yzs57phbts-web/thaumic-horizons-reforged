package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.render.model.ObjModel;
public final class NodeMonitorRenderer implements BlockEntityRenderer<NodeMonitor.MonitorEntity>{
    public NodeMonitorRenderer(BlockEntityRendererProvider.Context context){}
    static void draw(Direction direction,float rotation,boolean flashing,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();pose.translate(.5,.5,.5);pose.scale(.4F,.4F,.4F);
        switch(direction){case UP->pose.translate(0,-.8,0);case DOWN->{pose.translate(0,.8,0);pose.mulPose(Axis.XP.rotationDegrees(180));}case NORTH->{pose.translate(0,0,.8);pose.mulPose(Axis.XP.rotationDegrees(-90));}case SOUTH->{pose.translate(0,0,-.8);pose.mulPose(Axis.XP.rotationDegrees(90));}case WEST->{pose.translate(.8,0,0);pose.mulPose(Axis.ZP.rotationDegrees(90));}case EAST->{pose.translate(-.8,0,0);pose.mulPose(Axis.ZP.rotationDegrees(-90));}}
        pose.mulPose(Axis.YP.rotationDegrees(rotation));
        ObjModel.get(ResourceLocation.parse("thaumcraft:models/obj/scanner.obj")).renderAll(pose.last(),buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/nodemon.png"))),-1,light);
        ObjModel.get(ThaumicHorizons.id("textures/models/hexagon.obj")).renderAll(pose.last(),buffers.getBuffer(RenderType.entityTranslucent(ThaumicHorizons.id("textures/models/"+(flashing?"nodemonscreenactive":"nodemonscreen")+".png"))),-1,light);pose.popPose();
    }
    @Override public void render(NodeMonitor.MonitorEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){var state=entity.getBlockState();boolean active=state.getValue(NodeMonitor.ACTIVE);draw(state.getValue(NodeMonitor.FACING),entity.rotation+(active?0:partial),active&&entity.getLevel().getGameTime()%30>=15,pose,buffers,light);}
}
