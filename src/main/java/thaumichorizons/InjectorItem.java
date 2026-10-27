package thaumichorizons;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;

/** Seven independent cartridges; only injected mixtures and blast phials are ammunition. */
public final class InjectorItem extends CrossbowItem {
    public InjectorItem(Properties properties){super(properties);}
    public static boolean isAmmo(ItemStack stack){return stack.is(Content.item("injection"))||stack.is(Content.item("blast_phial"));}
    public static NonNullList<ItemStack> magazine(ItemStack weapon){var result=NonNullList.withSize(7,ItemStack.EMPTY);weapon.getOrDefault(DataComponents.CONTAINER,ItemContainerContents.EMPTY).copyInto(result);return result;}
    public static void saveMagazine(ItemStack weapon,java.util.List<ItemStack> ammo){weapon.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(ammo));}
    static int next(java.util.List<ItemStack> ammo){for(int i=0;i<ammo.size();i++)if(isAmmo(ammo.get(i))&&!ammo.get(i).isEmpty())return i;return -1;}
    public static void open(Player player,InteractionHand hand){
        if(player instanceof ServerPlayer server&&player.isAlive()&&!player.isSpectator()&&player.getItemInHand(hand).getItem() instanceof InjectorItem)
            server.openMenu(new SimpleMenuProvider((id,inv,p)->new InjectorMenu(id,inv,hand),Component.translatable("container.thaumichorizons.injector")),buf->buf.writeEnum(hand));
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        var weapon=player.getItemInHand(hand);
        if(player.isShiftKeyDown()){if(!level.isClientSide())open(player,hand);return InteractionResultHolder.sidedSuccess(weapon,level.isClientSide());}
        if(isCharged(weapon)){if(!level.isClientSide())fireLoaded(weapon,level,player);return InteractionResultHolder.consume(weapon);}
        if(next(magazine(weapon))<0)return InteractionResultHolder.fail(weapon);
        player.startUsingItem(hand);return InteractionResultHolder.consume(weapon);
    }
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return getChargeDuration(stack,entity)+3;}
    @Override public java.util.function.Predicate<ItemStack> getSupportedHeldProjectiles(){return InjectorItem::isAmmo;}
    @Override public java.util.function.Predicate<ItemStack> getAllSupportedProjectiles(){return InjectorItem::isAmmo;}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity entity,int slot,boolean selected){
        if(!level.isClientSide()&&entity instanceof Player player&&stack.has(DataComponents.CHARGED_PROJECTILES)){
            var loaded=stack.get(DataComponents.CHARGED_PROJECTILES);var valid=new java.util.ArrayList<ItemStack>();for(var cartridge:loaded.getItems())if(isAmmo(cartridge))valid.add(cartridge);else MedicalItem.give(player,cartridge.copy());
            if(valid.size()!=loaded.getItems().size()){if(valid.isEmpty())stack.remove(DataComponents.CHARGED_PROJECTILES);else stack.set(DataComponents.CHARGED_PROJECTILES,net.minecraft.world.item.component.ChargedProjectiles.of(valid));}
        }
    }
    @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){super.onUseTick(level,entity,stack,remaining);}
    @Override public void releaseUsing(ItemStack weapon,Level level,LivingEntity entity,int remaining){
        if(level.isClientSide()||!(entity instanceof Player player)||isCharged(weapon)||getUseDuration(weapon,entity)-remaining<getChargeDuration(weapon,entity))return;
        var ammo=magazine(weapon);int slot=next(ammo);if(slot<0)return;var cartridge=ammo.get(slot).copyWithCount(1);
        weapon.set(DataComponents.CHARGED_PROJECTILES,net.minecraft.world.item.component.ChargedProjectiles.of(cartridge));ammo.get(slot).shrink(1);
        var rotated=NonNullList.withSize(7,ItemStack.EMPTY);for(int i=0;i<7;i++)rotated.set(i,ammo.get((slot+1+i)%7));saveMagazine(weapon,rotated);
        level.playSound(null,player.blockPosition(),SoundEvents.CROSSBOW_LOADING_END.value(),SoundSource.PLAYERS,1,1);
    }
    private void fireLoaded(ItemStack weapon,Level level,Player player){
        var loaded=weapon.get(DataComponents.CHARGED_PROJECTILES);if(loaded==null||loaded.isEmpty())return;var cartridge=loaded.getItems().getFirst();if(!isAmmo(cartridge))return;
        Projectile shot;
        if(cartridge.is(Content.item("injection"))){
            var syringe=new SyringeProjectile(player,level,cartridge,weapon);syringe.setBaseDamage(1.5);syringe.setCritArrow(true);
            int power=enchantment(weapon,level,Enchantments.POWER);if(power>0)syringe.setBaseDamage(1.5+.5*power+.5);
            if(enchantment(weapon,level,Enchantments.FLAME)>0)syringe.igniteForSeconds(100);shot=syringe;
        }else{var phial=new BlastPhial(Content.PHIAL.get(),level);phial.setOwner(player);phial.setItem(cartridge);phial.setPos(player.getX(),player.getEyeY()-.1,player.getZ());shot=phial;}
        shot.shootFromRotation(player,player.getXRot(),player.getYRot(),0,3,1);if(!level.addFreshEntity(shot))return;
        weapon.remove(DataComponents.CHARGED_PROJECTILES);var hand=player.getMainHandItem()==weapon?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
        weapon.hurtAndBreak(1,player,hand==InteractionHand.MAIN_HAND?EquipmentSlot.MAINHAND:EquipmentSlot.OFFHAND);
        level.playSound(null,player.blockPosition(),SoundEvents.CROSSBOW_SHOOT,SoundSource.PLAYERS,1,1/(level.random.nextFloat()*.4F+1.2F)+.5F);
    }
    static int enchantment(ItemStack stack,Level level,net.minecraft.resources.ResourceKey<Enchantment> key){return EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(key),stack);}
    @Override public boolean isValidRepairItem(ItemStack stack,ItemStack material){return material.is(thaumcraft.registry.ModItems.THAUMIUM_INGOT.get());}
    @Override public int getEnchantmentValue(){return 3;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,java.util.List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);long count=magazine(stack).stream().filter(s->!s.isEmpty()&&isAmmo(s)).count();lines.add(Component.translatable("thaumichorizons.injector.ammo",count));lines.add(Component.translatable("thaumichorizons.injector.open"));}
}
