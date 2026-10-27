package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.*;
import thaumcraft.client.geom.*;
import thaumcraft.client.platform.*;

public final class SyntheticNodeRenderer implements BlockEntityRenderer<SyntheticNode> {
    public SyntheticNodeRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(SyntheticNode node,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(node.getAspectsBase(),node.getAspects(),node.rotation+partial,node.getBlockPos().getX()+(long)node.getBlockPos().getY()*node.getBlockPos().getZ(),pose,buffers);
        if(node.getLevel().getEntity(node.drainer) instanceof net.minecraft.world.entity.player.Player player&&player.isUsingItem()){
            var start=player.getEyePosition(partial).add(player.getViewVector(partial).scale(.5));var end=node.getBlockPos().getCenter();thaumcraft.client.render.WorldRendering.submitFloatyLine(buffers,pose,node.getBlockPos(),start.x,start.y-.1,start.z,end.x,end.y,end.z,node.drainColor,Math.min(player.getTicksUsingItem(),10)/10F,System.nanoTime());
        }
    }
    static void draw(AspectList capacity,AspectList stored,float rotation,long seed,PoseStack pose,MultiBufferSource buffers){
        long red=255,green=255,blue=255,total=0;if(stored.size()>0)for(var aspect:capacity.getAspects()){int amount=capacity.getAmount(aspect),color=aspect.getColor();red+=((color>>16)&255)*(long)amount;green+=((color>>8)&255)*(long)amount;blue+=(color&255)*(long)amount;total+=amount;}
        int tint=0xFF000000|((int)(red/(total+1))<<16)|((int)(green/(total+1))<<8)|(int)(blue/(total+1));var random=new java.util.Random(seed);int ticks=Minecraft.getInstance().player==null?0:Minecraft.getInstance().player.tickCount;float shade=(float)Math.sin((ticks+random.nextInt(10))/(5F+random.nextFloat()))*.075F+.925F;
        pose.pushPose();pose.translate(.5,-.15+.1*Math.sin(Math.toRadians(rotation)),.5);pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.scale((.15F+random.nextFloat()*.075F)*1.1F,(.5F+random.nextFloat()*.1F)*1.1F,(.15F+random.nextFloat()*.05F)*1.1F);pose.translate(0,2,0);pose.mulPose(Axis.ZP.rotation(.7071F));pose.mulPose(Axis.XP.rotation(.7071F));
        int light=thaumcraft.client.render.WorldRendering.emissiveLight(210*shade);ItemGeometry.submit(buffers,pose,RenderType.entityTranslucent(ResourceLocation.parse("thaumcraft:textures/model/crystal.png")),(matrix,consumer)->ModelBoxGeometry.box(new EntityQuadSink(matrix,consumer),-16,-16,0,16,16,16,0,0,64,32,true,tint,light));pose.popPose();
        if(stored.size()>0&&capacity.size()>0){double step=Math.PI*2/capacity.size(),radian=Math.toRadians(rotation),distance=.4+.1*Math.cos(radian);int i=0;for(var aspect:capacity.getAspects()){
            pose.pushPose();pose.translate(.5+distance*Math.sin(2*radian+step*i),.64+.1*Math.sin(radian),.5+distance*Math.cos(2*radian+step*i));pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());int color=0xE6000000|aspect.getColor();float scale=.1F+.005F*stored.getAmount(aspect);ItemGeometry.submit(buffers,pose,RenderType.entityTranslucent(ResourceLocation.parse("thaumcraft:textures/misc/nodes.png")),(matrix,consumer)->NodeStripGeometry.strip(new EntityQuadSink(matrix,consumer),scale,32,1,(int)rotation%32,color,LightTexture.FULL_BRIGHT));pose.popPose();i++;
        }}
    }
    @Override public boolean shouldRenderOffScreen(SyntheticNode node){return true;}
}
