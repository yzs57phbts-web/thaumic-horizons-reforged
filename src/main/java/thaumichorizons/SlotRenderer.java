package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
public final class SlotRenderer implements BlockEntityRenderer<Gateways.SlotEntity> {
    private final ModelPart model;
    public SlotRenderer(BlockEntityRendererProvider.Context context){model=context.bakeLayer(SlotModel.LAYER);}
    static void draw(ModelPart model,boolean hasKey,int color,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.translate(.5,-.5,.5);var key=model.getChild("Keystone");key.visible=false;var consumer=buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/receptacle.png")));model.render(pose,consumer,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);if(hasKey){key.visible=true;key.render(pose,consumer,light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,0xFF000000|(color&0xFFFFFF));}pose.popPose();}
    @Override public void render(Gateways.SlotEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){draw(model,!entity.contained.isEmpty(),Gateways.color(entity.contained),pose,buffers,light);}
}
