package thaumichorizons;
import com.google.gson.*;
import java.nio.file.*;
import net.minecraft.core.registries.BuiltInRegistries;
import thaumcraft.research.ResearchCategories;
import thaumcraft.crafting.TcRecipes;
/** Explicit development export; never writes anything during ordinary gameplay. */
final class RuntimeAudit {
    static void export(){
        String destination=System.getProperty("th.auditFile");if(destination==null)return;
        var root=new JsonObject();
        root.add("items",ids(BuiltInRegistries.ITEM));root.add("blocks",ids(BuiltInRegistries.BLOCK));root.add("entities",ids(BuiltInRegistries.ENTITY_TYPE));
        var research=new JsonArray();for(var entry:ResearchCategories.get("ThaumicHorizons").research()){var row=new JsonObject();row.addProperty("key",entry.key());var parents=new JsonArray();for(String parent:entry.parents())parents.add(parent);row.add("parents",parents);research.add(row);}root.add("research",research);
        var recipes=new JsonArray();for(var recipe:TcRecipes.all())if(recipe.key().startsWith("TH:")){var row=new JsonObject();row.addProperty("key",recipe.key());row.addProperty("research",recipe.research());row.addProperty("type",recipe.getClass().getSimpleName());recipes.add(row);}root.add("recipes",recipes);
        try{Path file=Path.of(destination);Files.createDirectories(file.getParent());Files.writeString(file,new GsonBuilder().setPrettyPrinting().create().toJson(root));}catch(java.io.IOException failure){throw new IllegalStateException("Cannot export registry audit",failure);}
    }
    private static JsonArray ids(net.minecraft.core.Registry<?> registry){var ids=new JsonArray();registry.keySet().stream().filter(id->id.getNamespace().equals(ThaumicHorizons.ID)).sorted().forEach(id->ids.add(id.toString()));return ids;}
}
