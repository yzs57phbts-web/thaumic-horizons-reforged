package thaumichorizons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;

public final class BloodRenderer implements BlockEntityRenderer<BloodInfuser> {
    private final ModelPart model,syringe;
    public BloodRenderer(BlockEntityRendererProvider.Context context){model=context.bakeLayer(BloodModel.LAYER);syringe=context.bakeLayer(SyringeModel.LAYER);}
    static void draw(ModelPart model,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();pose.translate(.5,1.5,.5);pose.mulPose(Axis.XP.rotationDegrees(180));
        model.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/bloodinfuser.png"))),light,overlay);pose.popPose();
    }
    @Override public void render(BloodInfuser be,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(model,pose,buffers,light,overlay);
        if(be.hasBlood()){
            pose.pushPose();pose.translate(1.90625,.75,.46875);pose.mulPose(Axis.ZP.rotationDegrees(90));
            drawSyringe(syringe,pose,buffers,light,overlay,0xBFFF0000);pose.popPose();
        }
    }
    static void drawSyringe(ModelPart model,PoseStack pose,MultiBufferSource buffers,int light,int overlay,int color){
        var texture=ThaumicHorizons.id("textures/models/syringe.png");
        model.getChild("body").render(pose,buffers.getBuffer(RenderType.entityTranslucent(texture)),light,overlay,color);
        for(String name:java.util.List.of("needle","plunger_a","plunger_b"))model.getChild(name).render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(texture)),light,overlay);
    }
    static void drawSyringeItem(net.minecraft.world.item.ItemStack s,net.minecraft.world.item.ItemDisplayContext display,ModelPart model,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();pose.translate(.5,.5,.5);
        // Legacy model uses pixels at y=21..24; centre those coordinates for the modern item transform.
        pose.translate(0,-1.40625,0);int color=s.is(Content.item("empty_syringe"))?0x80FFFFFF:s.has(net.minecraft.core.component.DataComponents.POTION_CONTENTS)?0xBF000000|s.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS).getColor():0xBFFF0000;
        drawSyringe(model,pose,buffers,light,overlay,color);pose.popPose();
    }
}
