package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.*;
import thaumcraft.wand.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class GatewayGameTests {
    private static PocketPlanes.Plane plane(GameTestHelper h,BlockPos origin){var data=PocketPlanes.get(h.getLevel().getServer());var plane=data.create("Gateway realm",new AspectList().add(Aspect.AIR,20),GlobalPos.of(h.getLevel().dimension(),origin),99);plane.ready=true;plane.color=0xABCDEF;return plane;}
    private static Gateways.SlotEntity frame(GameTestHelper h,BlockPos top,Direction.Axis axis){
        if(h.getLevel().getBlockEntity(top) instanceof Gateways.SlotEntity old)old.take();for(var pos:BlockPos.betweenClosed(top.offset(-2,-4,-2),top.offset(2,0,2)))h.getLevel().removeBlock(pos,false);h.getLevel().setBlockAndUpdate(top,Gateways.SLOT.get().defaultBlockState());for(int t=-2;t<=2;t++)for(int y=0;y>=-4;y--)if((Math.abs(t)==2||y==0||y==-4)&&(t!=0||y!=0)){boolean corner=Math.abs(t)==2&&(y==0||y==-4);h.getLevel().setBlockAndUpdate(axis==Direction.Axis.X?top.offset(t,y,0):top.offset(0,y,t),(corner?thaumcraft.registry.ModBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT.get():thaumcraft.registry.ModBlocks.ANCIENT_STONE.get()).defaultBlockState());}return (Gateways.SlotEntity)h.getLevel().getBlockEntity(top);
    }
    private static Player player(GameTestHelper h){var player=h.makeMockPlayer(GameType.SURVIVAL);var wand=Wands.create(WandRegistry.CAP_GOLD,WandRegistry.ROD_GREATWOOD,false);Wands.fill(wand);player.setItemInHand(InteractionHand.MAIN_HAND,wand);return player;}
    @GameTest(template="test/platform") public static void firstPlaneKeystoneIsBoundColoredAndKeepsNameAfterReload(GameTestHelper h){
        var local=new PocketPlanes();var plane=local.create("First plane",new AspectList(),GlobalPos.of(Level.OVERWORLD,h.absolutePos(new BlockPos(3,3,3))),0);plane.ready=true;plane.color=0x113355;var key=Gateways.key(plane);var loaded=ItemStack.parseOptional(h.getLevel().registryAccess(),(CompoundTag)key.save(h.getLevel().registryAccess()));h.assertTrue(Gateways.planeId(loaded)==0&&Gateways.color(loaded)==0x113355&&((TranslatableContents)loaded.getItem().getName(loaded).getContents()).getKey().equals("item.thaumichorizons.keystone_bound"),"first plane mislabeled as blank or lost tint");h.succeed();
    }
    @GameTest(template="test/platform") public static void completeFramesActivateInBothOrientationsAndPayVisOnce(GameTestHelper h){
        var top=h.absolutePos(new BlockPos(3,6,3));var plane=plane(h,top);var player=player(h);for(var axis:new Direction.Axis[]{Direction.Axis.X,Direction.Axis.Z}){var slot=frame(h,top,axis);slot.insert(Gateways.key(plane));var wand=player.getMainHandItem();int before=Wands.getVis(wand,Aspect.AIR);h.assertTrue(slot.activate(player,wand)&&slot.open&&slot.axis==axis&&Wands.getVis(wand,Aspect.AIR)<before,"valid gateway failed or was free "+axis);int after=Wands.getVis(wand,Aspect.AIR);h.assertTrue(!slot.activate(player,wand)&&Wands.getVis(wand,Aspect.AIR)==after,"reopening charged twice");h.assertTrue(plane.portals[slot.side].equals(GlobalPos.of(h.getLevel().dimension(),top))&&h.getLevel().getBlockState(top.below(2)).is(Gateways.PORTAL.get()),"gateway endpoint not stored");var saved=slot.saveWithFullMetadata(h.getLevel().registryAccess());var loaded=new Gateways.SlotEntity(top,slot.getBlockState());loaded.loadWithComponents(saved,h.getLevel().registryAccess());h.assertTrue(loaded.open&&loaded.axis==axis&&loaded.planeId()==plane.id,"receptacle lost active link on reload");slot.close();h.assertTrue(!slot.open&&plane.portals[slot.side]==null&&h.getLevel().isEmptyBlock(top.below(2)),"gateway did not close cleanly");}h.succeed();
    }
    @GameTest(template="test/platform") public static void invalidFrameCannotSpendVisOrReserveEndpoint(GameTestHelper h){
        var top=h.absolutePos(new BlockPos(3,6,3));var plane=plane(h,top);var slot=frame(h,top,Direction.Axis.X);slot.insert(Gateways.key(plane));h.getLevel().removeBlock(top.offset(-2,0,0),false);var player=player(h);int before=Wands.getVis(player.getMainHandItem(),Aspect.AIR);h.assertTrue(!slot.activate(player,player.getMainHandItem())&&Wands.getVis(player.getMainHandItem(),Aspect.AIR)==before&&java.util.Arrays.stream(plane.portals).allMatch(java.util.Objects::isNull),"incomplete frame lost vis or reserved a portal");h.succeed();
    }
    @GameTest(template="test/platform") public static void fifthGatewayIsRefusedWithoutCostOrFrameChanges(GameTestHelper h){
        var top=h.absolutePos(new BlockPos(3,6,3));var plane=plane(h,top);for(int i=0;i<4;i++)plane.portals[i]=GlobalPos.of(h.getLevel().dimension(),top.offset(i,0,1));var slot=frame(h,top,Direction.Axis.X);slot.insert(Gateways.key(plane));var player=player(h);int before=Wands.getVis(player.getMainHandItem(),Aspect.AIR);h.assertTrue(!slot.activate(player,player.getMainHandItem())&&!slot.open&&Wands.getVis(player.getMainHandItem(),Aspect.AIR)==before&&h.getLevel().getBlockState(top.offset(-2,0,0)).is(thaumcraft.registry.ModBlocks.ADVANCED_ALCHEMICAL_CONSTRUCT.get()),"fifth portal bypassed limit, spent vis or damaged frame");h.succeed();
    }
    @GameTest(template="test/platform") public static void breakingGatewayClosesPairAndBreakingSlotDropsOneBoundKey(GameTestHelper h){
        var top=h.absolutePos(new BlockPos(3,6,3));var plane=plane(h,top);var slot=frame(h,top,Direction.Axis.X);slot.insert(Gateways.key(plane));var player=player(h);slot.activate(player,player.getMainHandItem());int side=slot.side;h.getLevel().destroyBlock(top.offset(-2,0,0),true);var realm=h.getLevel().getServer().getLevel(PocketDimension.KEY);h.assertTrue(!slot.open&&plane.portals[side]==null&&realm.getBlockState(Gateways.inside(plane,side)).is(Materials.BLOCKS.get("void").get())&&!slot.contained.isEmpty(),"frame break failed to close both sides or lost key");h.getLevel().destroyBlock(top,true);var keys=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(top).inflate(2),item->item.getItem().is(Content.item("keystone")));h.assertTrue(keys.size()==1&&Gateways.planeId(keys.getFirst().getItem())==plane.id,"receptacle break duplicated or lost bound key");h.succeed();
    }
    @GameTest(template="test/platform") public static void gatewayPreservesOriginDimensionAndPortalCooldownOnReturn(GameTestHelper h){
        var top=h.absolutePos(new BlockPos(3,6,3));var plane=plane(h,top);var slot=frame(h,top,Direction.Axis.Z);slot.insert(Gateways.key(plane));var owner=player(h);slot.activate(owner,owner.getMainHandItem());var player=thaumcraft.test.TestSupport.mockServerPlayer(h);var pos=top.below(2);player.setPos(pos.getCenter());Gateways.teleport(player,pos,h.getLevel().getBlockState(pos));h.assertTrue(player.level().dimension()==PocketDimension.KEY&&player.blockPosition().equals(Gateways.inside(plane,slot.side).below())&&player.isOnPortalCooldown(),"gateway failed to enter correct plane");var inside=Gateways.inside(plane,slot.side).below();var realm=player.serverLevel();h.assertTrue(realm.noCollision(player,player.getBoundingBox().move(0,0,-1)),"arrival exit intersects terrain");player.setPortalCooldown(0);Gateways.teleport(player,inside,realm.getBlockState(inside));h.assertTrue(player.level().dimension()==h.getLevel().dimension()&&player.blockPosition().equals(top.below(2))&&player.getYRot()==270,"return lost dimension, position or perpendicular yaw");player.discard();slot.close();h.succeed();
    }
}
