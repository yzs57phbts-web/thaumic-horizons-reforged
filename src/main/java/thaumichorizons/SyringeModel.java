package thaumichorizons;

import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
public final class SyringeModel {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("syringe"),"main");
    public static LayerDefinition layer(){
        var mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("body",CubeListBuilder.create().texOffs(0,0).addBox(0,0,0,9,3,3),PartPose.offset(-3,21,-1));
        root.addOrReplaceChild("needle",CubeListBuilder.create().texOffs(25,0).addBox(0,0,0,4,1,1),PartPose.offset(-7,22,0));
        root.addOrReplaceChild("plunger_a",CubeListBuilder.create().texOffs(0,8).addBox(0,0,0,1,1,1),PartPose.offset(6,22,0));
        root.addOrReplaceChild("plunger_b",CubeListBuilder.create().texOffs(0,12).addBox(0,0,0,1,3,3),PartPose.offset(7,21,-1));
        return LayerDefinition.create(mesh,64,32);
    }
}
