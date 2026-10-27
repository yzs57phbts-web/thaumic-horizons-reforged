package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class LegacyBoatRenderer extends EntityRenderer<MagicBoats.LegacyBoat> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("legacy_boat"),"main");
    private final ModelPart model;
    public LegacyBoatRenderer(EntityRendererProvider.Context context){super(context);model=context.bakeLayer(LAYER);shadowRadius=.5F;}
    public static LayerDefinition layer(){
        var mesh=new MeshDefinition();var root=mesh.getRoot();root.addOrReplaceChild("bottom",CubeListBuilder.create().texOffs(0,8).addBox(-12,-8,-3,24,16,4),PartPose.offsetAndRotation(0,4,0,Mth.HALF_PI,0,0));
        float[][] parts={{-11,4,0,Mth.HALF_PI*3},{11,4,0,Mth.HALF_PI},{0,4,-9,Mth.PI},{0,4,9,0}};for(int i=0;i<4;i++){var p=parts[i];root.addOrReplaceChild("side"+i,CubeListBuilder.create().texOffs(0,0).addBox(-10,-7,-1,20,6,2),PartPose.offsetAndRotation(p[0],p[1],p[2],0,p[3],0));}return LayerDefinition.create(mesh,64,32);
    }
    @Override public ResourceLocation getTextureLocation(MagicBoats.LegacyBoat boat){return ThaumicHorizons.id("textures/entity/boat"+(boat.metal?"thaumium":"greatwood")+".png");}
    @Override public void render(MagicBoats.LegacyBoat boat,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
        pose.pushPose();pose.translate(0,.25,0);pose.mulPose(Axis.YP.rotationDegrees(180-yaw));float hurt=boat.getHurtTime()-partial,damage=Math.max(0,boat.getDamage()-partial);if(hurt>0)pose.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurt)*hurt*damage/10*boat.getHurtDir()));pose.scale(-1,-1,1);model.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(boat))),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);pose.popPose();super.render(boat,yaw,partial,pose,buffers,light);
    }
}
