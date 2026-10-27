package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class RareCreatureGameTests {
    @GameTest(template="test/platform") public static void allRareCreaturesHaveCorrectEggsAndPersistTheirType(GameTestHelper h){
        for(var entry:RareCreatures.SLIMES.entrySet()){var slime=entry.getValue().get().create(h.getLevel());slime.setSize(2,true);var egg=slime.getPickResult();h.assertTrue(egg!=null&&egg.getItem() instanceof SpawnEggItem&&((SpawnEggItem)egg.getItem()).getType(egg)==slime.getType(),"rare slime has no egg "+entry.getKey());var saved=new CompoundTag();slime.save(saved);var loaded=EntityType.loadEntityRecursive(saved,h.getLevel(),e->e);h.assertTrue(loaded instanceof RareCreatures.ElementalSlime restored&&restored.form.equals(entry.getKey())&&restored.getSize()==2,"slime type lost on reload");}
        var soul=RareCreatures.SOUL.get().create(h.getLevel());h.assertTrue(soul.getPickResult()!=null&&!soul.getPickResult().isEmpty()&&!soul.hurt(h.getLevel().damageSources().magic(),20),"soul egg or invulnerability missing");h.succeed();
    }
    @GameTest(template="test/platform") public static void rareSlimeSplitsOnlyIntoItsOwnType(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(3,3,3));for(var entry:RareCreatures.SLIMES.entrySet()){
            var slime=entry.getValue().get().create(h.getLevel());slime.moveTo(pos.getCenter());slime.setSize(4,true);slime.setHealth(0);slime.remove(Entity.RemovalReason.KILLED);
            var children=h.getLevel().getEntitiesOfClass(RareCreatures.ElementalSlime.class,new AABB(pos).inflate(2),e->e.getType()==entry.getValue().get());h.assertTrue(children.size()>=2&&children.size()<=4&&children.stream().allMatch(s->s.getSize()==2),"slime splits into wrong type or duplicates children "+entry.getKey());children.forEach(Entity::discard);
        }h.succeed();
    }
    @GameTest(template="test/platform") public static void medicalSlimeHealsOnceAndDoesNotHurtHealthyPlayer(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);player.setPos(h.absolutePos(new BlockPos(2,3,2)).getCenter());var slime=RareCreatures.SLIMES.get("med_slime").get().create(h.getLevel());slime.setSize(1,true);slime.setPos(player.position());slime.playerTouch(player);h.assertTrue(!slime.isRemoved(),"medical slime consumed on healthy player");player.setHealth(5);slime.playerTouch(player);h.assertTrue(slime.isRemoved()&&player.getHealth()==7,"medical slime did not heal by size plus one");slime.playerTouch(player);h.assertValueEqual((int)player.getHealth(),7,"consumed slime healed twice");h.succeed();
    }
    @GameTest(template="test/platform") public static void mercurialAndMeatSlimeDropOnlyTheirSpecialLoot(GameTestHelper h){
        var pos=h.absolutePos(new BlockPos(2,3,2));var mercury=RareCreatures.SLIMES.get("mercurial_slime").get().create(h.getLevel());mercury.setSize(1,true);mercury.moveTo(pos.getCenter());mercury.dropCustomDeathLoot(h.getLevel(),h.getLevel().damageSources().magic(),true);var meat=RareCreatures.SLIMES.get("meat_slime").get().create(h.getLevel());meat.setSize(1,true);meat.moveTo(pos.getCenter());meat.dropCustomDeathLoot(h.getLevel(),h.getLevel().damageSources().magic(),true);
        var drops=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2));h.assertTrue(drops.stream().filter(e->e.getItem().is(thaumcraft.registry.ModItems.QUICKSILVER.get())).mapToInt(e->e.getItem().getCount()).sum()==1&&drops.stream().noneMatch(e->e.getItem().is(Items.SLIME_BALL)),"rare slime loot lost or vanilla slime duplicated");h.assertValueEqual(drops.stream().mapToInt(e->e.getItem().getCount()).sum(),2,"unexpected extra rare slime loot");h.succeed();
    }
    @GameTest(template="test/platform") public static void lostSoulCaptureMakesUsableSoulJarWithoutDuplicatingJar(GameTestHelper h){
        var player=h.makeMockPlayer(GameType.SURVIVAL);player.getInventory().setItem(0,new ItemStack(thaumcraft.registry.ModBlocks.WARDED_JAR.get()));var soul=RareCreatures.SOUL.get().create(h.getLevel());h.assertTrue(Specimens.capture(player,soul)&&soul.isRemoved(),"soul was not captured");int soulJars=0,emptyJars=0;for(var stack:player.getInventory().items){if(Souls.isSoul(stack)){soulJars+=stack.getCount();h.assertTrue(Souls.profession(stack)==VillagerProfession.FARMER,"free soul has wrong default profession");}if(Specimens.isEmptyJar(stack))emptyJars+=stack.getCount();}h.assertTrue(soulJars==1&&emptyJars==0,"soul capture duplicated or lost jar");h.succeed();
    }
}
