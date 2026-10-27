package thaumichorizons;
import java.util.*;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import thaumcraft.aspect.*;

/** Original 1.7.10 aspect weights and potency/duration calculation. */
public final class BloodFormula {
    public static final Map<Aspect,Map<Integer,Integer>> weights=new LinkedHashMap<>();
    private static final Map<Integer,Float> duration=new HashMap<>();
    static {
        weights.put(Aspect.AIR, Map.of(1,3,8,4,13,3));
        weights.put(Aspect.EARTH, Map.of(2,3,3,4,5,1,11,2));
        weights.put(Aspect.FIRE, Map.of(1,1,5,2,12,6,16,1));
        weights.put(Aspect.WATER, Map.of(1,2,3,2,10,1,12,2,13,3));
        weights.put(Aspect.ORDER, Map.of(2,2,4,3,11,3,21,2));
        weights.put(Aspect.ENTROPY, Map.of(7,1,9,1,17,1,18,2,19,2,20,3));
        weights.put(Aspect.VOID, Map.of(14,4,17,2,27,4));
        weights.put(Aspect.LIGHT, Map.of(15,2,16,8));
        weights.put(Aspect.WEATHER, Map.of(1,1,8,2,15,1,28,8));
        weights.put(Aspect.MOTION, Map.of(1,5,8,5));
        weights.put(Aspect.COLD, Map.of(2,2,4,2,11,2,14,2,18,2));
        weights.put(Aspect.CRYSTAL, Map.of(4,2,5,2,14,6));
        weights.put(Aspect.LIFE, Map.of(6,2,10,6,21,2));
        weights.put(Aspect.POISON, Map.of(4,1,9,1,17,1,19,6,18,1));
        weights.put(Aspect.ENERGY, Map.of(1,2,3,2,5,1,8,1,26,1,28,3));
        weights.put(Aspect.EXCHANGE, Map.of(1,2,3,2,8,1,18,2,27,3));
        weights.put(Aspect.METAL, Map.of(2,2,3,2,5,2,11,4));
        weights.put(Aspect.DEATH, Map.of(7,6,9,1,17,1,18,1,20,1));
        weights.put(Aspect.FLIGHT, Map.of(1,2,8,8));
        weights.put(Aspect.DARKNESS, Map.of(14,4,15,5,16,1));
        weights.put(Aspect.SOUL, Map.of(6,2,10,1,14,3,16,2,21,1,26,1));
        weights.put(Aspect.HEAL, Map.of(6,6,10,2,21,2));
        weights.put(Aspect.TRAVEL, Map.of(1,8,8,2));
        weights.put(Aspect.ELDRITCH, Map.of(14,2,15,2,16,2,25,2,26,2));
        weights.put(Aspect.MAGIC, Map.of(25,5,26,5));
        weights.put(Aspect.AURA, Map.of(26,10));
        weights.put(Aspect.TAINT, Map.of(25,2,20,2,24,6));
        weights.put(Aspect.SLIME, Map.of(2,4,4,2,9,2,19,2));
        weights.put(Aspect.PLANT, Map.of(23,2,29,8));
        weights.put(Aspect.TREE, Map.of(2,2,11,2,29,6));
        weights.put(Aspect.BEAST, Map.of(1,1,5,5,10,1,13,1,16,1,21,1));
        weights.put(Aspect.FLESH, Map.of(10,2,21,2,23,6));
        weights.put(Aspect.UNDEAD, Map.of(17,2,20,2,7,6));
        weights.put(Aspect.MIND, Map.of(16,2,25,3,26,3,27,2));
        weights.put(Aspect.SENSES, Map.of(14,4,16,6));
        weights.put(Aspect.MAN, Map.of(6,3,10,3,21,4));
        weights.put(Aspect.CROP, Map.of(23,6,29,4));
        weights.put(Aspect.MINE, Map.of(3,10));
        weights.put(Aspect.TOOL, Map.of(1,1,3,8,11,1));
        weights.put(Aspect.HARVEST, Map.of(3,2,6,1,23,5,29,2));
        weights.put(Aspect.WEAPON, Map.of(3,2,5,8));
        weights.put(Aspect.ARMOR, Map.of(11,8,12,2));
        weights.put(Aspect.HUNGER, Map.of(18,1,23,8,27,1));
        weights.put(Aspect.GREED, Map.of(23,2,27,8));
        weights.put(Aspect.CRAFT, Map.of(4,2,6,2,11,2,21,4));
        weights.put(Aspect.CLOTH, Map.of(4,2,11,4,15,2,18,2));
        weights.put(Aspect.MECHANISM, Map.of(1,2,3,4,5,4));
        weights.put(Aspect.TRAP, Map.of(2,8,4,2));
        duration.put(1,1.2F);
        duration.put(2,0.8F);
        duration.put(3,1.2F);
        duration.put(4,0.8F);
        duration.put(5,1.2F);
        duration.put(8,1.2F);
        duration.put(9,0.2F);
        duration.put(10,0.6F);
        duration.put(11,1.2F);
        duration.put(12,1.2F);
        duration.put(13,1.2F);
        duration.put(14,1.2F);
        duration.put(15,0.2F);
        duration.put(16,1.2F);
        duration.put(17,0.4F);
        duration.put(18,0.8F);
        duration.put(19,0.6F);
        duration.put(20,0.4F);
        duration.put(21,1.2F);
        duration.put(23,0.4F);
        duration.put(24,0.6F);
        duration.put(25,1.2F);
        duration.put(26,0.6F);
        duration.put(27,1.2F);
        duration.put(28,0.8F);
        duration.put(29,1.5F);
    }
    public static Holder<MobEffect> effect(int id) {
        return switch(id){
            case 1->MobEffects.MOVEMENT_SPEED; case 2->MobEffects.MOVEMENT_SLOWDOWN;
            case 3->MobEffects.DIG_SPEED; case 4->MobEffects.DIG_SLOWDOWN;
            case 5->MobEffects.DAMAGE_BOOST; case 6->MobEffects.HEAL; case 7->MobEffects.HARM;
            case 8->MobEffects.JUMP; case 9->MobEffects.CONFUSION; case 10->MobEffects.REGENERATION;
            case 11->MobEffects.DAMAGE_RESISTANCE; case 12->MobEffects.FIRE_RESISTANCE;
            case 13->MobEffects.WATER_BREATHING; case 14->MobEffects.INVISIBILITY;
            case 15->MobEffects.BLINDNESS; case 16->MobEffects.NIGHT_VISION;
            case 17->MobEffects.HUNGER; case 18->MobEffects.WEAKNESS; case 19->MobEffects.POISON;
            case 20->MobEffects.WITHER; case 21->MobEffects.HEALTH_BOOST; case 23->MobEffects.SATURATION;
            case 24->thaumcraft.registry.ModEffects.TAINT_POISON; case 25->thaumcraft.registry.ModEffects.VIS_EXHAUST;
            case 26->Content.VIS_REGEN; case 27->Content.VACUUM; case 28->Content.SHOCK;
            case 29->Content.SYNTHESIS; default->throw new IllegalArgumentException("Unknown effect "+id);
        };
    }
    public record Result(List<MobEffectInstance> effects,int color) {}
    public static Result calculate(AspectList selected){
        Map<Integer,Integer> values=new HashMap<>();int total=0,r=0,g=0,b=0;
        for(Aspect a:selected.getAspects()){
            if(!weights.containsKey(a))continue;
            int count=selected.getAmount(a);if(count<=0)continue;
            weights.get(a).forEach((id,w)->values.merge(id,w*count,Integer::sum));
            total+=count;r+=((a.getColor()>>16)&255)*count;g+=((a.getColor()>>8)&255)*count;b+=(a.getColor()&255)*count;
        }
        int color=total==0?0:((r/total)<<16)|((g/total)<<8)|(b/total);
        List<MobEffectInstance> result=new ArrayList<>();
        for(int i=0;i<(total+1)/2&&!values.isEmpty();i++){
            int best=0,weight=0;
            for(var entry:values.entrySet())if(entry.getValue()>weight){best=entry.getKey();weight=entry.getValue();}
            int ticks=(best==6||best==7)?1:(int)(100*weight*duration.get(best));
            result.add(new MobEffectInstance(effect(best),ticks,weight/30,false,true));values.remove(best);
        }
        return new Result(List.copyOf(result),color);
    }
    public static ItemStack create(AspectList selected,boolean phial){
        Result result=calculate(selected);ItemStack stack=new ItemStack(Content.item(phial?"blast_phial":"injection"));
        stack.set(DataComponents.POTION_CONTENTS,new PotionContents(Optional.empty(),Optional.of(result.color()),result.effects()));return stack;
    }
}
