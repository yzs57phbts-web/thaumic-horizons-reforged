package thaumichorizons;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.*;
/** Alpha applies to every layer, including wool, armour and eyes, without changing its UVs. */
final class FadeBuffer implements MultiBufferSource {
    static int rendered;
    private final MultiBufferSource buffers;private final float alpha;
    FadeBuffer(MultiBufferSource buffers,float alpha){this.buffers=buffers;this.alpha=alpha;rendered++;}
    @Override public VertexConsumer getBuffer(RenderType type){
        if(type instanceof RenderType.CompositeRenderType composite){var texture=composite.state().textureState.cutoutTexture();if(texture.isPresent())type=RenderType.entityTranslucent(texture.get());}
        return new Alpha(buffers.getBuffer(type),alpha);
    }
    private record Alpha(VertexConsumer sink,float opacity) implements VertexConsumer {
        public VertexConsumer addVertex(float x,float y,float z){sink.addVertex(x,y,z);return this;}
        public VertexConsumer setColor(int r,int g,int b,int a){sink.setColor(r,g,b,Math.clamp((int)(a*opacity),0,255));return this;}
        public VertexConsumer setUv(float u,float v){sink.setUv(u,v);return this;}
        public VertexConsumer setUv1(int u,int v){sink.setUv1(u,v);return this;}
        public VertexConsumer setUv2(int u,int v){sink.setUv2(u,v);return this;}
        public VertexConsumer setNormal(float x,float y,float z){sink.setNormal(x,y,z);return this;}
    }
}
