package thaumichorizons;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;

public final class SpecimenJarBlock extends BaseEntityBlock {
    public SpecimenJarBlock(Properties p){super(p);}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(SpecimenJarBlock::new);}
    @Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new SpecimenJarEntity(p,s);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected VoxelShape getShape(BlockState s,BlockGetter level,BlockPos p,CollisionContext context){return box(3,0,3,13,13,13);}
    @Override public void setPlacedBy(Level level,BlockPos p,BlockState s,LivingEntity placer,ItemStack item){super.setPlacedBy(level,p,s,placer,item);if(level.getBlockEntity(p) instanceof SpecimenJarEntity jar){jar.stack=item.copyWithCount(1);jar.sync();}}
    @Override protected InteractionResult useWithoutItem(BlockState s,Level level,BlockPos p,Player player,BlockHitResult hit){
        if(player.isShiftKeyDown()&&level.getBlockEntity(p) instanceof SpecimenJarEntity jar){if(level instanceof net.minecraft.server.level.ServerLevel server){var e=jar.occupant();if(e==null||server.getEntity(e.getUUID())!=null)return InteractionResult.FAIL;e.moveTo(p.getX()+.5,p.getY()+1,p.getZ()+.5,e.getYRot(),e.getXRot());if(!server.noCollision(e)||!server.addFreshEntity(e))return InteractionResult.FAIL;jar.stack=ItemStack.EMPTY;level.setBlockAndUpdate(p,thaumcraft.registry.ModBlocks.WARDED_JAR.get().defaultBlockState());}return InteractionResult.sidedSuccess(level.isClientSide());}return InteractionResult.PASS;
    }
    @Override protected void onRemove(BlockState old,Level level,BlockPos p,BlockState next,boolean moving){if(!old.is(next.getBlock())&&!level.isClientSide()&&level.getBlockEntity(p) instanceof SpecimenJarEntity jar&&!jar.stack.isEmpty())Containers.dropItemStack(level,p.getX()+.5,p.getY()+.5,p.getZ()+.5,jar.stack.copy());super.onRemove(old,level,p,next,moving);}
    @Override public ItemStack getCloneItemStack(BlockState state,net.minecraft.world.phys.HitResult hit,LevelReader level,BlockPos pos,Player player){return level.getBlockEntity(pos) instanceof SpecimenJarEntity jar?jar.stack.copy():ItemStack.EMPTY;}
}
