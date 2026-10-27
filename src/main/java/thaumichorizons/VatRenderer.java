package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public final class VatRenderer implements BlockEntityRenderer<VatEntity> {
    static int renderedShells;
    public static final net.minecraft.client.model.geom.ModelLayerLocation CORPSE=new net.minecraft.client.model.geom.ModelLayerLocation(ThaumicHorizons.id("corpse"),"main");
    public static LayerDefinition corpseLayer(){
        var mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-4,-8,-4,8,8,8),PartPose.ZERO);
        root.addOrReplaceChild("hat",CubeListBuilder.create().texOffs(32,0).addBox(-4,-8,-4,8,8,8,new CubeDeformation(.5F)),PartPose.ZERO);
        root.addOrReplaceChild("body",CubeListBuilder.create().texOffs(16,16).addBox(-4,0,-2,8,12,4),PartPose.ZERO);
        root.addOrReplaceChild("right_arm",CubeListBuilder.create().texOffs(40,16).addBox(-3,-2,-2,4,12,4),PartPose.offsetAndRotation(-5,2,0,0,0,.1F));
        root.addOrReplaceChild("left_arm",CubeListBuilder.create().texOffs(40,16).mirror().addBox(-1,-2,-2,4,12,4),PartPose.offsetAndRotation(5,2,0,0,0,-.1F));
        root.addOrReplaceChild("right_leg",CubeListBuilder.create().texOffs(0,16).addBox(-2,0,-2,4,12,4),PartPose.offset(-1.9F,12,0));
        root.addOrReplaceChild("left_leg",CubeListBuilder.create().texOffs(0,16).mirror().addBox(-2,0,-2,4,12,4),PartPose.offset(1.9F,12,0));
        return LayerDefinition.create(mesh,64,32);
    }
    private final HumanoidModel<LivingEntity> corpse;
    public VatRenderer(BlockEntityRendererProvider.Context ctx){corpse=new HumanoidModel<>(ctx.bakeLayer(CORPSE));}
    @Override public AABB getRenderBoundingBox(VatEntity vat){return new AABB(vat.getBlockPos()).inflate(2,4,2);}
    @Override public void render(VatEntity vat,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(!vat.isController())return;var mc=Minecraft.getInstance();float time=(mc.player==null?0:mc.player.tickCount)+partial;pose.pushPose();pose.translate(.5,-2+.1*Math.cos(Math.toRadians(time)),.5);LivingEntity e=vat.occupant();
        if(e!=null&&!(e instanceof net.minecraft.world.entity.player.Player)){ContainedPreview.prepare(e,vat.getBlockPos().getX()+.5,vat.getBlockPos().getY()-2,vat.getBlockPos().getZ()+.5,vat.getLevel().getGameTime());mc.getEntityRenderDispatcher().setRenderShadow(false);try{var output=vat.mode==1?new FadeBuffer(buffers,Math.clamp((800-vat.progress+partial)/800F,0,1)):buffers;mc.getEntityRenderDispatcher().render(e,0,0,0,0,partial,pose,output,light);}finally{mc.getEntityRenderDispatcher().setRenderShadow(true);}}
        else if(vat.mode==3||vat.mode==4||vat.mode==2&&VatRecipes.get(vat.recipeKey)!=null&&VatRecipes.get(vat.recipeKey).self()){
            renderedShells++;
            // TileVatSlaveRender used the untouched child-model default at a 0.1 render scale.
            pose.translate(0,2.5-.2*Math.cos(Math.toRadians(time)),0);pose.mulPose(Axis.ZP.rotationDegrees(180));pose.scale(1.6F,1.6F,1.6F);corpse.renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/"+(vat.mode==3?"corpseeffigy":"corpseeffigyrevived")+".png"))),light,overlay);
        }else if(!vat.inventory.getStackInSlot(0).isEmpty())mc.getItemRenderer().renderStatic(vat.inventory.getStackInSlot(0),net.minecraft.world.item.ItemDisplayContext.GROUND,light,overlay,pose,buffers,vat.getLevel(),0);pose.popPose();
    }
}
