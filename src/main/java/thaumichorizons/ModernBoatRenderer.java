package thaumichorizons;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
/** Vanilla hull, two animated paddles, passenger poses, damage tilt and water mask. */
final class ModernBoatRenderer extends BoatRenderer {
    private final ListModel<Boat> model;
    ModernBoatRenderer(EntityRendererProvider.Context context){super(context,false);model=new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK)));}
    @Override public Pair<ResourceLocation,ListModel<Boat>> getModelWithLocation(Boat boat){return Pair.of(ThaumicHorizons.id("textures/entity/modern_boat_"+(((MagicBoats.LegacyBoat)boat).metal?"thaumium":"greatwood")+".png"),model);}
}
