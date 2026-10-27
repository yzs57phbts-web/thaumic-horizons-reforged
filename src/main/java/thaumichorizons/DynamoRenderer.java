package thaumichorizons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.render.model.ObjModel;

public final class DynamoRenderer implements BlockEntityRenderer<Dynamo> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("dynamo"),"main");
    private final ModelPart base;
    public static LayerDefinition layer(){var mesh=new MeshDefinition();mesh.getRoot().addOrReplaceChild("block",CubeListBuilder.create().texOffs(0,0).addBox(0,0,0,16,1,16),PartPose.ZERO);return LayerDefinition.create(mesh,64,32);}
    public DynamoRenderer(BlockEntityRendererProvider.Context c){base=c.bakeLayer(LAYER);}
    public static void draw(ModelPart base,boolean essentia,float rise,float rotation,float tilt,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        base.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id(essentia?"textures/models/dynamoessentiabase.png":"textures/models/dynamobase.png"))),light,overlay);
        pose.pushPose();pose.translate(.5,.2+rise,.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.mulPose(Axis.XP.rotationDegrees(tilt));pose.scale(.36F,.36F,.36F);
        var ring=ObjModel.get(ResourceLocation.parse("thaumcraft:models/obj/scanner.obj"));var buffer=buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id(essentia?"textures/models/thaumiumring.png":"textures/models/goldring.png")));
        ring.renderAll(pose.last(),buffer,-1,light);pose.mulPose(Axis.XP.rotationDegrees(-2*tilt));pose.scale(.5F,1,.5F);ring.renderAll(pose.last(),buffer,-1,light);pose.popPose();
    }
    @Override public void render(Dynamo be,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(base,be.isEssentia(),be.rise,be.rotation,be.tilt,pose,buffers,light,overlay);var mc=net.minecraft.client.Minecraft.getInstance();int color=be.fuel!=null&&be.isEssentia()?be.fuel.getColor():BloodFormula.calculate(be.provided).color();
        if(be.rise>=.3F&&be.ticks>0){var view=thaumcraft.client.render.WorldRendering.camera(mc.gameRenderer.getMainCamera(),be.getBlockPos());int frame=(int)Math.floorMod(System.nanoTime()/40_000_000L+be.getBlockPos().getX(),16);thaumcraft.client.platform.ItemGeometry.submit(buffers,pose,thaumcraft.client.platform.TcRenderTypes.additive(ResourceLocation.parse("thaumcraft:textures/item/lightningringv.png")),(matrix,consumer)->thaumcraft.client.geom.FacingQuads.frame(new thaumcraft.client.platform.VertexQuadSink(matrix,consumer),view,.5F,.5F,.5F,0,.2F,16,frame,0xE6000000|(color&0xFFFFFF),LightTexture.FULL_BRIGHT));}
        if(!be.isEssentia()&&be.ticks>0&&be.getLevel().getEntity(be.drainPlayer) instanceof net.minecraft.world.entity.player.Player player&&player.isUsingItem()){
            var origin=be.getBlockPos();var end=player.getEyePosition(partial).add(player.getLookAngle().scale(.5)).add(-.1,-.1,0);var start=origin.getCenter();float time=player.tickCount+partial;var params=new thaumcraft.client.geom.FloatyLineGeometry.Params(start.x,start.y,start.z,end.x,end.y,end.z,origin.getX(),origin.getY(),origin.getZ(),time,color,-.02F,Math.min(player.getTicksUsingItem(),10)/10F,.15F);thaumcraft.client.platform.ItemGeometry.submit(buffers,pose,thaumcraft.client.platform.TcRenderTypes.additive(ResourceLocation.parse("thaumcraft:textures/misc/wispy.png")),(matrix,consumer)->thaumcraft.client.geom.FloatyLineGeometry.render(params,new thaumcraft.client.platform.VertexQuadSink(matrix,consumer)));
        }
    }
}
