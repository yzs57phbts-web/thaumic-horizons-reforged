package thaumichorizons;
import com.mojang.blaze3d.platform.NativeImage;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

final class GolemSkins {
    private static final Map<BlockState,ResourceLocation> CACHE=new HashMap<>();
    private static final int[][] REGIONS={{1,8,8,8,0},{1,16,11,11,40},{1,9,6,6,70},{1,6,5,42,0},{1,4,6,66,21},{1,16,16,112,48},{0,8,8,16,0},{0,16,11,27,40},{0,9,6,15,70},{0,6,5,48,0},{0,4,6,70,21},{2,8,9,0,8},{2,11,12,0,51},{2,6,5,0,76},{2,5,16,37,5},{2,6,25,60,27},{2,16,16,88,48},{3,8,9,16,8},{3,11,12,27,51},{3,6,5,17,76},{3,5,16,48,5},{3,6,25,70,27},{4,8,9,8,8},{4,17,12,11,51},{4,9,5,6,76},{4,6,16,42,5},{4,4,25,66,27},{4,16,16,88,64},{5,8,9,24,8},{5,17,12,37,51},{5,9,5,21,76},{5,6,16,53,5},{5,4,25,76,27}};
    static ResourceLocation get(BlockState state){return state.isAir()?ThaumicHorizons.id("textures/models/golem_void.png"):CACHE.computeIfAbsent(state,GolemSkins::create);}
    static void clear(){var textures=Minecraft.getInstance().getTextureManager();for(var location:CACHE.values())if(location.getNamespace().equals(ThaumicHorizons.ID)&&location.getPath().startsWith("dynamic/"))textures.release(location);CACHE.clear();}
    private static ResourceLocation create(BlockState state){
        try{
            BufferedImage[] faces=new BufferedImage[6];
            var mc=Minecraft.getInstance();var model=mc.getBlockRenderer().getBlockModel(state);
            for(int face=0;face<6;face++){
                var quads=model.getQuads(state,Direction.from3DDataValue(face),net.minecraft.util.RandomSource.create(0));var sprite=quads.isEmpty()?model.getParticleIcon():quads.getFirst().getSprite();
                var name=sprite.contents().name();var location=ResourceLocation.fromNamespaceAndPath(name.getNamespace(),"textures/"+name.getPath()+".png");
                if(face==1&&state.is(Blocks.GRASS_BLOCK))location=ThaumicHorizons.id("textures/blocks/grasscolorized.png");
                try(var input=mc.getResourceManager().open(location)){faces[face]=ImageIO.read(input);}
                int width=Math.min(faces[face].getWidth(),sprite.contents().width()),height=Math.min(faces[face].getHeight(),sprite.contents().height());
                faces[face]=faces[face].getSubimage(0,0,width,height);
                if(state.getBlock() instanceof CakeBlock)faces[face]=faces[face].getSubimage(1,face<2?1:8,14,face<2?14:8);
            }
            var image=new BufferedImage(128,128,BufferedImage.TYPE_INT_ARGB);
            for(var region:REGIONS){int face=region[0],width=region[1],height=region[2];var scaled=faces[face].getScaledInstance(width,height,Image.SCALE_SMOOTH);var resized=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);var graphics=resized.createGraphics();graphics.drawImage(scaled,0,0,null);graphics.dispose();image.setRGB(region[3],region[4],width,height,resized.getRGB(0,0,width,height,null,0,width),0,width);}
            int[] eyes={-16777216,-16316917,-16185587,-15593446};image.setRGB(9,12,2,2,eyes,0,2);image.setRGB(13,12,2,2,eyes,0,2);
            var encoded=new ByteArrayOutputStream();ImageIO.write(image,"PNG",encoded);var texture=new DynamicTexture(NativeImage.read(new ByteArrayInputStream(encoded.toByteArray())));
            var location=ThaumicHorizons.id("dynamic/golem/"+Block.getId(state));mc.getTextureManager().register(location,texture);return location;
        }catch(Exception exception){org.slf4j.LoggerFactory.getLogger("ThaumicHorizons").warn("Could not build golem skin for {}",state,exception);return ResourceLocation.parse("thaumcraft:textures/model/golem_wood.png");}
    }
}
