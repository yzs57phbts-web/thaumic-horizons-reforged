package thaumichorizons;
import java.util.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import thaumcraft.client.aspect.*;
import thaumcraft.client.platform.GuiBlit;
import thaumcraft.client.research.ThaumometerClient;
import thaumcraft.research.ScanManager;

@EventBusSubscriber(modid=ThaumicHorizons.ID,value=Dist.CLIENT)
public final class LensClient {
    static final KeyMapping CHANGE=new KeyMapping("key.thaumichorizons.lens",86,"key.categories.thaumichorizons");
    private static final Map<BlockPos,BlockState> HIDDEN=new LinkedHashMap<>();
    private static ClientLevel hiddenLevel;
    private static String scanning="";
    private static int scanFrames;
    @SubscribeEvent public static void beforeTick(ClientTickEvent.Pre event){restore();}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null)return;while(CHANGE.consumeClick())if(mc.screen==null&&!Lenses.goggles(mc.player).isEmpty()){if(mc.player.isShiftKeyDown())PacketDistributor.sendToServer(new Lenses.Change("REMOVE"));else mc.setScreen(new LensRadialScreen());}
        String type=Lenses.installed(Lenses.goggles(mc.player));boolean vision=type.equals("fire")&&!mc.player.isUnderWater()||type.equals("water")&&mc.player.isUnderWater();var current=mc.player.getEffect(MobEffects.NIGHT_VISION);boolean own=current instanceof Vision effect&&effect.owned;
        if(vision&&(current==null||own&&current.getDuration()<242))mc.player.addEffect(new Vision());else if(!vision&&own)mc.player.removeEffect(MobEffects.NIGHT_VISION);
        if(type.equals("earth")&&mc.screen==null&&mc.hitResult instanceof BlockHitResult hit&&hit.getType()==HitResult.Type.BLOCK){
            hiddenLevel=mc.level;var center=hit.getBlockPos();var axis=hit.getDirection().getAxis();for(int i=-1;i<=1;i++)for(int j=-1;j<=1;j++){var pos=axis==net.minecraft.core.Direction.Axis.Y?center.offset(i,0,j):axis==net.minecraft.core.Direction.Axis.X?center.offset(0,i,j):center.offset(i,j,0);var state=mc.level.getBlockState(pos);if(!state.isAir()&&!state.hasBlockEntity()&&state.getLightEmission(mc.level,pos)==0&&!state.getCollisionShape(mc.level,pos).isEmpty()&&!state.is(Lenses.EVANESCENT.get())){HIDDEN.put(pos,state);mc.level.setBlock(pos,Lenses.EVANESCENT.get().defaultBlockState(),18);}}
        }
    }
    static void restore(){if(hiddenLevel!=null)for(var entry:HIDDEN.entrySet())if(hiddenLevel.hasChunkAt(entry.getKey())&&hiddenLevel.getBlockState(entry.getKey()).is(Lenses.EVANESCENT.get()))hiddenLevel.setBlock(entry.getKey(),entry.getValue(),18);HIDDEN.clear();hiddenLevel=null;}
    private static final class Vision extends MobEffectInstance {
        boolean owned=true;
        Vision(){super(MobEffects.NIGHT_VISION,255,0,true,false);}
        @Override public boolean update(MobEffectInstance other){boolean changed=super.update(other);if(!(other instanceof Vision))owned=false;return changed;}
    }
    @SubscribeEvent public static void overlays(RenderBlockScreenEffectEvent event){var player=event.getPlayer();if(Lenses.installed(Lenses.goggles(player)).equals("water")&&event.getOverlayType()==RenderBlockScreenEffectEvent.OverlayType.WATER||event.getOverlayType()==RenderBlockScreenEffectEvent.OverlayType.FIRE&&player.getVehicle() instanceof MagicBoats.LegacyBoat boat&&boat.metal)event.setCanceled(true);}
    static void render(GuiGraphics graphics,DeltaTracker delta){
        var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null||mc.screen!=null)return;String type=Lenses.installed(Lenses.goggles(mc.player));if(type.equals("air")&&mc.options.getCameraType().isFirstPerson())air(graphics);else if(type.equals("order_entropy"))order(graphics);else{scanning="";scanFrames=0;}
    }
    private static void order(GuiGraphics graphics){
        var mc=Minecraft.getInstance();var hit=ThaumometerClient.lookAt(mc.level,mc.player);if(hit==null||hit.target()==null){scanning="";scanFrames=0;return;}var target=hit.target();String key=hit.kind()+"/"+hit.pos()+"/"+hit.entityId()+"/"+target.key();boolean known=ScanManager.hasBeenScanned(mc.player,target);if(!key.equals(scanning)){scanning=key;scanFrames=0;}
        Component name=Component.literal("?");if(known){if(hit.entityId()!=0&&mc.level.getEntity(hit.entityId())!=null)name=mc.level.getEntity(hit.entityId()).getName();else name=mc.level.getBlockState(hit.pos()).getBlock().getName();}
        int centerX=graphics.guiWidth()/2,centerY=graphics.guiHeight()/2;graphics.drawString(mc.font,name,centerX-mc.font.width(name)/2,centerY-16,0xFFFFFFFF,true);
        if(known&&target.aspects()!=null){var aspects=target.aspects().getAspects();for(int i=0;i<aspects.size();i++){var aspect=aspects.get(i);int count=Math.min(5,aspects.size()-i/5*5),x=centerX-count*18/2+i%5*18,y=centerY+16+i/5*18;if(AspectVisibility.isKnown(aspect))AspectRenderer.drawTag(graphics,x,y,aspect,target.aspects().getAmount(aspect),0,.5F,false);else AspectRenderer.drawUnknown(graphics,x,y);}}
        if(!known&&ScanManager.validScan(target.aspects(),mc.player)&&++scanFrames>=250){PacketDistributor.sendToServer(new thaumcraft.network.ScanPayload(hit.kind(),hit.pos(),hit.entityId()));scanFrames=0;}else if(!known&&scanFrames>0&&scanFrames%20==0)mc.level.playLocalSound(mc.player.getX(),mc.player.getY(),mc.player.getZ(),thaumcraft.registry.ModSounds.CAMERATICKS.get(),net.minecraft.sounds.SoundSource.PLAYERS,.2F,.45F+mc.level.random.nextFloat()*.1F,false);
    }
    private static void air(GuiGraphics graphics){
        var mc=Minecraft.getInstance();var player=mc.player;var look=player.getLookAngle();var right=look.cross(new Vec3(.0001,-1,.0001)).normalize();var up=right.cross(look).normalize();double fov=Math.tan(Math.toRadians(mc.options.fov().get())/2)*2;
        for(var entity:mc.level.getEntitiesOfClass(LivingEntity.class,player.getBoundingBox().inflate(24),e->e!=player&&e.isAlive())){
            var delta=entity.position().add(0,entity.getBbHeight()/2,0).subtract(player.getEyePosition());double dot=delta.dot(look),distance=delta.length();if(dot<=.001||distance<.1)continue;var rejection=delta.subtract(look.scale(dot));double horiz=rejection.dot(right)/(dot*fov),vert=rejection.dot(up)/(dot*fov)/graphics.guiHeight()*graphics.guiWidth();double cx=graphics.guiWidth()*(1+horiz)/2,cy=graphics.guiHeight()*(1-vert)/2,size=Math.min(entity.getBbWidth(),entity.getBbHeight())*Math.min(graphics.guiWidth(),graphics.guiHeight())/distance;
            if(entity.getType().is(EntityTypeTags.UNDEAD)){int color=((int)(Math.max(0,.1-distance/240)*255)<<24)|0xFFFFFF;graphics.pose().pushPose();graphics.pose().translate(cx,cy,0);graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(entity.tickCount%360));int radius=(int)Math.min(256,size*2);blit(graphics,"vortex",-radius,-radius,radius*2,color);graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-2*(entity.tickCount%360)));blit(graphics,"vortex",-radius/2,-radius/2,radius,color);graphics.pose().popPose();}
            else{int color=((int)(Math.max(0,.3-distance/80)*255)<<24)|0xFFFFFF,count=Math.min(4,(int)(48/distance+1));for(int i=0;i<count;i++){int radius=(int)Math.min(256,size*((i*16/(count+1)+entity.tickCount%16)%16)/12);if(radius>0)blit(graphics,"ripple",(int)cx-radius,(int)cy-radius,radius*2,color);}}
        }
    }
    private static void blit(GuiGraphics graphics,String name,int x,int y,int size,int color){GuiBlit.blit(graphics,ThaumicHorizons.id("textures/fx/"+name+".png"),x,y,size,size,0,0,32,32,32,32,color);}
}
