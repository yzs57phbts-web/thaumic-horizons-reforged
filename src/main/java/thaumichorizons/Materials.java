package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.registries.DeferredBlock;

/** Original organ blocks, terrain materials, colored crystals and their exact basic behaviours. */
public final class Materials {
    public static final Map<String,DeferredBlock<? extends Block>> BLOCKS=new LinkedHashMap<>();
    static void init(){
        register("bone",()->new OrganBlock(()->Items.BONE,3,3,BlockBehaviour.Properties.of().strength(2.5F,2.5F)));
        register("brain",()->new OrganBlock(thaumcraft.registry.ModItems.ZOMBIE_BRAIN,2,2,BlockBehaviour.Properties.of().strength(.5F,.5F)));
        register("eyes",()->new OrganBlock(()->Items.SPIDER_EYE,3,3,BlockBehaviour.Properties.of().strength(.5F,.5F)));
        register("flesh",()->new OrganBlock(()->Items.BONE,3,3,BlockBehaviour.Properties.of().strength(.5F,.5F)));
        register("dust",()->new Dust(BlockBehaviour.Properties.of().strength(.5F).sound(SoundType.SAND)));
        register("leviathan",()->new Block(BlockBehaviour.Properties.of().strength(10,10)){
            @Override public void animateTick(BlockState state,Level level,BlockPos pos,net.minecraft.util.RandomSource random){for(Direction face:Direction.values())if(random.nextInt(10)==0&&!level.getBlockState(pos.relative(face)).isSolidRender(level,pos.relative(face)))level.addParticle(ParticleTypes.SMOKE,pos.getX()+.5+face.getStepX()*.5625,pos.getY()+.5+face.getStepY()*.5625,pos.getZ()+.5+face.getStepZ()*.5625,0,0,0);}
        });
        register("void",()->new Block(BlockBehaviour.Properties.of().strength(-1,60000).lightLevel(s->15).noLootTable().sound(SoundType.EMPTY).isSuffocating((s,l,p)->false).isViewBlocking((s,l,p)->false).isRedstoneConductor((s,l,p)->false)));
        for(var color:DyeColor.values())register(color.getName()+"_crystal",()->new TransparentBlock(BlockBehaviour.Properties.of().strength(1.75F,.5F).sound(SoundType.GLASS).noOcclusion()));
        register("deep_crystal",()->new TransparentBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.GLASS).lightLevel(s->15).noOcclusion()));
        food("meat",3,.3F);food("cooked_meat",7,.7F);food("meat_nugget",1,.1F);
        Content.ALL.put("void_putty",Content.ITEMS.registerSimpleItem("void_putty",new Item.Properties().stacksTo(16)));
    }
    static void food(String name,int nutrition,float saturation){Content.ALL.put(name,Content.ITEMS.registerSimpleItem(name,new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build())));}
    static void setup(){thaumcraft.device.SmeltingBonuses.register(()->Content.item("meat"),()->Content.item("meat_nugget"));}
    static <T extends Block> void register(String name,java.util.function.Supplier<T> factory){var block=Content.BLOCKS.register(name,factory);BLOCKS.put(name,block);Content.ALL.put(name,Content.ITEMS.registerSimpleBlockItem(name,block));}
    static final class OrganBlock extends Block {
        final java.util.function.Supplier<? extends Item> drop;final int min,range;
        OrganBlock(java.util.function.Supplier<? extends Item> drop,int min,int range,Properties properties){super(properties);this.drop=drop;this.min=min;this.range=range;}
        @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){
            var tool=params.getOptionalParameter(LootContextParams.TOOL);int fortune=0;
            if(tool!=null){var enchants=params.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT);if(EnchantmentHelper.getItemEnchantmentLevel(enchants.getHolderOrThrow(Enchantments.SILK_TOUCH),tool)>0)return List.of(new ItemStack(this));fortune=EnchantmentHelper.getItemEnchantmentLevel(enchants.getHolderOrThrow(Enchantments.FORTUNE),tool);}
            return List.of(new ItemStack(drop.get(),min+params.getLevel().random.nextInt(range)+params.getLevel().random.nextInt(fortune+1)));
        }
    }
    static final class Dust extends Block {
        Dust(Properties properties){super(properties);}
        @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Block.box(0,0,0,16,14,16);}
        @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){entity.setDeltaMovement(entity.getDeltaMovement().multiply(.4,1,.4));}
    }
}
