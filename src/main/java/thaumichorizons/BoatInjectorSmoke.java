package thaumichorizons;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.*;
/** Short opt-in check of the requested boats/injector update, without the full suite. */
final class BoatInjectorSmoke {
    private static int ticks;private static UUID wood,metal,cow;private static Vec3 start;
    private static ItemStack injection(int color,net.minecraft.core.Holder<MobEffect> effect){var stack=new ItemStack(Content.item("injection"));stack.set(DataComponents.POTION_CONTENTS,new PotionContents(Optional.empty(),Optional.of(color),List.of(new MobEffectInstance(effect,600))));return stack;}
    static boolean tick(Minecraft mc,MinecraftServer server){
        ticks++;
        if(ticks==1){mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{
            var level=server.overworld();var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.closeContainer();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.removeAllEffects();Infusions.DATA.set(player,new net.minecraft.nbt.CompoundTag());player.teleportTo(level,-10.5,65,80,0,0);player.getInventory().clearContent();
            for(var pos:BlockPos.betweenClosed(-44,63,70,-5,69,104))level.setBlockAndUpdate(pos,pos.getY()==63?Blocks.STONE_BRICKS.defaultBlockState():Blocks.AIR.defaultBlockState());
            for(var pos:BlockPos.betweenClosed(-40,64,74,-18,64,84))level.setBlockAndUpdate(pos,Blocks.WATER.defaultBlockState());for(var pos:BlockPos.betweenClosed(-40,64,90,-18,64,100))level.setBlockAndUpdate(pos,Blocks.LAVA.defaultBlockState());
            var a=MagicBoats.GREATWOOD.get().create(level);a.moveTo(-25,65,79,90,0);level.addFreshEntity(a);wood=a.getUUID();var b=MagicBoats.THAUMIUM.get().create(level);b.moveTo(-25,65,95,90,0);level.addFreshEntity(b);metal=b.getUUID();
            var target=EntityType.COW.create(level);target.moveTo(-10.5,65,84,0,0);target.setNoAi(true);level.addFreshEntity(target);cow=target.getUUID();
            var weapon=new ItemStack(Content.item("injector"));var ammo=NonNullList.withSize(7,ItemStack.EMPTY);ammo.set(0,injection(0xED521E,MobEffects.FIRE_RESISTANCE));InjectorItem.saveMagazine(weapon,ammo);player.getInventory().setItem(0,weapon);player.getInventory().setItem(2,injection(0xED521E,MobEffects.FIRE_RESISTANCE));player.getInventory().setItem(3,injection(0x318CFF,MobEffects.WATER_BREATHING));player.getInventory().selected=0;player.inventoryMenu.broadcastChanges();
            var recipe=level.getRecipeManager().byKey(ThaumicHorizons.id("greatwood_boat")).orElseThrow();var plank=net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.ResourceLocation.parse("thaumcraft:greatwood_planks"));var input=net.minecraft.world.item.crafting.CraftingInput.of(3,2,List.of(new ItemStack(plank),ItemStack.EMPTY,new ItemStack(plank),new ItemStack(plank),new ItemStack(plank),new ItemStack(plank)));if(!(recipe.value() instanceof net.minecraft.world.item.crafting.ShapedRecipe shaped)||!shaped.matches(input,level)||!shaped.assemble(input,level.registryAccess()).is(Content.item("greatwood_boat")))throw new IllegalStateException("Plain greatwood plank recipe failed");
        });}
        if(ticks==15){for(var type:List.of(MagicBoats.GREATWOOD,MagicBoats.THAUMIUM)){var boat=type.get().create(mc.level);var renderer=mc.getEntityRenderDispatcher().getRenderer(boat);if(!(renderer instanceof ModernBoatRenderer modern)||!(((net.minecraft.client.model.BoatModel)modern.getModelWithLocation(boat).getSecond()).parts().size()==7))throw new IllegalStateException("Boat does not use modern hull and paddles");}int red=mc.getItemColors().getColor(mc.player.getInventory().getItem(2),1),blue=mc.getItemColors().getColor(mc.player.getInventory().getItem(3),1);if((red&0xFFFFFF)!=0xED521E||(blue&0xFFFFFF)!=0x318CFF)throw new IllegalStateException("Injection tint is missing");}
        if(ticks==20){mc.options.keyUse.setDown(true);mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);}
        if(ticks==38)shot("horizons-1.0.1-injector-drawing.png");
        if(ticks==52){mc.options.keyUse.setDown(false);mc.gameMode.releaseUsingItem(mc.player);}
        if(ticks==60){if(!CrossbowItem.isCharged(mc.player.getMainHandItem()))throw new IllegalStateException("Injector did not keep its charge");server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();if(!server.overworld().getEntitiesOfClass(SyringeProjectile.class,player.getBoundingBox().inflate(10)).isEmpty())throw new IllegalStateException("Injector fired while charging");var weapon=player.getMainHandItem();var saved=ItemStack.parseOptional(server.registryAccess(),(net.minecraft.nbt.CompoundTag)weapon.save(server.registryAccess()));if(!CrossbowItem.isCharged(saved))throw new IllegalStateException("Loaded injector loses its charge on save");weapon.getItem().inventoryTick(weapon,player.level(),player,0,true);if(!CrossbowItem.isCharged(weapon))throw new IllegalStateException("Compatibility migration refunded a valid injection");});shot("horizons-1.0.1-injector-charged.png");}
        if(ticks==70)mc.gameMode.useItem(mc.player,InteractionHand.MAIN_HAND);
        if(ticks==85){if(CrossbowItem.isCharged(mc.player.getMainHandItem()))throw new IllegalStateException("Second click did not fire loaded injector");server.execute(()->{var target=(LivingEntity)server.overworld().getEntity(cow);if(!target.hasEffect(MobEffects.FIRE_RESISTANCE))throw new IllegalStateException("Actual injection projectile did not deliver mixture");});mc.setScreen(new net.minecraft.client.gui.screens.inventory.InventoryScreen(mc.player));}
        if(ticks==95){shot("horizons-1.0.1-colored-injections.png");mc.setScreen(null);System.out.println("TH_1_0_1_INJECTOR_OK: actual hold/release keeps charge, no early projectile, second click injects, saved charge and two potion colors");server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();var boat=server.overworld().getEntity(wood);player.getInventory().selected=8;player.teleportTo(boat.getX(),boat.getY(),boat.getZ());player.startRiding(boat);});}
        if(ticks==115){mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);start=mc.player.getVehicle().position();mc.options.keyUp.setDown(true);}
        if(ticks==145){var boat=(MagicBoats.LegacyBoat)mc.player.getVehicle();if(boat.position().distanceToSqr(start)<1||!boat.getPaddleState(0)||!boat.getPaddleState(1))throw new IllegalStateException("Vanilla boat movement/paddles failed");shot("horizons-1.0.1-greatwood-paddles.png");mc.options.keyUp.setDown(false);server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();var target=server.overworld().getEntity(metal);player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);player.removeAllEffects();player.teleportTo(target.getX(),target.getY(),target.getZ());player.startRiding(target);player.igniteForSeconds(20);});}
        if(ticks==170){start=mc.player.getVehicle().position();mc.options.keyUp.setDown(true);}
        if(ticks==200){var boat=(MagicBoats.LegacyBoat)mc.player.getVehicle();if(!boat.metal||!boat.fireImmune()||boat.getY()<64.2||boat.position().distanceToSqr(start)<1||mc.player.isOnFire()||!mc.player.hasEffect(MobEffects.FIRE_RESISTANCE)||mc.player.getHealth()<19)throw new IllegalStateException("Modern thaumium boat failed lava ride or fire protection");shot("horizons-1.0.1-thaumium-lava-paddles.png");mc.options.keyUp.setDown(false);System.out.println("TH_1_0_1_BOATS_OK: vanilla seven-part model, animated oars and steering, plain plank crafting, survival lava ride/fire protection");}
        if(ticks==210){mc.options.setCameraType(CameraType.FIRST_PERSON);server.execute(()->{var player=server.getPlayerList().getPlayers().getFirst();player.stopRiding();player.setGameMode(net.minecraft.world.level.GameType.CREATIVE);player.teleportTo(15.5,65,8.5);player.getInventory().selected=0;player.inventoryMenu.broadcastChanges();});}
        if(ticks==225)System.out.println("TH_1_0_1_FOCUSED_OK: only boats, injector and injection colors; full suite not run");return ticks>=230;
    }
    private static void shot(String name){var mc=Minecraft.getInstance();Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),c->System.out.println("TH_SCREENSHOT: "+name));}
}
