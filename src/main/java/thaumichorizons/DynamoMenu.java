package thaumichorizons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

public final class DynamoMenu extends AbstractContainerMenu {
    public final Dynamo tile;
    public DynamoMenu(int id,Inventory inv){this(id,inv,new Dynamo(BlockPos.ZERO,Content.VIS_DYNAMO.get().defaultBlockState()));}
    public DynamoMenu(int id,Inventory inv,Dynamo tile){super(Content.DYNAMO_MENU.get(),id);this.tile=tile;addDataSlot(new DataSlot(){public int get(){return tile.enabled;}public void set(int n){tile.enabled=n;}});}
    @Override public boolean clickMenuButton(Player p,int id){if(!stillValid(p)||id<1||id>6)return false;tile.enabled^=1<<(id-1);tile.sync();return true;}
    @Override public boolean stillValid(Player p){return p.level().isClientSide()||(!tile.isRemoved()&&p.level()==tile.getLevel()&&p.distanceToSqr(tile.getBlockPos().getCenter())<=64);}
    @Override public ItemStack quickMoveStack(Player p,int id){return ItemStack.EMPTY;}
}
