package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.research.ResearchManager;
import thaumcraft.wand.*;

public final class Vats {
    public static final DeferredBlock<VatBlock> GLASS=Content.BLOCKS.register("vat",()->new VatBlock(0,properties()));
    public static final DeferredBlock<VatBlock> SOLID=Content.BLOCKS.register("vat_solid",()->new VatBlock(1,properties()));
    public static final DeferredBlock<VatBlock> INTERIOR=Content.BLOCKS.register("vat_interior",()->new VatBlock(2,properties().noCollission()));
    // Hidden technical block items retain the original scanner tags on the formed structure.
    public static final DeferredItem<BlockItem> GLASS_ITEM=Content.ITEMS.registerSimpleBlockItem("vat",GLASS),SOLID_ITEM=Content.ITEMS.registerSimpleBlockItem("vat_solid",SOLID),INTERIOR_ITEM=Content.ITEMS.registerSimpleBlockItem("vat_interior",INTERIOR);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VatEntity>> TYPE=Content.TILES.register("vat",()->BlockEntityType.Builder.of(VatEntity::new,GLASS.get(),SOLID.get(),INTERIOR.get()).build(null));
    public static final DeferredBlock<VatDeviceBlock> MATRIX=Content.BLOCKS.register("modified_matrix",()->new VatDeviceBlock(false,BlockBehaviour.Properties.of().strength(2,2).noOcclusion()));
    public static final DeferredBlock<VatDeviceBlock> BEACON=Content.BLOCKS.register("soul_beacon",()->new VatDeviceBlock(true,BlockBehaviour.Properties.of().strength(7,7).lightLevel(s->15).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<VatDeviceEntity>> DEVICE=Content.TILES.register("vat_device",()->BlockEntityType.Builder.of(VatDeviceEntity::new,MATRIX.get(),BEACON.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<VatMenu>> MENU=Content.MENUS.register("vat",()->new MenuType<>(VatMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    private static BlockBehaviour.Properties properties(){return BlockBehaviour.Properties.of().strength(3,15).lightLevel(s->8).noOcclusion().noLootTable();}
    public static void init(){
        Content.ALL.put("modified_matrix",Content.ITEMS.registerSimpleBlockItem("modified_matrix",MATRIX));
        Content.ALL.put("soul_beacon",Content.ITEMS.registerSimpleBlockItem("soul_beacon",BEACON));
        Content.ALL.put("nutrients",Content.ITEMS.registerSimpleItem("nutrients"));
        Content.ALL.put("corpse_effigy",Content.ITEMS.registerSimpleItem("corpse_effigy",new Item.Properties().stacksTo(1)));
        Content.ALL.put("suicide_pill",Content.ITEMS.registerItem("suicide_pill",p->new Item(p){
            @Override public InteractionResultHolder<ItemStack> use(Level l,Player player,InteractionHand hand){
                if(!l.isClientSide()){var type=l.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,ThaumicHorizons.id("suicide")));player.hurt(new net.minecraft.world.damagesource.DamageSource(type),1000);}return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand),l.isClientSide());
            }
        },new Item.Properties()));
    }
    public static void setup(){
        WandTriggers.register(s->s.is(Blocks.GLASS)||s.is(thaumcraft.registry.ModBlocks.GREATWOOD_PLANKS.get())||s.is(thaumcraft.registry.ModBlocks.ALCHEMICAL_CONSTRUCT.get()),Vats::tryForm);
        VatRecipes.init();VatResearch.init();
    }
    public static BlockState raw(int x,int y,int z){
        boolean center=x==1&&z==1;
        return y==0||y==3?(center?thaumcraft.registry.ModBlocks.ALCHEMICAL_CONSTRUCT.get():thaumcraft.registry.ModBlocks.GREATWOOD_PLANKS.get()).defaultBlockState():(center?Blocks.WATER:Blocks.GLASS).defaultBlockState();
    }
    public static boolean fits(Level level,BlockPos origin){
        for(BlockPos offset:BlockPos.betweenClosed(0,0,0,2,3,2)){
            BlockPos p=origin.offset(offset);if(!level.hasChunkAt(p))return false;
            BlockState expected=raw(offset.getX(),offset.getY(),offset.getZ());
            if(!level.getBlockState(p).is(expected.getBlock()))return false;
            if(expected.is(Blocks.WATER)&&!level.getFluidState(p).isSource())return false;
        }return true;
    }
    public static boolean tryForm(Level level,ItemStack stack,Player player,BlockPos clicked,Direction side){
        if(!ResearchManager.isComplete(player,"healingVat")||!(stack.getItem() instanceof WandItem wand))return false;
        for(int x=-2;x<=0;x++)for(int y=-3;y<=0;y++)for(int z=-2;z<=0;z++){
            BlockPos origin=clicked.offset(x,y,z);if(!fits(level,origin))continue;
            var cost=new AspectList().add(Aspect.WATER,50).add(Aspect.EARTH,50).add(Aspect.ORDER,50);
            if(!wand.consumeAllVisCrafting(stack,player,cost,false))return false;
            for(BlockPos p:BlockPos.betweenClosed(origin,origin.offset(2,3,2)))if(!level.mayInteract(player,p)||!player.mayUseItemAt(p,side,stack))return false;
            if(level.isClientSide())return true;
            if(!wand.consumeAllVisCrafting(stack,player,cost,true))return false;
            form(level,origin);return true;
        }return false;
    }
    public static VatEntity form(Level level,BlockPos origin){
        BlockPos master=origin.offset(1,3,1);
        for(BlockPos offset:BlockPos.betweenClosed(0,0,0,2,3,2)){
            int x=offset.getX(),y=offset.getY(),z=offset.getZ();boolean center=x==1&&z==1;
            int part=(y==0||y==3)?(center?(y==3?7:6):(y==0&&(x==1||z==1)?4:5)):(center?0:10);
            Block block=part==0?INTERIOR.get():part==10?GLASS.get():SOLID.get();BlockPos p=origin.offset(offset);
            level.setBlock(p,block.defaultBlockState().setValue(VatBlock.PART,part).setValue(VatBlock.CELL,y*9+z*3+x),3);
            if(level.getBlockEntity(p) instanceof VatEntity be){be.masterPos=master;be.sync();}
        }
        level.playSound(null,master,thaumcraft.registry.ModSounds.WAND.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);
        return (VatEntity)level.getBlockEntity(master);
    }
    public static boolean validSubject(LivingEntity e){
        if(!(e instanceof Mob)||e.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)||e instanceof net.minecraft.world.entity.npc.AbstractVillager||e instanceof net.minecraft.world.entity.animal.AbstractGolem||e instanceof thaumcraft.entity.golem.GolemBase)return false;
        String id=net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString();
        return !Set.of("minecraft:ghast","minecraft:enderman","minecraft:witch","minecraft:blaze","minecraft:magma_cube","thaumcraft:pech","thaumcraft:wisp").contains(id)&&!(e instanceof thaumcraft.entity.TaintedMob);
    }
    public static CompoundTag genetics(LivingEntity e){
        CompoundTag tag=new CompoundTag();e.saveWithoutId(tag);tag.putString("id",net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString());
        stripEquipment(tag);
        if(Infusions.DATA.has(e))tag.put("THGeneticInfusions",Infusions.DATA.get(e).copy());
        return tag;
    }
    private static void stripEquipment(CompoundTag tag){for(String key:List.of("UUID","Pos","Motion","Rotation","Passengers","Inventory","Items","ArmorItems","HandItems","SaddleItem","ArmorItem","DecorItem","body_armor_item","body_armor_drop_chance","ArmorDropChances","HandDropChances","ChestedHorse","Equipment","Brain","Leash","Owner","Offers","DeathLootTable","DeathLootTableSeed","ForgeData","NeoForgeData","neoforge:attachments"))tag.remove(key);}
    public static LivingEntity cloneFrom(ItemStack sample,Level level){
        if(sample.is(Content.item("blood_sample"))){
            var tag=sample.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getCompound("critter").copy();
            stripEquipment(tag);
            Entity e=EntityType.loadEntityRecursive(tag,level,entity->entity);
            if(!(e instanceof LivingEntity living)||e instanceof Player||!validSubject(living))return null;
            if(e instanceof TamableAnimal tame){tame.setTame(false,false);tame.setOwnerUUID(null);tame.setOrderedToSit(false);}
            if(e instanceof net.minecraft.world.entity.animal.horse.AbstractHorse horse)horse.setOwnerUUID(null);
            if(tag.contains("THGeneticInfusions")){Infusions.DATA.set(living,tag.getCompound("THGeneticInfusions").copy());Infusions.apply(living);}
            return living;
        }
        EntityType<?> type=sample.is(Items.BEEF)?EntityType.COW:sample.is(Items.PORKCHOP)?EntityType.PIG:sample.is(Items.CHICKEN)?EntityType.CHICKEN:null;
        return type==null?null:(LivingEntity)type.create(level);
    }
}
