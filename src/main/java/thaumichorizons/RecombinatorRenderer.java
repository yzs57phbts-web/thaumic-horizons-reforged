package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.geom.*;
import thaumcraft.client.platform.*;

public final class RecombinatorRenderer implements BlockEntityRenderer<Recombinator> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("recombinator"),"main");
    private final ModelPart model;
    public RecombinatorRenderer(BlockEntityRendererProvider.Context context){model=context.bakeLayer(LAYER);}
    static LayerDefinition layer(){
        var mesh=new MeshDefinition();var root=mesh.getRoot();
        box(root,"Base",0,0,16,4,16,-8,20,-8);box(root,"Pearl",0,20,4,4,4,-2,14,-2);box(root,"Claw1A",0,28,2,8,1,-1,12,7);box(root,"Claw2A",0,28,2,8,1,-1,12,-8);box(root,"Claw3A",6,28,1,8,2,-8,12,-1);box(root,"Claw4A",6,28,1,8,2,7,12,-1);box(root,"EndA",0,48,4,2,4,-2,8,-2);box(root,"Shape1",0,38,8,2,8,-4,10,-4);box(root,"Claw1B",0,54,2,1,4,-1,11,4);box(root,"Claw2B",0,54,2,1,4,-1,11,-8);box(root,"Claw3B",0,59,4,1,2,4,11,-1);box(root,"Claw4B",0,59,4,1,2,-8,11,-1);return LayerDefinition.create(mesh,64,64);
    }
    private static void box(PartDefinition root,String name,int u,int v,int w,int h,int d,int x,int y,int z){root.addOrReplaceChild(name,CubeListBuilder.create().texOffs(u,v).addBox(0,0,0,w,h,d),PartPose.offset(x,y,z));}
    static void draw(ModelPart model,float count,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.translate(.5,-.5,.5);model.getChild("Pearl").y=14+(float)Math.sin(count/8)*2;model.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/recombinator.png"))),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);pose.popPose();}
    @Override public void render(Recombinator entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(model,entity.count+(entity.active?partial:0),pose,buffers,light);if(!entity.active)return;
        var camera=Minecraft.getInstance().gameRenderer.getMainCamera();var view=thaumcraft.client.render.WorldRendering.camera(camera,entity.getBlockPos());float y=1.5F+.2F*(float)Math.sin(entity.count/8F);var texture=ResourceLocation.parse("thaumcraft:textures/item/lightningringv.png");int frame=(int)((System.nanoTime()/40000000L+entity.getBlockPos().getX())%16);
        ItemGeometry.submit(buffers,pose,RenderType.entityTranslucent(texture),(matrix,consumer)->FacingQuads.frame(new EntityQuadSink(matrix,consumer),view,.5F,y,.5F,0,1.5F,16,frame,0xE6FFFFFF,LightTexture.FULL_BRIGHT));
        if(entity.getLevel().getBlockEntity(entity.getBlockPos().below()) instanceof thaumcraft.node.NodeBlockEntity){var beam=new BeamGeometry.Params(.5F,.2F,.5F,0,90,.7F,.1F,1,false,false,3,10,20,.1F,entity.getLevel().getGameTime(),Minecraft.getInstance().player.tickCount,partial,1,1,1);ItemGeometry.submit(buffers,pose,TcRenderTypes.additive(ResourceLocation.parse("thaumcraft:textures/misc/beam1.png")),(matrix,consumer)->BeamGeometry.render(beam,new VertexQuadSink(matrix,consumer)));}
    }
}
