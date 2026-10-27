package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.ItemGeometry;
import thaumcraft.client.platform.VertexQuadSink;
public final class RareCreatureRenderers {
    public static final class Slime extends SlimeRenderer {
        public Slime(EntityRendererProvider.Context context){super(context);}
        @Override public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Slime entity){String form=((RareCreatures.ElementalSlime)entity).form;return ThaumicHorizons.id("textures/entity/"+form.replace("_","")+".png");}
    }
    public static final class Soul extends EntityRenderer<RareCreatures.LostSoul>{
        public Soul(EntityRendererProvider.Context context){super(context);}
        @Override public ResourceLocation getTextureLocation(RareCreatures.LostSoul entity){return ThaumicHorizons.id("textures/misc/soul.png");}
        @Override public void render(RareCreatures.LostSoul entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){
            pose.pushPose();pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());float u=(entity.tickCount%16)/16F;var type=thaumcraft.client.platform.TcRenderTypes.additive(getTextureLocation(entity));
            ItemGeometry.submit(buffers,pose,type,(matrix,consumer)->{var sink=new VertexQuadSink(matrix,consumer);sink.vertex(-.25F,-.25F,0,u,1,-1,LightTexture.FULL_BRIGHT);sink.vertex(.25F,-.25F,0,u+1/16F,1,-1,LightTexture.FULL_BRIGHT);sink.vertex(.25F,.25F,0,u+1/16F,0,-1,LightTexture.FULL_BRIGHT);sink.vertex(-.25F,.25F,0,u,0,-1,LightTexture.FULL_BRIGHT);});pose.popPose();super.render(entity,yaw,partial,pose,buffers,light);
        }
    }
}
