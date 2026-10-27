package thaumichorizons;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
public final class SoulBeaconModel {
 public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("soul_beacon"),"main");
 public static LayerDefinition layer(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("Base",CubeListBuilder.create().texOffs(0,23).addBox(0.0f, 0.0f, 0.0f, 16, 1, 16),PartPose.offsetAndRotation(-8.0f, 23.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Core",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 12, 11, 12),PartPose.offsetAndRotation(-6.0f, 12.0f, -6.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw1A",CubeListBuilder.create().texOffs(48,0).addBox(0.0f, 0.0f, 0.0f, 2, 1, 2),PartPose.offsetAndRotation(6.0f, 12.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw2A",CubeListBuilder.create().texOffs(48,0).addBox(0.0f, 0.0f, 0.0f, 2, 1, 2),PartPose.offsetAndRotation(-8.0f, 12.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw3A",CubeListBuilder.create().texOffs(48,0).addBox(0.0f, 0.0f, 0.0f, 2, 1, 2),PartPose.offsetAndRotation(-1.0f, 12.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw4A",CubeListBuilder.create().texOffs(48,0).addBox(0.0f, 0.0f, 0.0f, 2, 1, 2),PartPose.offsetAndRotation(-1.0f, 12.0f, 6.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw3B",CubeListBuilder.create().texOffs(48,8).addBox(0.0f, 0.0f, 0.0f, 2, 4, 1),PartPose.offsetAndRotation(-1.0f, 8.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw4B",CubeListBuilder.create().texOffs(48,8).addBox(0.0f, 0.0f, 0.0f, 2, 4, 1),PartPose.offsetAndRotation(-1.0f, 8.0f, 7.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw1B",CubeListBuilder.create().texOffs(48,16).addBox(0.0f, 0.0f, 0.0f, 1, 4, 2),PartPose.offsetAndRotation(7.0f, 8.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw2B",CubeListBuilder.create().texOffs(48,16).addBox(0.0f, 0.0f, 0.0f, 1, 4, 2),PartPose.offsetAndRotation(-8.0f, 8.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw1C",CubeListBuilder.create().texOffs(48,48).addBox(0.0f, 0.0f, 0.0f, 4, 1, 2),PartPose.offsetAndRotation(3.0f, 8.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw2C",CubeListBuilder.create().texOffs(48,48).addBox(0.0f, 0.0f, 0.0f, 4, 1, 2),PartPose.offsetAndRotation(-7.0f, 8.0f, -1.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw3C",CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 2, 1, 4),PartPose.offsetAndRotation(-1.0f, 8.0f, -7.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Claw4C",CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 2, 1, 4),PartPose.offsetAndRotation(-1.0f, 8.0f, 3.0f,0.0f, 0.0f, 0.0f));return LayerDefinition.create(mesh,64,64);}
}
