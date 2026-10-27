package thaumichorizons;

import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import thaumcraft.aspect.*;
import thaumcraft.research.Knowledge;

/** Opt-in smoke test. All mutations are limited to a disposable th-validation world. */
@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT)
public final class ClientSmoke {
    private static int ticks,phase;
    @SubscribeEvent(priority=net.neoforged.bus.api.EventPriority.LOWEST) public static void tick(ClientTickEvent.Post event){
        if(!Boolean.getBoolean("th.clientSmoke"))return;
        var mc=Minecraft.getInstance();var server=mc.getSingleplayerServer();
        if(mc.player==null||mc.level==null||server==null||mc.getOverlay()!=null)return;
        if(!server.getWorldData().getLevelName().equals("th-validation"))throw new IllegalStateException("Smoke test requires disposable th-validation save");
        if(Boolean.getBoolean("th.boatVisualSmoke")){mc.options.pauseOnLostFocus=false;mc.options.hideGui=false;if(BoatVisualSmoke.tick(mc,server))mc.stop();return;}
        if(Boolean.getBoolean("th.boatInjectorSmoke")){mc.options.pauseOnLostFocus=false;mc.options.hideGui=false;if(BoatInjectorSmoke.tick(mc,server))mc.stop();return;}
        if(Boolean.getBoolean("th.riftSmokeOnly")){mc.options.pauseOnLostFocus=false;mc.options.hideGui=false;if(RiftSmoke.tick(mc,server))mc.stop();return;}
        if(Boolean.getBoolean("th.cloudSmokeOnly")){mc.options.pauseOnLostFocus=false;mc.options.hideGui=false;if(CloudSmoke.tick(mc,server)){System.out.println("TH_CLOUD_SMOKE_OK");mc.stop();}return;}
        if(Boolean.getBoolean("th.vehicleSmokeOnly")){mc.options.pauseOnLostFocus=false;mc.options.hideGui=false;if(VehicleSmoke.tick(mc,server)&&LensSmoke.tick(mc,server)&&CloudSmoke.tick(mc,server)){System.out.println("TH_VEHICLE_SMOKE_OK");mc.stop();}return;}
        if(ticks++==0){
            mc.options.pauseOnLostFocus=false;mc.options.hideGui=true;mc.setScreen(null);
            var checked=new java.util.LinkedHashMap<>(Content.ALL);checked.put("vat",Vats.GLASS_ITEM);checked.put("vat_solid",Vats.SOLID_ITEM);checked.put("vat_interior",Vats.INTERIOR_ITEM);
            for(var entry:checked.entrySet()){
                var s=new ItemStack(entry.getValue().get());var model=mc.getItemRenderer().getModel(s,mc.level,mc.player,0);
                if(model==mc.getModelManager().getMissingModel()||model.getParticleIcon().contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing model/texture "+entry.getKey());
                if(!net.minecraft.client.resources.language.I18n.exists(s.getDescriptionId()))throw new IllegalStateException("Missing translation "+s.getDescriptionId());
            }
            System.out.println("TH_CLIENT_MODELS_OK: "+checked.size()+" models and translations");
            for(var effect:java.util.List.of(Content.VIS_REGEN,Content.VIS_BOOST,Content.VACUUM,Content.SHOCK,Content.SYNTHESIS))if(mc.getMobEffectTextures().get(effect).contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing original effect icon "+effect.getId());
            server.execute(()->{
                var level=server.overworld();level.setDayTime(1000);level.setWeatherParameters(100000,0,false,false);
                for(BlockPos pos:BlockPos.betweenClosed(-6,63,-4,22,71,8))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(new BlockPos(0,64,0),Content.INFUSER.get().defaultBlockState());
                level.setBlockAndUpdate(new BlockPos(3,64,0),Content.VIS_DYNAMO.get().defaultBlockState());
                level.setBlockAndUpdate(new BlockPos(6,64,0),Content.ESSENTIA_DYNAMO.get().defaultBlockState());
                var blood=(BloodInfuser)level.getBlockEntity(new BlockPos(0,64,0));blood.inventory.setStackInSlot(0,new ItemStack(Content.item("blood_syringe"),8));blood.selected.add(Aspect.FIRE,4).add(Aspect.AIR,2).add(Aspect.LIFE,2);blood.acquired.add(Aspect.FIRE,2);blood.sync();
                for(var a:Aspect.values())Knowledge.get(server.getPlayerList().getPlayers().getFirst()).discoverAspect(a);
                var p=server.getPlayerList().getPlayers().getFirst();Knowledge.sync(p);p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getAbilities().flying=true;p.onUpdateAbilities();p.connection.teleport(3.5,66.5,7,180,20);
                var vat=Vats.form(level,new BlockPos(10,64,0));vat.subject=net.minecraft.world.entity.EntityType.WOLF.create(level);vat.mode=1;vat.progress=600;Infusions.add(vat.subject,4,p.getUUID(),new AspectList().add(Aspect.ARMOR,16));Infusions.add(vat.subject,8,p.getUUID(),new AspectList().add(Aspect.ENERGY,16));vat.sync();level.setBlockAndUpdate(vat.getBlockPos().above(),Vats.MATRIX.get().defaultBlockState());
                var body=Vats.form(level,new BlockPos(16,64,0));body.subject=null;body.playerSubject=null;body.mode=4;body.shellInfusions[0]=5;body.shellInfusions[1]=9;body.sync();level.setBlockAndUpdate(body.getBlockPos().above(),Vats.BEACON.get().defaultBlockState());
                for(var research:thaumcraft.research.ResearchCategories.get("ThaumicHorizons").research())Knowledge.get(p).addResearch(research.key());Knowledge.sync(p);
            });
        }
        if(ticks==70)server.execute(()->ContainmentFx.send(server.overworld(),new net.minecraft.world.phys.Vec3(3.5,65,3)));
        if(ticks==90){if(LegacyParticles.renderedContainments==0)throw new IllegalStateException("Original containment packet effect was not rendered");shot("horizons-machines.png");}
        if(ticks==100){mc.options.hideGui=false;server.execute(()->server.getPlayerList().getPlayers().getFirst().openMenu((BloodInfuser)server.overworld().getBlockEntity(new BlockPos(0,64,0))));}
        if(ticks==130){if(!(mc.screen instanceof BloodScreen))throw new IllegalStateException("Seroconverter menu not opened");shot("horizons-seroconverter.png");}
        if(ticks==140){if(!(mc.player.containerMenu instanceof BloodMenu m))throw new IllegalStateException("Missing live menu");mc.gameMode.handleInventoryButtonClick(m.containerId,64+BloodMenu.aspects().indexOf(Aspect.FIRE));}
        if(ticks==160){if(((BloodMenu)mc.player.containerMenu).tile.selected.getAmount(Aspect.FIRE)!=3)throw new IllegalStateException("Server button round trip failed");}
        if(ticks==170)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.closeContainer();p.openMenu((Dynamo)server.overworld().getBlockEntity(new BlockPos(3,64,0)));});
        if(ticks==200){if(!(mc.screen instanceof DynamoScreen))throw new IllegalStateException("Dynamo menu not opened");shot("horizons-dynamo.png");}
        if(ticks==215){server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.closeContainer();p.connection.teleport(14,68,7,180,12);});mc.options.hideGui=true;}
        if(ticks==230)shot("horizons-vats.png");
        if(ticks==231)server.execute(()->server.getPlayerList().getPlayers().getFirst().connection.teleport(17.5,64.6,5,180,0));
        if(ticks==240){if(FadeBuffer.rendered==0)throw new IllegalStateException("Clone fade was never rendered");if(VatRenderer.renderedShells==0||!((VatEntity)mc.level.getBlockEntity(new BlockPos(17,67,1))).isController())throw new IllegalStateException("Grown body model was never rendered");shot("horizons-grown-body.png");}
        if(ticks==245){mc.options.hideGui=false;server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.connection.teleport(11.5,68,6,180,15);p.openMenu((VatEntity)server.overworld().getBlockEntity(new BlockPos(11,67,1)));});}
        if(ticks==275){if(!(mc.screen instanceof VatScreen)||mc.player.containerMenu.slots.size()!=38)throw new IllegalStateException("Vat menu failed");if(((VatMenu)mc.player.containerMenu).value(5)!=4)throw new IllegalStateException("Creature infusion menu sync failed");shot("horizons-vat-menu.png");}
        if(ticks==280){server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.closeContainer();Infusions.add(p,2,null,new AspectList());Infusions.add(p,9,null,new AspectList());Infusions.add(p,10,null,new AspectList());});}
        if(ticks==300)net.neoforged.neoforge.network.PacketDistributor.sendToServer(new InfusionAction(0));
        if(ticks==320){if(!(mc.player.containerMenu instanceof net.minecraft.world.inventory.CraftingMenu))throw new IllegalStateException("Morphic fingers did not open server crafting menu");server.execute(()->server.getPlayerList().getPlayers().getFirst().closeContainer());net.neoforged.neoforge.network.PacketDistributor.sendToServer(new InfusionAction(1));net.neoforged.neoforge.network.PacketDistributor.sendToServer(new InfusionAction(2));}
        if(ticks==340){if(!Infusions.DATA.get(mc.player).getBoolean("climbDisabled")||!Infusions.DATA.get(mc.player).getBoolean("invisibilityDisabled"))throw new IllegalStateException("Self infusion toggles not synchronised");mc.setScreen(new thaumcraft.client.research.ResearchPageScreen(thaumcraft.research.ResearchCategories.getResearch("silverHeart"),2));}
        if(ticks==360){if(VatPages.rendered==0)throw new IllegalStateException("Custom vat recipe page was not rendered");shot("horizons-self-infusion-recipe.png");}
        if(ticks==365){mc.setScreen(null);server.execute(()->{
            var p=server.getPlayerList().getPlayers().getFirst();var body=(VatEntity)server.overworld().getBlockEntity(new BlockPos(17,67,1));body.shellInfusions=new int[]{1,3,4,5,6,7,8,9,10,0,0,0};body.mode=4;body.shellHealth=20;body.sync();
            p.getInventory().setItem(10,new ItemStack(Items.DIAMOND,64));Infusions.bindBeacon(p,new BlockPos(17,68,1));p.hurt(p.damageSources().genericKill(),Float.MAX_VALUE);
            if(body.mode!=0||!p.getUUID().equals(body.playerSubject)||!p.isAlive()||Infusions.has(p,2)||!Infusions.has(p,7)||!p.getInventory().getItem(10).isEmpty())throw new IllegalStateException("Real lethal-hit reincarnation failed");
            System.out.println("TH_REINCARNATION_OK: lethal damage cancelled, body consumed, inventory dropped, infusion set replaced");
        });}
        if(ticks==405&&!Infusions.has(mc.player,7))throw new IllegalStateException("Reincarnation attachment not synced to client");
        if(ticks>=405){boolean done=switch(phase){case 0->SpeciesSmoke.tick(mc,server);case 1->SoulSmoke.tick(mc,server);case 2->RegressionSmoke.tick(mc,server);case 3->DeviceSmoke.tick(mc,server);case 4->VehicleSmoke.tick(mc,server);case 5->LensSmoke.tick(mc,server);case 6->CloudSmoke.tick(mc,server);case 7->RiftSmoke.tick(mc,server);default->true;};if(done)phase++;}
        if(phase>=8){
            System.out.println("TH_CLIENT_SMOKE_OK: models, textures, menus, vat subjects, custom research pages, server key actions, lethal-hit reincarnation, eighteen species renderers, mounted Nightmare dimension travel and soul machines");mc.stop();
        }
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
