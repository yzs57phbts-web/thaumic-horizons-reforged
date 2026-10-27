package thaumichorizons;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import thaumcraft.node.NodeBlockEntity;
final class NodeCheat extends Item {
    NodeCheat(Properties properties){super(properties);}
    static void init(){Content.ALL.put("node_cheat",Content.ITEMS.registerItem("node_cheat",NodeCheat::new,new Item.Properties()));}
    @Override public InteractionResult useOn(UseOnContext context){var level=context.getLevel();var pos=context.getClickedPos();if(!(level.getBlockEntity(pos) instanceof NodeBlockEntity node))return InteractionResult.PASS;if(level.isClientSide())return InteractionResult.SUCCESS;if(context.getPlayer()!=null&&!level.mayInteract(context.getPlayer(),pos))return InteractionResult.FAIL;var aspects=node.getAspectsBase().copy();level.setBlockAndUpdate(pos,Vortex.BLOCK.get().defaultBlockState());var vortex=(Vortex)level.getBlockEntity(pos);vortex.aspects=aspects;vortex.cheat=true;vortex.sync();return InteractionResult.SUCCESS;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("thaumichorizons.node_cheat.1"));tooltip.add(Component.translatable("thaumichorizons.node_cheat.2"));super.appendHoverText(stack,context,tooltip,flag);}
}
