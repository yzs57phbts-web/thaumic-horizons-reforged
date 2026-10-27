package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
final class SoulSmoke {
    private static int ticks;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ++ticks;
        if(ticks==1)server.execute(()->{
            var level=server.overworld();var p=server.getPlayerList().getPlayers().getFirst();p.stopRiding();p.getAbilities().flying=true;p.onUpdateAbilities();p.connection.teleport(29.5,66.5,6,180,15);
            for(BlockPos pos:BlockPos.betweenClosed(23,63,-3,39,70,8))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
            for(var entity:level.getEntities((net.minecraft.world.entity.Entity)null,new net.minecraft.world.phys.AABB(23,62,-3,40,75,8),e->!(e instanceof net.minecraft.world.entity.player.Player)))entity.discard();
            for(int x:new int[]{26,30,34}){level.setBlockAndUpdate(new BlockPos(x,64,0),Souls.SIEVE.get().defaultBlockState());var sieve=(SoulMachine)level.getBlockEntity(new BlockPos(x,64,0));sieve.inventory.setStackInSlot(0,new ItemStack(Items.SOUL_SAND,16));sieve.ticksLeft=900;sieve.sync();}
            level.setBlockAndUpdate(new BlockPos(26,65,0),Souls.FORGE.get().defaultBlockState());var forge=(SoulMachine)level.getBlockEntity(new BlockPos(26,65,0));forge.essentia=3000;forge.souls=4;forge.sync();
            level.setBlockAndUpdate(new BlockPos(28,65,0),thaumcraft.registry.ModBlocks.WARDED_JAR.get().defaultBlockState());((thaumcraft.essentia.JarBlockEntity)level.getBlockEntity(new BlockPos(28,65,0))).addToContainer(thaumcraft.aspect.Aspect.MIND,64);
            for(int x=26;x<=28;x++)level.setBlockAndUpdate(new BlockPos(x,66,0),thaumcraft.registry.ModBlocks.ESSENTIA_TUBE.get().defaultBlockState());
            level.setBlockAndUpdate(new BlockPos(30,65,0),Souls.INSPIRATRON.get().defaultBlockState());var inspire=(SoulMachine)level.getBlockEntity(new BlockPos(30,65,0));inspire.inventory.setStackInSlot(0,new ItemStack(Items.PAPER,16));inspire.progress=50;inspire.sync();
            level.setBlockAndUpdate(new BlockPos(34,65,0),thaumcraft.registry.ModBlocks.BRAIN_JAR.get().defaultBlockState());level.setBlockAndUpdate(new BlockPos(24,64,0),Specimens.JAR.get().defaultBlockState());var jar=(SpecimenJarEntity)level.getBlockEntity(new BlockPos(24,64,0));jar.stack=Souls.soulJar(net.minecraft.world.entity.npc.VillagerProfession.LIBRARIAN);jar.sync();
        });
        if(ticks==60)shot("horizons-soul-devices.png");
        if(ticks==65){mc.options.hideGui=false;server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.openMenu((SoulMachine)server.overworld().getBlockEntity(new BlockPos(26,64,0)));});}
        if(ticks==95){if(!(mc.screen instanceof SoulScreen)||!(mc.player.containerMenu instanceof SoulMenu menu)||menu.count!=1||menu.tile.ticksLeft<=0||!menu.slots.getFirst().getItem().is(Items.SOUL_SAND))throw new IllegalStateException("Soul sieve live menu synchronization failed");shot("horizons-sieve-menu.png");}
        if(ticks==100)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.closeContainer();p.openMenu((SoulMachine)server.overworld().getBlockEntity(new BlockPos(30,65,0)));});
        if(ticks==130){if(!(mc.screen instanceof SoulScreen)||!(mc.player.containerMenu instanceof SoulMenu menu)||menu.count!=2||menu.tile.progress<50||!menu.slots.getFirst().getItem().is(Items.PAPER))throw new IllegalStateException("Inspiratron live menu synchronization failed");shot("horizons-inspiratron-menu.png");}
        if(ticks==140){server.execute(()->server.getPlayerList().getPlayers().getFirst().closeContainer());mc.options.hideGui=true;}
        if(ticks==210)System.out.println("TH_SOUL_MACHINES_OK: original device models, animated souls, live sieve and Inspiratron menus");
        return ticks>=210;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
