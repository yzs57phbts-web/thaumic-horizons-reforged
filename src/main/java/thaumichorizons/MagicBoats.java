package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.dispenser.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;

public final class MagicBoats {
    public static final DeferredHolder<EntityType<?>,EntityType<LegacyBoat>> GREATWOOD=Content.ENTITIES.register("greatwood_boat",()->EntityType.Builder.<LegacyBoat>of((type,level)->new LegacyBoat(type,level,false),MobCategory.MISC).sized(1.5F,.6F).clientTrackingRange(10).build("thaumichorizons:greatwood_boat"));
    public static final DeferredHolder<EntityType<?>,EntityType<LegacyBoat>> THAUMIUM=Content.ENTITIES.register("thaumium_boat",()->EntityType.Builder.<LegacyBoat>of((type,level)->new LegacyBoat(type,level,true),MobCategory.MISC).sized(1.5F,.6F).fireImmune().clientTrackingRange(10).build("thaumichorizons:thaumium_boat"));
    static void init(){for(boolean metal:new boolean[]{false,true}){String name=metal?"thaumium_boat":"greatwood_boat";Content.ALL.put(name,Content.ITEMS.registerItem(name,p->new BoatItem(metal,p),metal?new Item.Properties().stacksTo(1).fireResistant():new Item.Properties().stacksTo(1)));}}
    static void setup(){for(boolean metal:new boolean[]{false,true})DispenserBlock.registerBehavior(Content.item(metal?"thaumium_boat":"greatwood_boat"),new DefaultDispenseItemBehavior(){
        private final DefaultDispenseItemBehavior fallback=new DefaultDispenseItemBehavior();
        @Override protected ItemStack execute(BlockSource source,ItemStack stack){
            var face=source.state().getValue(DispenserBlock.FACING);var target=source.pos().relative(face);var level=source.level();var boat=(metal?THAUMIUM:GREATWOOD).get().create(level);double rise;
            if(boat.canBoatInFluid(level.getFluidState(target)))rise=1;
            else if(level.isEmptyBlock(target)&&boat.canBoatInFluid(level.getFluidState(target.below())))rise=0;
            else return fallback.dispense(source,stack);
            boat.setPos(source.pos().getX()+.5+face.getStepX()*1.125,source.pos().getY()+face.getStepY()+rise,source.pos().getZ()+.5+face.getStepZ()*1.125);boat.setYRot(face.toYRot());EntityType.<LegacyBoat>createDefaultStackConfig(level,stack,null).accept(boat);
            if(level.noCollision(boat,boat.getBoundingBox())&&level.addFreshEntity(boat)){stack.shrink(1);level.gameEvent(null,GameEvent.ENTITY_PLACE,boat.position());}return stack;
        }
    });}
    static void research(){
        // TC owns the base boat. Keep the legacy research and recipe-reference key for saved knowledge and addon links.
        TcRecipes.register(new CraftingRecipeRef("thaumichorizons:greatwood_boat","",net.minecraft.resources.ResourceLocation.parse("thaumcraft:greatwood_boat"),TcStack.of("thaumcraft:greatwood_boat",1)));
        MagicData.arcane("recipeThaumiumBoat","thaumiumBoat",TcStack.of("thaumichorizons:thaumium_boat",1),new AspectList().add(Aspect.FIRE,10).add(Aspect.WATER,10).add(Aspect.ORDER,10).add(Aspect.EARTH,10),new String[]{"STS","TBT","STS"},'S',TcIngredient.of(TcStack.of("thaumcraft:water_shard",1)),'T',TcIngredient.of(TcStack.of("thaumcraft:thaumium_ingot",1)),'B',TcIngredient.item("thaumcraft:greatwood_boat"));
        new ResearchItem("thaumiumBoat","ThaumicHorizons",new AspectList().add(Aspect.FIRE,2).add(Aspect.METAL,2).add(Aspect.MAGIC,2),7,8,1,TcStack.of("thaumichorizons:thaumium_boat",1)).setPages(ResearchPage.text("thaumiumBoat1"),ResearchPage.arcane("TH:recipeThaumiumBoat")).registerResearchItem();
    }
    public static final class BoatItem extends Item {
        final boolean metal;
        BoatItem(boolean metal,Properties properties){super(properties);this.metal=metal;}
        @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
            var stack=player.getItemInHand(hand);var hit=getPlayerPOVHitResult(level,player,ClipContext.Fluid.ANY);if(hit.getType()!=HitResult.Type.BLOCK)return InteractionResultHolder.pass(stack);
            for(var entity:level.getEntities(player,player.getBoundingBox().expandTowards(player.getViewVector(1).scale(5)).inflate(1),EntitySelector.NO_SPECTATORS.and(Entity::isPickable)))if(entity.getBoundingBox().inflate(entity.getPickRadius()).contains(player.getEyePosition()))return InteractionResultHolder.pass(stack);
            if(!level.mayInteract(player,hit.getBlockPos())||!player.mayUseItemAt(hit.getBlockPos(),hit.getDirection(),stack))return InteractionResultHolder.fail(stack);
            if(!metal)return thaumcraft.modern.ModernContent.BOATS.get("greatwood").get().use(level,player,hand);
            var boat=THAUMIUM.get().create(level);boat.setPos(hit.getLocation());boat.setYRot(player.getYRot());if(!level.noCollision(boat,boat.getBoundingBox()))return InteractionResultHolder.fail(stack);
            if(level instanceof ServerLevel server){EntityType.<LegacyBoat>createDefaultStackConfig(server,stack,player).accept(boat);if(!server.addFreshEntity(boat))return InteractionResultHolder.fail(stack);level.gameEvent(player,GameEvent.ENTITY_PLACE,hit.getLocation());stack.consume(1,player);}player.awardStat(Stats.ITEM_USED.get(this));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide());
        }
    }
    /** Kept under the same entity ID for existing saves; movement and seats are modern vanilla. */
    public static final class LegacyBoat extends Boat {
        public final boolean metal;
        LegacyBoat(EntityType<? extends Boat> type,Level level,boolean metal){super(type,level);this.metal=metal;}
        @Override public Item getDropItem(){return metal?Content.item("thaumium_boat"):thaumcraft.modern.ModernContent.BOATS.get("greatwood").get();}
        @Override public boolean canBoatInFluid(FluidState fluid){return fluid.is(FluidTags.WATER)||metal&&fluid.is(FluidTags.LAVA);}
        @Override public boolean canBoatInFluid(FluidType fluid){return fluid==net.neoforged.neoforge.common.NeoForgeMod.WATER_TYPE.value()||metal&&fluid==net.neoforged.neoforge.common.NeoForgeMod.LAVA_TYPE.value();}
        @Override public void tick(){
            // Lava is opaque, so vanilla's later water mask cannot hide it. Add buoyancy
            // before vanilla motion so the real floor, rather than a visual cover, stays dry.
            var fluid=level().getFluidState(blockPosition());
            if(metal&&isControlledByLocalInstance()&&!isNoGravity()&&fluid.is(FluidTags.LAVA)&&getY()<blockPosition().getY()+fluid.getHeight(level(),blockPosition()))setDeltaMovement(getDeltaMovement().add(0,getDefaultGravity()*.7,0));
            if(metal&&!level().isClientSide())for(var passenger:getPassengers()){passenger.clearFire();if(passenger instanceof LivingEntity living)living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,10,0,true,false));}
            super.tick();
        }
    }
}
