package thaumichorizons;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT)
public final class VortexSound extends AbstractTickableSoundInstance {
    private static final Map<Vortex,VortexSound> SOUNDS=new IdentityHashMap<>();
    private final Vortex tile;
    private VortexSound(Vortex tile){super(Vortex.SOUND.get(),SoundSource.AMBIENT,RandomSource.create());this.tile=tile;looping=true;delay=0;volume=1;x=tile.getBlockPos().getX()+.5;y=tile.getBlockPos().getY()+.5;z=tile.getBlockPos().getZ()+.5;}
    static void ensure(Vortex tile){if(!SOUNDS.containsKey(tile)){var sound=new VortexSound(tile);SOUNDS.put(tile,sound);Minecraft.getInstance().getSoundManager().play(sound);}}
    @Override public void tick(){var mc=Minecraft.getInstance();if(tile.isRemoved()||tile.getLevel()!=mc.level){stop();return;}if(mc.player!=null)volume=1/(1+(float)(mc.player.position().distanceTo(tile.getBlockPos().getCenter())/8));}
    @SubscribeEvent public static void clean(ClientTickEvent.Post event){var level=Minecraft.getInstance().level;SOUNDS.entrySet().removeIf(entry->{if(entry.getKey().isRemoved()||entry.getKey().getLevel()!=level){entry.getValue().stop();return true;}return false;});}
}
