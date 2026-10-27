package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import thaumcraft.aspect.*;
import thaumcraft.wand.*;
/** Real client/dimension/network checks, only reached in the disposable smoke world. */
final class RiftSmoke {
    private static final BlockPos ROOT=new BlockPos(-12,66,60),TOP=new BlockPos(0,70,60);
    private static int ticks,stage,age;
    private static volatile int planeId=-1;
    private static volatile boolean ready;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;age++;
        if(ticks>4800)throw new IllegalStateException("Pocket/gateway client validation timed out at stage "+stage);
        if(stage==0){stage=1;age=0;mc.options.hideGui=false;mc.setScreen(null);server.execute(()->{
            if(server.getLevel(PocketDimension.KEY)==null)throw new IllegalStateException("Production pocket dimension JSON was not loaded");
            var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();player.closeContainer();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.getAbilities().flying=true;player.onUpdateAbilities();Infusions.DATA.set(player,new net.minecraft.nbt.CompoundTag());player.removeAllEffects();
            level.setWeatherParameters(100000,0,false,false);level.setDayTime(1000);for(var pos:BlockPos.betweenClosed(-18,63,54,8,75,66))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(ROOT,Vortex.BLOCK.get().defaultBlockState());var rift=(Vortex)level.getBlockEntity(ROOT);rift.cheat=true;rift.ateDevices=true;rift.count=50;rift.aspects.add(Aspect.AIR,4).add(Aspect.EARTH,4).add(Aspect.LIGHT,4);rift.sync();
            var pearl=new ItemEntity(level,ROOT.getX()+.5,ROOT.getY()+.5,ROOT.getZ()+.5,new ItemStack(thaumcraft.registry.EldritchContent.PRIMORDIAL_PEARL.get()));level.addFreshEntity(pearl);rift.consumeItems();planeId=rift.pendingId;if(planeId<0)throw new IllegalStateException("Real pearl did not start pocket generation");
            player.teleportTo(level,-12,67,64,180,5);player.getInventory().setItem(0,new ItemStack(Content.item("disposable_wand")));player.getInventory().selected=0;player.inventoryMenu.broadcastChanges();
        });}
        if(stage==1&&age==60)shot("horizons-rift-and-disposable-wand.png");
        if(stage==1&&age%20==0)server.execute(()->{var plane=PocketPlanes.get(server).get(planeId);ready=plane!=null&&plane.ready;});
        if(stage==1&&ready){stage=2;age=0;server.execute(()->{var rift=(Vortex)server.overworld().getBlockEntity(ROOT);if(!rift.createdDimension){Vortex.tick(server.overworld(),ROOT,rift.getBlockState(),rift);}var player=server.getPlayerList().getPlayers().getFirst();player.setPortalCooldown(0);rift.travel(player);});}
        if(stage==2&&age==50){if(mc.level.dimension()!=PocketDimension.KEY)throw new IllegalStateException("Client did not enter generated pocket dimension");shot("horizons-generated-pocket-plane.png");server.execute(()->{
            var player=server.getPlayerList().getPlayers().getFirst();var realm=server.getLevel(PocketDimension.KEY);var pos=new BlockPos(0,129,planeId*256);var exit=(Vortex)realm.getBlockEntity(pos);var key=new ItemStack(Content.item("keystone"));player.setItemInHand(InteractionHand.MAIN_HAND,key);var result=key.getItem().onItemUseFirst(key,new net.minecraft.world.item.context.UseOnContext(player,InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(pos.getCenter(),Direction.UP,pos,false)));if(!result.consumesAction()||Gateways.planeId(key)!=planeId)throw new IllegalStateException("Keystone did not bind through real item use");player.setPortalCooldown(0);exit.travel(player);
        });}
        if(stage==2&&age==90){if(mc.level.dimension()!=net.minecraft.world.level.Level.OVERWORLD)throw new IllegalStateException("Client did not return through rift");stage=3;age=0;server.execute(()->{
            var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();var plane=PocketPlanes.get(server).get(planeId);level.setBlockAndUpdate(TOP,Gateways.SLOT.get().defaultBlockState());for(int t=-2;t<=2;t++)for(int y=0;y>=-4;y--)if((Math.abs(t)==2||y==0||y==-4)&&(t!=0||y!=0))level.setBlockAndUpdate(TOP.offset(t,y,0),(Math.abs(t)==2&&(y==0||y==-4)?thaumcraft.registry.ModBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT.get():thaumcraft.registry.ModBlocks.ANCIENT_STONE.get()).defaultBlockState());var slot=(Gateways.SlotEntity)level.getBlockEntity(TOP);slot.insert(Gateways.key(plane));var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);player.setItemInHand(InteractionHand.MAIN_HAND,wand);if(!slot.activate(player,wand))throw new IllegalStateException("Real client gateway frame did not activate");player.connection.teleport(.5,67,65,180,0);
        });}
        if(stage==3&&age==40)shot("horizons-activated-keystone-gateway.png");
        if(stage==3&&age==50)server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.setPortalCooldown(0);player.connection.teleport(.5,68,60.5,180,0);});
        if(stage==3&&age==90){if(mc.level.dimension()!=PocketDimension.KEY)throw new IllegalStateException("Walking into gateway did not change client dimension");shot("horizons-pocket-gateway-arrival.png");mc.options.keyUp.setDown(true);}
        if(stage==3&&age==115){mc.options.keyUp.setDown(false);server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();var plane=PocketPlanes.get(server).get(planeId);var inside=Gateways.inside(plane,0);if(player.level().dimension()!=PocketDimension.KEY||player.getZ()>inside.getZ()-1)throw new IllegalStateException("Player trapped in pocket gateway arrival wall");System.out.println("TH_POCKET_GATEWAY_WALK_OK: native client input leaves original arrival coordinates");player.setPortalCooldown(0);player.connection.teleport(inside.getX()+.5,127,inside.getZ()+.5,0,0);});}
        if(stage==3&&age==155){if(mc.level.dimension()!=net.minecraft.world.level.Level.OVERWORLD)throw new IllegalStateException("Walking into return gateway lost origin dimension");stage=4;age=0;server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.connection.teleport(.5,68,65,180,5);player.getInventory().setItem(0,new ItemStack(Content.item("injector")));player.getInventory().setItem(2,Gateways.key(PocketPlanes.get(server).get(planeId)));player.inventoryMenu.broadcastChanges();server.saveEverything(false,true,true);});}
        if(stage==4&&age==35){shot("horizons-injector-vanilla-palette-final.png");System.out.println("TH_RIFT_SMOKE_OK: production dimension, real pearl generation, original return rift, bound key, both gateway directions, client walk and saved links");}
        if(stage==4&&age==45)openRepairPage(mc);
        if(stage==4&&age==60){if(RiftPages.rendered==0)throw new IllegalStateException("Void putty recipe page was not rendered");shot("horizons-void-putty-research.png");mc.setScreen(new thaumcraft.client.research.ResearchPageScreen(thaumcraft.research.ResearchCategories.getResearch("warpedTumor"),0));}
        if(stage==4&&age==75)shot("horizons-configured-warped-tumor-page.png");
        if(stage==4&&age==80){mc.setScreen(null);server.execute(()->{ShowcaseStock.fill(server.overworld());var player=server.getPlayerList().getPlayers().getFirst();player.connection.teleport(15.5,65,8.5,180,8);player.getInventory().setItem(0,new ItemStack(Content.item("injector")));player.getInventory().setItem(1,new ItemStack(Content.item("lens_case")));player.getInventory().setItem(9,BloodFormula.create(new AspectList().add(Aspect.AIR,2),false));player.getInventory().setItem(10,BloodFormula.create(new AspectList().add(Aspect.FIRE,2),true));player.inventoryMenu.broadcastChanges();});}
        return stage==4&&age>=100;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
    private static void openRepairPage(Minecraft mc){
        var research=thaumcraft.research.ResearchCategories.getResearch("voidPutty");var recipe=thaumcraft.research.ResearchPage.custom(ThaumicHorizons.id("void_putty_example"),"");
        try{var index=thaumcraft.client.research.ResearchPageScreen.class.getDeclaredMethod("pageIndex",thaumcraft.research.ResearchItem.class,thaumcraft.research.ResearchPage.class);index.setAccessible(true);mc.setScreen(new thaumcraft.client.research.ResearchPageScreen(research,(int)index.invoke(null,research,recipe)));}catch(ReflectiveOperationException failure){throw new IllegalStateException("Cannot locate actual paginated repair recipe",failure);}
    }
}
