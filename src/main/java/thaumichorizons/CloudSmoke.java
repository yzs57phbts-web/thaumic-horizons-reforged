package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
final class CloudSmoke {
    private static int ticks;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1)server.execute(()->{
            var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();level.setDayTime(1000);level.setWeatherParameters(0,1200,true,false);level.setRainLevel(1);for(var entity:level.getEntities((Entity)null,new AABB(-67,62,23,-37,90,55),e->!(e instanceof net.minecraft.world.entity.player.Player)))entity.discard();for(var pos:BlockPos.betweenClosed(-66,63,24,-38,80,54))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
            for(int i=0;i<Clouds.NAMES.size();i++){var pos=new BlockPos(-63+i%5*5,73,36+i/5*9);level.setBlockAndUpdate(pos,Clouds.BLOCKS.get(Clouds.NAMES.get(i)).get().defaultBlockState());level.setBlockAndUpdate(new BlockPos(pos.getX(),64,pos.getZ()),(i==0?Blocks.FARMLAND:i==1?Blocks.ICE:i==3?Blocks.OAK_LOG:i==8?Blocks.DIRT:Blocks.STONE).defaultBlockState());var cloud=(Clouds.CloudEntity)level.getBlockEntity(pos);cloud.dropTimer=Clouds.PERIODS[i]>0?1:-1;cloud.findFloor();Clouds.CloudEntity.tick(level,pos,cloud.getBlockState(),cloud);}
            int i=0;for(var block:Spikes.BLOCKS.values()){var center=new BlockPos(-61+i*8,65,28);level.setBlockAndUpdate(center,Blocks.STONE.defaultBlockState());for(var face:Direction.values())level.setBlockAndUpdate(center.relative(face),block.get().defaultBlockState().setValue(Spikes.FACING,face));i++;}player.getAbilities().flying=true;player.onUpdateAbilities();player.connection.teleport(-52,74,53,180,12);
        });
        if(ticks==60){int clouds=0;for(String name:Clouds.NAMES){var stack=new ItemStack(Content.item(name+"_cloud"));var model=mc.getItemRenderer().getModel(stack,mc.level,mc.player,0);if(model==mc.getModelManager().getMissingModel())throw new IllegalStateException("Missing cloud model "+name);}for(int i=0;i<10;i++){var pos=new BlockPos(-63+i%5*5,73,36+i/5*9);if(mc.level.getBlockEntity(pos) instanceof Clouds.CloudEntity cloud&&cloud.raining)clouds++;}if(clouds!=10)throw new IllegalStateException("Cloud rain state did not reach client: "+clouds);shot("horizons-ten-clouds-rain.png");}
        if(ticks==65)server.execute(()->{var bolt=FiniteLightning.TYPE.get().create(server.overworld());bolt.setPos(-63.5,65,36.5);bolt.configure(8,false);server.overworld().addFreshEntity(bolt);});
        if(ticks==70)shot("horizons-finite-cloud-lightning.png");
        if(ticks==80)server.execute(()->server.getPlayerList().getPlayers().getFirst().connection.teleport(-53,68,32,180,22));
        if(ticks==110)shot("horizons-original-spikes.png");
        if(ticks==120){System.out.println("TH_CLOUD_SPIKE_MODELS_OK: ten rain columns and original textures, short lightning FX, three original spike OBJ skins on six faces");server.execute(()->server.overworld().setWeatherParameters(100000,0,false,false));}
        if(ticks==130)server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.connection.teleport(15.5,65,8.5,180,8);player.getInventory().setItem(0,new ItemStack(Content.item("injector")));player.getInventory().setItem(1,new ItemStack(Content.item("lens_case")));player.getInventory().selected=0;player.inventoryMenu.broadcastChanges();});
        return ticks>=150;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
