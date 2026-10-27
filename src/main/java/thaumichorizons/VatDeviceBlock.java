package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.wand.Wands;

public final class VatDeviceBlock extends BaseEntityBlock {
    public final boolean beacon;
    public VatDeviceBlock(boolean beacon,Properties p){super(p);this.beacon=beacon;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new VatDeviceBlock(beacon,p));}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new VatDeviceEntity(p,s);}
    @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return beacon||level.getBlockEntity(pos.below()) instanceof VatEntity vat&&vat.isController();}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return createTickerHelper(t,Vats.DEVICE.get(),VatDeviceEntity::tick);}
    @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){
        if(!beacon&&Wands.isWand(stack))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if(beacon&&!l.isClientSide())Infusions.bindBeacon(player,p);return ItemInteractionResult.sidedSuccess(l.isClientSide());
    }
    @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){if(beacon&&!l.isClientSide())Infusions.bindBeacon(player,p);return InteractionResult.sidedSuccess(l.isClientSide());}
    @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){
        if(!l.isClientSide()&&!s.is(next.getBlock())&&l.getBlockEntity(p.below()) instanceof VatEntity be&&be.mode==2)be.failInfusion();super.onRemove(s,l,p,next,moving);
    }
}
