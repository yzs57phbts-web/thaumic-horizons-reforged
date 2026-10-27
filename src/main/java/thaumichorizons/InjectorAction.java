package thaumichorizons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
public record InjectorAction(int hand) implements CustomPacketPayload {
    public static final Type<InjectorAction> TYPE=new Type<>(ThaumicHorizons.id("injector_magazine"));
    public static final StreamCodec<RegistryFriendlyByteBuf,InjectorAction> CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,InjectorAction::hand,InjectorAction::new);
    @Override public Type<InjectorAction> type(){return TYPE;}
    public void run(Player player){if(hand>=0&&hand<2)InjectorItem.open(player,InteractionHand.values()[hand]);}
}
