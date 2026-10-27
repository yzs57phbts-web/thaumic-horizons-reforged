package thaumichorizons;
final class EggPalette {
    private static final java.util.Map<String,int[]> COLORS=java.util.Map.ofEntries(
        java.util.Map.entry("chromatic_sheep",new int[]{16777215,10813695}),
        java.util.Map.entry("self_shearing_sheep",new int[]{16777215,10813695}),
        java.util.Map.entry("guardian_panther",new int[]{16761600,10813695}),
        java.util.Map.entry("gravekeeper",new int[]{16776960,10813695}),
        java.util.Map.entry("familiar",new int[]{0,10813695}),
        java.util.Map.entry("chocolate_cow",new int[]{8667136,10813695}),
        java.util.Map.entry("scholar_chicken",new int[]{0,10813695}),
        java.util.Map.entry("gold_chicken",new int[]{13873152,10813695}),
        java.util.Map.entry("ore_pig",new int[]{9671571,10813695}),
        java.util.Map.entry("taint_eater_pig",new int[]{3801179,10813695}),
        java.util.Map.entry("endersteed",new int[]{10813695,10813695}),
        java.util.Map.entry("seawolf",new int[]{32,10813695}),
        java.util.Map.entry("nether_hound",new int[]{16714752,10813695}),
        java.util.Map.entry("lunar_wolf",new int[]{16777215,10813695}),
        java.util.Map.entry("zombie_horse",new int[]{16777215,10813695}),
        java.util.Map.entry("skeleton_horse",new int[]{16777215,10813695}),
        java.util.Map.entry("nightmare",new int[]{16777215,10813695}),
        java.util.Map.entry("meat_slime",new int[]{16777215,10813695}),
        java.util.Map.entry("mercurial_slime",new int[]{16777215,10813695}),
        java.util.Map.entry("volt_slime",new int[]{16777215,10813695}),
        java.util.Map.entry("med_slime",new int[]{16777215,10813695}),
        java.util.Map.entry("sheeder",new int[]{16777215,10813695}),
        java.util.Map.entry("lost_soul",new int[]{16777215,10813695})
    );
    static int[] colors(String name){return COLORS.getOrDefault(name,new int[]{0xFFFFFF,10813695});}
}
