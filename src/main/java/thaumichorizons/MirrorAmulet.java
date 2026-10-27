package thaumichorizons;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import thaumcraft.aspect.*;
import thaumcraft.crafting.*;
import thaumcraft.device.*;
import thaumcraft.platform.*;
import thaumcraft.research.*;
import top.theillusivec4.curios.api.CuriosApi;

public final class MirrorAmulet extends HandMirrorItem implements Bauble,thaumcraft.item.gear.RunicArmor {
    MirrorAmulet(Properties properties){super(properties);}
    static void init(){Content.ALL.put("mirror_amulet",Content.ITEMS.registerItem("mirror_amulet",MirrorAmulet::new,new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));}
    @Override public BaubleType getBaubleType(ItemStack stack){return BaubleType.AMULET;}
    @Override public int getRunicCharge(ItemStack stack){return 0;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){return InteractionResultHolder.pass(player.getItemInHand(hand));}
    static void research(){
        TcRecipes.register(new InfusionRecipe("TH:recipeAmuletMirror","mirrorAmulet",TcStack.of("thaumichorizons:mirror_amulet",1),6,new AspectList().add(Aspect.DEATH,16).add(Aspect.ENTROPY,32).add(Aspect.VOID,32),TcIngredient.item("thaumcraft:primordial_pearl"),java.util.List.of(TcIngredient.item("thaumcraft:mundane_amulet"),TcIngredient.item("minecraft:map"),TcIngredient.item("thaumcraft:magic_mirror"))));
        new ResearchItem("mirrorAmulet","ThaumicHorizons",new AspectList().add(Aspect.DEATH,5).add(Aspect.ENTROPY,5).add(Aspect.VOID,5),17,4,2,TcStack.of("thaumichorizons:mirror_amulet",1)).setParents("cloneSelf").setParentsHidden("MIRROR").setConcealed().setPages(ResearchPage.text("mirrorAmulet1"),ResearchPage.infusion("TH:recipeAmuletMirror")).registerResearchItem();
    }
    static boolean rescue(Player player){
        var inventory=CuriosApi.getCuriosInventory(player).orElse(null);if(inventory==null)return false;ItemStack amulet=ItemStack.EMPTY;
        for(var handler:inventory.getCurios().values()){var stacks=handler.getStacks();for(int i=0;i<stacks.getSlots();i++)if(stacks.getStackInSlot(i).getItem() instanceof MirrorAmulet){amulet=stacks.getStackInSlot(i);break;}if(!amulet.isEmpty())break;}
        if(amulet.isEmpty())return false;boolean sent=false;
        for(int i=0;i<player.getInventory().getContainerSize();i++){var stack=player.getInventory().getItem(i);if(!stack.isEmpty()&&transport(amulet,stack,player)){player.getInventory().setItem(i,ItemStack.EMPTY);sent=true;}}
        for(var handler:inventory.getCurios().values())for(var stacks:java.util.List.of(handler.getStacks(),handler.getCosmeticStacks()))for(int i=0;i<stacks.getSlots();i++){var stack=stacks.getStackInSlot(i);if(!stack.isEmpty()&&stack!=amulet&&transport(amulet,stack,player)){stacks.setStackInSlot(i,ItemStack.EMPTY);sent=true;}}
        if(!sent)return false;
        amulet.shrink(1);player.getInventory().setChanged();player.inventoryMenu.broadcastChanges();var pearl=new Alchemite.InvulnerableDrop(Alchemite.DROPS.get(),player.level());pearl.setUnlimitedLifetime();pearl.setPos(player.position());pearl.setItem(new ItemStack(thaumcraft.registry.EldritchContent.PRIMORDIAL_PEARL.get()));pearl.setDefaultPickUpDelay();player.level().addFreshEntity(pearl);player.level().playSound(null,player.getX(),player.getEyeY(),player.getZ(),thaumcraft.registry.ModSounds.CRAFTFAIL.get(),SoundSource.PLAYERS,1,1);if(player.level() instanceof ServerLevel server)server.sendParticles(net.minecraft.core.particles.ParticleTypes.PORTAL,player.getX(),player.getEyeY(),player.getZ(),30,.4,.8,.4,.1);return true;
    }
    static boolean transport(ItemStack amulet,ItemStack stack,Player player){
        if(!(player.level() instanceof ServerLevel source)||stack.isEmpty())return false;var link=MirrorKeys.LINK.get(amulet);if(link==null)return false;var target=MirrorLinks.levelOf(source,link);if(target==null)return false;var tile=MirrorLinks.fetch(target,link.pos());if(!(tile instanceof MirrorBlockEntity mirror))return false;
        if(!target.addFreshEntity(MirrorLinks.ejected(target,link.pos(),mirror.getFacing(),stack.copy())))return false;target.blockEvent(link.pos(),mirror.getBlockState().getBlock(),1,0);return true;
    }
}
