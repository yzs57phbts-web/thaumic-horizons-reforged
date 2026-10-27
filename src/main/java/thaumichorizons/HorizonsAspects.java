package thaumichorizons;
import thaumcraft.aspect.*;
/** Explicit original scanner/aspect values override recipe inference on every registry rebuild. */
public final class HorizonsAspects extends AspectRegistrar {
 private HorizonsAspects(AspectRegistry registry){super(registry);}
 public static void init(){AspectSync.addRegistrar(registry->new HorizonsAspects(registry).registerAll());}
 private void registerAll(){
register("thaumichorizons:incubated_egg",(new AspectList()).add(Aspect.SLIME, 2).add(Aspect.LIFE, 2).add(Aspect.BEAST, 2));
register("thaumichorizons:focus_illumination",(new AspectList()).add(Aspect.LIGHT, 5).add(Aspect.ENERGY, 5));
register("thaumichorizons:empty_syringe",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1));
register("thaumichorizons:blood_sample",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1).add(Aspect.BEAST, 1));
register("thaumichorizons:blood_syringe",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1).add(Aspect.MAN, 1));
register("thaumichorizons:blood_phial",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1).add(Aspect.MAN, 1));
register("thaumichorizons:injection",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1).add(Aspect.MAN, 1));
register("thaumichorizons:blast_phial",(new AspectList()).add(Aspect.CRYSTAL, 1).add(Aspect.METAL, 1).add(Aspect.MAN, 1));
register("thaumichorizons:nutrients",(new AspectList()).add(Aspect.HUNGER, 2));
register("thaumichorizons:chocolate_bar",(new AspectList()).add(Aspect.HUNGER, 2).add(Aspect.LIFE, 1));
register("thaumichorizons:ice_cream",(new AspectList()).add(Aspect.HUNGER, 3).add(Aspect.LIFE, 1).add(Aspect.COLD, 1));
register("thaumichorizons:chocolate_milk",copy("minecraft:bucket").add(Aspect.HUNGER, 2).add(Aspect.LIFE, 2));
register("thaumichorizons:ink_egg",(new AspectList()).add(Aspect.SLIME, 2).add(Aspect.DARKNESS, 2));
register("thaumichorizons:gold_egg",(new AspectList()).add(Aspect.SLIME, 2).add(Aspect.GREED, 1));
register("thaumichorizons:mirror_amulet",(new AspectList()).add(Aspect.DEATH, 8).add(Aspect.VOID, 6).add(Aspect.GREED, 6).add(Aspect.TRAVEL, 6));
register("thaumichorizons:focus_animation",(new AspectList()).add(Aspect.SOUL, 8).add(Aspect.MAN, 8).add(Aspect.MOTION, 8).add(Aspect.CRAFT, 8));
register("thaumichorizons:void_putty",(new AspectList()).add(Aspect.CRAFT, 4).add(Aspect.ELDRITCH, 4));
register("thaumichorizons:crystal_wand",(new AspectList()).add(Aspect.CRYSTAL, 6).add(Aspect.MAGIC, 4));
register("thaumichorizons:meat",(new AspectList()).add(Aspect.FLESH, 4));
register("thaumichorizons:cooked_meat",(new AspectList()).add(Aspect.FLESH, 3).add(Aspect.FIRE, 1));
register("thaumichorizons:meat_nugget",(new AspectList()).add(Aspect.FLESH, 1));
register("thaumichorizons:alchemite",(new AspectList()).add(Aspect.ENERGY, 4).add(Aspect.ENTROPY, 4).add(Aspect.EXCHANGE, 2) .add(Aspect.MAGIC, 2).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:vat",(new AspectList()).add(Aspect.MECHANISM, 1).add(Aspect.HEAL, 1));
register("thaumichorizons:vat_solid",(new AspectList()).add(Aspect.MECHANISM, 1).add(Aspect.HEAL, 1));
register("thaumichorizons:chocolate",(new AspectList()).add(Aspect.HUNGER, 5).add(Aspect.LIFE, 5));
register("thaumichorizons:soul_beacon",(new AspectList()).add(Aspect.SOUL, 8).add(Aspect.TRAVEL, 8).add(Aspect.TRAP, 8).add(Aspect.LIGHT, 8));
register("thaumichorizons:brain",(new AspectList()).add(Aspect.MIND, 4).add(Aspect.FLESH, 4));
register("thaumichorizons:void",(new AspectList()).add(Aspect.VOID, 4).add(Aspect.TRAP, 4));
register("thaumichorizons:dust",(new AspectList()).add(Aspect.ENTROPY, 2));
register("thaumichorizons:eyes",(new AspectList()).add(Aspect.SENSES, 2).add(Aspect.BEAST, 1));
register("thaumichorizons:bone",(new AspectList()).add(Aspect.DEATH, 2));
register("thaumichorizons:flesh",(new AspectList()).add(Aspect.FLESH, 2));
register("thaumichorizons:white_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:orange_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:magenta_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:light_blue_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:yellow_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:lime_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:pink_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:gray_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:light_gray_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:cyan_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:purple_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:blue_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:brown_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:green_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:red_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:black_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2));
register("thaumichorizons:deep_crystal",(new AspectList()).add(Aspect.CRYSTAL, 2).add(Aspect.LIGHT, 2).add(Aspect.EXCHANGE, 1));
register("thaumichorizons:metal_spike",(new AspectList()).add(Aspect.WEAPON, 3).add(Aspect.METAL, 3));
register("thaumichorizons:wooden_spike",(new AspectList()).add(Aspect.WEAPON, 3).add(Aspect.TREE, 3));
register("thaumichorizons:tooth_spike",(new AspectList()).add(Aspect.WEAPON, 3).add(Aspect.BEAST, 3));
entity("thaumichorizons:chromatic_sheep",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.EARTH, 2).add(Aspect.SENSES, 2) .add(Aspect.EXCHANGE, 2));
entity("thaumichorizons:self_shearing_sheep",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.EARTH, 2).add(Aspect.TOOL, 2).add(Aspect.CLOTH, 2));
entity("thaumichorizons:guardian_panther",(new AspectList()).add(Aspect.BEAST, 4).add(Aspect.WEAPON, 4).add(Aspect.ENTROPY, 4));
entity("thaumichorizons:familiar",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.MAGIC, 3).add(Aspect.AURA, 3));
entity("thaumichorizons:gravekeeper",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.DEATH, 3).add(Aspect.ORDER, 3));
entity("thaumichorizons:chocolate_cow",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.EARTH, 3).add(Aspect.SENSES, 3));
entity("thaumichorizons:scholar_chicken",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 2).add(Aspect.AIR, 1).add(Aspect.DARKNESS, 2) .add(Aspect.MIND, 2));
entity("thaumichorizons:gold_chicken",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.FLIGHT, 2).add(Aspect.AIR, 1).add(Aspect.GREED, 3));
entity("thaumichorizons:ore_pig",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.EARTH, 2).add(Aspect.METAL, 4));
entity("thaumichorizons:taint_eater_pig",(new AspectList()).add(Aspect.BEAST, 2).add(Aspect.EARTH, 2).add(Aspect.HEAL, 2).add(Aspect.ORDER, 2));
entity("thaumichorizons:seawolf",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.EARTH, 3).add(Aspect.WATER, 3));
entity("thaumichorizons:lunar_wolf",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.EARTH, 3).add(Aspect.MAGIC, 3));
entity("thaumichorizons:nether_hound",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.EARTH, 3).add(Aspect.FIRE, 3));
entity("thaumichorizons:animated_golem",(new AspectList()).add(Aspect.AIR, 2).add(Aspect.EARTH, 2).add(Aspect.MAGIC, 2));
entity("thaumichorizons:endersteed",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.ELDRITCH, 4));
entity("thaumichorizons:nightmare",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.ELDRITCH, 4).add(Aspect.FIRE, 4) .add(Aspect.TRAVEL, 4));
entity("thaumichorizons:sheeder",(new AspectList()).add(Aspect.BEAST, 3).add(Aspect.CLOTH, 4));
 }
}
