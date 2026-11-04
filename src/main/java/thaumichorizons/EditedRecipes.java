package thaumichorizons;

import java.util.*;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;

/** Saved Recipe Editor projects, applied after all native common setup registrations. */
public final class EditedRecipes {
    private EditedRecipes() {}
    public static void register() {
        if (TcRecipes.get("TH:recipeSoulSieve") != null && true) {
            TcRecipes.register(new InfusionRecipe("TH:recipeSoulSieve", "soulExtractor", new TcStack(ResourceLocation.parse("thaumichorizons:soul_sieve"), 1, null, Map.ofEntries()), 4, new AspectList().add(Aspect.getAspect("spiritus"), 16).add(Aspect.getAspect("machina"), 8).add(Aspect.getAspect("permutatio"), 8), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:hopper"), 1, null, Map.ofEntries())), List.of(TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:amber"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:enchanted_fabric"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:piston"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:brush"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:enchanted_fabric"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:piston"), 1, null, Map.ofEntries())))));
        }
        if (TcRecipes.get("TH:recipeFocusIllumination") != null && true) {
            TcRecipes.register(new CrucibleRecipe("TH:recipeFocusIllumination", "focusIllumination", new TcStack(ResourceLocation.parse("thaumichorizons:focus_illumination"), 1, null, Map.ofEntries(Map.entry("th_light_color", "11"))), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:sea_lantern"), 1, null, Map.ofEntries())), new AspectList().add(Aspect.getAspect("lux"), 12).add(Aspect.getAspect("potentia"), 8).add(Aspect.getAspect("ignis"), 8)));
        }
        if (TcRecipes.get("TH:recipeSoulBeacon") != null && true) {
            TcRecipes.register(new InfusionRecipe("TH:recipeSoulBeacon", "cloneSelf", new TcStack(ResourceLocation.parse("thaumichorizons:soul_beacon"), 1, null, Map.ofEntries()), 8, new AspectList().add(Aspect.getAspect("spiritus"), 16).add(Aspect.getAspect("vinculum"), 32).add(Aspect.getAspect("iter"), 32).add(Aspect.getAspect("mortuus"), 32), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:primordial_pearl"), 1, null, Map.ofEntries())), List.of(TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:warded_jar"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:void_metal_ingot"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:ender_eye"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:beacon"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:respawn_anchor"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:ender_eye"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:void_metal_ingot"), 1, null, Map.ofEntries())))));
        }
        if (TcRecipes.get("TH:recipeLensAir") != null && true) {
            TcRecipes.register(new InfusionRecipe("TH:recipeLensAir", "lensAir", new TcStack(ResourceLocation.parse("thaumichorizons:lens_air"), 1, null, Map.ofEntries()), 1, new AspectList().add(Aspect.getAspect("sensus"), 18).add(Aspect.getAspect("auram"), 4).add(Aspect.getAspect("victus"), 12).add(Aspect.getAspect("exanimis"), 12), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:glass_pane"), 1, null, Map.ofEntries())), List.of(TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:bone"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:air_shard"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:spectral_arrow"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:egg"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:spectral_arrow"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:air_shard"), 1, null, Map.ofEntries())))));
        }
        if (TcRecipes.get("TH:recipeAmuletMirror") != null && true) {
            TcRecipes.register(new InfusionRecipe("TH:recipeAmuletMirror", "mirrorAmulet", new TcStack(ResourceLocation.parse("thaumichorizons:mirror_amulet"), 1, null, Map.ofEntries()), 6, new AspectList().add(Aspect.getAspect("mortuus"), 16).add(Aspect.getAspect("perditio"), 32).add(Aspect.getAspect("vacuos"), 32), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:primordial_pearl"), 1, null, Map.ofEntries())), List.of(TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:mundane_amulet"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:recovery_compass"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("thaumcraft:magic_mirror"), 1, null, Map.ofEntries())))));
        }
        if (TcRecipes.get("TH:alternateGolemBell") != null && true) {
            TcRecipes.register(new ShapedArcaneRecipe("TH:alternateGolemBell", "GOLEMBELL", new TcStack(ResourceLocation.parse("thaumichorizons:golem_bell"), 1, null, Map.ofEntries()), new AspectList().add(Aspect.getAspect("ordo"), 5), 3, 3, List.of(TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:amethyst_shard"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:quartz"), 1, null, Map.ofEntries())), TcIngredient.EMPTY, TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:quartz"), 1, null, Map.ofEntries())), TcIngredient.of(new TcStack(ResourceLocation.parse("minecraft:quartz"), 1, null, Map.ofEntries())), TcIngredient.EMPTY, TcIngredient.EMPTY, TcIngredient.EMPTY, TcIngredient.tag("c:rods/wooden"))));
        }
    }
}
