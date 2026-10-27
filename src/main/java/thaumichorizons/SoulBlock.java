package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class SoulBlock extends BaseEntityBlock {
    final int kind;
    SoulBlock(int kind,Properties properties){super(properties);this.kind=kind;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new SoulBlock(kind,p));}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState state){return new SoulMachine(p,state);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,Souls.TYPE.get(),SoulMachine::tick);}
    @Override protected ItemInteractionResult useItemOn(ItemStack item,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){
        if(level.getBlockEntity(pos) instanceof SoulMachine machine){
            if(kind==2){if(!level.isClientSide())machine.bottle(player,item);}
            else if(!level.isClientSide())player.openMenu(machine);
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(kind==2)return InteractionResult.PASS;if(!level.isClientSide()&&level.getBlockEntity(pos) instanceof SoulMachine machine)player.openMenu(machine);return InteractionResult.sidedSuccess(level.isClientSide());}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof SoulMachine machine&&!level.isClientSide())for(int i=0;i<machine.inventory.getSlots();i++)popResource(level,pos,machine.inventory.getStackInSlot(i));
        super.onRemove(state,level,pos,next,moving);
    }
}
