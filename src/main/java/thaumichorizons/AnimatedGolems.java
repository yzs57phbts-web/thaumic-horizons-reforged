package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.entity.golem.*;
import thaumcraft.research.*;
import thaumcraft.wand.*;
import thaumcraft.wand.focus.*;

public final class AnimatedGolems {
    public static final DeferredHolder<EntityType<?>,EntityType<AnimatedGolem>> TYPE=Content.ENTITIES.register("animated_golem",()->EntityType.Builder.of(AnimatedGolem::new,MobCategory.MISC).sized(.4F,.95F).clientTrackingRange(8).updateInterval(3).build("thaumichorizons:animated_golem"));
    static void init(){
        Content.ALL.put("golem_powder",Content.ITEMS.registerItem("golem_powder",Powder::new,new Item.Properties()));
        Content.ALL.put("focus_animation",Content.ITEMS.registerItem("focus_animation",Focus::new,FocusItem.properties(new Item.Properties())));
        Content.ALL.put("golem_bell",Content.ITEMS.registerItem("golem_bell",thaumcraft.item.golem.GolemBellItem::new,new Item.Properties().stacksTo(1)));
        Content.ITEMS.registerItem("golem_placer",Placer::new,new Item.Properties().stacksTo(1));
    }
    static void attributes(EntityAttributeCreationEvent event){event.put(TYPE.get(),GolemBase.createAttributes().build());}
    static boolean valid(BlockState state,Level level,BlockPos pos){return !state.isAir()&&!state.hasBlockEntity()&&state.getDestroySpeed(level,pos)>=0&&(state.isSolidRender(level,pos)||state.getBlock() instanceof CakeBlock||state.is(Blocks.CACTUS)||state.is(Blocks.GLASS)||state.is(Blocks.PACKED_ICE)||state.is(Blocks.ICE)||state.is(Blocks.COBWEB));}
    static AnimatedGolem animate(Level level,BlockPos pos,Player player,int life,boolean berserk,boolean explosive,ItemStack wand){
        var state=level.getBlockState(pos);if(!valid(state,level,pos)||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,Direction.UP,player.getMainHandItem()))return null;
        var golem=TYPE.get().create(level);golem.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,0,0);golem.initialize(state,life,berserk,explosive,player.getUUID(),GolemSnapshot.EMPTY);
        var cost=new AspectList();for(var primal:Aspect.getPrimalAspects())cost.add(primal,golem.materialType().visCost);
        if(!wand.isEmpty()&&!((WandItem)wand.getItem()).consumeAllVis(wand,player,cost,false,false)){golem.discard();return null;}
        if(player instanceof net.minecraft.server.level.ServerPlayer server&&net.neoforged.neoforge.common.CommonHooks.fireBlockBreak((net.minecraft.server.level.ServerLevel)level,server.gameMode.getGameModeForPlayer(),server,pos,state).isCanceled()){golem.discard();return null;}
        level.removeBlock(pos,false);if(!level.addFreshEntity(golem)){level.setBlockAndUpdate(pos,state);golem.discard();return null;}
        if(!wand.isEmpty()&&!((WandItem)wand.getItem()).consumeAllVis(wand,player,cost,true,false)){golem.discard();level.setBlockAndUpdate(pos,state);return null;}
        level.playSound(null,pos,thaumcraft.registry.ModSounds.WAND.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);level.broadcastEntityEvent(golem,(byte)7);return golem;
    }
    static void research(){
        TcRecipes.register(new CrucibleRecipe("TH:recipeGolemPowder","golemPowder",TcStack.of("thaumichorizons:golem_powder",1),TcIngredient.of(TcStack.of("thaumcraft:salis_mundus",1)),new AspectList().add(Aspect.MAN,4).add(Aspect.SOUL,4).add(Aspect.MOTION,4)));
        new ResearchItem("golemPowder","ThaumicHorizons",new AspectList().add(Aspect.MAN,3).add(Aspect.SOUL,3).add(Aspect.MOTION,3).add(Aspect.CRAFT,3),12,1,3,TcStack.of("thaumichorizons:golem_powder",1)).setParents("GOLEMTHAUMIUM","ADVANCEDGOLEM","incarnationVat").setConcealed().setPages(ResearchPage.text("golemPowder1"),ResearchPage.crucible("TH:recipeGolemPowder"),ResearchPage.text("golemPowder2"),ResearchPage.text("golemPowder3")).registerResearchItem();
        var components=new java.util.ArrayList<TcIngredient>();for(String name:new String[]{"thaumcraft:golem_core_blank","thaumichorizons:blood_syringe","thaumcraft:golem_upgrade_air","thaumcraft:golem_upgrade_fire","thaumcraft:golem_upgrade_order","thaumcraft:golem_core_blank","thaumichorizons:blood_syringe","thaumcraft:golem_upgrade_earth","thaumcraft:golem_upgrade_water","thaumcraft:golem_upgrade_entropy"})components.add(TcIngredient.of(TcStack.of(name,1)));
        TcRecipes.register(new InfusionRecipe("TH:recipeFocusAnimation","focusAnimation",TcStack.of("thaumichorizons:focus_animation",1),10,new AspectList().add(Aspect.MAN,64).add(Aspect.MOTION,64).add(Aspect.SOUL,64).add(Aspect.CRAFT,64),TcIngredient.of(TcStack.of("thaumcraft:primordial_pearl",1)),components));
        new ResearchItem("focusAnimation","ThaumicHorizons",new AspectList().add(Aspect.MAN,5).add(Aspect.MOTION,5).add(Aspect.SOUL,5).add(Aspect.CRAFT,3),12,-1,3,TcStack.of("thaumichorizons:focus_animation",1)).setParents("PRIMPEARL","golemPowder").setConcealed().setPages(ResearchPage.text("focusAnimation1"),ResearchPage.infusion("TH:recipeFocusAnimation"),ResearchPage.text("focusAnimation2")).registerResearchItem();
        TcRecipes.register(new InfusionRecipe("TH:advancedAnimatedGolem","ADVANCEDGOLEM",TcStack.withData("thaumichorizons:golem_placer",1,java.util.Map.of("advanced","1")),3,new AspectList().add(Aspect.MIND,8).add(Aspect.SENSES,8).add(Aspect.LIFE,8),TcIngredient.of(TcStack.of("thaumichorizons:golem_placer",1)),java.util.List.of(TcIngredient.of(TcStack.of("minecraft:redstone",1)),TcIngredient.of(TcStack.of("minecraft:glowstone_dust",1)),TcIngredient.of(TcStack.of("minecraft:gunpowder",1)),TcIngredient.of(TcStack.of("thaumcraft:warded_jar",1)),TcIngredient.of(TcStack.of("thaumcraft:zombie_brain",1)))));
        if(TcRecipes.get("GolemBell") instanceof ShapedArcaneRecipe bell)TcRecipes.register(new ShapedArcaneRecipe(bell.key(),bell.research(),TcStack.of("thaumichorizons:golem_bell",1),bell.aspects(),bell.width(),bell.height(),bell.grid()));
        if(HorizonsConfig.ALTERNATE_BELL.get()){
            MagicData.arcane("alternateGolemBell","GOLEMBELL",TcStack.of("thaumichorizons:golem_bell"),new AspectList().add(Aspect.ORDER,5),new String[]{"QQ ","QQ ","  S"},'Q',TcIngredient.item("minecraft:quartz"),'S',TcIngredient.tag("c:rods/wooden"));
            new ResearchItem("alternateGolemBell","ThaumicHorizons",new AspectList().add(Aspect.ORDER,2).add(Aspect.ENERGY,2).add(Aspect.CRYSTAL,3),-2,0,1,TcStack.of("thaumichorizons:golem_bell")).setSiblings("GOLEMBELL").setConcealed().setPages(ResearchPage.text("alternateGolemBell1"),ResearchPage.arcane("TH:alternateGolemBell")).registerResearchItem();
        }
        var advanced=ResearchCategories.getResearch("ADVANCEDGOLEM");if(advanced!=null){var pages=new java.util.ArrayList<>(advanced.pages());pages.add(ResearchPage.infusion("TH:advancedAnimatedGolem"));advanced.setPages(pages.toArray(ResearchPage[]::new));}
    }
    public static final class Focus extends FocusItem {
        public static final FocusUpgradeType BERSERK=new FocusUpgradeType(106,"berserk",ThaumicHorizons.id("textures/foci/berserk.png"),new AspectList().add(Aspect.WEAPON,8));
        public static final FocusUpgradeType DETONATION=new FocusUpgradeType(107,"detonation",ThaumicHorizons.id("textures/foci/detonation.png"),new AspectList().add(Aspect.WEAPON,8));
        Focus(Properties properties){super(properties);}
        @Override public int getFocusColor(ItemStack stack){return 15054592;}
        @Override public net.minecraft.resources.ResourceLocation getFocusTexture(ItemStack stack){return ThaumicHorizons.id("items/focus_animation");}
        @Override public AspectList getVisCost(ItemStack stack){var cost=new AspectList();for(var aspect:Aspect.getPrimalAspects())cost.add(aspect,1000);return cost;}
        @Override public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack stack,int rank){return rank<1||rank>5?null:rank==3?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.EXTEND,BERSERK}:rank==5?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.EXTEND,DETONATION}:new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.EXTEND};}
        @Override public InteractionResult onFocusRightClick(ItemStack wand,Level level,Player player,HitResult hit){if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return InteractionResult.PASS;if(level.isClientSide())return InteractionResult.SUCCESS;var focus=Wands.getFocusItem(wand);return animate(level,blockHit.getBlockPos(),player,600+200*getUpgradeLevel(focus,FocusUpgradeType.EXTEND),getUpgradeLevel(focus,BERSERK)>0,getUpgradeLevel(focus,DETONATION)>0,wand)!=null?InteractionResult.SUCCESS:InteractionResult.FAIL;}
    }
    static final class Powder extends Item {
        Powder(Properties properties){super(properties);}
        @Override public InteractionResult useOn(UseOnContext context){if(context.getPlayer()==null)return InteractionResult.PASS;if(context.getLevel().isClientSide())return InteractionResult.SUCCESS;return animate(context.getLevel(),context.getClickedPos(),context.getPlayer(),1200,false,false,ItemStack.EMPTY)!=null?InteractionResult.SUCCESS:InteractionResult.FAIL;}
    }
    static final class Placer extends Item {
        Placer(Properties properties){super(properties);}
        @Override public InteractionResult onItemUseFirst(ItemStack stack,UseOnContext context){return useOn(context);}
        @Override public InteractionResult useOn(UseOnContext context){
            var player=context.getPlayer();if(player==null||player.isShiftKeyDown())return InteractionResult.PASS;var level=context.getLevel();var pos=context.getClickedPos().relative(context.getClickedFace());if(!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,context.getClickedFace(),context.getItemInHand()))return InteractionResult.FAIL;
            if(!level.isClientSide()){
                var stack=context.getItemInHand();var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();var block=NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK),tag.getCompound("thBlock"));var golem=TYPE.get().create(level);golem.moveTo(pos.getX()+.5,pos.getY(),pos.getZ()+.5,level.random.nextFloat()*360,0);golem.initialize(block,block.isAir()?-420:600,tag.getBoolean("thBerserk"),tag.getBoolean("thExplosive"),player.getUUID(),GolemKeys.snapshot(stack));golem.voidWarnings=tag.getInt("thVoidWarnings");golem.setCustomName(stack.get(DataComponents.CUSTOM_NAME));golem.setHome(pos,context.getClickedFace());
                GolemKeys.FLUID.getOrDefault(stack,java.util.Optional.empty()).ifPresent(fluid->golem.setFluidCarried(fluid.create()));golem.setEssentia(Aspect.getAspect(GolemKeys.ESSENTIA.getOrDefault(stack,"")),GolemKeys.ESSENTIA_AMOUNT.getOrDefault(stack,0));
                if(!level.noCollision(golem)||!level.addFreshEntity(golem)){golem.discard();return InteractionResult.FAIL;}stack.consume(1,player);
            }return InteractionResult.SUCCESS;
        }
        @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(tag.contains("thBlock")){var id=net.minecraft.resources.ResourceLocation.tryParse(tag.getCompound("thBlock").getString("Name"));if(id!=null){var block=net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(id);lines.add(block==Blocks.AIR?Component.translatable("entity.thaumichorizons.voidling"):new ItemStack(block).getHoverName());}}}
    }
}
