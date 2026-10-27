package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;

public final class VatBlock extends BaseEntityBlock {
    public static final IntegerProperty PART=IntegerProperty.create("part",0,10);
    public static final IntegerProperty CELL=IntegerProperty.create("cell",0,35);
    public final int kind;
    public VatBlock(int kind,Properties p){super(p);this.kind=kind;registerDefaultState(stateDefinition.any().setValue(PART,kind==2?0:kind==0?10:5).setValue(CELL,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new VatBlock(kind,p));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(PART,CELL);}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new VatEntity(p,s);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> type){return createTickerHelper(type,Vats.TYPE.get(),VatEntity::tick);}
    @Override protected RenderShape getRenderShape(BlockState s){return kind==2?RenderShape.INVISIBLE:RenderShape.MODEL;}
    @Override protected boolean skipRendering(BlockState s,BlockState next,Direction side){return kind==0&&next.is(this)||super.skipRendering(s,next,side);}
    @Override protected void entityInside(BlockState s,Level l,BlockPos p,Entity e){if(kind==2){e.setAirSupply(300);e.clearFire();}}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos p,Player player,InteractionHand hand,BlockHitResult hit){use(l,p,player,hand,s);return ItemInteractionResult.sidedSuccess(l.isClientSide());}
    @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){use(l,p,player,InteractionHand.MAIN_HAND,s);return InteractionResult.sidedSuccess(l.isClientSide());}
    private void use(Level l,BlockPos p,Player player,InteractionHand hand,BlockState s){if(!l.isClientSide()&&l.getBlockEntity(p) instanceof VatEntity part){var vat=part.master();if(vat!=null)vat.interact(player,hand,s.getValue(PART)==0||s.getValue(PART)==7);}}
    @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){
        if(!l.isClientSide()&&!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof VatEntity part){var vat=part.master();if(vat!=null&&!vat.dismantling)vat.dismantle(p);}
        super.onRemove(s,l,p,next,moving);
    }
}
