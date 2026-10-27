package thaumichorizons;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.*;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.fx.*;
import thaumcraft.client.geom.QuadSink;
import thaumcraft.client.platform.WorldFxRenderer;
/** Original TH particle UVs, timings and movement, through the TC modern particle backend. */
final class LegacyParticles {
    private static final Map<VatEntity,Long> LAST=new WeakHashMap<>();
    static int renderedContainments;
    static void dynamo(Dynamo dynamo){if(dynamo.isEssentia()&&dynamo.ticks>0&&dynamo.fuel!=null){var pos=dynamo.getBlockPos().getCenter();WorldFxRenderer.add(new EssentiaTrailFx(dynamo.getLevel().random,pos.x,pos.y-.5,pos.z,pos.x,pos.y,pos.z,(int)dynamo.getLevel().getGameTime(),dynamo.fuel.getColor(),.3F));}}
    static void containment(ContainmentFx packet){var mc=Minecraft.getInstance();if(mc.level!=null)WorldFxRenderer.add(new Containment(mc.level.random,packet.x(),packet.y(),packet.z()));}
    static void vat(VatEntity vat){
        if(!vat.isController()||vat.effectTime<=0||vat.getLevel().getGameTime()-vat.effectTime>10||Objects.equals(LAST.get(vat),vat.effectTime))return;LAST.put(vat,vat.effectTime);
        var level=vat.getLevel();var center=vat.getBlockPos().getCenter().add(0,-1.5,0);int count=(int)level.getGameTime();
        for(int i=0;i<10;i++)WorldFxRenderer.add(new Bubble(level.random,center.x+(level.random.nextFloat()-.5)*1.5,center.y-1,center.z+(level.random.nextFloat()-.5)*1.5,count,vat.essentiaColor,.3F,i*2+2));
        if(vat.suckingFrom!=null){var from=vat.suckingFrom.getCenter();for(int i=0;i<4;i++)WorldFxRenderer.add(new EssentiaTrailFx(level.random,from.x,from.y,from.z,center.x,center.y,center.z,count+i,vat.essentiaColor,.8F));}
    }
    static final class Containment extends ParticleFx {
        Containment(RandomSource random,double x,double y,double z){super(random,x,y,z,40);scale=1;}
        public ResourceLocation texture(){return ResourceLocation.parse("thaumcraft:textures/misc/eldritch_portal.png");}
        public int blendMode(){return 771;}
        public void tick(){super.tick();scale/=1.15F;}
        public void render(QuadSink sink,float partial,Vec3 camera,float yaw,float pitch){renderedContainments++;int frame=(int)Math.floorMod(System.nanoTime()/50_000_000L,16);billboard(sink,partial,camera,yaw,pitch,3*scale,frame/16F,0,(frame+1)/16F,1,.1F);}
    }
    static final class Bubble extends ParticleFx {
        private final int count;private int delay;
        Bubble(RandomSource random,double x,double y,double z,int count,int color,float factor,int delay){super(random,x,y,z,20+random.nextInt(20));this.count=count;this.delay=delay;scale=(Mth.sin(count/2F)*.1F+1)*factor;motionY=.025F+Mth.sin(count/3F)*.002F;red=(color>>16&255)/255F*(.8F+random.nextFloat()*.2F);green=(color>>8&255)/255F*(.8F+random.nextFloat()*.2F);blue=(color&255)/255F*(.8F+random.nextFloat()*.2F);}
        public int blendMode(){return 771;}
        public void tick(){prevX=x;prevY=y;prevZ=z;if(delay>0){delay--;return;}age++;motionY+=.00125;scale*=1.05F;y+=motionY;motionY*=.985;}
        public void render(QuadSink sink,float partial,Vec3 camera,float yaw,float pitch){if(delay>0)return;float pulse=Mth.sin((age-count)/5F)*.25F+1;billboard(sink,partial,camera,yaw,pitch,.1F*scale*pulse,.5625F,.0625F,.625F,.125F,.5F);}
    }
}
