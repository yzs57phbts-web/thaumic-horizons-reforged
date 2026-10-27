package thaumichorizons;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.Horse;
/** Original 1.7.10 horse geometry/UV layout and animations adapted to ModelPart. See audit/legacy-horse-model.json. */
public final class LegacyHorseModel extends EntityModel<Horse> {
 public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("legacy_horse"),"main");
 private final ModelPart body,tailBase,tailMiddle,tailTip,backLeftLeg,backLeftShin,backLeftHoof,backRightLeg,backRightShin,backRightHoof,frontLeftLeg,frontLeftShin,frontLeftHoof,frontRightLeg,frontRightShin,frontRightHoof,head,mouthTop,mouthBottom,horseLeftEar,horseRightEar,muleLeftEar,muleRightEar,neck,muleLeftChest,muleRightChest,horseSaddleBottom,horseSaddleFront,horseSaddleBack,horseLeftSaddleMetal,horseLeftSaddleRope,horseRightSaddleMetal,horseRightSaddleRope,horseLeftFaceMetal,horseRightFaceMetal,horseLeftRein,horseRightRein,mane,horseFaceRopes;private Horse horse;
 public LegacyHorseModel(ModelPart root){this.body=root.getChild("body");this.tailBase=root.getChild("tailBase");this.tailMiddle=root.getChild("tailMiddle");this.tailTip=root.getChild("tailTip");this.backLeftLeg=root.getChild("backLeftLeg");this.backLeftShin=root.getChild("backLeftShin");this.backLeftHoof=root.getChild("backLeftHoof");this.backRightLeg=root.getChild("backRightLeg");this.backRightShin=root.getChild("backRightShin");this.backRightHoof=root.getChild("backRightHoof");this.frontLeftLeg=root.getChild("frontLeftLeg");this.frontLeftShin=root.getChild("frontLeftShin");this.frontLeftHoof=root.getChild("frontLeftHoof");this.frontRightLeg=root.getChild("frontRightLeg");this.frontRightShin=root.getChild("frontRightShin");this.frontRightHoof=root.getChild("frontRightHoof");this.head=root.getChild("head");this.mouthTop=root.getChild("head").getChild("mouthTop");this.mouthBottom=root.getChild("head").getChild("mouthBottom");this.horseLeftEar=root.getChild("horseLeftEar");this.horseRightEar=root.getChild("horseRightEar");this.muleLeftEar=root.getChild("muleLeftEar");this.muleRightEar=root.getChild("muleRightEar");this.neck=root.getChild("neck");this.muleLeftChest=root.getChild("muleLeftChest");this.muleRightChest=root.getChild("muleRightChest");this.horseSaddleBottom=root.getChild("horseSaddleBottom");this.horseSaddleFront=root.getChild("horseSaddleFront");this.horseSaddleBack=root.getChild("horseSaddleBack");this.horseLeftSaddleMetal=root.getChild("horseLeftSaddleMetal");this.horseLeftSaddleRope=root.getChild("horseLeftSaddleRope");this.horseRightSaddleMetal=root.getChild("horseRightSaddleMetal");this.horseRightSaddleRope=root.getChild("horseRightSaddleRope");this.horseLeftFaceMetal=root.getChild("horseLeftFaceMetal");this.horseRightFaceMetal=root.getChild("horseRightFaceMetal");this.horseLeftRein=root.getChild("horseLeftRein");this.horseRightRein=root.getChild("horseRightRein");this.mane=root.getChild("mane");this.horseFaceRopes=root.getChild("horseFaceRopes");}
 public static LayerDefinition layer(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("body",CubeListBuilder.create().texOffs(0,34).addBox(-5.0F, -8.0F, -19.0F, 10, 10, 24),PartPose.offsetAndRotation(0.0F, 11.0F, 9.0F,0,0,0));
root.addOrReplaceChild("tailBase",CubeListBuilder.create().texOffs(44,0).addBox(-1.0F, -1.0F, 0.0F, 2, 2, 3),PartPose.offsetAndRotation(0.0F, 3.0F, 14.0F,-1.134464F, 0.0F, 0.0F));
root.addOrReplaceChild("tailMiddle",CubeListBuilder.create().texOffs(38,7).addBox(-1.5F, -2.0F, 3.0F, 3, 4, 7),PartPose.offsetAndRotation(0.0F, 3.0F, 14.0F,-1.134464F, 0.0F, 0.0F));
root.addOrReplaceChild("tailTip",CubeListBuilder.create().texOffs(24,3).addBox(-1.5F, -4.5F, 9.0F, 3, 4, 7),PartPose.offsetAndRotation(0.0F, 3.0F, 14.0F,-1.40215F, 0.0F, 0.0F));
root.addOrReplaceChild("backLeftLeg",CubeListBuilder.create().texOffs(78,29).addBox(-2.5F, -2.0F, -2.5F, 4, 9, 5),PartPose.offsetAndRotation(4.0F, 9.0F, 11.0F,0,0,0));
root.addOrReplaceChild("backLeftShin",CubeListBuilder.create().texOffs(78,43).addBox(-2.0F, 0.0F, -1.5F, 3, 5, 3),PartPose.offsetAndRotation(4.0F, 16.0F, 11.0F,0,0,0));
root.addOrReplaceChild("backLeftHoof",CubeListBuilder.create().texOffs(78,51).addBox(-2.5F, 5.1F, -2.0F, 4, 3, 4),PartPose.offsetAndRotation(4.0F, 16.0F, 11.0F,0,0,0));
root.addOrReplaceChild("backRightLeg",CubeListBuilder.create().texOffs(96,29).addBox(-1.5F, -2.0F, -2.5F, 4, 9, 5),PartPose.offsetAndRotation(-4.0F, 9.0F, 11.0F,0,0,0));
root.addOrReplaceChild("backRightShin",CubeListBuilder.create().texOffs(96,43).addBox(-1.0F, 0.0F, -1.5F, 3, 5, 3),PartPose.offsetAndRotation(-4.0F, 16.0F, 11.0F,0,0,0));
root.addOrReplaceChild("backRightHoof",CubeListBuilder.create().texOffs(96,51).addBox(-1.5F, 5.1F, -2.0F, 4, 3, 4),PartPose.offsetAndRotation(-4.0F, 16.0F, 11.0F,0,0,0));
root.addOrReplaceChild("frontLeftLeg",CubeListBuilder.create().texOffs(44,29).addBox(-1.9F, -1.0F, -2.1F, 3, 8, 4),PartPose.offsetAndRotation(4.0F, 9.0F, -8.0F,0,0,0));
root.addOrReplaceChild("frontLeftShin",CubeListBuilder.create().texOffs(44,41).addBox(-1.9F, 0.0F, -1.6F, 3, 5, 3),PartPose.offsetAndRotation(4.0F, 16.0F, -8.0F,0,0,0));
root.addOrReplaceChild("frontLeftHoof",CubeListBuilder.create().texOffs(44,51).addBox(-2.4F, 5.1F, -2.1F, 4, 3, 4),PartPose.offsetAndRotation(4.0F, 16.0F, -8.0F,0,0,0));
root.addOrReplaceChild("frontRightLeg",CubeListBuilder.create().texOffs(60,29).addBox(-1.1F, -1.0F, -2.1F, 3, 8, 4),PartPose.offsetAndRotation(-4.0F, 9.0F, -8.0F,0,0,0));
root.addOrReplaceChild("frontRightShin",CubeListBuilder.create().texOffs(60,41).addBox(-1.1F, 0.0F, -1.6F, 3, 5, 3),PartPose.offsetAndRotation(-4.0F, 16.0F, -8.0F,0,0,0));
root.addOrReplaceChild("frontRightHoof",CubeListBuilder.create().texOffs(60,51).addBox(-1.6F, 5.1F, -2.1F, 4, 3, 4),PartPose.offsetAndRotation(-4.0F, 16.0F, -8.0F,0,0,0));
var headDef=root.addOrReplaceChild("head",CubeListBuilder.create().texOffs(0,0).addBox(-2.5F, -10.0F, -1.5F, 5, 5, 7),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
headDef.addOrReplaceChild("mouthTop",CubeListBuilder.create().texOffs(24,18).addBox(-2.0F, -10.0F, -7.0F, 4, 3, 6),PartPose.offsetAndRotation(0.0F, 3.95F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
headDef.addOrReplaceChild("mouthBottom",CubeListBuilder.create().texOffs(24,27).addBox(-2.0F, -7.0F, -6.5F, 4, 2, 5),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("horseLeftEar",CubeListBuilder.create().texOffs(0,0).addBox(0.45F, -12.0F, 4.0F, 2, 3, 1),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("horseRightEar",CubeListBuilder.create().texOffs(0,0).addBox(-2.45F, -12.0F, 4.0F, 2, 3, 1),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("muleLeftEar",CubeListBuilder.create().texOffs(0,12).addBox(-2.0F, -16.0F, 4.0F, 2, 7, 1),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, (float) (Math.PI / 12)));
root.addOrReplaceChild("muleRightEar",CubeListBuilder.create().texOffs(0,12).addBox(0.0F, -16.0F, 4.0F, 2, 7, 1),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, (float) (-Math.PI / 12)));
root.addOrReplaceChild("neck",CubeListBuilder.create().texOffs(0,12).addBox(-2.05F, -9.8F, -2.0F, 4, 14, 8),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("muleLeftChest",CubeListBuilder.create().texOffs(0,34).addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3),PartPose.offsetAndRotation(-7.5F, 3.0F, 10.0F,0.0F, 1.570796F, 0.0F));
root.addOrReplaceChild("muleRightChest",CubeListBuilder.create().texOffs(0,47).addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3),PartPose.offsetAndRotation(4.5F, 3.0F, 10.0F,0.0F, 1.570796F, 0.0F));
root.addOrReplaceChild("horseSaddleBottom",CubeListBuilder.create().texOffs(80,0).addBox(-5.0F, 0.0F, -3.0F, 10, 1, 8),PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseSaddleFront",CubeListBuilder.create().texOffs(106,9).addBox(-1.5F, -1.0F, -3.0F, 3, 1, 2),PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseSaddleBack",CubeListBuilder.create().texOffs(80,9).addBox(-4.0F, -1.0F, 3.0F, 8, 1, 2),PartPose.offsetAndRotation(0.0F, 2.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseLeftSaddleMetal",CubeListBuilder.create().texOffs(74,0).addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2),PartPose.offsetAndRotation(5.0F, 3.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseLeftSaddleRope",CubeListBuilder.create().texOffs(70,0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),PartPose.offsetAndRotation(5.0F, 3.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseRightSaddleMetal",CubeListBuilder.create().texOffs(74,4).addBox(-0.5F, 6.0F, -1.0F, 1, 2, 2),PartPose.offsetAndRotation(-5.0F, 3.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseRightSaddleRope",CubeListBuilder.create().texOffs(80,0).addBox(-0.5F, 0.0F, -0.5F, 1, 6, 1),PartPose.offsetAndRotation(-5.0F, 3.0F, 2.0F,0,0,0));
root.addOrReplaceChild("horseLeftFaceMetal",CubeListBuilder.create().texOffs(74,13).addBox(1.5F, -8.0F, -4.0F, 1, 2, 2),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("horseRightFaceMetal",CubeListBuilder.create().texOffs(74,13).addBox(-2.5F, -8.0F, -4.0F, 1, 2, 2),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("horseLeftRein",CubeListBuilder.create().texOffs(44,10).addBox(2.6F, -6.0F, -6.0F, 0, 3, 16),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,0,0,0));
root.addOrReplaceChild("horseRightRein",CubeListBuilder.create().texOffs(44,5).addBox(-2.6F, -6.0F, -6.0F, 0, 3, 16),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,0,0,0));
root.addOrReplaceChild("mane",CubeListBuilder.create().texOffs(58,0).addBox(-1.0F, -11.5F, 5.0F, 2, 16, 4),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));
root.addOrReplaceChild("horseFaceRopes",CubeListBuilder.create().texOffs(80,12).addBox(-2.5F, -10.1F, -7.0F, 5, 5, 12,new CubeDeformation( 0.2F)),PartPose.offsetAndRotation(0.0F, 4.0F, -10.0F,(float) (Math.PI / 6), 0.0F, 0.0F));return LayerDefinition.create(mesh,128,128);}
 @Override public void setupAnim(Horse horse,float limb,float amount,float age,float yaw,float pitch){}
 @Override public void prepareMobModel(Horse var1,float var2,float var3,float var4){this.horse=var1;

      
      float var5 = this.updateHorseRotation(var1.yBodyRotO, var1.yBodyRot, var4);
      float var6 = this.updateHorseRotation(var1.yHeadRotO, var1.yHeadRot, var4);
      float var7 = var1.xRotO + (var1.getXRot() - var1.xRotO) * var4;
      float var8 = var6 - var5;
      float var9 = var7 / (float) (180.0 / Math.PI);
      if (var8 > 20.0F) {
         var8 = 20.0F;
      }

      if (var8 < -20.0F) {
         var8 = -20.0F;
      }

      if (var3 > 0.2F) {
         var9 += Mth.cos(var2 * 0.4F) * 0.15F * var3;
      }

      Horse var10 = (Horse)var1;
      float var11 = var10.getEatAnim(var4);
      float var12 = var10.getStandAnim(var4);
      float var13 = 1.0F - var12;
      float var14 = var10.getMouthAnim(var4);
      boolean var15 = var10.tailCounter != 0;
      boolean var16 = var10.isSaddled();
      boolean var17 = var10.isVehicle();
      float var18 = var1.tickCount + var4;
      float var19 = Mth.cos(var2 * 0.6662F + 3.141593F);
      float var20 = var19 * 0.8F * var3;
      this.head.y = 4.0F;
      this.head.z = -10.0F;
      this.tailBase.y = 3.0F;
      this.tailMiddle.z = 14.0F;
      this.muleRightChest.y = 3.0F;
      this.muleRightChest.z = 10.0F;
      this.body.xRot = 0.0F;
      this.head.xRot = (float) (Math.PI / 6) + var9;
      this.head.yRot = var8 / (float) (180.0 / Math.PI);
      this.head.xRot = var12 * ((float) (Math.PI / 12) + var9) + var11 * 2.18166F + (1.0F - Math.max(var12, var11)) * this.head.xRot;
      this.head.yRot = var12 * (var8 / (float) (180.0 / Math.PI)) + (1.0F - Math.max(var12, var11)) * this.head.yRot;
      this.head.y = var12 * -6.0F + var11 * 11.0F + (1.0F - Math.max(var12, var11)) * this.head.y;
      this.head.z = var12 * -1.0F + var11 * -10.0F + (1.0F - Math.max(var12, var11)) * this.head.z;
      this.tailBase.y = var12 * 9.0F + var13 * this.tailBase.y;
      this.tailMiddle.z = var12 * 18.0F + var13 * this.tailMiddle.z;
      this.muleRightChest.y = var12 * 5.5F + var13 * this.muleRightChest.y;
      this.muleRightChest.z = var12 * 15.0F + var13 * this.muleRightChest.z;
      this.body.xRot = var12 * -0.7853981F + var13 * this.body.xRot;
      this.horseLeftEar.y = this.head.y;
      this.horseRightEar.y = this.head.y;
      this.muleLeftEar.y = this.head.y;
      this.muleRightEar.y = this.head.y;
      this.neck.y = this.head.y;
      this.mouthTop.y = 0.02F;
      this.mouthBottom.y = 0.0F;
      this.mane.y = this.head.y;
      this.horseLeftEar.z = this.head.z;
      this.horseRightEar.z = this.head.z;
      this.muleLeftEar.z = this.head.z;
      this.muleRightEar.z = this.head.z;
      this.neck.z = this.head.z;
      this.mouthTop.z = 0.02F - var14 * 1.0F;
      this.mouthBottom.z = 0.0F + var14 * 1.0F;
      this.mane.z = this.head.z;
      this.horseLeftEar.xRot = this.head.xRot;
      this.horseRightEar.xRot = this.head.xRot;
      this.muleLeftEar.xRot = this.head.xRot;
      this.muleRightEar.xRot = this.head.xRot;
      this.neck.xRot = this.head.xRot;
      this.mouthTop.xRot = 0.0F - 0.09424778F * var14;
      this.mouthBottom.xRot = 0.0F + (float) (Math.PI / 20) * var14;
      this.mane.xRot = this.head.xRot;
      this.horseLeftEar.yRot = this.head.yRot;
      this.horseRightEar.yRot = this.head.yRot;
      this.muleLeftEar.yRot = this.head.yRot;
      this.muleRightEar.yRot = this.head.yRot;
      this.neck.yRot = this.head.yRot;
      this.mouthTop.yRot = 0.0F;
      this.mouthBottom.yRot = 0.0F;
      this.mane.yRot = this.head.yRot;
      this.muleLeftChest.xRot = var20 / 5.0F;
      this.muleRightChest.xRot = -var20 / 5.0F;
      float var21 = (float) (Math.PI / 2);
      float var22 = (float) (Math.PI * 3.0 / 2.0);
      float var23 = (float) (-Math.PI / 3);
      float var24 = (float) (Math.PI / 12) * var12;
      float var25 = Mth.cos(var18 * 0.6F + 3.141593F);
      this.frontLeftLeg.y = -2.0F * var12 + 9.0F * var13;
      this.frontLeftLeg.z = -2.0F * var12 + -8.0F * var13;
      this.frontRightLeg.y = this.frontLeftLeg.y;
      this.frontRightLeg.z = this.frontLeftLeg.z;
      this.backLeftShin.y = this.backLeftLeg.y
         + Mth.sin((float) (Math.PI / 2) + var24 + var13 * (-var19 * 0.5F * var3)) * 7.0F;
      this.backLeftShin.z = this.backLeftLeg.z
         + Mth.cos((float) (Math.PI * 3.0 / 2.0) + var24 + var13 * (-var19 * 0.5F * var3)) * 7.0F;
      this.backRightShin.y = this.backRightLeg.y
         + Mth.sin((float) (Math.PI / 2) + var24 + var13 * (var19 * 0.5F * var3)) * 7.0F;
      this.backRightShin.z = this.backRightLeg.z
         + Mth.cos((float) (Math.PI * 3.0 / 2.0) + var24 + var13 * (var19 * 0.5F * var3)) * 7.0F;
      float var26 = ((float) (-Math.PI / 3) + var25) * var12 + var20 * var13;
      float var27 = ((float) (-Math.PI / 3) + -var25) * var12 + -var20 * var13;
      this.frontLeftShin.y = this.frontLeftLeg.y + Mth.sin((float) (Math.PI / 2) + var26) * 7.0F;
      this.frontLeftShin.z = this.frontLeftLeg.z + Mth.cos((float) (Math.PI * 3.0 / 2.0) + var26) * 7.0F;
      this.frontRightShin.y = this.frontRightLeg.y + Mth.sin((float) (Math.PI / 2) + var27) * 7.0F;
      this.frontRightShin.z = this.frontRightLeg.z + Mth.cos((float) (Math.PI * 3.0 / 2.0) + var27) * 7.0F;
      this.backLeftLeg.xRot = var24 + -var19 * 0.5F * var3 * var13;
      this.backLeftShin.xRot = -0.08726646F * var12 + (-var19 * 0.5F * var3 - Math.max(0.0F, var19 * 0.5F * var3)) * var13;
      this.backLeftHoof.xRot = this.backLeftShin.xRot;
      this.backRightLeg.xRot = var24 + var19 * 0.5F * var3 * var13;
      this.backRightShin.xRot = -0.08726646F * var12 + (var19 * 0.5F * var3 - Math.max(0.0F, -var19 * 0.5F * var3)) * var13;
      this.backRightHoof.xRot = this.backRightShin.xRot;
      this.frontLeftLeg.xRot = var26;
      this.frontLeftShin.xRot = (this.frontLeftLeg.xRot + (float) Math.PI * Math.max(0.0F, 0.2F + var25 * 0.2F)) * var12
         + (var20 + Math.max(0.0F, var19 * 0.5F * var3)) * var13;
      this.frontLeftHoof.xRot = this.frontLeftShin.xRot;
      this.frontRightLeg.xRot = var27;
      this.frontRightShin.xRot = (this.frontRightLeg.xRot + (float) Math.PI * Math.max(0.0F, 0.2F - var25 * 0.2F)) * var12
         + (-var20 + Math.max(0.0F, -var19 * 0.5F * var3)) * var13;
      this.frontRightHoof.xRot = this.frontRightShin.xRot;
      this.backLeftHoof.y = this.backLeftShin.y;
      this.backLeftHoof.z = this.backLeftShin.z;
      this.backRightHoof.y = this.backRightShin.y;
      this.backRightHoof.z = this.backRightShin.z;
      this.frontLeftHoof.y = this.frontLeftShin.y;
      this.frontLeftHoof.z = this.frontLeftShin.z;
      this.frontRightHoof.y = this.frontRightShin.y;
      this.frontRightHoof.z = this.frontRightShin.z;
      if (var16) {
         this.horseSaddleBottom.y = var12 * 0.5F + var13 * 2.0F;
         this.horseSaddleBottom.z = var12 * 11.0F + var13 * 2.0F;
         this.horseSaddleFront.y = this.horseSaddleBottom.y;
         this.horseSaddleBack.y = this.horseSaddleBottom.y;
         this.horseLeftSaddleRope.y = this.horseSaddleBottom.y;
         this.horseRightSaddleRope.y = this.horseSaddleBottom.y;
         this.horseLeftSaddleMetal.y = this.horseSaddleBottom.y;
         this.horseRightSaddleMetal.y = this.horseSaddleBottom.y;
         this.muleLeftChest.y = this.muleRightChest.y;
         this.horseSaddleFront.z = this.horseSaddleBottom.z;
         this.horseSaddleBack.z = this.horseSaddleBottom.z;
         this.horseLeftSaddleRope.z = this.horseSaddleBottom.z;
         this.horseRightSaddleRope.z = this.horseSaddleBottom.z;
         this.horseLeftSaddleMetal.z = this.horseSaddleBottom.z;
         this.horseRightSaddleMetal.z = this.horseSaddleBottom.z;
         this.muleLeftChest.z = this.muleRightChest.z;
         this.horseSaddleBottom.xRot = this.body.xRot;
         this.horseSaddleFront.xRot = this.body.xRot;
         this.horseSaddleBack.xRot = this.body.xRot;
         this.horseLeftRein.y = this.head.y;
         this.horseRightRein.y = this.head.y;
         this.horseFaceRopes.y = this.head.y;
         this.horseLeftFaceMetal.y = this.head.y;
         this.horseRightFaceMetal.y = this.head.y;
         this.horseLeftRein.z = this.head.z;
         this.horseRightRein.z = this.head.z;
         this.horseFaceRopes.z = this.head.z;
         this.horseLeftFaceMetal.z = this.head.z;
         this.horseRightFaceMetal.z = this.head.z;
         this.horseLeftRein.xRot = var9;
         this.horseRightRein.xRot = var9;
         this.horseFaceRopes.xRot = this.head.xRot;
         this.horseLeftFaceMetal.xRot = this.head.xRot;
         this.horseRightFaceMetal.xRot = this.head.xRot;
         this.horseFaceRopes.yRot = this.head.yRot;
         this.horseLeftFaceMetal.yRot = this.head.yRot;
         this.horseLeftRein.yRot = this.head.yRot;
         this.horseRightFaceMetal.yRot = this.head.yRot;
         this.horseRightRein.yRot = this.head.yRot;
         if (var17) {
            this.horseLeftSaddleRope.xRot = (float) (-Math.PI / 3);
            this.horseLeftSaddleMetal.xRot = (float) (-Math.PI / 3);
            this.horseRightSaddleRope.xRot = (float) (-Math.PI / 3);
            this.horseRightSaddleMetal.xRot = (float) (-Math.PI / 3);
            this.horseLeftSaddleRope.zRot = 0.0F;
            this.horseLeftSaddleMetal.zRot = 0.0F;
            this.horseRightSaddleRope.zRot = 0.0F;
            this.horseRightSaddleMetal.zRot = 0.0F;
         } else {
            this.horseLeftSaddleRope.xRot = var20 / 3.0F;
            this.horseLeftSaddleMetal.xRot = var20 / 3.0F;
            this.horseRightSaddleRope.xRot = var20 / 3.0F;
            this.horseRightSaddleMetal.xRot = var20 / 3.0F;
            this.horseLeftSaddleRope.zRot = var20 / 5.0F;
            this.horseLeftSaddleMetal.zRot = var20 / 5.0F;
            this.horseRightSaddleRope.zRot = -var20 / 5.0F;
            this.horseRightSaddleMetal.zRot = -var20 / 5.0F;
         }
      }

      var21 = -1.3089F + var3 * 1.5F;
      if (var21 > 0.0F) {
         var21 = 0.0F;
      }

      if (var15) {
         this.tailBase.yRot = Mth.cos(var18 * 0.7F);
         var21 = 0.0F;
      } else {
         this.tailBase.yRot = 0.0F;
      }

      this.tailMiddle.yRot = this.tailBase.yRot;
      this.tailTip.yRot = this.tailBase.yRot;
      this.tailMiddle.y = this.tailBase.y;
      this.tailTip.y = this.tailBase.y;
      this.tailMiddle.z = this.tailBase.z;
      this.tailTip.z = this.tailBase.z;
      this.tailBase.xRot = var21;
      this.tailMiddle.xRot = var21;
      this.tailTip.xRot = -0.2618F + var21;
   }
 @Override public void renderToBuffer(PoseStack pose,VertexConsumer buffer,int light,int overlay,int color){

      Horse var8 = horse;
      int var9 = horseType(var8);
      float var10 = var8.getEatAnim(0.0F);
      boolean var11 = !var8.isBaby();
      boolean var12 = var11 && var8.isSaddled();
      boolean var13 = var11 && false;
      boolean var14 = var9 == 1 || var9 == 2;
      float var15 = horseSize(var8);
      boolean var16 = var8.isVehicle();
      if (var12) {
         this.horseFaceRopes.render(pose,buffer,light,overlay,color);
         this.horseSaddleBottom.render(pose,buffer,light,overlay,color);
         this.horseSaddleFront.render(pose,buffer,light,overlay,color);
         this.horseSaddleBack.render(pose,buffer,light,overlay,color);
         this.horseLeftSaddleRope.render(pose,buffer,light,overlay,color);
         this.horseLeftSaddleMetal.render(pose,buffer,light,overlay,color);
         this.horseRightSaddleRope.render(pose,buffer,light,overlay,color);
         this.horseRightSaddleMetal.render(pose,buffer,light,overlay,color);
         this.horseLeftFaceMetal.render(pose,buffer,light,overlay,color);
         this.horseRightFaceMetal.render(pose,buffer,light,overlay,color);
         if (var16) {
            this.horseLeftRein.render(pose,buffer,light,overlay,color);
            this.horseRightRein.render(pose,buffer,light,overlay,color);
         }
      }

      if (!var11) {
         pose.pushPose();
         pose.scale(var15, 0.5F + var15 * 0.5F, var15);
         pose.translate(0.0F, 0.95F * (1.0F - var15), 0.0F);
      }

      this.backLeftLeg.render(pose,buffer,light,overlay,color);
      this.backLeftShin.render(pose,buffer,light,overlay,color);
      this.backLeftHoof.render(pose,buffer,light,overlay,color);
      this.backRightLeg.render(pose,buffer,light,overlay,color);
      this.backRightShin.render(pose,buffer,light,overlay,color);
      this.backRightHoof.render(pose,buffer,light,overlay,color);
      this.frontLeftLeg.render(pose,buffer,light,overlay,color);
      this.frontLeftShin.render(pose,buffer,light,overlay,color);
      this.frontLeftHoof.render(pose,buffer,light,overlay,color);
      this.frontRightLeg.render(pose,buffer,light,overlay,color);
      this.frontRightShin.render(pose,buffer,light,overlay,color);
      this.frontRightHoof.render(pose,buffer,light,overlay,color);
      if (!var11) {
         pose.popPose();
         pose.pushPose();
         pose.scale(var15, var15, var15);
         pose.translate(0.0F, 1.35F * (1.0F - var15), 0.0F);
      }

      this.body.render(pose,buffer,light,overlay,color);
      this.tailBase.render(pose,buffer,light,overlay,color);
      this.tailMiddle.render(pose,buffer,light,overlay,color);
      this.tailTip.render(pose,buffer,light,overlay,color);
      this.neck.render(pose,buffer,light,overlay,color);
      this.mane.render(pose,buffer,light,overlay,color);
      if (!var11) {
         pose.popPose();
         pose.pushPose();
         float var17 = 0.5F + var15 * var15 * 0.5F;
         pose.scale(var17, var17, var17);
         if (var10 <= 0.0F) {
            pose.translate(0.0F, 1.35F * (1.0F - var15), 0.0F);
         } else {
            pose.translate(0.0F, 0.9F * (1.0F - var15) * var10 + 1.35F * (1.0F - var15) * (1.0F - var10), 0.15F * (1.0F - var15) * var10);
         }
      }

      if (var14) {
         this.muleLeftEar.render(pose,buffer,light,overlay,color);
         this.muleRightEar.render(pose,buffer,light,overlay,color);
      } else {
         this.horseLeftEar.render(pose,buffer,light,overlay,color);
         this.horseRightEar.render(pose,buffer,light,overlay,color);
      }

      this.head.render(pose,buffer,light,overlay,color);
      if (!var11) {
         pose.popPose();
      }

      if (var13) {
         this.muleLeftChest.render(pose,buffer,light,overlay,color);
         this.muleRightChest.render(pose,buffer,light,overlay,color);
      }
   }
 private static int horseType(Horse horse){return horse instanceof SpeciesHorse h?switch(h.form()){case "zombie_horse"->3;case "skeleton_horse"->4;default->0;}:0;}
 private static float horseSize(Horse horse){return horse.isBaby()?.5F+(24000+Math.max(-24000,horse.getAge()))/48000F:1;}
 private float updateHorseRotation(float var1,float var2,float var3){

      float var4 = var2 - var1;

      while (var4 < -180.0F) {
         var4 += 360.0F;
      }

      while (var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }
}
