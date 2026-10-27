package thaumichorizons;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import thaumcraft.platform.*;

public final class Lenses {
    public static final List<String> TYPES=List.of("fire","water","earth","air","order_entropy");
    public static final DeferredBlock<Block> EVANESCENT=Content.BLOCKS.registerSimpleBlock("evanescent",BlockBehaviour.Properties.of().strength(-1,Float.MAX_VALUE).noOcclusion().noLootTable());
    static void init(){
        for(String type:TYPES)Content.ALL.put("lens_"+type,Content.ITEMS.registerItem("lens_"+type,p->new Lens(type,p),new Item.Properties()));Content.ALL.put("lens_case",Content.ITEMS.registerItem("lens_case",Case::new,new Item.Properties().stacksTo(1).component(DataComponents.CONTAINER,ItemContainerContents.EMPTY)));Content.ITEMS.registerSimpleBlockItem("evanescent",EVANESCENT);
        ModNetwork.toServer(Change.TYPE,Change.CODEC,(payload,player)->change(player,payload.lens));
    }
    public static final class Lens extends Item {public final String type;Lens(String type,Properties properties){super(properties);this.type=type;}}
    public static final class Case extends Item implements Bauble {
        Case(Properties properties){super(properties);}
        @Override public BaubleType getBaubleType(ItemStack stack){return BaubleType.BELT;}
        @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){var stack=player.getItemInHand(hand);if(player instanceof ServerPlayer server)server.openMenu(new SimpleMenuProvider((id,inv,p)->new LensCaseMenu(id,inv,hand),stack.getHoverName()),buf->buf.writeEnum(hand));return InteractionResultHolder.sidedSuccess(stack,level.isClientSide());}
    }
    static NonNullList<ItemStack> contents(ItemStack stack){var contents=NonNullList.withSize(18,ItemStack.EMPTY);stack.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY).copyInto(contents);return contents;}
    static void contents(ItemStack stack,List<ItemStack> contents){stack.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(contents));}
    public static ItemStack goggles(Player player){var stack=player.getItemBySlot(EquipmentSlot.HEAD);return stack.getItem() instanceof thaumcraft.node.Revealer?stack:ItemStack.EMPTY;}
    public static String installed(ItemStack goggles){String type=goggles.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getString("thArcaneLens");return TYPES.contains(type)?type:"";}
    private static ItemStack installedStack(ItemStack goggles,Player player){var data=goggles.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();var stack=ItemStack.parseOptional(player.registryAccess(),data.getCompound("thArcaneLensStack"));String type=installed(goggles);return stack.getItem() instanceof Lens?stack.copyWithCount(1):type.isEmpty()?ItemStack.EMPTY:new ItemStack(Content.item("lens_"+type));}
    private static void install(ItemStack goggles,ItemStack lens,Player player){var data=goggles.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();if(lens.getItem() instanceof Lens item){data.putString("thArcaneLens",item.type);data.put("thArcaneLensStack",lens.copyWithCount(1).save(player.registryAccess()));}else{data.remove("thArcaneLens");data.remove("thArcaneLensStack");}goggles.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
    private interface Location {ItemStack get();void set(ItemStack stack);}
    private static List<Location> locations(Player player,boolean includeCases){
        var list=new ArrayList<Location>();var cases=new ArrayList<ItemStack>();var curios=top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player).orElse(null);if(curios!=null)for(var handler:curios.getCurios().values()){var stacks=handler.getStacks();for(int i=0;i<stacks.getSlots();i++)if(stacks.getStackInSlot(i).getItem() instanceof Case)cases.add(stacks.getStackInSlot(i));}
        for(int i=0;i<36;i++){final int slot=i;var stack=player.getInventory().getItem(i);if(stack.getItem() instanceof Case)cases.add(stack);else list.add(new Location(){public ItemStack get(){return player.getInventory().getItem(slot);}public void set(ItemStack value){player.getInventory().setItem(slot,value);}});}
        if(includeCases){var inCases=new ArrayList<Location>();for(var pouch:cases)for(int i=0;i<18;i++){final int slot=i;inCases.add(new Location(){public ItemStack get(){return contents(pouch).get(slot);}public void set(ItemStack stack){var content=contents(pouch);content.set(slot,stack);contents(pouch,content);}});}inCases.addAll(list);return inCases;}return list;
    }
    public static List<String> available(Player player){var types=new TreeSet<String>();for(var location:locations(player,true))if(location.get().getItem() instanceof Lens lens)types.add(lens.type);return List.copyOf(types);}
    public static boolean change(Player player,String type){
        if(player.level().isClientSide()||player.containerMenu!=player.inventoryMenu)return false;var goggles=goggles(player);if(goggles.isEmpty()||!(TYPES.contains(type)||type.equals("REMOVE")))return false;var previous=installedStack(goggles,player);if(type.equals("REMOVE")&&previous.isEmpty()||!type.equals("REMOVE")&&type.equals(installed(goggles)))return false;
        var places=locations(player,true);Location selected=null;ItemStack next=ItemStack.EMPTY;
        if(!type.equals("REMOVE")){for(var place:places)if(place.get().getItem() instanceof Lens lens&&lens.type.equals(type)){selected=place;next=place.get().copyWithCount(1);break;}if(selected==null)return false;}
        if(selected!=null){var left=selected.get().copy();left.shrink(1);selected.set(left);}
        if(!previous.isEmpty()){
            Location target=null;for(var place:places){var existing=place.get();if(existing.isEmpty()||ItemStack.isSameItemSameComponents(existing,previous)&&existing.getCount()<existing.getMaxStackSize()){target=place;break;}}
            if(target==null){if(selected!=null){var restored=selected.get().copy();if(restored.isEmpty())restored=next.copy();else restored.grow(1);selected.set(restored);}return false;}
            var combined=target.get().copy();if(combined.isEmpty())combined=previous;else combined.grow(1);target.set(combined);
        }
        install(goggles,next,player);player.getInventory().setChanged();player.inventoryMenu.broadcastChanges();player.level().playSound(null,player.blockPosition(),thaumcraft.registry.ModSounds.CAMERATICKS.get(),SoundSource.PLAYERS,.3F,type.equals("REMOVE")?.9F:1);return true;
    }
    public record Change(String lens) implements CustomPacketPayload {
        public static final Type<Change> TYPE=new Type<>(ThaumicHorizons.id("change_lens"));public static final StreamCodec<RegistryFriendlyByteBuf,Change> CODEC=StreamCodec.composite(ByteBufCodecs.stringUtf8(32),Change::lens,Change::new);
        @Override public Type<Change> type(){return TYPE;}
    }
}
