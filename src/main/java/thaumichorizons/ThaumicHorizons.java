package thaumichorizons;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(ThaumicHorizons.ID)
public final class ThaumicHorizons {
    public static final String ID="thaumichorizons";
    public ThaumicHorizons(IEventBus bus,net.neoforged.fml.ModContainer container){
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.COMMON,HorizonsConfig.SPEC);
        PocketDimension.init(bus);
        Content.init(bus);
        bus.addListener(Content::capabilities);
        bus.addListener(Species::attributes);
        bus.addListener(AnimatedGolems::attributes);
        bus.addListener(RareCreatures::attributes);
        bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->{CrystalWands.setup();MagicData.init();Vats.setup();Materials.setup();MagicBoats.setup();HorizonsAspects.init();}));
        NeoForge.EVENT_BUS.addListener(Effects::tick);
        NeoForge.EVENT_BUS.addListener(Species::restoreShowcaseAi);
        NeoForge.EVENT_BUS.register(Infusions.class);
    }
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath(ID,path);}
}
