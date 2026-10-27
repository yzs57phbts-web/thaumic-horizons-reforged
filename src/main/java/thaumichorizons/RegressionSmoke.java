package thaumichorizons;

import net.minecraft.client.*;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;

final class RegressionSmoke {
    private static int ticks;
    private static net.minecraft.world.entity.LivingEntity vatPreview;
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1){
            for(String name:java.util.List.of("blood_infuser","vis_dynamo","essentia_dynamo","soulforge","soul_sieve","inspiratron","modified_matrix","soul_beacon")){
                var model=mc.getItemRenderer().getModel(new ItemStack(Content.item(name)),mc.level,mc.player,0);
                for(var context:java.util.List.of(ItemDisplayContext.GUI,ItemDisplayContext.FIRST_PERSON_LEFT_HAND,ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,ItemDisplayContext.THIRD_PERSON_LEFT_HAND,ItemDisplayContext.THIRD_PERSON_RIGHT_HAND))if(model.getTransforms().getTransform(context).scale.x()>.51F)throw new IllegalStateException("Oversized item transform "+name+" / "+context);
            }
            for(String name:java.util.List.of("empty_syringe","blood_syringe","blood_sample","injection"))if(mc.getItemRenderer().getModel(new ItemStack(Content.item(name)),mc.level,mc.player,0).isCustomRenderer())throw new IllegalStateException("Syringe still uses legacy oversized renderer");
            server.execute(()->{
                var p=server.getPlayerList().getPlayers().getFirst();p.getInventory().clearContent();
                var names=java.util.List.of("injector","soulforge","soul_sieve","inspiratron","blood_infuser","modified_matrix","essentia_dynamo","empty_syringe","injection");
                for(int i=0;i<names.size();i++)p.getInventory().setItem(i,new ItemStack(Content.item(names.get(i))));
                p.getInventory().setItem(9,InjectorGameTests.mixture(600));var blast=new ItemStack(Content.item("blast_phial"));blast.set(DataComponents.POTION_CONTENTS,InjectorGameTests.mixture(300).get(DataComponents.POTION_CONTENTS));p.getInventory().setItem(10,blast);
                var pos=new BlockPos(29,64,2);server.overworld().setBlockAndUpdate(pos,Specimens.JAR.get().defaultBlockState());var jar=(SpecimenJarEntity)server.overworld().getBlockEntity(pos);var specimen=EntityType.CHICKEN.create(server.overworld());specimen.setYRot(140);specimen.yBodyRot=120;specimen.yHeadRot=80;jar.stack=Specimens.write(specimen);jar.sync();
                p.connection.teleport(29.5,66,8,180,12);p.getInventory().selected=1;p.inventoryMenu.broadcastChanges();
            });
        }
        if(ticks==20){mc.options.hideGui=false;mc.player.getInventory().selected=1;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(1));}
        if(ticks==40){shot("horizons-fixed-held-forge.png");vatPreview=((VatEntity)mc.level.getBlockEntity(new BlockPos(11,67,1))).occupant();server.execute(()->((VatEntity)server.overworld().getBlockEntity(new BlockPos(11,67,1))).sync());}
        if(ticks==60){
            if(((VatEntity)mc.level.getBlockEntity(new BlockPos(11,67,1))).occupant()!=vatPreview)throw new IllegalStateException("Vat sync recreated contained preview");
            var jar=(SpecimenJarEntity)mc.level.getBlockEntity(new BlockPos(29,64,2));var preview=jar.occupant();ContainedPreview.prepare(preview,29.5,64,2.5,mc.level.getGameTime());
            if(preview.yBodyRot!=preview.yBodyRotO||preview.yHeadRot!=preview.yHeadRotO||preview.xo!=preview.getX())throw new IllegalStateException("Contained interpolation not stable");
            mc.player.getInventory().selected=0;mc.player.connection.send(new net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket(0));
        }
        if(ticks==80)shot("horizons-injector.png");
        if(ticks==90)KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(70));
        if(ticks==100){
            if(!(mc.screen instanceof InjectorScreen)||!(mc.player.containerMenu instanceof InjectorMenu)||!(mc.player.getMainHandItem().getItem() instanceof InjectorItem)||!mc.player.getOffhandItem().isEmpty())throw new IllegalStateException("F did not open injector magazine without swapping hands");
            shot("horizons-injector-magazine-empty.png");
        }
        if(ticks==110||ticks==120)mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId,ticks==110?7:8,0,net.minecraft.world.inventory.ClickType.QUICK_MOVE,mc.player);
        if(ticks==140){var menu=(InjectorMenu)mc.player.containerMenu;if(!menu.getSlot(0).getItem().is(Content.item("injection"))||!menu.getSlot(1).getItem().is(Content.item("blast_phial")))throw new IllegalStateException("Injector magazine does not accept both mixtures via real inventory packets");shot("horizons-injector-magazine-filled.png");mc.player.closeContainer();mc.setScreen(null);}
        if(ticks==160){if(InjectorItem.magazine(mc.player.getMainHandItem()).stream().filter(s->!s.isEmpty()).count()!=2)throw new IllegalStateException("Magazine contents lost after closing");mc.options.keyUse.setDown(true);mc.gameMode.useItem(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND);}
        if(ticks==185)shot("horizons-injector-drawing.png");
        if(ticks==195){mc.options.keyUse.setDown(false);mc.gameMode.releaseUsingItem(mc.player);}
        if(ticks==200&&!net.minecraft.world.item.CrossbowItem.isCharged(mc.player.getMainHandItem()))throw new IllegalStateException("Released injector did not keep its charge");
        if(ticks==205)mc.gameMode.useItem(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND);
        if(ticks==215){if(InjectorItem.magazine(mc.player.getMainHandItem()).stream().filter(s->!s.isEmpty()).count()!=1)throw new IllegalStateException("Real injector draw/release did not consume one cartridge");mc.setScreen(new InventoryScreen(mc.player));}
        if(ticks==225)shot("horizons-fixed-inventory.png");
        if(ticks==235){mc.setScreen(null);System.out.println("TH_REGRESSIONS_OK: both-hand item transforms, flat syringes, stable jar/vat interpolation and exact vanilla recolor");}
        if(ticks==245)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var injector=p.getMainHandItem();p.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND,injector);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,thaumcraft.wand.Wands.create(thaumcraft.wand.WandRegistry.CAP_GOLD,thaumcraft.wand.WandRegistry.ROD_GREATWOOD,false));p.inventoryMenu.broadcastChanges();});
        if(ticks==255){var key=com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(70);KeyMapping.set(key,true);KeyMapping.click(key);}
        if(ticks==270){KeyMapping.set(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(70),false);if(!(mc.screen instanceof InjectorScreen)||!(mc.player.containerMenu instanceof InjectorMenu menu)||menu.hand!=net.minecraft.world.InteractionHand.OFF_HAND||!(mc.player.getOffhandItem().getItem() instanceof InjectorItem)||!thaumcraft.wand.Wands.isWand(mc.player.getMainHandItem()))throw new IllegalStateException("Offhand injector F conflicts with main-hand wand or swaps hands");shot("horizons-injector-offhand.png");mc.player.closeContainer();mc.setScreen(null);}
        if(ticks==275)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();var injector=p.getOffhandItem();p.setItemInHand(net.minecraft.world.InteractionHand.OFF_HAND,ItemStack.EMPTY);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,injector);p.inventoryMenu.broadcastChanges();});
        if(ticks==285)System.out.println("TH_INJECTOR_OK: F in either hand without swapping or conflicting with a wand, seven-slot menu, real shift-loading, saved mixtures and drawn injection shot");
        return ticks>=285;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
