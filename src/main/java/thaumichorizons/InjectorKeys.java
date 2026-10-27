package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT)
public final class InjectorKeys {
    static final KeyMapping MAGAZINE=new KeyMapping("key.thaumichorizons.injector",70,"key.categories.thaumichorizons");
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.HIGHEST) public static void tick(ClientTickEvent.Pre event){
        var mc=Minecraft.getInstance();if(mc.player==null||mc.screen!=null){while(MAGAZINE.consumeClick()){}return;}
        while(MAGAZINE.consumeClick()){
            InteractionHand hand=mc.player.getMainHandItem().getItem() instanceof InjectorItem?InteractionHand.MAIN_HAND:mc.player.getOffhandItem().getItem() instanceof InjectorItem?InteractionHand.OFF_HAND:null;
            if(hand==null)continue;
            if(MAGAZINE.same(mc.options.keySwapOffhand)){while(mc.options.keySwapOffhand.consumeClick()){}mc.options.keySwapOffhand.setDown(false);}
            var focusKey=thaumcraft.client.wand.WandKeyHandler.CHANGE_FOCUS;if(MAGAZINE.same(focusKey)){while(focusKey.consumeClick()){}focusKey.setDown(false);}
            PacketDistributor.sendToServer(new InjectorAction(hand.ordinal()));
        }
    }
}
