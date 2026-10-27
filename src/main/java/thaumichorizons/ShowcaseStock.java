package thaumichorizons;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import thaumcraft.aspect.*;
/** Supplies for the opt-in disposable validation map, never called in normal gameplay. */
final class ShowcaseStock {
    static void fill(ServerLevel level){
        var stacks=new ArrayList<ItemStack>();CreativeOrder.items().forEach((id,item)->{if(!id.equals("thaumic_crossbow")){if(id.equals("focus_illumination")){for(int color=0;color<16;color++)stacks.add(Illumination.focus(color));}else stacks.add(new ItemStack(item.get()));}});stacks.add(Vortex.stableItem());for(var aspect:Aspect.getPrimalAspects()){stacks.add(BloodFormula.create(new AspectList().add(aspect,8),false));stacks.add(BloodFormula.create(new AspectList().add(aspect,8),true));}
        for(var pos:BlockPos.betweenClosed(-8,63,49,24,63,53))level.setBlockAndUpdate(pos,Blocks.STONE_BRICKS.defaultBlockState());
        int count=(stacks.size()+26)/27;for(int i=0;i<count;i++){var pos=new BlockPos(-6+i*4,64,50);level.setBlockAndUpdate(pos,Blocks.CHEST.defaultBlockState());var chest=(ChestBlockEntity)level.getBlockEntity(pos);chest.clearContent();for(int slot=0;slot<27&&i*27+slot<stacks.size();slot++)chest.setItem(slot,stacks.get(i*27+slot));chest.setChanged();}
        var pos=new BlockPos(-6,64,52);level.setBlockAndUpdate(pos,Blocks.OAK_SIGN.defaultBlockState());var sign=(SignBlockEntity)level.getBlockEntity(pos);var text=new SignText().setMessage(0,Component.literal("TH 1.0.0")).setMessage(1,Component.literal("Все предметы →")).setMessage(2,Component.literal("F: инъектор")).setMessage(3,Component.literal("V: линзы"));sign.setText(text,true);sign.setText(text,false);sign.setChanged();
    }
}
