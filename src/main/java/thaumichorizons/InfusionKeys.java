package thaumichorizons;
import net.minecraft.client.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT)
public final class InfusionKeys {
    static final KeyMapping MORPHIC=new KeyMapping("key.thaumichorizons.morphic",77,"key.categories.thaumichorizons");
    static final KeyMapping CLIMB=new KeyMapping("key.thaumichorizons.climb",67,"key.categories.thaumichorizons");
    static final KeyMapping INVISIBILITY=new KeyMapping("key.thaumichorizons.invisibility",88,"key.categories.thaumichorizons");
    @SubscribeEvent public static void tick(ClientTickEvent.Post e){var mc=Minecraft.getInstance();if(mc.player==null)return;while(MORPHIC.consumeClick())if(Infusions.has(mc.player,2))PacketDistributor.sendToServer(new InfusionAction(0));while(CLIMB.consumeClick())if(Infusions.has(mc.player,9))PacketDistributor.sendToServer(new InfusionAction(1));while(INVISIBILITY.consumeClick())if(Infusions.has(mc.player,10))PacketDistributor.sendToServer(new InfusionAction(2));}
}
