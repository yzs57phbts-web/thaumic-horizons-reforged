package thaumichorizons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.ItemGeometry;

public final class SoulMachineRenderer implements BlockEntityRenderer<SoulMachine> {
    private final ModelPart sieve,inspiratron,forge,brain,brine;
    public SoulMachineRenderer(BlockEntityRendererProvider.Context ctx){sieve=ctx.bakeLayer(SoulModels.SIEVE);inspiratron=ctx.bakeLayer(SoulModels.INSPIRATRON);forge=ctx.bakeLayer(SoulModels.FORGE);brain=ctx.bakeLayer(SoulModels.BRAIN);brine=ctx.bakeLayer(SoulModels.BRINE);}
    @Override public void render(SoulMachine tile,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        draw(tile.kind(),sieve,inspiratron,forge,brain,brine,tile.sieveMotion,tile.ticksLeft,tile.kind()==1?tile.previousRotation+partial*net.minecraft.util.Mth.wrapDegrees((tile.rotation-tile.previousRotation)*180/(float)Math.PI)*((float)Math.PI/180):tile.rotation,tile.getLevel().getGameTime()+partial,pose,buffers,light,overlay);
        if(tile.kind()==2){
            double rad=Math.toRadians((tile.getLevel().getGameTime()+partial)*1.25%360),dist=.1+.1*Math.cos(rad);
            for(int i=0;i<tile.souls;i++)soul(pose,buffers,.5+dist*Math.sin(2*rad+Math.PI*2*i/tile.souls),.85,.5+dist*Math.cos(2*rad+Math.PI*2*i/tile.souls),.1F,tile.getLevel().getGameTime()+partial);
            if(tile.forging>0)for(double x:new double[]{.025,.975})for(double z:new double[]{.025,.975})billboard(pose,buffers,x,.75,z,.2F,ResourceLocation.parse("thaumcraft:textures/item/lightningringv.png"),16,tile.getLevel().getGameTime()+partial,.9F,false);
        }else if(tile.kind()==0&&tile.extracting&&tile.sieveMotion==0&&Minecraft.getInstance().level!=null)Minecraft.getInstance().level.addParticle(net.minecraft.core.particles.ParticleTypes.SOUL,tile.getBlockPos().getX()+.5,tile.getBlockPos().getY()+.8,tile.getBlockPos().getZ()+.5,0,.01,0);
    }
    static void draw(int kind,ModelPart sieve,ModelPart inspiratron,ModelPart forge,ModelPart brain,ModelPart brine,int motion,int ticks,float rotation,float time,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        pose.pushPose();pose.translate(.5,1.5,.5);pose.mulPose(Axis.XP.rotationDegrees(180));
        if(kind==0){
            var vertex=buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/soulsieve.png")));
            for(String name:new String[]{"TopSide1","TopSide2","TopSide3","TopSide4","TopBottom","Middle","Bottom"})sieve.getChild(name).render(pose,vertex,light,overlay);
            if(ticks>0)sieve.getChild("Sand").render(pose,vertex,light,overlay);
            pose.pushPose();pose.translate(0,-.15625F*(Math.cos(Math.toRadians(motion))-1),0);
            for(String name:new String[]{"SieveSide1","SieveSide2","SieveSide3","SieveSide4","SieveCenter1","SieveCenter2","SieveCenter3","SieveCenter4","SieveOuter1A","SieveOuter1B","SieveOuter2A","SieveOuter2B","SieveOuter3A","SieveOuter3B","SieveOuter4A","SieveOuter4B"})sieve.getChild(name).render(pose,vertex,light,overlay);
            pose.popPose();
        }else{
            pose.pushPose();pose.translate(0,1.5,0);
            if(kind==1){pose.translate(0,-.125,0);pose.pushPose();pose.translate(0,-.8+Math.sin(time/14)*.03+.03,0);pose.mulPose(Axis.YP.rotation(rotation));pose.mulPose(Axis.YP.rotationDegrees(-90));pose.scale(.4F,.4F,.4F);brain.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/brain.png"))),light,overlay);pose.popPose();brine.getChild("jar").render(pose,buffers.getBuffer(RenderType.entityTranslucent(ThaumicHorizons.id("textures/models/jarbrine.png"))),light,overlay);}
            else{pose.translate(0,-.25,0);for(int sign:new int[]{1,-1}){pose.pushPose();pose.mulPose(Axis.YP.rotationDegrees(rotation));pose.mulPose(Axis.ZP.rotationDegrees(-90));pose.mulPose(Axis.XP.rotationDegrees(sign*90));pose.translate(0,-.55,0);pose.scale(.4F,.4F,.4F);brain.render(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(ThaumicHorizons.id("textures/models/brain.png"))),light,overlay);pose.popPose();}pose.translate(0,-1.25,0);brine.getChild("forge").render(pose,buffers.getBuffer(RenderType.entityTranslucent(ThaumicHorizons.id("textures/models/jarbrine2.png"))),light,overlay);}
            pose.popPose();(kind==1?inspiratron:forge).render(pose,buffers.getBuffer(RenderType.entityTranslucent(ThaumicHorizons.id("textures/models/"+(kind==1?"inspiratron":"soulforge")+".png"))),light,overlay);
        }pose.popPose();
    }
    static void soul(PoseStack pose,MultiBufferSource buffers,double x,double y,double z,float size,float ticks){billboard(pose,buffers,x,y,z,size,ThaumicHorizons.id("textures/misc/soul.png"),16,ticks,.9F,true);}
    static void billboard(PoseStack pose,MultiBufferSource buffers,double x,double y,double z,float size,ResourceLocation texture,int frames,float ticks,float alpha,boolean ignoreDepth){
        pose.pushPose();pose.translate(x,y,z);pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());float u=((int)(ticks*1.25)%frames)/(float)frames,u1=u+1F/frames;int color=((int)(alpha*255)<<24)|0xFFFFFF;
        var type=ignoreDepth?thaumcraft.client.platform.TcRenderTypes.translucentNoDepth(texture):thaumcraft.client.platform.TcRenderTypes.translucent(texture);
        ItemGeometry.submit(buffers,pose,type,(matrix,consumer)->{var s=new thaumcraft.client.platform.VertexQuadSink(matrix,consumer);s.vertex(-size,-size,0,u,1,color,LightTexture.FULL_BRIGHT);s.vertex(size,-size,0,u1,1,color,LightTexture.FULL_BRIGHT);s.vertex(size,size,0,u1,0,color,LightTexture.FULL_BRIGHT);s.vertex(-size,size,0,u,0,color,LightTexture.FULL_BRIGHT);});pose.popPose();
    }
}
