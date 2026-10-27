package thaumichorizons;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class Client {
    @SubscribeEvent public static void reload(RegisterClientReloadListenersEvent event){event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener)manager->GolemSkins.clear());}
    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(InjectorKeys.MAGAZINE);event.register(InfusionKeys.MORPHIC);event.register(InfusionKeys.CLIMB);event.register(InfusionKeys.INVISIBILITY);event.register(LensClient.CHANGE);}
    @SubscribeEvent public static void lensHud(RegisterGuiLayersEvent event){event.registerAbove(net.neoforged.neoforge.client.gui.VanillaGuiLayers.HOTBAR,ThaumicHorizons.id("lenses"),LensClient::render);}
    @SubscribeEvent public static void dimensionEffects(RegisterDimensionSpecialEffectsEvent event){event.register(ThaumicHorizons.id("pocket_plane"),new PocketDimensionEffects());}
    @SubscribeEvent public static void layers(EntityRenderersEvent.RegisterLayerDefinitions e){SpeciesClient.layers(e);e.registerLayerDefinition(BloodModel.LAYER,BloodModel::create);e.registerLayerDefinition(DynamoRenderer.LAYER,DynamoRenderer::layer);e.registerLayerDefinition(SyringeModel.LAYER,SyringeModel::layer);e.registerLayerDefinition(SoulBeaconModel.LAYER,SoulBeaconModel::layer);e.registerLayerDefinition(VatRenderer.CORPSE,VatRenderer::corpseLayer);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers e){
        SpeciesClient.register(e);
        e.registerBlockEntityRenderer(Souls.TYPE.get(),SoulMachineRenderer::new);
        e.registerBlockEntityRenderer(NodeMonitor.TYPE.get(),NodeMonitorRenderer::new);
        e.registerBlockEntityRenderer(SyntheticNode.TYPE.get(),SyntheticNodeRenderer::new);
        e.registerBlockEntityRenderer(Recombinator.TYPE.get(),RecombinatorRenderer::new);
        e.registerBlockEntityRenderer(Spikes.TYPE.get(),SpikeRenderer::new);
        e.registerBlockEntityRenderer(Clouds.TYPE.get(),CloudRenderer::new);
        e.registerBlockEntityRenderer(Vortex.TYPE.get(),VortexRenderer::new);e.registerBlockEntityRenderer(RiftDevices.AMP_TYPE.get(),RiftDeviceRenderer.Amp::new);e.registerBlockEntityRenderer(RiftDevices.STAB_TYPE.get(),RiftDeviceRenderer.Stabilizer::new);
        e.registerBlockEntityRenderer(Gateways.TYPE.get(),SlotRenderer::new);
        e.registerBlockEntityRenderer(Content.BLOOD.get(),BloodRenderer::new);
        e.registerBlockEntityRenderer(Content.DYNAMO.get(),DynamoRenderer::new);
        e.registerBlockEntityRenderer(Vats.TYPE.get(),VatRenderer::new);e.registerBlockEntityRenderer(Vats.DEVICE.get(),VatDeviceRenderer::new);
        e.registerBlockEntityRenderer(Specimens.TYPE.get(),SpecimenJarRenderer::new);
        e.registerEntityRenderer(Content.EGG.get(),net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        e.registerEntityRenderer(Content.PHIAL.get(),net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        e.registerEntityRenderer(Content.SYRINGE.get(),net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        e.registerEntityRenderer(Alchemite.ENTITY.get(),net.minecraft.client.renderer.entity.TntRenderer::new);
        e.registerEntityRenderer(Alchemite.DROPS.get(),net.minecraft.client.renderer.entity.ItemEntityRenderer::new);
        e.registerEntityRenderer(AnimatedGolems.TYPE.get(),AnimatedGolemRenderer::new);
        for(var type:RareCreatures.SLIMES.values())e.registerEntityRenderer(type.get(),RareCreatureRenderers.Slime::new);
        e.registerEntityRenderer(RareCreatures.SOUL.get(),RareCreatureRenderers.Soul::new);
        e.registerEntityRenderer(MagicBoats.GREATWOOD.get(),ModernBoatRenderer::new);e.registerEntityRenderer(MagicBoats.THAUMIUM.get(),ModernBoatRenderer::new);
        e.registerEntityRenderer(FiniteLightning.TYPE.get(),net.minecraft.client.renderer.entity.NoopRenderer::new);
    }
    @SubscribeEvent public static void soulLayers(EntityRenderersEvent.RegisterLayerDefinitions e){e.registerLayerDefinition(SoulModels.SIEVE,SoulModels::sieve);e.registerLayerDefinition(SoulModels.INSPIRATRON,SoulModels::inspiratron);e.registerLayerDefinition(SoulModels.FORGE,SoulModels::forge);e.registerLayerDefinition(SoulModels.BRAIN,SoulModels::brain);e.registerLayerDefinition(SoulModels.BRINE,SoulModels::brine);e.registerLayerDefinition(LegacyBoatRenderer.LAYER,LegacyBoatRenderer::layer);e.registerLayerDefinition(RecombinatorRenderer.LAYER,RecombinatorRenderer::layer);e.registerLayerDefinition(RiftDeviceModel.LAYER,RiftDeviceModel::layer);e.registerLayerDefinition(SlotModel.LAYER,SlotModel::layer);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent e){e.register(Content.BLOOD_MENU.get(),BloodScreen::new);e.register(Content.DYNAMO_MENU.get(),DynamoScreen::new);e.register(Vats.MENU.get(),VatScreen::new);e.register(Souls.SIEVE_MENU.get(),SoulScreen::new);e.register(Souls.INSPIRATRON_MENU.get(),SoulScreen::new);e.register(Content.INJECTOR_MENU.get(),InjectorScreen::new);e.register(Content.LENS_CASE_MENU.get(),LensCaseScreen::new);}
    @SubscribeEvent public static void setup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent e){e.enqueueWork(()->{
        thaumcraft.client.research.CustomResearchPages.register(ThaumicHorizons.id("vat_infusion"),VatPages::render);
        thaumcraft.client.research.CustomResearchPages.register(ThaumicHorizons.id("void_putty_example"),RiftPages::repair);
        ThermalFoci.clientBeam=FocusBeamFx::draw;
        Specimens.clientBeam=FocusBeamFx::containment;VatEntity.clientTick=LegacyParticles::vat;Dynamo.clientTick=LegacyParticles::dynamo;
        thaumcraft.platform.ModNetwork.setClientHandler(ContainmentFx.TYPE,LegacyParticles::containment);
        FiniteLightning.visual=FiniteLightningClient::draw;
        Vortex.clientTick=VortexSound::ensure;
        for(String name:java.util.List.of("injector","thaumic_crossbow")){
            net.minecraft.client.renderer.item.ItemProperties.register(Content.item(name),net.minecraft.resources.ResourceLocation.withDefaultNamespace("pull"),(stack,level,player,seed)->player!=null&&player.getUseItem()==stack?Math.min(1,(stack.getUseDuration(player)-player.getUseItemRemainingTicks())/(float)net.minecraft.world.item.CrossbowItem.getChargeDuration(stack,player)):0);
            net.minecraft.client.renderer.item.ItemProperties.register(Content.item(name),net.minecraft.resources.ResourceLocation.withDefaultNamespace("pulling"),(stack,level,player,seed)->player!=null&&player.isUsingItem()&&player.getUseItem()==stack?1:0);
            net.minecraft.client.renderer.item.ItemProperties.register(Content.item(name),net.minecraft.resources.ResourceLocation.withDefaultNamespace("charged"),(stack,level,player,seed)->net.minecraft.world.item.CrossbowItem.isCharged(stack)?1:0);
        }
        net.minecraft.client.renderer.item.ItemProperties.register(Content.item("focus_illumination"),ThaumicHorizons.id("illumination_color"),(stack,level,player,seed)->Illumination.color(stack));
        net.minecraft.client.renderer.item.ItemProperties.register(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ThaumicHorizons.id("creature_icon")),ThaumicHorizons.id("dummy_type"),(stack,level,player,seed)->CreativeTools.dummyType(stack));
    });}
    @SubscribeEvent public static void blockColors(RegisterColorHandlersEvent.Block e){e.register((s,l,p,t)->0x3F76E4,Vats.GLASS.get());}
    @SubscribeEvent public static void keystoneColor(RegisterColorHandlersEvent.Item event){event.register((stack,index)->0xFF000000|(Gateways.color(stack)&0xFFFFFF),Content.item("keystone"));}
    @SubscribeEvent public static void injectionColor(RegisterColorHandlersEvent.Item event){event.register((stack,index)->index==1?0xFF000000|stack.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS,net.minecraft.world.item.alchemy.PotionContents.EMPTY).getColor():0xFFFFFFFF,Content.item("injection"));}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent e){
        e.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){
            private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){
                if(renderer==null){var mc=net.minecraft.client.Minecraft.getInstance();renderer=new net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(),mc.getEntityModels()){
                    @Override public void renderByItem(net.minecraft.world.item.ItemStack s,net.minecraft.world.item.ItemDisplayContext display,com.mojang.blaze3d.vertex.PoseStack pose,net.minecraft.client.renderer.MultiBufferSource buffers,int light,int overlay){
                        if(s.is(Content.INFUSER.get().asItem()))BloodRenderer.draw(mc.getEntityModels().bakeLayer(BloodModel.LAYER),pose,buffers,light,overlay);
                        else if(s.is(Content.item("disposable_wand")))thaumcraft.client.render.WandSpecialRenderer.render(s,thaumcraft.client.render.LegacyItemView.of(display),pose,buffers,light);
                        else if(s.is(NodeMonitor.BLOCK.get().asItem()))NodeMonitorRenderer.draw(net.minecraft.core.Direction.UP,0,false,pose,buffers,light);
                        else if(s.is(SyntheticNode.BLOCK.get().asItem()))SyntheticNodeRenderer.draw(new thaumcraft.aspect.AspectList(),new thaumcraft.aspect.AspectList(),0,0,pose,buffers);
                        else if(s.is(Recombinator.BLOCK.get().asItem()))RecombinatorRenderer.draw(mc.getEntityModels().bakeLayer(RecombinatorRenderer.LAYER),0,pose,buffers,light);
                        else if(s.getItem() instanceof net.minecraft.world.item.BlockItem spikeItem&&spikeItem.getBlock() instanceof Spikes.SpikeBlock spike)SpikeRenderer.draw(spike.type,net.minecraft.core.Direction.UP,pose,buffers,light);
                        else if(s.is(RiftDevices.AMPLIFIER.get().asItem()))RiftDeviceRenderer.amplifier(net.minecraft.core.Direction.NORTH,0,true,pose,buffers,light);
                        else if(s.is(RiftDevices.STABILIZER.get().asItem()))RiftDeviceRenderer.stabilizer(mc.getEntityModels().bakeLayer(RiftDeviceModel.LAYER),net.minecraft.core.Direction.UP,pose,buffers,light);
                        else if(s.is(Gateways.SLOT.get().asItem()))SlotRenderer.draw(mc.getEntityModels().bakeLayer(SlotModel.LAYER),false,0xFFFFFF,pose,buffers,light);
                        else if(s.is(Content.VIS_DYNAMO.get().asItem())||s.is(Content.ESSENTIA_DYNAMO.get().asItem()))DynamoRenderer.draw(mc.getEntityModels().bakeLayer(DynamoRenderer.LAYER),s.is(Content.ESSENTIA_DYNAMO.get().asItem()),0,0,0,pose,buffers,light,overlay);
                        else if(s.is(Vats.MATRIX.get().asItem())||s.is(Vats.BEACON.get().asItem()))VatDeviceRenderer.draw(s.is(Vats.BEACON.get().asItem()),mc.getEntityModels().bakeLayer(SoulBeaconModel.LAYER),null,0,pose,buffers,light,overlay);
                        else if(s.getItem() instanceof net.minecraft.world.item.BlockItem block&&block.getBlock() instanceof SoulBlock soul)SoulMachineRenderer.draw(soul.kind,mc.getEntityModels().bakeLayer(SoulModels.SIEVE),mc.getEntityModels().bakeLayer(SoulModels.INSPIRATRON),mc.getEntityModels().bakeLayer(SoulModels.FORGE),mc.getEntityModels().bakeLayer(SoulModels.BRAIN),mc.getEntityModels().bakeLayer(SoulModels.BRINE),0,0,0,0,pose,buffers,light,overlay);
                    }
                };}return renderer;
            }
        },Content.INFUSER.get().asItem(),Content.VIS_DYNAMO.get().asItem(),Content.ESSENTIA_DYNAMO.get().asItem(),Vats.MATRIX.get().asItem(),Vats.BEACON.get().asItem(),Souls.SIEVE.get().asItem(),Souls.INSPIRATRON.get().asItem(),Souls.FORGE.get().asItem(),NodeMonitor.BLOCK.get().asItem(),SyntheticNode.BLOCK.get().asItem(),Recombinator.BLOCK.get().asItem(),Spikes.BLOCKS.get("metal").get().asItem(),Spikes.BLOCKS.get("wooden").get().asItem(),Spikes.BLOCKS.get("tooth").get().asItem(),RiftDevices.AMPLIFIER.get().asItem(),RiftDevices.STABILIZER.get().asItem(),Gateways.SLOT.get().asItem(),Content.item("disposable_wand"));
    }
}
