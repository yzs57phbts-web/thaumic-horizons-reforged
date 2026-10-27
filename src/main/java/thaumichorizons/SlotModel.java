package thaumichorizons;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
final class SlotModel {
 static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("keystone_receptacle"),"main");
 static LayerDefinition layer(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("Corner1",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(-8.0f, 20.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner2",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(-8.0f, 20.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner3",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(4.0f, 20.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner4",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(4.0f, 20.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side1A",CubeListBuilder.create().texOffs(0,8).addBox(0.0f, 0.0f, 0.0f, 8, 4, 4),PartPose.offsetAndRotation(-4.0f, 8.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner5",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(-8.0f, 8.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner6",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(-8.0f, 8.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner7",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(4.0f, 8.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Corner8",CubeListBuilder.create().texOffs(0,0).addBox(0.0f, 0.0f, 0.0f, 4, 4, 4),PartPose.offsetAndRotation(4.0f, 8.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side1B",CubeListBuilder.create().texOffs(0,8).addBox(0.0f, 0.0f, 0.0f, 8, 4, 4),PartPose.offsetAndRotation(-4.0f, 8.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side1C",CubeListBuilder.create().texOffs(0,8).addBox(0.0f, 0.0f, 0.0f, 8, 4, 4),PartPose.offsetAndRotation(-4.0f, 20.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side1D",CubeListBuilder.create().texOffs(0,8).addBox(0.0f, 0.0f, 0.0f, 8, 4, 4),PartPose.offsetAndRotation(-4.0f, 20.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side2A",CubeListBuilder.create().texOffs(0,16).addBox(0.0f, 0.0f, 0.0f, 4, 8, 4),PartPose.offsetAndRotation(-8.0f, 12.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side2B",CubeListBuilder.create().texOffs(0,16).addBox(0.0f, 0.0f, 0.0f, 4, 8, 4),PartPose.offsetAndRotation(4.0f, 12.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side2C",CubeListBuilder.create().texOffs(0,16).addBox(0.0f, 0.0f, 0.0f, 4, 8, 4),PartPose.offsetAndRotation(-8.0f, 12.0f, 4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side2D",CubeListBuilder.create().texOffs(0,16).addBox(0.0f, 0.0f, 0.0f, 4, 8, 4),PartPose.offsetAndRotation(4.0f, 12.0f, -8.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side3A",CubeListBuilder.create().texOffs(0,28).addBox(0.0f, 0.0f, 0.0f, 4, 4, 8),PartPose.offsetAndRotation(-8.0f, 8.0f, -4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side3B",CubeListBuilder.create().texOffs(0,28).addBox(0.0f, 0.0f, 0.0f, 4, 4, 8),PartPose.offsetAndRotation(4.0f, 8.0f, -4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side3C",CubeListBuilder.create().texOffs(0,28).addBox(0.0f, 0.0f, 0.0f, 4, 4, 8),PartPose.offsetAndRotation(-8.0f, 20.0f, -4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Side3D",CubeListBuilder.create().texOffs(0,28).addBox(0.0f, 0.0f, 0.0f, 4, 4, 8),PartPose.offsetAndRotation(4.0f, 20.0f, -4.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Keystone",CubeListBuilder.create().texOffs(24,0).addBox(0.0f, 0.0f, 0.0f, 8, 8, 8),PartPose.offsetAndRotation(-4.0f, 12.0f, -4.0f,0.0f, 0.0f, 0.0f));
 return LayerDefinition.create(mesh,64,64);}
}
