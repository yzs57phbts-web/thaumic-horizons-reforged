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
import net.minecraft.world.phys.shapes.*;
import thaumcraft.wand.Wands;

public final class DynamoBlock extends BaseEntityBlock {
    public final boolean essentia;
    public DynamoBlock(boolean essentia,Properties p){super(p);this.essentia=essentia;}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new DynamoBlock(essentia,p));}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new Dynamo(p,s);}
    @Override protected RenderShape getRenderShape(BlockState s){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Shapes.block();}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> type){return createTickerHelper(type,Content.DYNAMO.get(),Dynamo::tick);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState s,Level l,BlockPos pos,Player p,InteractionHand hand,BlockHitResult hit){
        if(!essentia&&Wands.isWand(stack))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        open(l,pos,p);return ItemInteractionResult.sidedSuccess(l.isClientSide());
    }
    @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos pos,Player p,BlockHitResult hit){open(l,pos,p);return InteractionResult.sidedSuccess(l.isClientSide());}
    private void open(Level l,BlockPos pos,Player p){if(!essentia&&!l.isClientSide()&&l.getBlockEntity(pos) instanceof Dynamo be)p.openMenu(be);}
}
