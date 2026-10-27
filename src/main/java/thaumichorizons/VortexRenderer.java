package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.geom.*;
import thaumcraft.client.platform.*;
public final class VortexRenderer implements BlockEntityRenderer<Vortex> {
    private static final ResourceLocation NODES=ResourceLocation.parse("thaumcraft:textures/misc/nodes.png"),VORTEX=ResourceLocation.parse("thaumcraft:textures/misc/vortex.png");
    public VortexRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(Vortex node,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        var mc=Minecraft.getInstance();if(mc.player==null||node.aspects.size()==0)return;double distance=mc.player.position().distanceTo(node.getBlockPos().getCenter());if(distance>64)return;float alpha=(float)((64-distance)/64);var view=thaumcraft.client.render.WorldRendering.camera(mc.gameRenderer.getMainCamera(),node.getBlockPos());long time=System.nanoTime();int frame=Math.floorMod(time/40000000L+node.getBlockPos().getX(),32),index=0;float angle=0;
        for(var aspect:node.aspects.getAspects()){
            long period=(5000L+500L*index)*5_000_000L;angle=(float)((time%period)/(double)period*Math.PI*2);float size=node.beams<6||node.count<50?4:4/3F;float opacity=alpha*(aspect.getBlend()==771?1.5F:1)/Math.max(1,node.aspects.size()/2F);strip(pose,buffers,view,size,angle,frame,0,aspect.getColor(),opacity,aspect.getBlend()==771);index++;
        }
        if(!node.cheat&&(node.beams<6||node.count<50)){
            float scale=node.collapsing?Math.max(0,1-(node.count+partial)/25F):node.count<50?Math.min(1,(node.count+partial)/50F):1;float rotation=angle*20*scale/(1+2*node.beams);pose.pushPose();pose.translate(.5,.5,.5);pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());pose.mulPose(Axis.ZP.rotation(rotation));float radius=.8F*scale;
            ItemGeometry.submit(buffers,pose,TcRenderTypes.translucent(VORTEX),(matrix,consumer)->{var sink=new VertexQuadSink(matrix,consumer);int color=0xCCFFFFFF;sink.vertex(-radius,-radius,0,0,1,color,LightTexture.FULL_BRIGHT);sink.vertex(radius,-radius,0,1,1,color,LightTexture.FULL_BRIGHT);sink.vertex(radius,radius,0,1,0,color,LightTexture.FULL_BRIGHT);sink.vertex(-radius,radius,0,0,0,color,LightTexture.FULL_BRIGHT);});pose.popPose();
        }else strip(pose,buffers,view,3,angle,frame,2,0xFFFFFF,alpha,true);
        if(node.createdDimension)for(var aspect:node.aspects.getAspects()){long period=(5000L+500L*index++)*5_000_000L;float rotation=(float)((time%period)/(double)period*Math.PI*2);strip(pose,buffers,view,.5F,rotation,frame,0,aspect.getColor(),alpha/Math.max(1,node.aspects.size()/2F),true);}
    }
    private static void strip(PoseStack pose,MultiBufferSource buffers,FacingQuads.Camera view,float size,float angle,int frame,int strip,int rgb,float alpha,boolean translucent){int color=(Math.clamp((int)(alpha*255),0,255)<<24)|(rgb&0xFFFFFF);ItemGeometry.submit(buffers,pose,translucent?TcRenderTypes.translucent(NODES):TcRenderTypes.additive(NODES),(matrix,consumer)->FacingQuads.strip(new VertexQuadSink(matrix,consumer),view,.5F,.5F,.5F,angle,size,32,strip,frame,color,LightTexture.FULL_BRIGHT));}
    @Override public boolean shouldRenderOffScreen(Vortex node){return true;}
    @Override public int getViewDistance(){return 64;}
}
