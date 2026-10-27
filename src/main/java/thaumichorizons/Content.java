package thaumichorizons;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.capabilities.*;

public final class Content {
    public static final DeferredRegister.Items ITEMS=DeferredRegister.createItems(ThaumicHorizons.ID);
    public static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks(ThaumicHorizons.ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,ThaumicHorizons.ID);
    public static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,ThaumicHorizons.ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,ThaumicHorizons.ID);
    public static final DeferredRegister<MobEffect> EFFECTS=DeferredRegister.create(Registries.MOB_EFFECT,ThaumicHorizons.ID);
    public static final DeferredRegister<CreativeModeTab> TABS=DeferredRegister.create(Registries.CREATIVE_MODE_TAB,ThaumicHorizons.ID);
    public static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPE_SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,ThaumicHorizons.ID);
    public static final DeferredRegister<net.minecraft.sounds.SoundEvent> SOUNDS=DeferredRegister.create(Registries.SOUND_EVENT,ThaumicHorizons.ID);
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<VoidPuttyRecipe>> VOID_PUTTY_RECIPE=RECIPE_SERIALIZERS.register("void_putty_repair",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(VoidPuttyRecipe::new));
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<Illumination.DyeRecipe>> ILLUMINATION_DYE_RECIPE=RECIPE_SERIALIZERS.register("illumination_dye",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(Illumination.DyeRecipe::new));
    public static final Map<String,DeferredItem<? extends Item>> ALL=new LinkedHashMap<>();
    public static final DeferredBlock<InfuserBlock> INFUSER=BLOCKS.register("blood_infuser",()->new InfuserBlock(BlockBehaviour.Properties.of().strength(1,1).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<BloodInfuser>> BLOOD=TILES.register("blood_infuser",()->BlockEntityType.Builder.of(BloodInfuser::new,INFUSER.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<BloodMenu>> BLOOD_MENU=MENUS.register("blood_infuser",()->new MenuType<>(BloodMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredBlock<DynamoBlock> VIS_DYNAMO=BLOCKS.register("vis_dynamo",()->new DynamoBlock(false,BlockBehaviour.Properties.of().strength(.7F,1).lightLevel(s->7).noOcclusion()));
    public static final DeferredBlock<DynamoBlock> ESSENTIA_DYNAMO=BLOCKS.register("essentia_dynamo",()->new DynamoBlock(true,BlockBehaviour.Properties.of().strength(.7F,1).lightLevel(s->7).noOcclusion()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<Dynamo>> DYNAMO=TILES.register("dynamo",()->BlockEntityType.Builder.of(Dynamo::new,VIS_DYNAMO.get(),ESSENTIA_DYNAMO.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<DynamoMenu>> DYNAMO_MENU=MENUS.register("vis_dynamo",()->new MenuType<>(DynamoMenu::new,net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<EntityType<?>,EntityType<IncubatedEgg>> EGG=ENTITIES.register("incubated_egg",()->EntityType.Builder.<IncubatedEgg>of(IncubatedEgg::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(4).updateInterval(10).build("thaumichorizons:incubated_egg"));
    public static final DeferredHolder<EntityType<?>,EntityType<BlastPhial>> PHIAL=ENTITIES.register("blast_phial",()->EntityType.Builder.<BlastPhial>of(BlastPhial::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(4).updateInterval(10).build("thaumichorizons:blast_phial"));
    public static final DeferredHolder<EntityType<?>,EntityType<SyringeProjectile>> SYRINGE=ENTITIES.register("syringe",()->EntityType.Builder.<SyringeProjectile>of(SyringeProjectile::new,MobCategory.MISC).sized(.5F,.5F).clientTrackingRange(8).updateInterval(1).build("thaumichorizons:syringe"));
    public static final DeferredHolder<MenuType<?>,MenuType<InjectorMenu>> INJECTOR_MENU=MENUS.register("injector",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new InjectorMenu(id,inv,buf.readEnum(net.minecraft.world.InteractionHand.class))));
    public static final DeferredHolder<MenuType<?>,MenuType<LensCaseMenu>> LENS_CASE_MENU=MENUS.register("lens_case",()->net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create((id,inv,buf)->new LensCaseMenu(id,inv,buf.readEnum(net.minecraft.world.InteractionHand.class))));
    public static final DeferredHolder<MobEffect,MobEffect> VIS_REGEN=EFFECTS.register("vis_regen",()->new MobEffect(MobEffectCategory.BENEFICIAL,5701759){});
    public static final DeferredHolder<MobEffect,MobEffect> VACUUM=EFFECTS.register("vacuum",()->new MobEffect(MobEffectCategory.BENEFICIAL,4210752){});
    public static final DeferredHolder<MobEffect,MobEffect> SHOCK=EFFECTS.register("shock",()->new MobEffect(MobEffectCategory.BENEFICIAL,11513861){});
    public static final DeferredHolder<MobEffect,MobEffect> SYNTHESIS=EFFECTS.register("synthesis",()->new MobEffect(MobEffectCategory.BENEFICIAL,3253280){});
    // Registered in 1.7.10, but deliberately had no performEffect implementation or obtainable recipe.
    public static final DeferredHolder<MobEffect,MobEffect> VIS_BOOST=EFFECTS.register("vis_boost",()->new MobEffect(MobEffectCategory.BENEFICIAL,6697847){});
    public static void init(IEventBus bus){
        Infusions.init();Vats.init();Specimens.init();Species.init();Souls.init();ThaumicCrossbow.init();Materials.init();Illumination.init();NodeMonitor.init();Alchemite.init();ThermalFoci.init();CreativeTools.init();CrystalWands.init();AnimatedGolems.init();RareCreatures.init();MagicBoats.init();SyntheticNode.init();Recombinator.init();MirrorAmulet.init();Lenses.init();Spikes.init();FiniteLightning.init();Clouds.init();Vortex.init();RiftDevices.init();Gateways.init();NodeCheat.init();
        ALL.put("blood_infuser",ITEMS.registerSimpleBlockItem("blood_infuser",INFUSER));
        ALL.put("vis_dynamo",ITEMS.registerSimpleBlockItem("vis_dynamo",VIS_DYNAMO));
        ALL.put("essentia_dynamo",ITEMS.registerSimpleBlockItem("essentia_dynamo",ESSENTIA_DYNAMO));
        for(String n:List.of("empty_syringe","blood_syringe","blood_sample","injection","blood_phial","blast_phial","incubated_egg"))
            ALL.put(n,ITEMS.registerItem(n,p->new MedicalItem(n,p),new Item.Properties().stacksTo(Set.of("injection","blast_phial","blood_sample").contains(n)?1:64)));
        TABS.register("main",()->CreativeModeTab.builder().title(Component.translatable("itemGroup.thaumichorizons")).icon(()->new ItemStack(INFUSER.get())).displayItems((p,o)->CreativeOrder.items().forEach((name,item)->{if(name.equals("focus_illumination")){for(int color=0;color<16;color++)o.accept(Illumination.focus(color));}else if(name.equals("vortex")){o.accept(item.get());o.accept(Vortex.stableItem());}else if(!name.equals("thaumic_crossbow")&&!name.equals("greatwood_boat"))o.accept(item.get());})).build());
        BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);MENUS.register(bus);ENTITIES.register(bus);EFFECTS.register(bus);TABS.register(bus);RECIPE_SERIALIZERS.register(bus);SOUNDS.register(bus);
    }
    public static Item item(String key){return ALL.get(key).get();}
    public static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,BLOOD.get(),(be,side)->be.automation(side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,Vats.TYPE.get(),(be,side)->be.automation());event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,Souls.TYPE.get(),(be,side)->be.automation());}
}
