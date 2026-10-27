package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class InjectorGameTests {
    static ItemStack mixture(int ticks){var stack=new ItemStack(Content.item("injection"));stack.set(DataComponents.POTION_CONTENTS,new PotionContents(java.util.Optional.empty(),java.util.Optional.of(0x963CDE),java.util.List.of(new MobEffectInstance(MobEffects.POISON,ticks,1))));return stack;}
    static Player player(GameTestHelper h){var player=h.makeMockPlayer(GameType.SURVIVAL);player.setPos(h.absolutePos(new BlockPos(2,3,2)).getCenter());player.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Content.item("injector")));return player;}
    static void load(Player p,ItemStack... cartridges){var slots=NonNullList.withSize(7,ItemStack.EMPTY);for(int i=0;i<cartridges.length;i++)slots.set(i,cartridges[i]);InjectorItem.saveMagazine(p.getMainHandItem(),slots);}
    static void fire(Player p){var stack=p.getMainHandItem();((InjectorItem)stack.getItem()).releaseUsing(stack,p.level(),p,stack.getUseDuration(p)-30);((InjectorItem)stack.getItem()).use(p.level(),p,InteractionHand.MAIN_HAND);}
    @GameTest(template="test/platform") public static void magazineHasSevenSlotsAndRejectsArrows(GameTestHelper h){
        var player=player(h);var menu=new InjectorMenu(0,player.getInventory(),InteractionHand.MAIN_HAND);
        h.assertValueEqual(menu.slots.size(),43,"wrong inventory layout");
        for(int i=0;i<7;i++){h.assertTrue(menu.getSlot(i).mayPlace(mixture(200))&&menu.getSlot(i).mayPlace(new ItemStack(Content.item("blast_phial")))&&!menu.getSlot(i).mayPlace(new ItemStack(Items.ARROW))&&menu.getSlot(i).getMaxStackSize()==1,"magazine accepted invalid cartridge");menu.getSlot(i).set(mixture(200+i));}
        var reloaded=new InjectorMenu(1,player.getInventory(),InteractionHand.MAIN_HAND);
        for(int i=0;i<7;i++)h.assertTrue(ItemStack.isSameItemSameComponents(reloaded.getSlot(i).getItem(),mixture(200+i)),"magazine changes were not saved");h.succeed();
    }
    @GameTest(template="test/platform") public static void magazineShiftClickAndHandLockDoNotDuplicateWeapon(GameTestHelper h){
        var player=player(h);var menu=new InjectorMenu(0,player.getInventory(),InteractionHand.MAIN_HAND);player.getInventory().setItem(9,mixture(300));
        h.assertTrue(!menu.getSlot(34+player.getInventory().selected).mayPickup(player),"weapon can be picked up while its magazine is open");
        menu.quickMoveStack(player,7);h.assertTrue(menu.getSlot(0).hasItem()&&player.getInventory().getItem(9).isEmpty(),"shift click did not fill magazine");
        var weapon=player.getMainHandItem();menu.clicked(0,player.getInventory().selected,net.minecraft.world.inventory.ClickType.SWAP,player);
        h.assertTrue(player.getMainHandItem()==weapon&&menu.stillValid(player),"number key moved weapon into its own magazine");
        menu.quickMoveStack(player,0);int mixtures=0;for(var stack:player.getInventory().items)if(stack.is(Content.item("injection")))mixtures+=stack.getCount();h.assertTrue(InjectorItem.next(InjectorItem.magazine(weapon))<0&&mixtures==1,"unloading duplicated or lost mixture");h.succeed();
    }
    @GameTest(template="test/platform") public static void magazineSurvivesItemSaveWithEveryMixture(GameTestHelper h){
        var player=player(h);load(player,mixture(200),mixture(400),new ItemStack(Content.item("blast_phial")));var weapon=player.getMainHandItem();
        var nbt=weapon.save(h.getLevel().registryAccess());var restored=ItemStack.parseOptional(h.getLevel().registryAccess(),(CompoundTag)nbt);var ammo=InjectorItem.magazine(restored);
        h.assertTrue(ItemStack.isSameItemSameComponents(ammo.get(0),mixture(200))&&ItemStack.isSameItemSameComponents(ammo.get(1),mixture(400))&&ammo.get(2).is(Content.item("blast_phial"))&&ammo.get(6).isEmpty(),"saved magazine lost positions or mixtures");h.succeed();
    }
    @GameTest(template="test/platform") public static void injectorConsumesOneMixtureAndRotatesMagazine(GameTestHelper h){
        var player=player(h);load(player,mixture(200),mixture(400));player.getInventory().setItem(9,new ItemStack(Items.ARROW,12));fire(player);
        var ammo=InjectorItem.magazine(player.getMainHandItem());h.assertTrue(ItemStack.isSameItemSameComponents(ammo.get(0),mixture(400))&&ammo.stream().filter(s->!s.isEmpty()).count()==1,"shot did not rotate one cartridge");
        h.assertValueEqual(player.getMainHandItem().getDamageValue(),1,"durability cost");h.assertValueEqual(player.getInventory().getItem(9).getCount(),12,"injector consumed ordinary arrows");
        var shots=h.getLevel().getEntitiesOfClass(SyringeProjectile.class,new AABB(player.blockPosition()).inflate(3));h.assertTrue(shots.stream().anyMatch(s->s.getOwner()==player&&ItemStack.isSameItemSameComponents(s.getPickupItemStackOrigin(),mixture(200))),"wrong projectile or mixture");h.succeed();
    }
    @GameTest(template="test/platform") public static void injectorNeverFiresArrowsOrEmptyCartridges(GameTestHelper h){
        var player=player(h);player.getInventory().setItem(9,new ItemStack(Items.ARROW,64));var item=(InjectorItem)player.getMainHandItem().getItem();
        h.assertTrue(item.use(h.getLevel(),player,InteractionHand.MAIN_HAND).getResult()==InteractionResult.FAIL,"empty injector accepted ordinary arrows");fire(player);
        h.assertValueEqual(player.getMainHandItem().getDamageValue(),0,"empty trigger consumed durability");h.succeed();
    }
    @GameTest(template="test/platform") public static void injectedMixtureAppliesOnActualProjectileCollision(GameTestHelper h){
        var player=player(h);load(player,mixture(600));var cow=EntityType.COW.create(h.getLevel());cow.setPos(player.getX(),player.getY()+.3,player.getZ()+3);h.getLevel().addFreshEntity(cow);fire(player);
        var shot=h.getLevel().getEntitiesOfClass(SyringeProjectile.class,new AABB(player.blockPosition()).inflate(3)).stream().filter(s->s.getOwner()==player).findFirst().orElseThrow();
        for(int i=0;i<10&&!shot.isRemoved();i++)shot.tick();h.assertTrue(cow.hasEffect(MobEffects.POISON)&&cow.getEffect(MobEffects.POISON).getAmplifier()==1,"syringe hit did not deliver mixture");h.succeed();
    }
    @GameTest(template="test/platform") public static void blastCartridgeLaunchesPhialAndPreservesMixture(GameTestHelper h){
        var player=player(h);var phial=new ItemStack(Content.item("blast_phial"));phial.set(DataComponents.POTION_CONTENTS,mixture(350).get(DataComponents.POTION_CONTENTS));load(player,phial);fire(player);
        var shots=h.getLevel().getEntitiesOfClass(BlastPhial.class,new AABB(player.blockPosition()).inflate(3));h.assertTrue(shots.stream().anyMatch(s->s.getOwner()==player&&ItemStack.isSameItemSameComponents(s.getItem(),phial)),"blast shot lost mixture");h.succeed();
    }
    @GameTest(template="test/platform") public static void missedSyringePersistsRecoverableMixture(GameTestHelper h){
        var player=player(h);var syringe=new SyringeProjectile(player,h.getLevel(),mixture(1234),player.getMainHandItem());var tag=new CompoundTag();syringe.save(tag);
        var restored=(SyringeProjectile)EntityType.loadEntityRecursive(tag,h.getLevel(),e->e);h.assertTrue(restored!=null&&restored.pickup==net.minecraft.world.entity.projectile.AbstractArrow.Pickup.ALLOWED&&ItemStack.isSameItemSameComponents(restored.getPickupItemStackOrigin(),mixture(1234)),"missed syringe lost recoverable mixture on reload");h.succeed();
    }
}
