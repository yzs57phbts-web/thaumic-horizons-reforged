package thaumichorizons;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.platform.Baubles;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class LensGameTests {
    private static Player player(GameTestHelper h){var player=h.makeMockPlayer(GameType.SURVIVAL);player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(thaumcraft.registry.ModItems.GOGGLES_OF_REVEALING.get()));return player;}
    @GameTest(template="test/platform") public static void installingLensConsumesOneAndSwapReturnsOldLensToExistingStack(GameTestHelper h){
        var player=player(h);player.getInventory().setItem(0,new ItemStack(Content.item("lens_fire"),64));player.getInventory().setItem(1,new ItemStack(Content.item("lens_water"),64));h.assertTrue(Lenses.change(player,"fire")&&player.getInventory().getItem(0).getCount()==63&&Lenses.installed(Lenses.goggles(player)).equals("fire"),"install consumed wrong amount");h.assertTrue(Lenses.change(player,"water")&&player.getInventory().getItem(0).getCount()==64&&player.getInventory().getItem(1).getCount()==63,"swapping failed to return old lens");h.assertTrue(Lenses.change(player,"REMOVE")&&player.getInventory().getItem(1).getCount()==64&&Lenses.installed(Lenses.goggles(player)).isEmpty(),"removal lost or duplicated lens");h.succeed();
    }
    @GameTest(template="test/platform") public static void fullInventorySwapRollsBackWithoutLosingEitherLens(GameTestHelper h){
        var player=player(h);player.getInventory().setItem(0,new ItemStack(Content.item("lens_fire")));Lenses.change(player,"fire");for(int i=0;i<36;i++)player.getInventory().setItem(i,new ItemStack(Items.DIAMOND,64));player.getInventory().setItem(0,new ItemStack(Content.item("lens_water"),64));h.assertTrue(!Lenses.change(player,"water")&&player.getInventory().getItem(0).getCount()==64&&Lenses.installed(Lenses.goggles(player)).equals("fire"),"failed swap consumed either lens");h.assertTrue(!Lenses.change(player,"REMOVE")&&Lenses.installed(Lenses.goggles(player)).equals("fire"),"removal lost lens into full inventory");player.getInventory().setItem(1,ItemStack.EMPTY);h.assertTrue(Lenses.change(player,"REMOVE")&&player.getInventory().getItem(1).is(Content.item("lens_fire")),"freeing slot did not permit safe removal");h.succeed();
    }
    @GameTest(template="test/platform") public static void equippedCaseSuppliesAndReceivesLensesWithFullInventory(GameTestHelper h){
        var player=player(h);var pouch=new ItemStack(Content.item("lens_case"));var contents=Lenses.contents(pouch);var fire=new ItemStack(Content.item("lens_fire"));fire.set(DataComponents.CUSTOM_NAME,net.minecraft.network.chat.Component.literal("Custom lens"));contents.set(0,fire);contents.set(1,new ItemStack(Content.item("lens_water")));Lenses.contents(pouch,contents);Baubles.set(player,3,pouch);for(int i=0;i<36;i++)player.getInventory().setItem(i,new ItemStack(Items.DIAMOND,64));h.assertTrue(Lenses.available(player).size()==2&&Lenses.change(player,"fire")&&Lenses.change(player,"water"),"equipped case did not provide lenses");var saved=Lenses.contents(Baubles.get(player,3));h.assertTrue(saved.get(0).is(Content.item("lens_fire"))&&saved.get(0).getHoverName().getString().equals("Custom lens")&&saved.get(1).isEmpty(),"case swap lost original metadata");var tag=(net.minecraft.nbt.CompoundTag)Lenses.goggles(player).save(h.getLevel().registryAccess());var restored=ItemStack.parseOptional(h.getLevel().registryAccess(),tag);h.assertTrue(Lenses.installed(restored).equals("water"),"installed lens lost on reload");h.succeed();
    }
    @GameTest(template="test/platform") public static void caseMenuLoadsLensesRejectsOtherItemsAndLocksItself(GameTestHelper h){
        var player=player(h);var pouch=new ItemStack(Content.item("lens_case"));player.setItemInHand(InteractionHand.MAIN_HAND,pouch);player.getInventory().setItem(9,new ItemStack(Content.item("lens_air"),8));player.getInventory().setItem(10,new ItemStack(Items.DIAMOND));var menu=new LensCaseMenu(1,player.getInventory(),InteractionHand.MAIN_HAND);menu.quickMoveStack(player,18);h.assertTrue(Lenses.contents(pouch).get(0).getCount()==8&&player.getInventory().getItem(9).isEmpty(),"case shift-loading lost contents");h.assertTrue(menu.quickMoveStack(player,19).isEmpty(),"case accepted diamond transfer");h.assertTrue(player.getInventory().getItem(10).is(Items.DIAMOND),"invalid transfer removed diamond");h.assertTrue(!menu.getSlot(45).mayPickup(player),"case can be picked up from locked slot "+menu.lockedSlot+", selected="+player.getInventory().selected);menu.clicked(0,0,ClickType.SWAP,player);h.assertTrue(player.getMainHandItem()==pouch&&Lenses.contents(pouch).get(0).getCount()==8,"number swap moved case into itself");h.succeed();
    }
    @GameTest(template="test/platform") public static void lensChangeRequiresActualGogglesAndOwnedLens(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);player.getInventory().setItem(0,new ItemStack(Content.item("lens_fire"),3));h.assertTrue(!Lenses.change(player,"fire")&&player.getInventory().getItem(0).getCount()==3,"lens consumed without goggles");player.setItemSlot(EquipmentSlot.HEAD,new ItemStack(thaumcraft.registry.ModItems.GOGGLES_OF_REVEALING.get()));h.assertTrue(!Lenses.change(player,"invalid")&&!Lenses.change(player,"air")&&Lenses.installed(Lenses.goggles(player)).isEmpty(),"invalid or unowned lens installed");h.succeed();
    }
}
