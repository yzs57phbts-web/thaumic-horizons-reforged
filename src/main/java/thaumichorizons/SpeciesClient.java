package thaumichorizons;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class SpeciesClient {
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event){event.registerLayerDefinition(FamiliarModel.LAYER,FamiliarModel::layer);event.registerLayerDefinition(LegacyHorseModel.LAYER,LegacyHorseModel::layer);}
    static ResourceLocation texture(String name){return ThaumicHorizons.id("textures/entity/"+name+".png");}
    public static void register(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(Species.CHROMATIC.get(),SheepRenderer::new);event.registerEntityRenderer(Species.SHEARING.get(),SheepRenderer::new);
        for(var type:java.util.List.of(Species.SCHOLAR,Species.GOLD))event.registerEntityRenderer(type.get(),BirdRenderer::new);
        event.registerEntityRenderer(Species.CHOCOLATE.get(),ChocRenderer::new);
        for(var type:java.util.List.of(Species.PANTHER,Species.GRAVEKEEPER))event.registerEntityRenderer(type.get(),FelineRenderer::new);
        event.registerEntityRenderer(Species.FAMILIAR.get(),FamiliarRenderer::new);
        for(var type:java.util.List.of(Species.SEA,Species.LUNAR,Species.NETHER))event.registerEntityRenderer(type.get(),CanineRenderer::new);
        for(var type:java.util.List.of(Species.ORE,Species.TAINT_EATER))event.registerEntityRenderer(type.get(),BoarRenderer::new);
        event.registerEntityRenderer(Species.SHEEDER.get(),SheederRenderer::new);
        for(var type:java.util.List.of(SpeciesHorse.ENDERSTEED,SpeciesHorse.NIGHTMARE,SpeciesHorse.ZOMBIE,SpeciesHorse.SKELETON))event.registerEntityRenderer(type.get(),SteedRenderer::new);
    }
    static final class BirdRenderer extends ChickenRenderer {
        BirdRenderer(EntityRendererProvider.Context ctx){super(ctx);shadowRadius=.5F;}
        @Override public ResourceLocation getTextureLocation(Chicken bird){return texture(((Species.Form)bird).form().equals("gold_chicken")?"goldchicken":"scholarchicken");}
    }
    static final class ChocRenderer extends CowRenderer {
        ChocRenderer(EntityRendererProvider.Context ctx){super(ctx);shadowRadius=.7F;}
        @Override public ResourceLocation getTextureLocation(Cow cow){return texture("chocolatecow");}
    }
    static final class FelineRenderer extends CatRenderer {
        FelineRenderer(EntityRendererProvider.Context ctx){super(ctx);layers.clear();}
        @Override public ResourceLocation getTextureLocation(Cat cat){if(((Species.Form)cat).form().equals("gravekeeper"))return texture("gravekeeper");var data=Infusions.DATA.get(cat);if(!data.contains("wildOcelot")||data.getBoolean("wildOcelot"))return texture("cat_ocelot_legacy");String variant=cat.getVariant().unwrapKey().map(k->k.location().getPath()).orElse("");return switch(variant){case "tuxedo"->texture("cat_black_legacy");case "red"->texture("cat_red_legacy");case "siamese"->texture("cat_siamese_legacy");default->super.getTextureLocation(cat);};}
        @Override protected void scale(Cat cat,PoseStack pose,float partial){super.scale(cat,pose,partial);if(((Species.Form)cat).form().equals("guardian_panther"))pose.scale(2,2,2);}
    }
    static final class FamiliarRenderer extends MobRenderer<Cat,FamiliarModel> {
        FamiliarRenderer(EntityRendererProvider.Context ctx){super(ctx,new FamiliarModel(ctx.bakeLayer(FamiliarModel.LAYER)),.5F);}
        @Override public ResourceLocation getTextureLocation(Cat cat){return texture("familiar");}
        @Override protected void scale(Cat cat,PoseStack pose,float partial){pose.scale(.8F,.8F,.8F);}
    }
    static final class CanineRenderer extends WolfRenderer {
        CanineRenderer(EntityRendererProvider.Context ctx){super(ctx);}
        @Override public ResourceLocation getTextureLocation(Wolf wolf){return texture(switch(((Species.Form)wolf).form()){case "seawolf"->"seawolf";case "lunar_wolf"->"lunarwolf";default->"netherhound";});}
    }
    static final class BoarRenderer extends PigRenderer {
        BoarRenderer(EntityRendererProvider.Context ctx){super(ctx);}
        @Override public ResourceLocation getTextureLocation(Pig pig){return texture(((Species.Form)pig).form().equals("ore_pig")?"oreboar":"taintfeeder");}
    }
    static final class SheederRenderer extends SpiderRenderer<Species.Sheeder> {
        SheederRenderer(EntityRendererProvider.Context ctx){super(ctx);layers.clear();addLayer(new RenderLayer<Species.Sheeder,SpiderModel<Species.Sheeder>>(this){
            @Override public void render(PoseStack pose,MultiBufferSource buffers,int light,Species.Sheeder spider,float limb,float amount,float partial,float age,float yaw,float pitch){if(!spider.isSheared())getParentModel().renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(texture("sheederoverlay"))),light,net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);}
        });}
        @Override public ResourceLocation getTextureLocation(Species.Sheeder spider){return texture("sheeder");}
    }
    static final class SteedRenderer extends MobRenderer<Horse,LegacyHorseModel> {
        SteedRenderer(EntityRendererProvider.Context ctx){super(ctx,new LegacyHorseModel(ctx.bakeLayer(LegacyHorseModel.LAYER)),.75F);}
        @Override public ResourceLocation getTextureLocation(Horse horse){String form=((Species.Form)horse).form();if(form.equals("zombie_horse"))return texture("horse_zombie_legacy");if(form.equals("skeleton_horse"))return texture("horse_skeleton_legacy");String name=form.equals("nightmare")?"nightmare":"endersteed";var armor=horse.getBodyArmorItem();return texture(name+(armor.is(Items.IRON_HORSE_ARMOR)?"iron":armor.is(Items.GOLDEN_HORSE_ARMOR)?"gold":armor.is(Items.DIAMOND_HORSE_ARMOR)?"diamond":""));}
    }
}
