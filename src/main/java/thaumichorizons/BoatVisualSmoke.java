package thaumichorizons;
import java.util.UUID;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
/** Only checks the lava-floor and modern item-icon update in the disposable world. */
final class BoatVisualSmoke {
    private static int ticks;private static UUID boatId;private static volatile boolean checked;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1){mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.closeContainer();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.getAbilities().flying=true;player.onUpdateAbilities();for(var pos:BlockPos.betweenClosed(-31,63,89,-19,64,120))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.LAVA.defaultBlockState());for(var entity:level.getEntitiesOfClass(MagicBoats.LegacyBoat.class,new net.minecraft.world.phys.AABB(-32,63,88,-18,70,122)))entity.discard();var boat=MagicBoats.THAUMIUM.get().create(level);boat.moveTo(-25,65,95,0,0);level.addFreshEntity(boat);boatId=boat.getUUID();player.teleportTo(level,-25,69,95,0,90);player.getInventory().setItem(0,new ItemStack(Content.item("thaumium_boat")));player.getInventory().setItem(1,new ItemStack(Content.item("greatwood_boat")));player.inventoryMenu.broadcastChanges();});}
        if(ticks>=60&&ticks<=100&&ticks%10==0)server.execute(()->{var level=server.overworld();var boat=(MagicBoats.LegacyBoat)level.getEntity(boatId);var pos=BlockPos.containing(boat.getX(),64,boat.getZ());double surface=64+level.getFluidState(pos).getHeight(level,pos),floor=boat.getY()+3/16D;if(floor<surface+.01)throw new IllegalStateException("Lava crosses the actual boat floor: floor="+floor+", surface="+surface);checked=true;});
        if(ticks==80)shot("horizons-1.0.2-dry-lava-boat.png");
        if(ticks==105){if(!checked)throw new IllegalStateException("Lava buoyancy not checked");for(String name:new String[]{"thaumium_boat","greatwood_boat"}){var model=mc.getItemRenderer().getModel(new ItemStack(Content.item(name)),mc.level,mc.player,0);if(!model.getParticleIcon().contents().name().getPath().contains("modern_boat_"))throw new IllegalStateException("Old boat icon is still active");}mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));}
        if(ticks==115)shot("horizons-1.0.2-modern-boat-icons.png");
        if(ticks==120){mc.setScreen(null);server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();var boat=server.overworld().getEntity(boatId);player.teleportTo(boat.getX(),boat.getY(),boat.getZ());player.startRiding(boat);});}
        if(ticks==140){mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);mc.options.keyUp.setDown(true);}
        if(ticks==170){mc.options.keyUp.setDown(false);shot("horizons-1.0.2-dry-lava-ride.png");if(!(mc.player.getVehicle() instanceof MagicBoats.LegacyBoat boat)||boat.getY()+3/16D<64.9)throw new IllegalStateException("Boat sinks while rowing");System.out.println("TH_1_0_2_BOATS_OK: actual hull floor above lava, including rowing; both native boat item icons");}
        if(ticks==180){mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.teleportTo(15.5,65,8.5);});}return ticks>=190;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
