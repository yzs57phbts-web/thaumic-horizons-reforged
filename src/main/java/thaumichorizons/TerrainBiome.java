package thaumichorizons;
enum TerrainBiome {
    desert("minecraft:desert",false),savanna("minecraft:savanna",false),jungle("minecraft:jungle",false),plains("minecraft:plains",false),forest("minecraft:forest",false),icePlains("minecraft:snowy_plains",true),taint("thaumcraft:tainted_land",false),eerie("thaumcraft:eerie",false),magicalForest("thaumcraft:magical_forest",false);
    enum TempCategory {COLD,MEDIUM}
    final String key;
    final int biomeID;
    private final boolean cold;
    TerrainBiome(String key,boolean cold){this.key=key;this.cold=cold;biomeID=ordinal();}
    TempCategory getTempCategory(){return cold?TempCategory.COLD:TempCategory.MEDIUM;}
}
