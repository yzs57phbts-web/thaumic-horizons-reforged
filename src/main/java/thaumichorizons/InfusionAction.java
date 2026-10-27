package thaumichorizons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.effect.MobEffects;

public record InfusionAction(int action) implements CustomPacketPayload {
    public static final Type<InfusionAction> TYPE=new Type<>(ThaumicHorizons.id("infusion_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf,InfusionAction> CODEC=StreamCodec.composite(ByteBufCodecs.VAR_INT,InfusionAction::action,InfusionAction::new);
    @Override public Type<InfusionAction> type(){return TYPE;}
    public void run(Player p){
        if(!p.isAlive()||p.isSpectator()||p.level().isClientSide())return;
        if(action==0&&Infusions.has(p,2))p.openMenu(new SimpleMenuProvider((id,inv,player)->new CraftingMenu(id,inv,ContainerLevelAccess.NULL){@Override public boolean stillValid(Player player){return player.isAlive()&&Infusions.has(player,2);}},Component.translatable("container.crafting")));
        else if(action==1&&Infusions.has(p,9)){var tag=Infusions.DATA.get(p);tag.putBoolean("climbDisabled",!tag.getBoolean("climbDisabled"));Infusions.DATA.sync(p);}
        else if(action==2&&Infusions.has(p,10)){var tag=Infusions.DATA.get(p);tag.putBoolean("invisibilityDisabled",!tag.getBoolean("invisibilityDisabled"));Infusions.DATA.sync(p);if(tag.getBoolean("invisibilityDisabled"))p.removeEffect(MobEffects.INVISIBILITY);else Infusions.apply(p);}
    }
}
