package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.wand.WandInteractable;

public final class VatDeviceEntity extends thaumcraft.vis.VisRelayBlockEntity implements WandInteractable {
    public VatDeviceEntity(BlockPos p,BlockState s){super(Vats.DEVICE.get(),p,s);}
    public VatEntity vat(){return level!=null&&level.getBlockEntity(worldPosition.below()) instanceof VatEntity v&&v.isController()?v:null;}
    @Override public int getRange(){return getBlockState().is(Vats.MATRIX.get())?8:0;}
    public static void tick(Level l,BlockPos p,BlockState s,VatDeviceEntity be){if(s.is(Vats.MATRIX.get()))be.tickNetwork(l);}
    @Override public InteractionResult onWandUseOn(Level l,BlockPos p,Player player,ItemStack wand,Direction side){return activate(l,player);}
    @Override public InteractionResult onWandUse(Level l,BlockPos p,Player player,ItemStack wand){return activate(l,player);}
    private InteractionResult activate(Level l,Player p){if(!getBlockState().is(Vats.MATRIX.get())||vat()==null)return InteractionResult.PASS;if(!l.isClientSide())vat().startInfusion(p);return InteractionResult.SUCCESS;}
}
