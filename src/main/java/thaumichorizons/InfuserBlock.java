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
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.minecraft.world.level.BlockGetter;

public final class InfuserBlock extends BaseEntityBlock {
    public static final MapCodec<InfuserBlock> CODEC=simpleCodec(InfuserBlock::new);
    public InfuserBlock(Properties p){super(p);}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Shapes.block();}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new BloodInfuser(p,s);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){return l.isClientSide()?null:createTickerHelper(t,Content.BLOOD.get(),BloodInfuser::tick);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){open(l,p,player);return ItemInteractionResult.sidedSuccess(l.isClientSide());}
    @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){open(l,p,player);return InteractionResult.sidedSuccess(l.isClientSide());}
    private void open(Level l,BlockPos p,Player player){if(!l.isClientSide()&&l.getBlockEntity(p) instanceof BloodInfuser be)player.openMenu(be);}
    @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){
        if(!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof BloodInfuser be){for(int i=0;i<10;i++)Containers.dropItemStack(l,p.getX()+.5,p.getY()+.5,p.getZ()+.5,be.inventory.getStackInSlot(i));}
        super.onRemove(s,l,p,next,moving);
    }
}
