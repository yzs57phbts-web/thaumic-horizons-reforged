package thaumichorizons;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public final class SyringeProjectile extends AbstractArrow implements ItemSupplier {
    public SyringeProjectile(EntityType<? extends SyringeProjectile> type,Level level){super(type,level);}
    public SyringeProjectile(LivingEntity owner,Level level,ItemStack ammo,ItemStack weapon){super(Content.SYRINGE.get(),owner,level,ammo,weapon);pickup=Pickup.ALLOWED;}
    @Override protected ItemStack getDefaultPickupItem(){return new ItemStack(Content.item("injection"));}
    @Override public ItemStack getItem(){return getPickupItemStackOrigin();}
    @Override protected void doPostHurtEffects(LivingEntity target){super.doPostHurtEffects(target);MedicalItem.apply(getPickupItemStackOrigin(),target,getOwner()==null?this:getOwner());}
}
