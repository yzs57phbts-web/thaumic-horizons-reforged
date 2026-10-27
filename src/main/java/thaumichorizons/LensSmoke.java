package thaumichorizons;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.aspect.*;
import thaumcraft.research.*;
import thaumcraft.node.NodeBlockEntity;
final class LensSmoke {
    private static int ticks;
    private static boolean deathRequested;
    private static volatile boolean deathPassed;
    private static boolean finished;
    static boolean tick(Minecraft mc,MinecraftServer server){
        if(finished)return true;
        ticks++;
        if(ticks==1){
            for(var entry:Content.ALL.entrySet()){var stack=new ItemStack(entry.getValue().get());var model=mc.getItemRenderer().getModel(stack,mc.level,mc.player,0);if(model==mc.getModelManager().getMissingModel()||model.getParticleIcon().contents().name().getPath().equals("missingno"))throw new IllegalStateException("Missing late-content model: "+entry.getKey());}
            server.execute(()->{
                var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.getAbilities().flying=true;player.onUpdateAbilities();player.removeAllEffects();
                for(var entity:level.getEntities((Entity)null,new AABB(-6,62,36,11,75,49),e->!(e instanceof net.minecraft.world.entity.player.Player)))entity.discard();for(var pos:BlockPos.betweenClosed(-6,63,36,10,71,48))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
                for(var pos:BlockPos.betweenClosed(3,65,39,5,67,40))level.setBlockAndUpdate(pos,pos.getZ()==40?Blocks.STONE.defaultBlockState():Blocks.DIAMOND_BLOCK.defaultBlockState());level.setBlockAndUpdate(new BlockPos(3,65,40),Blocks.GLOWSTONE.defaultBlockState());level.setBlockAndUpdate(new BlockPos(5,65,40),Blocks.CHEST.defaultBlockState());
                for(int x:new int[]{0,3}){var pos=new BlockPos(x,64,x==0?40:37);level.setBlockAndUpdate(pos,thaumcraft.registry.ModBlocks.AURA_NODE.get().defaultBlockState());((NodeBlockEntity)level.getBlockEntity(pos)).setAspects(new AspectList().add(x==0?Aspect.AIR:Aspect.FIRE,30));}level.setBlockAndUpdate(new BlockPos(0,65,40),Recombinator.BLOCK.get().defaultBlockState());level.setBlockAndUpdate(new BlockPos(1,65,40),Blocks.REDSTONE_BLOCK.defaultBlockState());
                level.setBlockAndUpdate(new BlockPos(-2,64,42),Blocks.STONE.defaultBlockState());level.setBlockAndUpdate(new BlockPos(-2,65,42),thaumcraft.registry.ModBlocks.MAGIC_MIRROR.get().defaultBlockState().setValue(thaumcraft.device.MirrorBlock.FACING,Direction.UP));
                var shardPos=new BlockPos(-3,65,40);level.setBlockAndUpdate(shardPos,SyntheticNode.BLOCK.get().defaultBlockState());var shard=(SyntheticNode)level.getBlockEntity(shardPos);shard.setAspects(new AspectList().add(Aspect.AIR,30).add(Aspect.MAGIC,15));shard.addToContainer(Aspect.AIR,25);shard.addToContainer(Aspect.MAGIC,10);
                var cow=EntityType.COW.create(level);cow.moveTo(7,64,42,0,0);cow.setNoAi(true);level.addFreshEntity(cow);var zombie=EntityType.ZOMBIE.create(level);zombie.moveTo(1.5,64,43,0,0);zombie.setNoAi(true);zombie.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.IRON_HELMET));level.addFreshEntity(zombie);
                player.getInventory().clearContent();player.getInventory().selected=0;player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(thaumcraft.registry.ModItems.GOGGLES_OF_REVEALING.get()));var pouch=new ItemStack(Content.item("lens_case"));var contents=Lenses.contents(pouch);for(int i=0;i<5;i++)contents.set(i,new ItemStack(Content.item("lens_"+Lenses.TYPES.get(i)),3));Lenses.contents(pouch,contents);player.setItemInHand(InteractionHand.MAIN_HAND,pouch);player.getInventory().setItem(9,new ItemStack(Content.item("lens_fire"),5));
                Knowledge.KEY.set(player,new PlayerKnowledge());for(var aspect:Aspect.values())Knowledge.get(player).discoverAspect(aspect);for(var research:ResearchCategories.get("ThaumicHorizons").research())Knowledge.get(player).addResearch(research.key());Knowledge.sync(player);player.inventoryMenu.broadcastChanges();player.connection.teleport(4.5,65,44.5,180,0);level.setDayTime(1000);
            });
        }
        if(ticks==25){mc.player.getInventory().selected=0;mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
        if(ticks==35){if(!(mc.screen instanceof LensCaseScreen))throw new IllegalStateException("Lens case did not open through actual use packet");shot("horizons-lens-case.png");mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId,18,0,net.minecraft.world.inventory.ClickType.QUICK_MOVE,mc.player);}
        if(ticks==45){if(!mc.player.getInventory().getItem(9).isEmpty())throw new IllegalStateException("Lens case network shift-loading failed");mc.player.closeContainer();}
        if(ticks==55)KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(86));
        if(ticks==65){if(!(mc.screen instanceof LensRadialScreen))throw new IllegalStateException("V did not open lens radial");shot("horizons-lens-radial.png");mc.screen.mouseClicked(mc.screen.width/2D,mc.screen.height/2D-60,0);}
        if(ticks==80){if(!Lenses.installed(Lenses.goggles(mc.player)).equals("air"))throw new IllegalStateException("Radial did not install server-owned air lens");shot("horizons-lens-air.png");PacketDistributor.sendToServer(new Lenses.Change("fire"));server.execute(()->server.overworld().setDayTime(18000));}
        if(ticks==95){if(!mc.player.hasEffect(MobEffects.NIGHT_VISION))throw new IllegalStateException("Fire lens did not grant night vision");shot("horizons-lens-fire.png");server.execute(()->server.getPlayerList().getPlayers().getFirst().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,1800,0)));}
        if(ticks==105)PacketDistributor.sendToServer(new Lenses.Change("REMOVE"));
        if(ticks==120){var effect=mc.player.getEffect(MobEffects.NIGHT_VISION);if(effect==null||effect.getDuration()<1000)throw new IllegalStateException("Removing lens erased ordinary night vision potion");server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.removeAllEffects();for(var pos:BlockPos.betweenClosed(0,64,41,9,70,49))server.overworld().setBlockAndUpdate(pos,(pos.getX()==0||pos.getX()==9||pos.getY()==64||pos.getY()==70||pos.getZ()==41||pos.getZ()==49?Blocks.GLASS:Blocks.WATER).defaultBlockState());Lenses.change(player,"water");});}
        if(ticks==145){if(!mc.player.isUnderWater()||!mc.player.hasEffect(MobEffects.NIGHT_VISION))throw new IllegalStateException("Water lens underwater vision failed");shot("horizons-lens-water.png");server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();for(var pos:BlockPos.betweenClosed(0,64,41,9,70,49))server.overworld().setBlockAndUpdate(pos,Blocks.AIR.defaultBlockState());server.overworld().setDayTime(1000);Lenses.change(player,"earth");player.connection.teleport(4.5,65,44.5,180,0);});}
        if(ticks==180){if(!mc.level.getBlockState(new BlockPos(4,66,40)).is(Lenses.EVANESCENT.get())||!mc.level.getBlockState(new BlockPos(3,65,40)).is(Blocks.GLOWSTONE)||!(mc.level.getBlockEntity(new BlockPos(5,65,40)) instanceof net.minecraft.world.level.block.entity.ChestBlockEntity))throw new IllegalStateException("Earth lens window missing or replaced protected light/container");server.execute(()->{if(!server.overworld().getBlockState(new BlockPos(4,66,40)).is(Blocks.STONE))throw new IllegalStateException("Earth lens modified server world");});shot("horizons-lens-earth.png");PacketDistributor.sendToServer(new Lenses.Change("order_entropy"));}
        if(ticks==205){if(!mc.level.getBlockState(new BlockPos(4,66,40)).is(Blocks.STONE))throw new IllegalStateException("Earth lens left a hole after switching");server.execute(()->server.overworld().setBlockAndUpdate(new BlockPos(4,66,40),Blocks.LAPIS_BLOCK.defaultBlockState()));}
        if(ticks>=260&&!deathRequested){var target=ScanManager.resolveBlock(mc.level,new BlockPos(4,66,40));if(target!=null&&ScanManager.hasBeenScanned(mc.player,target)){
            shot("horizons-lens-order-entropy.png");System.out.println("TH_LENSES_OK: V radial, real case menu/shift-loading, night vision with potion preservation, clear underwater view, reversible local earth window, automatic server scan");deathRequested=true;
            server.execute(()->{var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();var amulet=new ItemStack(Content.item("mirror_amulet"));thaumcraft.device.MirrorKeys.LINK.set(amulet,GlobalPos.of(level.dimension(),new BlockPos(-2,65,42)));thaumcraft.platform.Baubles.set(player,0,amulet);player.getInventory().setItem(1,new ItemStack(Items.DIAMOND,24));var vat=(VatEntity)level.getBlockEntity(new BlockPos(17,67,1));vat.mode=4;vat.shellHealth=20;vat.shellInfusions=new int[12];vat.shellInfusions[0]=7;vat.sync();Infusions.bindBeacon(player,new BlockPos(17,68,1));player.hurt(level.damageSources().genericKill(),Float.MAX_VALUE);
                int diamonds=level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new AABB(-3,64,41,0,68,44)).stream().filter(e->e.getItem().is(Items.DIAMOND)).mapToInt(e->e.getItem().getCount()).sum();if(!player.isAlive()||diamonds!=24||!thaumcraft.platform.Baubles.get(player,0).isEmpty()||vat.mode!=0||!Infusions.has(player,7))throw new IllegalStateException("Mirror amulet or reincarnation real lethal-hit integration failed");System.out.println("TH_MIRROR_AMULET_OK: actual lethal hit sent inventory through mirror before beacon reincarnation");deathPassed=true;player.connection.teleport(-1,67,44,180,20);
            });
        }
        }
        if(ticks>700&&!deathPassed)throw new IllegalStateException("Order/entropy lens failed automatic scan or mirror lethal-hit integration");
        if(deathPassed&&ticks%20==0){shot("horizons-recombinator-shard.png");finished=true;return true;}return false;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
