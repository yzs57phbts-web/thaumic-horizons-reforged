package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;
import thaumcraft.wand.*;
import thaumcraft.wand.focus.*;

/** Original continuous smelting and crystallisation beams, including rank-specific upgrades. */
public final class ThermalFoci {
    @FunctionalInterface interface Beam {void draw(Level level,Player source,Vec3 end,int color,int impact);}
    static Beam clientBeam=(l,p,v,c,i)->{};
    static void init(){for(boolean dissolve:new boolean[]{false,true}){String name=dissolve?"focus_disintegration":"focus_liquefaction";Content.ALL.put(name,Content.ITEMS.registerItem(name,p->new Focus(dissolve,p),FocusItem.properties(new Item.Properties())));}}
    static void research(){
        TcRecipes.register(new InfusionRecipe("TH:recipeFocusLiquefaction","focusLiquefaction",TcStack.of("thaumichorizons:focus_liquefaction",1),3,new AspectList().add(Aspect.FIRE,12).add(Aspect.METAL,8).add(Aspect.EXCHANGE,4),TcIngredient.of(TcStack.of("minecraft:diamond",1)),List.of(TcIngredient.of(TcStack.of("thaumcraft:focus_fire",1)),TcIngredient.of(TcStack.of("thaumcraft:salis_mundus",1)),TcIngredient.of(TcStack.of("thaumcraft:focus_excavation",1)))));
        new ResearchItem("focusLiquefaction","ThaumicHorizons",new AspectList().add(Aspect.FIRE,3).add(Aspect.METAL,3).add(Aspect.EXCHANGE,2),1,8,2,TcStack.of("thaumichorizons:focus_liquefaction",1)).setParents("focusIllumination","FOCUSEXCAVATION").setConcealed().setPages(ResearchPage.text("focusLiquefaction1"),ResearchPage.infusion("TH:recipeFocusLiquefaction")).registerResearchItem();
        TcRecipes.register(new InfusionRecipe("TH:recipeFocusDisintegration","focusDisintegration",TcStack.of("thaumichorizons:focus_disintegration",1),7,new AspectList().add(Aspect.ENTROPY,32).add(Aspect.MAGIC,24).add(Aspect.CRYSTAL,32).add(Aspect.EXCHANGE,16),TcIngredient.of(TcStack.of("thaumcraft:focus_excavation",1)),List.of(TcIngredient.of(TcStack.of("thaumcraft:liquid_death_bucket",1)),TcIngredient.of(TcStack.of("thaumcraft:primal_charm",1)),TcIngredient.of(TcStack.of("thaumcraft:liquid_death_bucket",1)),TcIngredient.of(TcStack.of("thaumcraft:alumentum",1)))));
        new ResearchItem("focusDisintegration","ThaumicHorizons",new AspectList().add(Aspect.MAGIC,3).add(Aspect.CRYSTAL,2).add(Aspect.ENTROPY,3),3,8,2,TcStack.of("thaumichorizons:focus_disintegration",1)).setParents("focusLiquefaction","alchemite","LIQUIDDEATH").setConcealed().setPages(ResearchPage.text("focusDisintegration1"),ResearchPage.infusion("TH:recipeFocusDisintegration")).registerResearchItem();
    }
    static Entity pointed(Player player,double range){
        Vec3 start=player.getEyePosition(),end=start.add(player.getLookAngle().scale(range));double nearest=player.level().clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player)).getLocation().distanceToSqr(start);Entity result=null;
        for(var entity:player.level().getEntities(player,player.getBoundingBox().expandTowards(player.getLookAngle().scale(range)).inflate(1),e->e.isAlive()&&(e instanceof Mob||e instanceof ItemEntity))){var hit=entity.getBoundingBox().inflate(.1).clip(start,end);if(hit.isPresent()&&hit.get().distanceToSqr(start)<nearest){nearest=hit.get().distanceToSqr(start);result=entity;}}return result;
    }
    static ItemStack smelt(Level level,ItemStack input){if(input.isEmpty())return ItemStack.EMPTY;var recipe=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,new SingleRecipeInput(input),level);return recipe.isEmpty()?ItemStack.EMPTY:recipe.get().value().assemble(new SingleRecipeInput(input),level.registryAccess());}
    static int meltable(BlockState state){
        if(state.is(Blocks.STONE))return 6;if(state.getBlock() instanceof TntBlock)return 4;if(state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.MYCELIUM))return 3;
        if(state.is(Blocks.ICE)||state.is(Blocks.PACKED_ICE)||state.is(Blocks.BLUE_ICE))return 2;
        if(state.is(BlockTags.LEAVES)||state.getBlock() instanceof BushBlock||state.is(Blocks.SNOW)||state.is(Blocks.SNOW_BLOCK)||state.is(Blocks.VINE)||state.is(Blocks.COBWEB))return 1;return 0;
    }
    public static final class Focus extends FocusItem {
        public static final FocusUpgradeType NUGGETS=new FocusUpgradeType(103,"nuggets",ThaumicHorizons.id("textures/foci/nuggets.png"),new AspectList().add(Aspect.METAL,8));
        public static final FocusUpgradeType PURITY=new FocusUpgradeType(104,"purity",ThaumicHorizons.id("textures/foci/purity.png"),new AspectList().add(Aspect.ORDER,8));
        public static final FocusUpgradeType ENERVATION=new FocusUpgradeType(105,"enervation",ThaumicHorizons.id("textures/foci/enervation.png"),new AspectList().add(Aspect.DEATH,8));
        final boolean dissolve;
        private final Map<Player,Progress> progress=new WeakHashMap<>();
        private static final class Progress {BlockPos target;float amount;}
        Focus(boolean dissolve,Properties properties){super(properties);this.dissolve=dissolve;}
        @Override public int getFocusColor(ItemStack stack){return dissolve?7930047:16711680;}
        @Override public net.minecraft.resources.ResourceLocation getFocusTexture(ItemStack stack){return ThaumicHorizons.id("items/"+(dissolve?"focus_disintegration":"focus_liquefaction"));}
        @Override public AspectList getVisCost(ItemStack stack){return dissolve?new AspectList().add(Aspect.FIRE,30).add(Aspect.ENTROPY,30).add(Aspect.EARTH,30):new AspectList().add(Aspect.FIRE,40);}
        AspectList creatureCost(){return dissolve?new AspectList().add(Aspect.FIRE,12).add(Aspect.ENTROPY,12).add(Aspect.EARTH,12):new AspectList().add(Aspect.FIRE,15);}
        @Override public WandFocusAnimation getAnimation(ItemStack stack){return WandFocusAnimation.CHARGE;}
        @Override public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack stack,int rank){if(rank<1||rank>5)return null;if(rank==3)return new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY,FocusUpgradeType.ENLARGE};if(rank==5)return new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY,dissolve?ENERVATION:PURITY};return dissolve?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY}:new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,FocusUpgradeType.POTENCY,NUGGETS};}
        @Override public InteractionResult onFocusRightClick(ItemStack stack,Level level,Player player,HitResult hit){return InteractionResult.CONSUME;}
        @Override public void onPlayerStoppedUsingFocus(ItemStack stack,Level level,Player player,int remaining){progress.remove(player);}
        private boolean pay(ItemStack stack,Player player,AspectList cost,boolean consume){return ((WandItem)stack.getItem()).consumeAllVis(stack,player,cost,consume,false);}
        @Override public void onUsingFocusTick(ItemStack wand,Player player,int remaining){
            Level level=player.level();var focus=Wands.getFocusItem(wand);if(!pay(wand,player,getVisCost(focus),false)){player.stopUsingItem();progress.remove(player);return;}
            var blockHit=FocusUtils.getTargetBlock(level,player,true,10);var entity=pointed(player,10);var state=progress.computeIfAbsent(player,p->new Progress());Vec3 target=entity!=null?entity.position():blockHit!=null?blockHit.getLocation():player.getEyePosition().add(player.getLookAngle().scale(10));
            if(level.isClientSide()){clientBeam.draw(level,player,target,getFocusColor(focus),entity!=null||blockHit!=null?2+getUpgradeLevel(focus,FocusUpgradeType.ENLARGE)*8:0);return;}
            int potency=getUpgradeLevel(focus,FocusUpgradeType.POTENCY);
            if(entity instanceof Mob mob){state.target=null;state.amount=0;if(pay(wand,player,creatureCost(),true)){
                if(dissolve){int enervation=getUpgradeLevel(focus,ENERVATION);if(enervation>0)mob.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,40));mob.hurt(new net.minecraft.world.damagesource.DamageSource(level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(thaumcraft.damage.ModDamageTypes.DISSOLVE),player),.5F+.25F*potency+.5F*enervation);}
                else mob.hurt(level.damageSources().inFire(),1+.5F*potency);
            }return;}
            if(entity instanceof ItemEntity item&&processItem(player,wand,item)){state.target=null;state.amount=0;return;}
            if(blockHit==null){state.target=null;state.amount=0;return;}
            var pos=blockHit.getBlockPos();var block=level.getBlockState(pos);float hardness;
            if(dissolve){if(Alchemite.aspects(new ItemStack(block.getBlock())).isEmpty())return;hardness=block.getDestroySpeed(level,pos);if(hardness<0)return;}
            else{int melting=meltable(block);boolean flammable=block.isFlammable(level,pos,Direction.UP);if(melting==0&&!flammable&&smelt(level,new ItemStack(block.getBlock())).isEmpty())return;hardness=melting==6?20:melting>0||flammable?.25F:2;}
            if(!pos.equals(state.target)){state.target=pos.immutable();state.amount=0;return;}
            if(state.amount<hardness){state.amount+=dissolve?.05F+potency*.015F:.15F+potency*.05F;return;}
            processBlock(player,wand,pos);if(getUpgradeLevel(focus,FocusUpgradeType.ENLARGE)>0)for(var neighbour:BlockPos.betweenClosed(pos.offset(-1,-1,-1),pos.offset(1,1,1)))if(!neighbour.equals(pos))processBlock(player,wand,neighbour);
            state.target=null;state.amount=0;
        }
        boolean processItem(Player player,ItemStack wand,ItemEntity entity){
            var input=entity.getItem();if(dissolve){if(input.is(thaumcraft.registry.ModItems.CRYSTALLIZED_ESSENCE.get()))return false;var aspects=Alchemite.aspects(input);if(aspects.isEmpty()||!pay(wand,player,getVisCost(Wands.getFocusItem(wand)),true))return false;Alchemite.crystals(player.level(),entity.getX(),entity.getY(),entity.getZ(),aspects,input.getCount());entity.discard();return true;}
            var cooked=smelt(player.level(),input);if(cooked.isEmpty()||!pay(wand,player,getVisCost(Wands.getFocusItem(wand)),true))return false;int count=cooked.getCount()*input.getCount();cooked.setCount(Math.min(cooked.getMaxStackSize(),count));entity.setItem(cooked);for(count-=cooked.getCount();count>0;count-=cooked.getMaxStackSize())entity.spawnAtLocation(cooked.copyWithCount(Math.min(cooked.getMaxStackSize(),count)));return true;
        }
        boolean processBlock(Player player,ItemStack wand,BlockPos pos){
            Level level=player.level();var block=level.getBlockState(pos);if(block.isAir()||block.getDestroySpeed(level,pos)<0||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,Direction.UP,wand))return false;
            if(player instanceof ServerPlayer server&&net.neoforged.neoforge.common.CommonHooks.fireBlockBreak((ServerLevel)level,server.gameMode.getGameModeForPlayer(),server,pos,block).isCanceled())return false;
            var focus=Wands.getFocusItem(wand);var input=new ItemStack(block.getBlock());
            if(dissolve){var aspects=Alchemite.aspects(input);if(aspects.isEmpty()||!pay(wand,player,getVisCost(focus),true))return false;Alchemite.crystals(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,aspects,1);level.removeBlock(pos,false);return true;}
            int melt=meltable(block);var output=smelt(level,input);boolean flammable=block.isFlammable(level,pos,Direction.UP);if(melt==0&&output.isEmpty()&&!flammable||!pay(wand,player,getVisCost(focus),true))return false;
            if(melt==1)level.removeBlock(pos,false);
            else if(melt==2)level.setBlockAndUpdate(pos,level.dimensionType().ultraWarm()?Blocks.AIR.defaultBlockState():Blocks.WATER.defaultBlockState());
            else if(melt==3)level.setBlockAndUpdate(pos,Blocks.DIRT.defaultBlockState());
            else if(melt==4){block.onCaughtFire(level,pos,Direction.UP,player);level.removeBlock(pos,false);}
            else if(melt==6)level.setBlockAndUpdate(pos,Blocks.LAVA.defaultBlockState());
            else if(!output.isEmpty()){
                if(getUpgradeLevel(focus,PURITY)>0){var cluster=thaumcraft.item.tool.SpecialMining.find(block,input,9999,level.random);var pure=smelt(level,cluster);if(!pure.isEmpty())output=pure;}
                int nuggets=getUpgradeLevel(focus,NUGGETS);var bonus=thaumcraft.device.SmeltingBonuses.get(input);if(bonus!=null&&nuggets>0){int count=1;for(int i=0;i<nuggets;i++)if(level.random.nextFloat()<.45F)count++;Block.popResource(level,pos,new ItemStack(bonus,count));}
                if(output.getItem() instanceof BlockItem item)level.setBlockAndUpdate(pos,item.getBlock().defaultBlockState());else{level.removeBlock(pos,false);Block.popResource(level,pos,output);}
            }else level.setBlockAndUpdate(pos,Blocks.FIRE.defaultBlockState());return true;
        }
    }
}
