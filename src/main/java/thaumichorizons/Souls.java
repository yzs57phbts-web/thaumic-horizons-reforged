package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.*;

/** Original soul-sand → sieve → brain/knowledge/assembled soul progression. */
public final class Souls {
    public static final DeferredBlock<SoulBlock> SIEVE=block("soul_sieve",0),INSPIRATRON=block("inspiratron",1),FORGE=block("soulforge",2);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SoulMachine>> TYPE=Content.TILES.register("soul_machine",()->BlockEntityType.Builder.of(SoulMachine::new,SIEVE.get(),INSPIRATRON.get(),FORGE.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<SoulMenu>> SIEVE_MENU=Content.MENUS.register("soul_sieve",()->new MenuType<>((i,inv)->new SoulMenu(i,inv,0),net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>,MenuType<SoulMenu>> INSPIRATRON_MENU=Content.MENUS.register("inspiratron",()->new MenuType<>((i,inv)->new SoulMenu(i,inv,1),net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    private static DeferredBlock<SoulBlock> block(String name,int kind){return Content.BLOCKS.register(name,()->new SoulBlock(kind,BlockBehaviour.Properties.of().strength(.7F,1).noOcclusion()));}
    static void init(){Content.ALL.put("soul_sieve",Content.ITEMS.registerSimpleBlockItem("soul_sieve",SIEVE));Content.ALL.put("inspiratron",Content.ITEMS.registerSimpleBlockItem("inspiratron",INSPIRATRON));Content.ALL.put("soulforge",Content.ITEMS.registerSimpleBlockItem("soulforge",FORGE));}
    public static boolean isSoul(ItemStack stack){return stack.is(Content.item("specimen_jar"))&&stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getBoolean("isSoul");}
    public static ItemStack soulJar(VillagerProfession profession){
        var tag=new CompoundTag();tag.putBoolean("isSoul",true);tag.putString("profession",BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).toString());
        var stack=new ItemStack(Content.item("specimen_jar"));stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;
    }
    public static VillagerProfession profession(ItemStack jar){
        var id=ResourceLocation.tryParse(jar.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("profession"));
        return id!=null&&BuiltInRegistries.VILLAGER_PROFESSION.containsKey(id)?BuiltInRegistries.VILLAGER_PROFESSION.get(id):VillagerProfession.FARMER;
    }
    static VillagerProfession randomProfession(Level level){
        var list=new ArrayList<>(List.of(VillagerProfession.FARMER,VillagerProfession.LIBRARIAN,VillagerProfession.CLERIC,VillagerProfession.TOOLSMITH,VillagerProfession.BUTCHER));
        for(var profession:BuiltInRegistries.VILLAGER_PROFESSION)if(!BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession).getNamespace().equals("minecraft"))list.add(profession);
        return list.get(level.random.nextInt(list.size()));
    }
}
