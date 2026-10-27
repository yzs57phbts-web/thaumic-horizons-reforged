package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.gametest.*;
import thaumcraft.aspect.Aspect;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class RegressionGameTests {
    @GameTest(template="test/platform") public static void forgeNeverOverdrawsFuelAndResumesAfterRefill(GameTestHelper h){
        var sieve=SoulGameTests.sieve(h);h.setBlock(new BlockPos(2,3,2),Souls.FORGE.get());SoulMachine forge=h.getBlockEntity(new BlockPos(2,3,2));
        sieve.inventory.setStackInSlot(0,new ItemStack(Items.SOUL_SAND));forge.essentia=1;
        forge.addSoulBits(11);h.assertTrue(forge.progress==1&&forge.essentia==0,"accelerated batch overdraws Cognitio");
        sieve.extract();h.assertTrue(sieve.ticksLeft==1200&&!sieve.extracting,"unfuelled forge consumed sand bits");
        forge.addEssentia(Aspect.MIND,1,Direction.UP);sieve.extract();h.assertTrue(sieve.extracting&&sieve.ticksLeft==1199&&forge.progress==2,"sieve failed to resume after refill");
        forge.souls=15;forge.progress=9599;forge.essentia=100;forge.addSoulBits(11);
        h.assertTrue(forge.souls==16&&forge.progress==0&&forge.essentia==99,"full soul storage lost bits or fuel");h.succeed();
    }
    @GameTest(template="test/platform") public static void everySpeciesHasWorkingPickEggAndNormalKnockback(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.CREATIVE);player.setPos(h.absolutePos(new BlockPos(1,3,1)).getCenter());
        for(var entry:Species.TYPES.entrySet()){
            var entity=(Mob)entry.getValue().get().create(h.getLevel());entity.setPos(player.getX()+2,player.getY(),player.getZ());
            var pick=entity.getPickResult();h.assertTrue(pick!=null&&!pick.isEmpty()&&pick.getItem() instanceof SpawnEggItem,"middle click has no egg: "+entry.getKey());
            h.assertTrue(((SpawnEggItem)pick.getItem()).getType(pick)==entity.getType(),"egg creates wrong species: "+entry.getKey());
            entity.hurt(entity.damageSources().playerAttack(player),1);
            h.assertTrue(!entity.isNoAi()&&entity.getDeltaMovement().horizontalDistanceSqr()>0,"species resists ordinary hit: "+entry.getKey());
            entity.setNoAi(true);entity.setCustomName(net.minecraft.network.chat.Component.literal(entry.getKey()));
            Species.restoreShowcaseAi(new net.neoforged.neoforge.event.entity.EntityJoinLevelEvent(entity,h.getLevel()));
            h.assertTrue(!entity.isNoAi(),"old display mob stays immobile: "+entry.getKey());
        }h.succeed();
    }
    @GameTest(template="test/platform") public static void containedPoseNeverInterpolatesFromWorldOrigin(GameTestHelper h){
        var entity=Species.SCHOLAR.get().create(h.getLevel());entity.yBodyRotO=-170;entity.yHeadRotO=90;entity.walkAnimation.update(1,1);
        ContainedPreview.prepare(entity,40,65,2,81234);
        h.assertTrue(entity.xo==40&&entity.xOld==40&&entity.yo==65&&entity.zo==2,"contained pose interpolates from saved position");
        h.assertTrue(entity.yBodyRotO==entity.yBodyRot&&entity.yHeadRotO==entity.yHeadRot&&entity.walkAnimation.speed(.2F)==0&&entity.walkAnimation.speed(.8F)==0&&entity.tickCount==81234,"contained idle animation resets each frame");
        h.succeed();
    }
    @GameTest(template="test/platform") public static void repeatedJarSyncPreservesPreviewIdentity(GameTestHelper h){
        h.setBlock(new BlockPos(2,2,2),Specimens.JAR.get());SpecimenJarEntity jar=h.getBlockEntity(new BlockPos(2,2,2));jar.stack=Specimens.write(EntityType.WOLF.create(h.getLevel()));var preview=jar.occupant();
        var saved=jar.saveWithFullMetadata(h.getLevel().registryAccess());jar.loadWithComponents(saved,h.getLevel().registryAccess());
        h.assertTrue(jar.occupant()==preview,"unchanged jar packet recreated preview");h.succeed();
    }
}
