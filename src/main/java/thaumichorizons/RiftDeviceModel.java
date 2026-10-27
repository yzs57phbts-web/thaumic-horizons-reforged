package thaumichorizons;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
final class RiftDeviceModel {
    static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("vortex_attenuator"),"main");
    static LayerDefinition layer(){var mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("Base",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 16, 4, 16),PartPose.offsetAndRotation(-8.0f, 20.0f, -8.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Rod",CubeListBuilder.create().texOffs(0,20).addBox(0.0f, 0.0f, 0.0f, 2, 12, 2),PartPose.offsetAndRotation(-1.0f, 8.0f, -1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_A1",CubeListBuilder.create().texOffs(0,34).addBox(0.0f, 0.0f, 0.0f, 12, 1, 1),PartPose.offsetAndRotation(-6.0f, 18.0f, -6.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_A2",CubeListBuilder.create().texOffs(0,34).addBox(0.0f, 0.0f, 0.0f, 12, 1, 1),PartPose.offsetAndRotation(-6.0f, 18.0f, 5.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_A3",CubeListBuilder.create().texOffs(0,36).addBox(0.0f, 0.0f, 0.0f, 1, 1, 10),PartPose.offsetAndRotation(5.0f, 18.0f, -5.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_A4",CubeListBuilder.create().texOffs(0,36).addBox(0.0f, 0.0f, 0.0f, 1, 1, 10),PartPose.offsetAndRotation(-6.0f, 18.0f, -5.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_B1",CubeListBuilder.create().texOffs(0,47).addBox(0.0f, 0.0f, 0.0f, 8, 1, 1),PartPose.offsetAndRotation(-4.0f, 15.0f, 3.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_B2",CubeListBuilder.create().texOffs(0,47).addBox(0.0f, 0.0f, 0.0f, 8, 1, 1),PartPose.offsetAndRotation(-4.0f, 15.0f, -4.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_B3",CubeListBuilder.create().texOffs(0,49).addBox(0.0f, 0.0f, 0.0f, 1, 1, 6),PartPose.offsetAndRotation(3.0f, 15.0f, -3.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_B4",CubeListBuilder.create().texOffs(0,49).addBox(0.0f, 0.0f, 0.0f, 1, 1, 6),PartPose.offsetAndRotation(-4.0f, 15.0f, -3.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_C1",CubeListBuilder.create().texOffs(0,56).addBox(0.0f, 0.0f, 0.0f, 4, 1, 1),PartPose.offsetAndRotation(-2.0f, 12.0f, -2.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_C2",CubeListBuilder.create().texOffs(0,56).addBox(0.0f, 0.0f, 0.0f, 4, 1, 1),PartPose.offsetAndRotation(-2.0f, 12.0f, 1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_C3",CubeListBuilder.create().texOffs(0,58).addBox(0.0f, 0.0f, 0.0f, 1, 1, 2),PartPose.offsetAndRotation(1.0f, 12.0f, -1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Ring_C4",CubeListBuilder.create().texOffs(0,58).addBox(0.0f, 0.0f, 0.0f, 1, 1, 2),PartPose.offsetAndRotation(-2.0f, 12.0f, -1.0f,0.0f, 0.0f, 0.0f));
        return LayerDefinition.create(mesh,64,64);
    }
}
