package thaumichorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.client.render.entity.golem.*;
public final class AnimatedGolemRenderer extends GolemRenderer {
    public AnimatedGolemRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public void extractRenderState(GolemBase entity,GolemRenderState state,float partial){super.extractRenderState(entity,state,partial);if(entity instanceof AnimatedGolem golem){state.texture=GolemSkins.get(golem.material());state.awakeWithoutCore=golem.getCore()<0;if(state.awakeWithoutCore)state.bootup=0;}}
}
