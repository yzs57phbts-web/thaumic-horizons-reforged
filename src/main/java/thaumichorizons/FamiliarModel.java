package thaumichorizons;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
public final class FamiliarModel extends EntityModel<Cat> {
 public static final ModelLayerLocation LAYER=new ModelLayerLocation(ThaumicHorizons.id("familiar"),"main");
 private final ModelPart root,Body,Tail1,Tail2,LegBackL,Ear1,Nose,Main,Ear2,HatBuckle,HatC,HatB,HatA,HatBase,LegBackR,LegFrontL,LegFrontR;private int field_78163_i=1;
 public FamiliarModel(ModelPart root){this.root=root;this.Body=root.getChild("Body");this.Tail1=root.getChild("Tail1");this.Tail2=root.getChild("Tail2");this.LegBackL=root.getChild("LegBackL");this.Ear1=root.getChild("Ear1");this.Nose=root.getChild("Nose");this.Main=root.getChild("Main");this.Ear2=root.getChild("Ear2");this.HatBuckle=root.getChild("HatBuckle");this.HatC=root.getChild("HatC");this.HatB=root.getChild("HatB");this.HatA=root.getChild("HatA");this.HatBase=root.getChild("HatBase");this.LegBackR=root.getChild("LegBackR");this.LegFrontL=root.getChild("LegFrontL");this.LegFrontR=root.getChild("LegFrontR");}
 public static LayerDefinition layer(){var mesh=new MeshDefinition();var root=mesh.getRoot();
root.addOrReplaceChild("Body",CubeListBuilder.create().texOffs(20,0).addBox(-2.0f, 3.0f, -8.0f, 4, 16, 6),PartPose.offsetAndRotation(0.0f, 12.0f, -10.0f,((float) Math.PI / 2F), 0.0f, 0.0f));
root.addOrReplaceChild("Tail1",CubeListBuilder.create().texOffs(0,15).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1),PartPose.offsetAndRotation(0.0f, 15.0f, 8.0f,((float) Math.PI / 2F), 0.0f, 0.0f));
root.addOrReplaceChild("Tail2",CubeListBuilder.create().texOffs(4,15).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1),PartPose.offsetAndRotation(0.0f, 15.0f, 14.0f,((float) Math.PI / 2F), 0.0f, 0.0f));
root.addOrReplaceChild("LegBackL",CubeListBuilder.create().texOffs(8,13).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2),PartPose.offsetAndRotation(1.1f, 18.0f, 5.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Ear1",CubeListBuilder.create().texOffs(0,10).addBox(-2.0f, -3.0f, 0.0f, 1, 1, 2),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Nose",CubeListBuilder.create().texOffs(0,24).addBox(-1.5f, 0.0f, -4.0f, 3, 2, 2),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Main",CubeListBuilder.create().texOffs(0,0).addBox(-2.5f, -2.0f, -3.0f, 5, 4, 5),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("Ear2",CubeListBuilder.create().texOffs(6,10).addBox(1.0f, -3.0f, 0.0f, 1, 1, 2),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("HatBuckle",CubeListBuilder.create().texOffs(48,13).addBox(-1.0f, -4.0f, -3.0f, 2, 1, 1),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("HatC",CubeListBuilder.create().texOffs(48,11).addBox(-0.5f, -7.0f, -1.0f, 1, 1, 1),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("HatB",CubeListBuilder.create().texOffs(48,8).addBox(-1.0f, -6.0f, -1.5f, 2, 1, 2),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("HatA",CubeListBuilder.create().texOffs(48,0).addBox(-1.5f, -5.0f, -2.0f, 3, 2, 3),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("HatBase",CubeListBuilder.create().texOffs(16,24).addBox(-3.0f, -3.0f, -3.5f, 6, 1, 6),PartPose.offsetAndRotation(0.0f, 15.0f, -9.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("LegBackR",CubeListBuilder.create().texOffs(8,13).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2),PartPose.offsetAndRotation(-1.1f, 18.0f, 5.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("LegFrontL",CubeListBuilder.create().texOffs(40,0).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2),PartPose.offsetAndRotation(1.2f, 13.8f, -5.0f,0.0f, 0.0f, 0.0f));
root.addOrReplaceChild("LegFrontR",CubeListBuilder.create().texOffs(40,0).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2),PartPose.offsetAndRotation(-1.2f, 13.8f, -5.0f,0.0f, 0.0f, 0.0f));return LayerDefinition.create(mesh,64,32);}
 @Override public void renderToBuffer(PoseStack pose,VertexConsumer buffer,int light,int overlay,int color){root.render(pose,buffer,light,overlay,color);}
 @Override public void setupAnim(Cat p_78087_7_,float p_78087_1_,float p_78087_2_,float p_78087_3_,float p_78087_4_,float p_78087_5_){

        this.Main.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.Main.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.Nose.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.Nose.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.Ear1.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.Ear1.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.Ear2.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.Ear2.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.HatA.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.HatA.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.HatB.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.HatB.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.HatC.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.HatC.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.HatBuckle.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.HatBuckle.yRot = p_78087_4_ / (180F / (float) Math.PI);
        this.HatBase.xRot = p_78087_5_ / (180F / (float) Math.PI);
        this.HatBase.yRot = p_78087_4_ / (180F / (float) Math.PI);
        if (this.field_78163_i != 3) {
            this.Body.xRot = ((float) Math.PI / 2F);
            if (this.field_78163_i == 2) {
                this.LegBackL.xRot = Mth.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                this.LegBackR.xRot = Mth.cos(p_78087_1_ * 0.6662f + 0.3f) * 1.0f * p_78087_2_;
                this.LegFrontL.xRot = Mth.cos(p_78087_1_ * 0.6662f + (float) Math.PI + 0.3f) * 1.0f
                        * p_78087_2_;
                this.LegFrontR.xRot = Mth.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f
                        * p_78087_2_;
                this.Tail2.xRot = 1.7278761f
                        + ((float) Math.PI / 10F) * Mth.cos(p_78087_1_) * p_78087_2_;
            } else {
                this.LegBackL.xRot = Mth.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                this.LegBackR.xRot = Mth.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f * p_78087_2_;
                this.LegFrontL.xRot = Mth.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f
                        * p_78087_2_;
                this.LegFrontR.xRot = Mth.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                if (this.field_78163_i == 1) {
                    this.Tail2.xRot = 1.7278761f
                            + ((float) Math.PI / 4F) * Mth.cos(p_78087_1_) * p_78087_2_;
                } else {
                    this.Tail2.xRot = 1.7278761f + 0.47123894f * Mth.cos(p_78087_1_) * p_78087_2_;
                }
            }
        }
    }
 @Override public void prepareMobModel(Cat p_78086_1_,float p_78086_2_,float p_78086_3_,float p_78086_4_){

        final Cat entityocelot = (Cat) p_78086_1_;
        this.Body.y = 12.0f;
        this.Body.z = -10.0f;
        this.Main.y = 15.0f;
        this.Main.z = -9.0f;
        this.Nose.y = 15.0f;
        this.Nose.z = -9.0f;
        this.Ear1.y = 15.0f;
        this.Ear1.z = -9.0f;
        this.Ear2.y = 15.0f;
        this.Ear2.z = -9.0f;
        this.HatBase.y = 15.0f;
        this.HatBase.z = -9.0f;
        this.HatB.y = 15.0f;
        this.HatB.z = -9.0f;
        this.HatA.y = 15.0f;
        this.HatA.z = -9.0f;
        this.HatC.y = 15.0f;
        this.HatC.z = -9.0f;
        this.HatBuckle.y = 15.0f;
        this.HatBuckle.z = -9.0f;
        this.Tail1.y = 15.0f;
        this.Tail1.z = 8.0f;
        this.Tail2.y = 20.0f;
        this.Tail2.z = 14.0f;
        final ModelPart legFrontL = this.LegFrontL;
        final ModelPart legFrontR = this.LegFrontR;
        final float n = 13.8f;
        legFrontR.y = n;
        legFrontL.y = n;
        final ModelPart legFrontL2 = this.LegFrontL;
        final ModelPart legFrontR2 = this.LegFrontR;
        final float n2 = -5.0f;
        legFrontR2.z = n2;
        legFrontL2.z = n2;
        final ModelPart legBackL = this.LegBackL;
        final ModelPart legBackR = this.LegBackR;
        final float n3 = 18.0f;
        legBackR.y = n3;
        legBackL.y = n3;
        final ModelPart legBackL2 = this.LegBackL;
        final ModelPart legBackR2 = this.LegBackR;
        final float n4 = 5.0f;
        legBackR2.z = n4;
        legBackL2.z = n4;
        this.Tail1.xRot = 0.9f;
        if (entityocelot.isCrouching()) {
            final ModelPart body = this.Body;
            ++body.y;
            final ModelPart main = this.Main;
            main.y += 2.0f;
            final ModelPart nose = this.Nose;
            nose.y += 2.0f;
            final ModelPart ear1 = this.Ear1;
            ear1.y += 2.0f;
            final ModelPart ear2 = this.Ear2;
            ear2.y += 2.0f;
            final ModelPart hatBase = this.HatBase;
            hatBase.y += 2.0f;
            final ModelPart hatA = this.HatA;
            hatA.y += 2.0f;
            final ModelPart hatB = this.HatB;
            hatB.y += 2.0f;
            final ModelPart hatC = this.HatC;
            hatC.y += 2.0f;
            final ModelPart hatBuckle = this.HatBuckle;
            hatBuckle.y += 2.0f;
            final ModelPart tail1 = this.Tail1;
            ++tail1.y;
            final ModelPart tail2 = this.Tail2;
            tail2.y -= 4.0f;
            final ModelPart tail3 = this.Tail2;
            tail3.z += 2.0f;
            this.Tail1.xRot = ((float) Math.PI / 2F);
            this.Tail2.xRot = ((float) Math.PI / 2F);
            this.field_78163_i = 0;
        } else if (entityocelot.isSprinting()) {
            this.Tail2.y = this.Tail1.y;
            final ModelPart tail4 = this.Tail2;
            tail4.z += 2.0f;
            this.Tail1.xRot = ((float) Math.PI / 2F);
            this.Tail2.xRot = ((float) Math.PI / 2F);
            this.field_78163_i = 2;
        } else if (entityocelot.isInSittingPose()) {
            this.Body.xRot = ((float) Math.PI / 4F);
            final ModelPart body2 = this.Body;
            body2.y -= 4.0f;
            final ModelPart body3 = this.Body;
            body3.z += 5.0f;
            final ModelPart main2 = this.Main;
            main2.y -= 3.3f;
            final ModelPart main3 = this.Main;
            ++main3.z;
            final ModelPart nose2 = this.Nose;
            nose2.y -= 3.3f;
            final ModelPart nose3 = this.Nose;
            ++nose3.z;
            final ModelPart ear3 = this.Ear1;
            ear3.y -= 3.3f;
            final ModelPart ear4 = this.Ear1;
            ++ear4.z;
            final ModelPart ear5 = this.Ear2;
            ear5.y -= 3.3f;
            final ModelPart ear6 = this.Ear2;
            ++ear6.z;
            final ModelPart hatBase2 = this.HatBase;
            hatBase2.y -= 3.3f;
            final ModelPart hatBase3 = this.HatBase;
            ++hatBase3.z;
            final ModelPart hatA2 = this.HatA;
            hatA2.y -= 3.3f;
            final ModelPart hatA3 = this.HatA;
            ++hatA3.z;
            final ModelPart hatB2 = this.HatB;
            hatB2.y -= 3.3f;
            final ModelPart hatB3 = this.HatB;
            ++hatB3.z;
            final ModelPart hatC2 = this.HatC;
            hatC2.y -= 3.3f;
            final ModelPart hatC3 = this.HatC;
            ++hatC3.z;
            final ModelPart hatBuckle2 = this.HatBuckle;
            hatBuckle2.y -= 3.3f;
            final ModelPart hatBuckle3 = this.HatBuckle;
            ++hatBuckle3.z;
            final ModelPart tail5 = this.Tail1;
            tail5.y += 8.0f;
            final ModelPart tail6 = this.Tail1;
            tail6.z -= 2.0f;
            final ModelPart tail7 = this.Tail2;
            tail7.y += 2.0f;
            final ModelPart tail8 = this.Tail2;
            tail8.z -= 0.8f;
            this.Tail1.xRot = 1.7278761f;
            this.Tail2.xRot = 2.670354f;
            final ModelPart legFrontL3 = this.LegFrontL;
            final ModelPart legFrontR3 = this.LegFrontR;
            final float n5 = -0.15707964f;
            legFrontR3.xRot = n5;
            legFrontL3.xRot = n5;
            final ModelPart legFrontL4 = this.LegFrontL;
            final ModelPart legFrontR4 = this.LegFrontR;
            final float n6 = 15.8f;
            legFrontR4.y = n6;
            legFrontL4.y = n6;
            final ModelPart legFrontL5 = this.LegFrontL;
            final ModelPart legFrontR5 = this.LegFrontR;
            final float n7 = -7.0f;
            legFrontR5.z = n7;
            legFrontL5.z = n7;
            final ModelPart legBackL3 = this.LegBackL;
            final ModelPart legBackR3 = this.LegBackR;
            final float n8 = -((float) Math.PI / 2F);
            legBackR3.xRot = n8;
            legBackL3.xRot = n8;
            final ModelPart legBackL4 = this.LegBackL;
            final ModelPart legBackR4 = this.LegBackR;
            final float n9 = 21.0f;
            legBackR4.y = n9;
            legBackL4.y = n9;
            final ModelPart legBackL5 = this.LegBackL;
            final ModelPart legBackR5 = this.LegBackR;
            final float n10 = 1.0f;
            legBackR5.z = n10;
            legBackL5.z = n10;
            this.field_78163_i = 3;
        } else {
            this.field_78163_i = 1;
        }
    }
}
