package thaumichorizons;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.platform.*;
public final class CloudRenderer implements BlockEntityRenderer<Clouds.CloudEntity> {
    private static final int[] COLORS={0xFF2040FF,-1,0xFF2040FF,0xFF20FF40,0xFF404040,0xFFFF4020,0xFFAA40C8,-1,0xFFA0FFA0,0xFFFFE640};
    public CloudRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(Clouds.CloudEntity entity,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){
        if(!entity.raining)return;int kind=entity.kind(),color=COLORS[kind];ResourceLocation texture=kind==1?ThaumicHorizons.id("textures/environment/firerain.png"):entity.cold()?ResourceLocation.withDefaultNamespace("textures/environment/snow.png"):ThaumicHorizons.id("textures/environment/rain.png");float time=entity.getLevel().getGameTime()+partial+entity.getBlockPos().getX()+16*entity.getBlockPos().getZ(),scroll=-time*.2F-(float)Math.floor(-time*.1F),v0=-1+scroll,v1=entity.distance/4F+v0,bottom=-Math.max(0,entity.distance-1);
        ItemGeometry.submit(buffers,pose,TcRenderTypes.translucent(texture),(matrix,consumer)->{var sink=new VertexQuadSink(matrix,consumer);float[][] corners={{0,0},{1,0},{1,1},{0,1}};for(int i=0;i<4;i++){var a=corners[i];var b=corners[(i+1)%4];sink.vertex(a[0],bottom,a[1],1,v1,color,LightTexture.FULL_BRIGHT);sink.vertex(a[0],0,a[1],1,v0,color,LightTexture.FULL_BRIGHT);sink.vertex(b[0],0,b[1],0,v0,color,LightTexture.FULL_BRIGHT);sink.vertex(b[0],bottom,b[1],0,v1,color,LightTexture.FULL_BRIGHT);}});
    }
    @Override public boolean shouldRenderOffScreen(Clouds.CloudEntity entity){return true;}
    @Override public int getViewDistance(){return 256;}
}
