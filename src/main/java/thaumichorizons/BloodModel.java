package thaumichorizons;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
public final class BloodModel {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("blood_infuser"),"main");
    public static LayerDefinition create(){MeshDefinition mesh=new MeshDefinition();PartDefinition root=mesh.getRoot();
        root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 16, 10, 16), PartPose.offsetAndRotation(-8.0f, 14.0f, -8.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("SyringeSlot", CubeListBuilder.create().texOffs(0,32).addBox(0.0f, 0.0f, 0.0f, 6, 6, 6), PartPose.offsetAndRotation(-3.0f, 8.0f, -3.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe1A", CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 2, 2, 3), PartPose.offsetAndRotation(-1.0f, 9.0f, 3.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe1B", CubeListBuilder.create().texOffs(0,54).addBox(0.0f, 0.0f, 0.0f, 2, 3, 2), PartPose.offsetAndRotation(-1.0f, 11.0f, 4.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe2A", CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 2, 2, 3), PartPose.offsetAndRotation(-1.0f, 9.0f, -6.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe2B", CubeListBuilder.create().texOffs(0,54).addBox(0.0f, 0.0f, 0.0f, 2, 3, 2), PartPose.offsetAndRotation(-1.0f, 11.0f, -6.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe3A", CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 3, 2, 2), PartPose.offsetAndRotation(3.0f, 9.0f, -1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe3B", CubeListBuilder.create().texOffs(0,54).addBox(0.0f, 0.0f, 0.0f, 2, 3, 2), PartPose.offsetAndRotation(4.0f, 11.0f, -1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe4A", CubeListBuilder.create().texOffs(0,48).addBox(0.0f, 0.0f, 0.0f, 3, 2, 2), PartPose.offsetAndRotation(-6.0f, 9.0f, -1.0f,0.0f, 0.0f, 0.0f));
        root.addOrReplaceChild("Pipe4B", CubeListBuilder.create().texOffs(0,54).addBox(0.0f, 0.0f, 0.0f, 2, 3, 2), PartPose.offsetAndRotation(-6.0f, 11.0f, -1.0f,0.0f, 0.0f, 0.0f));
        return LayerDefinition.create(mesh,64,64);
    }
}
