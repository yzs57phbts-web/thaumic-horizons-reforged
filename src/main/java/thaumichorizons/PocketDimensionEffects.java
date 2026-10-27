package thaumichorizons;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
final class PocketDimensionEffects extends DimensionSpecialEffects {
    PocketDimensionEffects(){super(Float.NaN,false,SkyType.NONE,false,false);}
    @Override public Vec3 getBrightnessDependentFogColor(Vec3 color,float brightness){return new Vec3(.02734375,.01171875,.16015625);}
    @Override public boolean isFoggyAt(int x,int z){return false;}
}
