package thaumichorizons;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.entity.golem.GolemStats;

public enum GolemMaterial {
    GRASS(10,0,.38F,false,2,1,75,0,750),DIRT(10,0,.38F,false,1,1,75,0,500),
    WOOD(20,6,.35F,false,2,4,75,1,750),PLANT(10,0,.4F,false,2,1,75,0,750),
    ROCK(30,12,.32F,true,2,16,100,3,1000),METAL(35,15,.31F,true,2,32,125,4,1250),
    CLOTH(10,0,.4F,false,2,1,75,0,750),SAND(10,0,.4F,false,1,1,75,0,500),
    REDSTONE(20,9,.35F,true,2,8,45,2,1000),TNT(10,0,.38F,false,1,1,75,0,750),
    ICE(20,6,.35F,false,2,4,75,1,750),CACTUS(20,6,.35F,false,2,4,75,1,1000),
    CLAY(25,9,.33F,true,1,8,100,2,750),CAKE(20,6,.35F,false,2,4,25,1,750),
    WEB(15,6,.35F,false,2,4,40,1,1000),VOID(25,15,.4F,true,2,8,20,3,0);
    public final GolemStats stats;public final int visCost;
    GolemMaterial(int health,int armor,float speed,boolean fire,int upgrades,int carry,int regen,int strength,int vis){stats=new GolemStats(health,armor,speed,fire,upgrades,carry,regen,strength,name().equals("ROCK")||name().equals("METAL")||name().equals("REDSTONE"));visCost=vis;}
    public static GolemMaterial of(BlockState state){
        if(state.isAir())return VOID;if(state.is(Blocks.CACTUS))return CACTUS;if(state.getBlock() instanceof CakeBlock)return CAKE;if(state.is(Blocks.COBWEB))return WEB;if(state.is(Blocks.CLAY))return CLAY;
        if(state.is(Blocks.REDSTONE_BLOCK))return REDSTONE;if(state.getBlock() instanceof TntBlock)return TNT;
        if(state.is(Blocks.ICE)||state.is(Blocks.PACKED_ICE)||state.is(Blocks.BLUE_ICE))return ICE;
        if(state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.MYCELIUM)||state.is(Blocks.HAY_BLOCK))return GRASS;if(state.is(BlockTags.DIRT)||state.getBlock() instanceof FarmBlock)return DIRT;
        if(state.is(BlockTags.LEAVES)||state.getBlock() instanceof BushBlock||state.is(Blocks.VINE))return PLANT;
        if(state.is(BlockTags.LOGS)||state.is(BlockTags.PLANKS)||state.getSoundType()==SoundType.WOOD||state.getSoundType()==SoundType.NETHER_WOOD||state.is(Blocks.PUMPKIN)||state.is(Blocks.MELON))return WOOD;
        if(state.is(BlockTags.WOOL)||state.is(BlockTags.WOOL_CARPETS)||state.is(Blocks.SPONGE)||state.is(Blocks.WET_SPONGE))return CLOTH;
        if(state.is(BlockTags.ANVIL)||state.getSoundType()==SoundType.METAL||state.getSoundType()==SoundType.NETHERITE_BLOCK)return METAL;
        if(state.is(BlockTags.SAND)||state.is(Blocks.GRAVEL)||state.getBlock() instanceof ConcretePowderBlock)return SAND;
        if(state.is(BlockTags.MINEABLE_WITH_PICKAXE)||state.is(Materials.BLOCKS.get("bone").get())||state.is(Materials.BLOCKS.get("leviathan").get()))return ROCK;return SAND;
    }
}
