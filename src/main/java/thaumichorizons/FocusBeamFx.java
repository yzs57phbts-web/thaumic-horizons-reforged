package thaumichorizons;
import java.util.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.fx.WandBeamFx;
import thaumcraft.client.platform.WorldFxRenderer;
final class FocusBeamFx {
    private record Entry(int color,WandBeamFx beam){}
    private static final Map<Player,Entry> BEAMS=new WeakHashMap<>();
    static void containment(Level level,Player player,Vec3 end,int color,int impact){draw(level,player,end,color,impact);var beam=BEAMS.get(player).beam();beam.setType(2);beam.setReverse(true);beam.setWidth(2);}
    static void draw(Level level,Player player,Vec3 end,int color,int impact){
        var entry=BEAMS.get(player);WandBeamFx beam;
        if(entry!=null&&entry.color()==color&&entry.beam().isFor(player)){beam=entry.beam();beam.retarget(end.x,end.y,end.z);}
        else{beam=new WandBeamFx(level,player,end.x,end.y,end.z,(color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,8);beam.setType(0);beam.setReverse(false);WorldFxRenderer.add(beam);BEAMS.put(player,new Entry(color,beam));}
        beam.setEndMod(impact);beam.setImpact(5);BEAMS.entrySet().removeIf(e->e.getValue().beam().isDead());
    }
}
