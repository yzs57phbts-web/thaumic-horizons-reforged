package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import thaumcraft.aspect.*;
import thaumcraft.entity.golem.*;
import thaumcraft.node.NodeBlockEntity;
import thaumcraft.wand.*;

final class DeviceSmoke {
    private static int ticks;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1){
            for(int color=0;color<16;color++){
                var stack=Illumination.focus(color);var model=mc.getItemRenderer().getModel(stack,mc.level,mc.player,0);var expected=ThaumicHorizons.id("items/focus_illumination."+color);
                if(!model.getParticleIcon().contents().name().equals(expected)||!net.minecraft.client.resources.language.I18n.exists("item.focusIllumination."+color+".name"))throw new IllegalStateException("Illumination variant missing: "+color);
            }
            server.execute(()->{
                var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();
                for(var old:level.getEntities((Entity)null,new AABB(-6,62,10,34,80,36),e->!(e instanceof net.minecraft.world.entity.player.Player)))old.discard();
                for(var pos:BlockPos.betweenClosed(-6,63,10,32,74,34))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
                var displays=new net.minecraft.world.level.block.state.BlockState[]{Blocks.GRASS_BLOCK.defaultBlockState(),Blocks.OAK_LOG.defaultBlockState(),Blocks.STONE.defaultBlockState(),Blocks.IRON_BLOCK.defaultBlockState(),Blocks.CAKE.defaultBlockState(),Blocks.CLAY.defaultBlockState(),Blocks.COBWEB.defaultBlockState(),Materials.BLOCKS.get("red_crystal").get().defaultBlockState()};
                for(int i=0;i<displays.length;i++){var golem=AnimatedGolems.TYPE.get().create(level);golem.moveTo(i*4,64,18,0,0);golem.initialize(displays[i],600,false,false,player.getUUID(),new GolemSnapshot(4,false,java.util.List.of(),"",java.util.List.of(),net.minecraft.world.item.component.ItemContainerContents.EMPTY,java.util.List.of(),0));level.addFreshEntity(golem);}
                int i=0;for(var block:Materials.BLOCKS.values()){level.setBlockAndUpdate(new BlockPos(-3+i%12*3,64,24+i/12*3),block.get().defaultBlockState());i++;}
                for(int x:new int[]{1,5}){var nodePos=new BlockPos(x,64,12);level.setBlockAndUpdate(nodePos,thaumcraft.registry.ModBlocks.AURA_NODE.get().defaultBlockState());var node=(NodeBlockEntity)level.getBlockEntity(nodePos);node.setAspects(new AspectList().add(Aspect.AIR,x==1?20:1).add(Aspect.FIRE,20));node.sync();level.setBlockAndUpdate(nodePos.above(),NodeMonitor.BLOCK.get().defaultBlockState());}
                for(int color=0;color<16;color++)level.setBlockAndUpdate(new BlockPos(-3+color*2,68,31),Illumination.LIGHT.get().defaultBlockState().setValue(Illumination.COLOR,color));
                player.getInventory().setItem(9,player.getMainHandItem());player.getInventory().setItem(0,new ItemStack(Content.item("disposable_wand")));for(int slot=1;slot<=3;slot++)player.getInventory().setItem(slot,new ItemStack(Content.item(new String[]{"focus_illumination","focus_liquefaction","focus_disintegration"}[slot-1])));player.getInventory().setItem(4,new ItemStack(Content.item("focus_animation")));player.getInventory().setItem(5,new ItemStack(Content.item("golem_bell")));player.getInventory().setItem(6,new ItemStack(Content.item("node_monitor")));player.getInventory().setItem(7,new ItemStack(Content.item("alchemite")));player.getInventory().setItem(8,new ItemStack(Content.item("golem_powder")));player.getInventory().selected=0;player.inventoryMenu.broadcastChanges();player.connection.teleport(13,69,36,180,20);
            });
        }
        if(ticks==20){mc.player.getInventory().selected=0;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(0));}
        if(ticks==70){shot("horizons-new-devices-golems.png");
            for(var entity:mc.level.entitiesForRendering())if(entity instanceof AnimatedGolem golem){var texture=GolemSkins.get(golem.material());if(!texture.getPath().startsWith("dynamic/golem/"))throw new IllegalStateException("Golem material skin fell back to wood: "+golem.material());}
        }
        if(ticks==80)server.execute(()->server.getPlayerList().getPlayers().getFirst().connection.teleport(3,67,17,180,16));
        if(ticks==110)shot("horizons-node-monitor.png");
        if(ticks==120){mc.player.getInventory().selected=6;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(6));}
        if(ticks==140)shot("horizons-node-monitor-held.png");
        if(ticks==150)server.execute(()->{server.overworld().setDayTime(18000);server.getPlayerList().getPlayers().getFirst().connection.teleport(13,69,34,180,0);});
        if(ticks==190)shot("horizons-colored-illumination.png");
        if(ticks==200)server.execute(()->{server.overworld().setDayTime(1000);server.getPlayerList().getPlayers().getFirst().connection.teleport(15.5,65,8.5,180,8);});
        if(ticks==215)System.out.println("TH_NEW_DEVICES_OK: sixteen illumination sprites, colored light, original golem skin projection, material models, disposable wand and node monitor");
        return ticks>=215;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
