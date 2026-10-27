package thaumichorizons;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.*;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.research.*;
import thaumcraft.wand.*;
import thaumcraft.wand.focus.*;

public final class Illumination {
    public static final int[] COLORS={1973019,11743532,3887386,5320730,2437522,8073150,2651799,11250603,4408131,14188952,4312372,14602026,6719955,12801229,15435844,15790320};
    public static final IntegerProperty COLOR=IntegerProperty.create("color",0,15);
    public static final DeferredBlock<LightBlock> LIGHT=Content.BLOCKS.register("light",()->new LightBlock(false,BlockBehaviour.Properties.of().strength(0).noOcclusion().noCollission().noLootTable().lightLevel(s->14)));
    public static final DeferredBlock<LightBlock> SOLAR_LIGHT=Content.BLOCKS.register("solar_light",()->new LightBlock(true,BlockBehaviour.Properties.of().strength(0).noOcclusion().noCollission().noLootTable().lightLevel(s->14)));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LightEntity>> TYPE=Content.TILES.register("illumination",()->BlockEntityType.Builder.of(LightEntity::new,LIGHT.get(),SOLAR_LIGHT.get()).build(null));
    static void init(){
        Content.ALL.put("focus_illumination",Content.ITEMS.registerItem("focus_illumination",Focus::new,FocusItem.properties(new Item.Properties())));
        Content.ITEMS.registerSimpleBlockItem("light",LIGHT);Content.ITEMS.registerSimpleBlockItem("solar_light",SOLAR_LIGHT);
    }
    static int color(ItemStack stack){return Math.clamp(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("thLightColor"),0,15);}
    static void color(ItemStack stack,int color){var tag=stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();tag.putInt("thLightColor",Math.clamp(color,0,15));stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}
    static ItemStack focus(int color){var stack=new ItemStack(Content.item("focus_illumination"));color(stack,color);return stack;}
    static void research(){
        TcStack.registerDataHandler("th_light_color",(stack,value)->color(stack,Integer.parseInt(value)));TcStack.registerDataMatcher("th_light_color",(stack,value)->color(stack)==Integer.parseInt(value));
        var yellow=TcStack.withData("thaumichorizons:focus_illumination",1,java.util.Map.of("th_light_color","11"));
        TcRecipes.register(new CrucibleRecipe("TH:recipeFocusIllumination","focusIllumination",yellow,TcIngredient.of(TcStack.of("minecraft:glowstone",1)),new AspectList().add(Aspect.LIGHT,12).add(Aspect.ENERGY,8).add(Aspect.FIRE,8)));
        new ResearchItem("focusIllumination","ThaumicHorizons",new AspectList().add(Aspect.LIGHT,2).add(Aspect.ENERGY,2).add(Aspect.MAGIC,1),-1,6,1,yellow).setParents("FOCUSFIRE").setConcealed().setPages(ResearchPage.text("focusIllumination1"),ResearchPage.crucible("TH:recipeFocusIllumination")).registerResearchItem();
    }
    public static final class Focus extends FocusItem {
        public static final FocusUpgradeType SOLAR=new FocusUpgradeType(102,"solar",ThaumicHorizons.id("textures/foci/solar.png"),new AspectList().add(Aspect.ORDER,8).add(Aspect.VOID,4));
        Focus(Properties properties){super(properties);}
        @Override public Component getName(ItemStack stack){return Component.translatable("item.focusIllumination."+color(stack)+".name");}
        @Override public int getFocusColor(ItemStack focus){return COLORS[color(focus)];}
        @Override public net.minecraft.resources.ResourceLocation getFocusTexture(ItemStack stack){return ThaumicHorizons.id("items/focus_illumination."+color(stack));}
        @Override public AspectList getVisCost(ItemStack stack){return new AspectList().add(Aspect.FIRE,100).add(Aspect.AIR,100);}
        @Override public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack stack,int rank){return rank==3?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL,SOLAR}:rank>=1&&rank<=5?new FocusUpgradeType[]{FocusUpgradeType.FRUGAL}:null;}
        @Override public InteractionResult onFocusRightClick(ItemStack wand,Level level,Player player,HitResult hit){
            if(!(hit instanceof BlockHitResult blockHit)||hit.getType()!=HitResult.Type.BLOCK)return InteractionResult.PASS;
            var pos=blockHit.getBlockPos().relative(blockHit.getDirection());if(!level.isEmptyBlock(pos)||!level.mayInteract(player,pos)||!player.mayUseItemAt(pos,blockHit.getDirection(),wand))return InteractionResult.FAIL;
            var focus=Wands.getFocusItem(wand);if(!((WandItem)wand.getItem()).consumeAllVis(wand,player,getVisCost(focus),true,false))return InteractionResult.FAIL;
            if(!level.isClientSide()){level.setBlockAndUpdate(pos,(getUpgradeLevel(focus,SOLAR)>0?SOLAR_LIGHT:LIGHT).get().defaultBlockState().setValue(COLOR,color(focus)));level.playSound(null,pos,thaumcraft.registry.ModSounds.WAND.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,1);}
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }
    public static final class LightBlock extends BaseEntityBlock {
        final boolean solar;
        LightBlock(boolean solar,Properties properties){super(properties);this.solar=solar;registerDefaultState(stateDefinition.any().setValue(COLOR,0));}
        @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new LightBlock(solar,p));}
        @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(COLOR);}
        @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new LightEntity(pos,state);}
        @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.INVISIBLE;}
        @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(4.8,4.8,4.8,11.2,11.2,11.2);}
        @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(solar&&entity.getType().is(EntityTypeTags.UNDEAD))entity.igniteForSeconds(5);}
        @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TYPE.get(),LightEntity::tick);}
    }
    public static final class LightEntity extends thaumcraft.blockentity.TcBlockEntity {
        LightEntity(BlockPos pos,BlockState state){super(TYPE.get(),pos,state);}
        static void tick(Level level,BlockPos pos,BlockState state,LightEntity entity){if(level.isClientSide()&&level.random.nextInt(8)==0)thaumcraft.fx.Fx.get().sparkle(level,pos.getX()+.55F,pos.getY()+.5F,pos.getZ()+.55F,.5F,COLORS[state.getValue(COLOR)],0);}
    }
    public static final class DyeRecipe extends CustomRecipe {
        public DyeRecipe(CraftingBookCategory category){super(category);}
        @Override public boolean matches(CraftingInput input,Level level){return !assemble(input,level.registryAccess()).isEmpty();}
        @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider registry){ItemStack focus=ItemStack.EMPTY;int dye=-1;for(var stack:input.items()){if(stack.isEmpty())continue;if(stack.getItem() instanceof Focus){if(!focus.isEmpty())return ItemStack.EMPTY;focus=stack;}else if(stack.getItem() instanceof DyeItem color)dye=15-color.getDyeColor().getId();else return ItemStack.EMPTY;}if(focus.isEmpty()||dye<0)return ItemStack.EMPTY;var output=focus.copyWithCount(1);color(output,dye);return output;}
        @Override public boolean canCraftInDimensions(int width,int height){return width*height>=2;}
        @Override public RecipeSerializer<?> getSerializer(){return Content.ILLUMINATION_DYE_RECIPE.get();}
    }
}
