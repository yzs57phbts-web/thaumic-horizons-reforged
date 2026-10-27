package thaumichorizons;
import net.neoforged.neoforge.common.ModConfigSpec;
final class HorizonsConfig {
    static final ModConfigSpec SPEC;
    static final ModConfigSpec.BooleanValue ALTERNATE_BELL,POCKET_PLANES;
    static final ModConfigSpec.IntValue WARPED_TUMOR;
    static {var builder=new ModConfigSpec.Builder();builder.push("general");ALTERNATE_BELL=builder.comment("Use the original Thaumic Horizons golem bell recipe and research.").worldRestart().define("alternateGolemBell",true);POCKET_PLANES=builder.comment("Enable pocket plane creation and its research.").worldRestart().define("enablePocketPlane",true);WARPED_TUMOR=builder.comment("Points of warp held by a warped tumor.").worldRestart().defineInRange("warpedTumorValue",50,0,100000);builder.pop();SPEC=builder.build();}
}
