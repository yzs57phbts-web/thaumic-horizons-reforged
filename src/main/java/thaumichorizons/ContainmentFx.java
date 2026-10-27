package thaumichorizons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import thaumcraft.platform.ModNetwork;
record ContainmentFx(double x,double y,double z) implements CustomPacketPayload {
    static final Type<ContainmentFx> TYPE=new Type<>(ThaumicHorizons.id("containment_fx"));
    static final StreamCodec<RegistryFriendlyByteBuf,ContainmentFx> CODEC=StreamCodec.of((buffer,fx)->{buffer.writeDouble(fx.x);buffer.writeDouble(fx.y);buffer.writeDouble(fx.z);},buffer->new ContainmentFx(buffer.readDouble(),buffer.readDouble(),buffer.readDouble()));
    public Type<ContainmentFx> type(){return TYPE;}
    static void send(ServerLevel level,Vec3 position){ModNetwork.sendToNear(level,null,position.x,position.y,position.z,32,new ContainmentFx(position.x,position.y,position.z));}
}
