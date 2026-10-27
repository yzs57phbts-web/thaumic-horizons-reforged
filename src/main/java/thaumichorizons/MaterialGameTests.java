package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.*;
@GameTestHolder(ThaumicHorizons.ID)
@PrefixGameTestTemplate(false)
public final class MaterialGameTests {
    @GameTest(template="test/platform") public static void organDropsAndSilkTouchUseLegacyQuantities(GameTestHelper h){
        var silk=new ItemStack(Items.DIAMOND_PICKAXE);silk.enchant(h.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.SILK_TOUCH),1);
        for(String name:java.util.List.of("brain","bone","eyes","flesh")){
            var pos=h.absolutePos(new BlockPos(2,2,2));var block=Materials.BLOCKS.get(name).get();h.getLevel().setBlockAndUpdate(pos,block.defaultBlockState());
            var drops=Block.getDrops(block.defaultBlockState(),h.getLevel(),pos,null,null,ItemStack.EMPTY);var drop=drops.getFirst();int minimum=name.equals("brain")?2:3,maximum=name.equals("brain")?3:5;
            h.assertTrue(drop.getCount()>=minimum&&drop.getCount()<=maximum,"wrong original organ yield "+name);
            h.assertTrue(drop.is(name.equals("brain")?thaumcraft.registry.ModItems.ZOMBIE_BRAIN.get():name.equals("eyes")?Items.SPIDER_EYE:Items.BONE),"wrong organ drop "+name);
            var intact=Block.getDrops(block.defaultBlockState(),h.getLevel(),pos,null,null,silk);h.assertTrue(intact.size()==1&&intact.getFirst().is(block.asItem())&&intact.getFirst().getCount()==1,"silk touch lost block "+name);
        }h.succeed();
    }
    @GameTest(template="test/platform") public static void dustHasSunkenCollisionAndSlowsHorizontalMotion(GameTestHelper h){
        var block=(Materials.Dust)Materials.BLOCKS.get("dust").get();var pos=h.absolutePos(new BlockPos(2,2,2));
        h.assertTrue(block.defaultBlockState().getCollisionShape(h.getLevel(),pos).bounds().maxY==.875,"dust collision must be seven eighths high");
        var mob=EntityType.COW.create(h.getLevel());mob.setDeltaMovement(1,.5,-1);block.entityInside(block.defaultBlockState(),h.getLevel(),pos,mob);h.assertTrue(mob.getDeltaMovement().distanceTo(new Vec3(.4,.5,-.4))<.000001,"dust changed vertical motion or did not slow horizontal motion");h.succeed();
    }
    @GameTest(template="test/platform") public static void allSixteenCrystalsDropTheirOwnColor(GameTestHelper h){
        for(var color:DyeColor.values()){
            var block=Materials.BLOCKS.get(color.getName()+"_crystal").get();var pos=h.absolutePos(new BlockPos(2,2,2));h.getLevel().setBlockAndUpdate(pos,block.defaultBlockState());var drops=Block.getDrops(block.defaultBlockState(),h.getLevel(),pos,null,null,ItemStack.EMPTY);
            h.assertTrue(drops.size()==1&&drops.getFirst().is(block.asItem()),"crystal changed color on break: "+color);
        }h.succeed();
    }
    @GameTest(template="test/platform") public static void puttyFullyRepairsWithoutLosingEnchantmentsOrName(GameTestHelper h){
        var sword=new ItemStack(Items.DIAMOND_SWORD);sword.setDamageValue(800);sword.set(DataComponents.CUSTOM_NAME,Component.literal("Test sword"));var unbreaking=h.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.UNBREAKING);sword.enchant(unbreaking,3);
        var putty=new ItemStack(Content.item("void_putty"));var recipe=new VoidPuttyRecipe(CraftingBookCategory.MISC);var input=CraftingInput.of(2,1,java.util.List.of(sword,putty));
        h.assertTrue(recipe.matches(input,h.getLevel()),"valid void putty repair refused");var repaired=recipe.assemble(input,h.getLevel().registryAccess());
        h.assertTrue(repaired.getDamageValue()==0&&repaired.getCount()==1&&repaired.getHoverName().getString().equals("Test sword")&&EnchantmentHelper.getItemEnchantmentLevel(unbreaking,repaired)==3&&sword.getDamageValue()==800,"repair lost components or changed input");
        h.assertTrue(!recipe.matches(CraftingInput.of(2,1,java.util.List.of(sword,new ItemStack(Items.SLIME_BALL))),h.getLevel()),"ordinary slime repaired item");h.succeed();
    }
}
