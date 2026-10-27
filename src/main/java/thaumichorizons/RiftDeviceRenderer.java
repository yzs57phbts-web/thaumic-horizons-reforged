package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.geom.BeamGeometry;
import thaumcraft.client.platform.*;
import thaumcraft.client.render.model.ObjModel;
public final class RiftDeviceRenderer {
    static void amplifier(Direction facing,float count,boolean item,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();if(item)pose.translate(.75,.25,0);else switch(facing){case NORTH->pose.translate(.5,.5,0);case SOUTH->{pose.translate(.5,.5,1);pose.mulPose(Axis.YP.rotationDegrees(180));}case WEST->{pose.translate(0,.5,.5);pose.mulPose(Axis.YP.rotationDegrees(90));}case EAST->{pose.translate(1,.5,.5);pose.mulPose(Axis.YP.rotationDegrees(270));}default->pose.translate(.5,.5,.5);}
        var model=ObjModel.get(ResourceLocation.parse("thaumcraft:models/obj/node_stabilizer.obj"));var base=RenderType.entityCutoutNoCull(ResourceLocation.parse("thaumcraft:textures/model/node_converter.png"));var glow=RenderType.entityTranslucentEmissive(ResourceLocation.parse("thaumcraft:textures/model/node_converter_over.png"));float v=Math.min(50,Math.max(0,count))/137;int ticks=Minecraft.getInstance().player==null?0:Minecraft.getInstance().player.tickCount;
        model.renderPart("lock",pose.last(),buffers.getBuffer(base),-1,light);int brightness=thaumcraft.client.render.WorldRendering.emissiveLight(50+170*(v*2.5F*((float)Math.sin(ticks/3F)*.1F+.9F)));model.renderPart("lock",pose.last(),buffers.getBuffer(glow),0xFFCCCC00,brightness);
        for(int i=0;i<4;i++){pose.pushPose();pose.mulPose(Axis.ZP.rotationDegrees(90*i));pose.mulPose(Axis.YP.rotationDegrees(45));pose.translate(0,0,v);model.renderPart("piston",pose.last(),buffers.getBuffer(base),-1,light);brightness=thaumcraft.client.render.WorldRendering.emissiveLight(50+170*(v*2.5F*((float)Math.sin((ticks+i*5)/3F)*.1F+.9F)));model.renderPart("piston",pose.last(),buffers.getBuffer(glow),0xFFCCCC00,brightness);pose.popPose();}pose.popPose();
    }
    static void stabilizer(ModelPart model,Direction facing,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();pose.translate(.5,0,.5);switch(facing){case DOWN->pose.translate(0,-.5,0);case UP->{pose.mulPose(Axis.XP.rotationDegrees(180));pose.translate(0,-1.5,0);}case NORTH->{pose.mulPose(Axis.XP.rotationDegrees(90));pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(.5,-1,0);}case SOUTH->{pose.mulPose(Axis.XP.rotationDegrees(270));pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(-.5,-1,0);}case WEST->{pose.mulPose(Axis.ZP.rotationDegrees(270));pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,-1,-.5);}case EAST->{pose.mulPose(Axis.ZP.rotationDegrees(90));pose.mulPose(Axis.YP.rotationDegrees(90));pose.translate(0,-1,.5);}}
        model.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/attenuator.png"))),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);pose.popPose();
    }
    public static final class Amp implements BlockEntityRenderer<RiftDevices.Amplifier> {
        public Amp(BlockEntityRendererProvider.Context context){}
        @Override public void render(RiftDevices.Amplifier entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){amplifier(entity.getBlockState().getValue(RiftDevices.FACING),entity.count+(entity.active?partial:0),false,pose,buffers,light);}
    }
    public static final class Stabilizer implements BlockEntityRenderer<RiftDevices.Stabilizer> {
        private final ModelPart model;
        public Stabilizer(BlockEntityRendererProvider.Context context){model=context.bakeLayer(RiftDeviceModel.LAYER);}
        @Override public void render(RiftDevices.Stabilizer entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
            var facing=entity.getBlockState().getValue(RiftDevices.FACING);stabilizer(model,facing,pose,buffers,light);if(!entity.active)return;var target=entity.target==null?entity.getBlockPos().relative(facing,10):entity.target;var normal=Vec3.atLowerCornerOf(facing.getNormal());var end=target.getCenter().subtract(normal);var delta=entity.getBlockPos().getCenter().subtract(end);float length=(float)delta.length();if(length<=.001)return;float yaw=(float)Math.toDegrees(Math.atan2(delta.x,delta.z)),pitch=(float)Math.toDegrees(Math.atan2(delta.y,Math.sqrt(delta.x*delta.x+delta.z*delta.z)));var level=entity.getLevel();float ticks=Minecraft.getInstance().player==null?0:Minecraft.getInstance().player.tickCount;
            var beam=new BeamGeometry.Params(.5F,.5F,.5F,yaw,pitch,length,2,1,false,false,1,10,20,2,level.getGameTime(),ticks,partial,0,128/255F,1);ItemGeometry.submit(buffers,pose,TcRenderTypes.additive(ResourceLocation.parse("thaumcraft:textures/misc/beam1.png")),(matrix,consumer)->BeamGeometry.render(beam,new VertexQuadSink(matrix,consumer)));
            for(int i=0;i<3;i++){float age=(level.getGameTime()+partial+i*5)%14;int frame=Math.min(15,(int)(age/13*14)+1);pose.pushPose();var offset=target.getCenter().subtract(Vec3.atLowerCornerOf(entity.getBlockPos()));pose.translate(offset.x,offset.y,offset.z);float ringYaw=switch(facing){case NORTH->180;case WEST->90;case EAST->270;default->0;},ringPitch=facing==Direction.DOWN?90:facing==Direction.UP?-90:0;pose.mulPose(Axis.YP.rotationDegrees(-ringYaw));pose.mulPose(Axis.XP.rotationDegrees(ringPitch));pose.scale(.5F,.5F,-.5F);ObjModel.get(ResourceLocation.parse("thaumcraft:models/obj/hemis.obj")).renderAll(pose.last(),buffers.getBuffer(TcRenderTypes.additive(ResourceLocation.parse("thaumcraft:textures/model/ripple"+frame+".png"))),0xFF008080,LightTexture.FULL_BRIGHT);pose.popPose();}
        }
        @Override public boolean shouldRenderOffScreen(RiftDevices.Stabilizer entity){return true;}
    }
}
