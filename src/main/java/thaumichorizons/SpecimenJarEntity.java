package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class SpecimenJarEntity extends thaumcraft.blockentity.TcBlockEntity implements thaumcraft.wand.WandInteractable {
    public ItemStack stack=ItemStack.EMPTY;
    private LivingEntity subject;
    public SpecimenJarEntity(BlockPos p,BlockState s){super(Specimens.TYPE.get(),p,s);}
    public LivingEntity occupant(){if(subject==null&&level!=null&&!stack.isEmpty())subject=Specimens.read(stack,level);return subject;}
    public boolean release(net.minecraft.world.entity.player.Player player){
        if(!(level instanceof net.minecraft.server.level.ServerLevel server)||Souls.isSoul(stack)||!level.mayInteract(player,worldPosition)||!player.mayUseItemAt(worldPosition,net.minecraft.core.Direction.UP,player.getMainHandItem()))return false;
        var entity=occupant();if(entity==null||server.getEntity(entity.getUUID())!=null)return false;
        if(entity instanceof net.minecraft.world.entity.TamableAnimal pet&&pet.getOwnerUUID()==null)pet.setOwnerUUID(player.getUUID());
        entity.moveTo(worldPosition.getX()+.5,worldPosition.getY()+.1,worldPosition.getZ()+.5,0,0);if(!server.addFreshEntity(entity))return false;
        stack=ItemStack.EMPTY;level.levelEvent(2001,worldPosition,net.minecraft.world.level.block.Block.getId(thaumcraft.registry.ModBlocks.WARDED_JAR.get().defaultBlockState()));level.setBlockAndUpdate(worldPosition,net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
        level.playSound(null,worldPosition,net.minecraft.sounds.SoundEvents.GLASS_BREAK,net.minecraft.sounds.SoundSource.BLOCKS,1,.9F+level.random.nextFloat()*.2F);return true;
    }
    @Override public net.minecraft.world.InteractionResult onWandUseOn(net.minecraft.world.level.Level level,BlockPos p,net.minecraft.world.entity.player.Player player,ItemStack wand,Direction side){if(Souls.isSoul(stack))return net.minecraft.world.InteractionResult.PASS;if(level.isClientSide())return net.minecraft.world.InteractionResult.SUCCESS;return release(player)?net.minecraft.world.InteractionResult.SUCCESS:net.minecraft.world.InteractionResult.FAIL;}
    @Override public net.minecraft.world.InteractionResult onWandUse(net.minecraft.world.level.Level level,BlockPos p,net.minecraft.world.entity.player.Player player,ItemStack wand){return onWandUseOn(level,p,player,wand,Direction.UP);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registry){super.saveAdditional(tag,registry);if(!stack.isEmpty())tag.put("specimen",stack.save(registry));}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registry){super.loadAdditional(tag,registry);var incoming=ItemStack.parseOptional(registry,tag.getCompound("specimen"));if(!ItemStack.isSameItemSameComponents(stack,incoming))subject=null;stack=incoming;}
}
