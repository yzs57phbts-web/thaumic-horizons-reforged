package thaumichorizons;

import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.neoforged.neoforge.items.*;
import thaumcraft.aspect.*;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.infusion.PedestalBlockEntity;
import thaumcraft.research.*;

/** Four-layer legacy vat. The top centre (part 7) owns all state and inventory. */
public final class VatEntity extends thaumcraft.blockentity.TcBlockEntity implements EssentiaTransport,AspectSource,MenuProvider {
    public BlockPos masterPos;
    public int mode,progress,instability,symmetry,cycles,itemCount;
    public float shellHealth=20;
    public int[] shellInfusions=new int[12];
    public AspectList stored=new AspectList(),demand=new AspectList(),recipeCost=new AspectList();
    public LivingEntity subject;
    private CompoundTag pendingSubject;
    public UUID playerSubject,operator;
    public String recipeKey="";
    public final List<Integer> remaining=new ArrayList<>();
    public final List<BlockPos> pedestals=new ArrayList<>();
    public BlockPos suckingFrom;
    public int essentiaColor;
    public long effectTime;
    public boolean dismantling;
    private boolean loading;
    private int clock;
    static java.util.function.Consumer<VatEntity> clientTick=vat->{};
    public final ItemStackHandler inventory=new ItemStackHandler(2){
        @Override protected void onContentsChanged(int slot){if(!loading)sync();}
        @Override public boolean isItemValid(int slot,ItemStack s){return slot==0?(s.is(Content.item("blood_sample"))||s.is(Content.item("corpse_effigy"))||s.is(Items.BEEF)||s.is(Items.PORKCHOP)||s.is(Items.CHICKEN)):s.is(Content.item("nutrients"));}
    };
    public VatEntity(BlockPos p,BlockState s){super(Vats.TYPE.get(),p,s);}
    @Override public void onLoad(){super.onLoad();if(isController()&&level!=null&&!level.isClientSide()&&mode==2)scanPedestals();}
    public boolean isController(){return getBlockState().hasProperty(VatBlock.PART)&&getBlockState().getValue(VatBlock.PART)==7;}
    public VatEntity master(){
        if(isController())return this;
        if(level==null)return null;
        if(masterPos!=null&&level.hasChunkAt(masterPos)&&level.getBlockEntity(masterPos) instanceof VatEntity v&&v.isController())return v;
        return null;
    }
    public int part(){return getBlockState().getValue(VatBlock.PART);}
    public LivingEntity occupant(){
        if(playerSubject!=null&&level!=null)return level.getPlayerByUUID(playerSubject);
        if(subject==null&&pendingSubject!=null&&level!=null){
            Entity e=EntityType.loadEntityRecursive(pendingSubject,level,entity->entity);if(e instanceof LivingEntity living)subject=living;pendingSubject=null;
        }return subject;
    }
    public boolean hasOccupant(){return playerSubject!=null||occupant()!=null;}
    public boolean capture(LivingEntity entity){
        if(!isController()||mode!=0||hasOccupant()||entity instanceof Player)return false;
        CompoundTag tag=new CompoundTag();if(!entity.save(tag))return false;
        tag.remove("Passengers");
        Entity loaded=EntityType.loadEntityRecursive(tag,level,e->e);if(!(loaded instanceof LivingEntity living))return false;
        subject=living;subject.setPos(worldPosition.getX()+.5,worldPosition.getY()-2,worldPosition.getZ()+.5);entity.discard();sync();return true;
    }
    public void interact(Player player,InteractionHand hand,boolean direct){
        if(mode==2)return;
        ItemStack held=player.getItemInHand(hand);
        if(mode==4&&Souls.isSoul(held)){
            if(shellInfusions[1]==0){
                var villager=net.minecraft.world.entity.EntityType.VILLAGER.create(level);villager.setVillagerData(villager.getVillagerData().setProfession(Souls.profession(held)));villager.setPersistenceRequired();
                subject=villager;subject.setPos(worldPosition.getX()+.5,worldPosition.getY()-2,worldPosition.getZ()+.5);mode=0;shellInfusions=new int[12];held.shrink(1);MedicalItem.give(player,new ItemStack(thaumcraft.registry.ModBlocks.WARDED_JAR.get()));
                level.playSound(null,worldPosition,thaumcraft.registry.ModSounds.WHISPERS.get(),net.minecraft.sounds.SoundSource.BLOCKS,1,level.random.nextFloat());
                if(level instanceof ServerLevel server)server.sendParticles(net.minecraft.core.particles.ParticleTypes.END_ROD,worldPosition.getX()+.5,worldPosition.getY()-1.5,worldPosition.getZ()+.5,20,.5,.8,.5,.01);
                sync();
            }return;
        }
        if(mode==0&&!hasOccupant()&&held.is(Content.item("specimen_jar"))){
            LivingEntity loaded=Specimens.read(held,level);if(loaded!=null){subject=loaded;held.shrink(1);MedicalItem.give(player,new ItemStack(thaumcraft.registry.ModBlocks.WARDED_JAR.get()));sync();}return;
        }
        if(mode==0&&occupant()!=null&&!(occupant() instanceof Player)&&Specimens.isEmptyJar(held)){
            ItemStack jar=Specimens.write(occupant());held.shrink(1);MedicalItem.give(player,jar);subject=null;sync();return;
        }
        if(mode==0&&direct&&!hasOccupant()){
            playerSubject=player.getUUID();player.teleportTo(worldPosition.getX()+.5,worldPosition.getY()-2,worldPosition.getZ()+.5);sync();return;
        }
        if(mode==0&&player.getUUID().equals(playerSubject)){
            playerSubject=null;player.teleportTo(worldPosition.getX()+.5,worldPosition.getY()+(level.getBlockState(worldPosition.above()).is(Vats.BEACON.get())?2:1),worldPosition.getZ()+.5);sync();return;
        }
        player.openMenu(this);
    }
    public boolean intact(){
        BlockPos origin=worldPosition.offset(-1,-3,-1);
        for(BlockPos off:BlockPos.betweenClosed(0,0,0,2,3,2)){
            BlockPos p=origin.offset(off);if(!level.hasChunkAt(p))return true;
            if(!(level.getBlockEntity(p) instanceof VatEntity v)||!worldPosition.equals(v.masterPos))return false;
        }return true;
    }
    public static void tick(Level level,BlockPos p,BlockState state,VatEntity be){
        if(!be.isController())return;
        if(level.isClientSide()){if(be.progress>0)be.progress--;clientTick.accept(be);return;}
        if(be.dismantling)return;
        if(++be.clock%20==0&&!be.intact()){be.dismantle(null);return;}
        LivingEntity contained=be.occupant();
        if(contained!=null){contained.clearFire();contained.setAirSupply(300);if(contained instanceof Player player){
            if(!player.isAlive()||player.level()!=level||player.distanceToSqr(p.getCenter())>100){be.playerSubject=null;be.sync();}
            else if(be.clock%5==0)player.teleportTo(p.getX()+.5,p.getY()-2,p.getZ()+.5);
        }}
        switch(be.mode){
            case 0->be.idle();
            case 1->be.cloneCycle();
            case 2->{if(++be.cycles%10==0)be.infusionCycle();}
            case 3->be.effigyCycle();
            default->{}
        }
        if(be.mode!=2&&be.progress<=0&&be.needed()!=null)be.pull();
        if(be.progress>0){be.progress--;be.setChanged();if(be.progress%10==0)be.sync();}
    }
    private static final List<Holder<MobEffect>> CURED=List.of(MobEffects.POISON,MobEffects.BLINDNESS,MobEffects.HUNGER,MobEffects.WEAKNESS,MobEffects.WITHER,MobEffects.CONFUSION,MobEffects.DIG_SLOWDOWN,MobEffects.MOVEMENT_SLOWDOWN,thaumcraft.registry.ModEffects.BLURRED_VISION,thaumcraft.registry.ModEffects.INFECTIOUS_VIS_EXHAUST,thaumcraft.registry.ModEffects.TAINT_POISON,thaumcraft.registry.ModEffects.THAUMARHIA,thaumcraft.registry.ModEffects.VIS_EXHAUST);
    private void idle(){
        demand=new AspectList();LivingEntity e=occupant();
        if(e!=null){
            boolean undead=e.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD);Aspect strong=undead?Aspect.UNDEAD:Aspect.HEAL,weak=undead?Aspect.DEATH:Aspect.LIFE;
            if(e.getHealth()<e.getMaxHealth()){
                if(progress<=0&&stored.getAmount(strong)>0){stored.remove(strong,1);e.heal(8);progress=40;sync();}
                if(e.getHealth()<e.getMaxHealth())demand.add(strong,1);
                if(progress<=0&&stored.getAmount(weak)>0){stored.remove(weak,1);e.heal(4);progress=50;sync();}
                if(e.getHealth()<e.getMaxHealth())demand.add(weak,1);
            }
            if(CURED.stream().anyMatch(e::hasEffect)){
                if(progress<=0&&stored.getAmount(strong)>0){stored.remove(strong,1);CURED.forEach(e::removeEffect);progress=50;sync();}else demand.merge(strong,1);
            }
            if(e instanceof Player player&&player.getFoodData().needsFood()){
                if(progress<=0&&stored.getAmount(Aspect.HUNGER)>0){stored.remove(Aspect.HUNGER,1);player.getFoodData().eat(4,2);progress=50;sync();}
                if(player.getFoodData().needsFood())demand.add(Aspect.HUNGER,1);
            }
        }else{
            ItemStack sample=inventory.getStackInSlot(0);
            if(sample.is(Content.item("corpse_effigy"))){mode=3;demand=new AspectList().add(Aspect.LIFE,8).add(Aspect.HEAL,8);progress=80;sync();}
            else if(!sample.isEmpty()&&!inventory.getStackInSlot(1).isEmpty()){
                if(Vats.cloneFrom(sample,level)==null)return;
                mode=1;demand=new AspectList().add(Aspect.LIFE,4);
                if(sample.is(Content.item("blood_sample"))){var gene=sample.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getCompound("critter").getCompound("THGeneticInfusions");demand.add(BloodInfuser.loadAspects(gene.getCompound("cost")));}
                progress=40;sync();
            }
        }
    }
    private boolean hasDemand(){for(Aspect a:demand.getAspects())if(stored.getAmount(a)<demand.getAmount(a))return false;return true;}
    private void cloneCycle(){
        if(!hasOccupant()){
            if(inventory.getStackInSlot(0).isEmpty()||inventory.getStackInSlot(1).isEmpty()){resetWork();return;}
            if(!hasDemand())return;
            LivingEntity clone=Vats.cloneFrom(inventory.getStackInSlot(0),level);if(clone==null){resetWork();return;}
            subject=clone;subject.setUUID(UUID.randomUUID());subject.setPos(worldPosition.getX()+.5,worldPosition.getY()-2,worldPosition.getZ()+.5);
            inventory.extractItem(0,1,false);inventory.extractItem(1,1,false);progress=800;demand=new AspectList();stored=new AspectList();sync();
        }else if(progress<=0){mode=0;sync();}
    }
    private void effigyCycle(){
        if(!inventory.getStackInSlot(0).is(Content.item("corpse_effigy"))){resetWork();return;}
        if(progress<=0&&hasDemand()){
            inventory.extractItem(0,1,false);mode=4;shellHealth=20;demand=new AspectList();stored=new AspectList();shellInfusions=new int[12];sync();
        }
    }
    public Aspect needed(){
        if(progress>0||mode==4)return null;
        for(Aspect a:demand.getAspects())if(mode==2?demand.getAmount(a)>0:stored.getAmount(a)<demand.getAmount(a))return a;
        return null;
    }
    public boolean pull(){
        boolean drew=false;
        for(Direction direction:Direction.Plane.HORIZONTAL){
            BlockPos connector=worldPosition.below(3).relative(direction);if(!level.hasChunkAt(connector)||!(level.getBlockEntity(connector) instanceof VatEntity part)||part.part()!=4)continue;
            for(Direction face:Direction.values()){
                BlockPos p=connector.relative(face);if(!level.hasChunkAt(p))continue;
                if(level.getBlockEntity(p) instanceof EssentiaTransport tube&&!(tube instanceof VatEntity)&&tube.isConnectable(face.getOpposite())&&tube.canOutputTo(face.getOpposite())&&tube.getSuctionAmount(face.getOpposite())<128&&tube.getMinimumSuction()<=128){
                    Aspect a=needed();if(a!=null&&tube.takeEssentia(a,1,face.getOpposite())==1){receive(a,1);suckingFrom=p;drew=true;break;}
                }
            }
        }return drew;
    }
    public int receive(Aspect aspect,int n){
        if(n<=0||needed()!=aspect)return 0;
        int accepted=Math.min(n,mode==2?demand.getAmount(aspect):demand.getAmount(aspect)-stored.getAmount(aspect));
        if(mode==2)demand.remove(aspect,accepted);else stored.add(aspect,accepted);
        essentiaColor=aspect.getColor();effectTime=level==null?0:level.getGameTime();sync();return accepted;
    }
    public void scanPedestals(){
        pedestals.clear();List<BlockPos> stabilizers=new ArrayList<>();
        for(int x=-12;x<=12;x++)for(int z=-12;z<=12;z++){
            if(x==0&&z==0)continue;boolean found=false;
            for(int y=-5;y<=10;y++){
                BlockPos p=worldPosition.offset(x,-y,z);if(!level.hasChunkAt(p))continue;
                if(!found&&y>0&&Math.abs(x)<=8&&Math.abs(z)<=8&&level.getBlockEntity(p) instanceof PedestalBlockEntity){pedestals.add(p);found=true;}
                else if(VatRecipes.stabilizer(level,p))stabilizers.add(p);
            }
        }
        symmetry=0;
        for(BlockPos p:pedestals){var ped=(PedestalBlockEntity)level.getBlockEntity(p);symmetry+=ped.getStack().isEmpty()?2:3;BlockPos mirror=new BlockPos(2*worldPosition.getX()-p.getX(),p.getY(),2*worldPosition.getZ()-p.getZ());if(level.hasChunkAt(mirror)&&level.getBlockEntity(mirror) instanceof PedestalBlockEntity other)symmetry-=2+(!ped.getStack().isEmpty()&&!other.getStack().isEmpty()?1:0);}
        float stability=0;for(BlockPos p:stabilizers){stability+=.1F;BlockPos mirror=new BlockPos(2*worldPosition.getX()-p.getX(),p.getY(),2*worldPosition.getZ()-p.getZ());if(level.hasChunkAt(mirror)&&VatRecipes.stabilizer(level,mirror))stability-=.2F;}symmetry+=(int)stability;
    }
    public boolean startInfusion(Player player){
        if(mode!=0&&mode!=4||!level.getBlockState(worldPosition.above()).is(Vats.MATRIX.get()))return false;
        if(mode==0&&!Vats.validSubject(occupant()))return false;
        scanPedestals();List<ItemStack> items=pedestals.stream().map(p->((PedestalBlockEntity)level.getBlockEntity(p)).getStack()).filter(s->!s.isEmpty()).toList();
        VatRecipes.Recipe recipe=VatRecipes.find(items,mode==4,occupant(),player,shellInfusions);if(recipe==null)return false;
        recipeKey=recipe.key();operator=player.getUUID();recipeCost=recipe.cost().copy();demand=recipe.cost().copy();stored=new AspectList();remaining.clear();for(int i=0;i<recipe.ingredients().size();i++)remaining.add(i);
        instability=symmetry+recipe.instability();cycles=0;itemCount=0;mode=2;sync();level.playSound(null,worldPosition,thaumcraft.registry.ModSounds.CRAFTSTART.get(),net.minecraft.sounds.SoundSource.BLOCKS,.5F,1);return true;
    }
    public void infusionCycle(){
        VatRecipes.Recipe recipe=VatRecipes.get(recipeKey);if(recipe==null){failInfusion();return;}
        if(!recipe.self()&&(occupant()==null||occupant() instanceof Player)){failInfusion();return;}
        if(instability>0&&level.random.nextInt(500)<=instability)VatInstability.event(this);
        if(instability>0){
            float vis=100;for(Aspect a:List.of(Aspect.EARTH,Aspect.WATER,Aspect.ORDER))vis=Math.min(vis,thaumcraft.vis.VisNet.drainVis(level,worldPosition.above(),a,100));
            float damage=instability/10F/(5+vis);
            if(recipe.self()){shellHealth-=damage;if(shellHealth<=0){failInfusion();return;}}
            else if(occupant()!=null){subject.setHealth(subject.getHealth()-damage);if(subject.getHealth()<=0){failInfusion();return;}}
        }
        if(!demand.isEmpty()){
            if(!pull()){if(level.random.nextInt(Math.max(1,100-recipe.instability()*3))==0)instability=Math.min(25,instability+1);scanPedestals();}sync();return;
        }
        if(!remaining.isEmpty()){
            for(int index:new ArrayList<>(remaining))for(BlockPos p:pedestals){
                if(!level.hasChunkAt(p)||!(level.getBlockEntity(p) instanceof PedestalBlockEntity pedestal))continue;
                ItemStack item=pedestal.getStack();if(!recipe.ingredients().get(index).test(item))continue;
                if(itemCount==0){itemCount=5;suckingFrom=p;effectTime=level.getGameTime();}
                else if(itemCount--<=1){ItemStack remainder=item.getCraftingRemainingItem();pedestal.setStackFromInfusion(remainder);remaining.remove(Integer.valueOf(index));}
                sync();return;
            }return;
        }
        if(recipe.self()){for(int i=0;i<12;i++)if(shellInfusions[i]==0){shellInfusions[i]=recipe.infusion();break;}mode=4;}
        else{
            LivingEntity out=recipe.apply(occupant(),operator,recipeCost);if(out==null){failInfusion();return;}subject=out;mode=0;
        }
        instability=0;recipeKey="";demand=new AspectList();recipeCost=new AspectList();sync();
        if(level instanceof ServerLevel server)server.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANT,worldPosition.getX()+.5,worldPosition.getY()-1,worldPosition.getZ()+.5,40,1,1,1,.05);
    }
    public void failInfusion(){
        if(level!=null&&!level.isClientSide()&&(occupant()!=null&&!(occupant() instanceof Player)||mode==4||mode==2&&VatRecipes.get(recipeKey)!=null&&VatRecipes.get(recipeKey).self())){
            level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,.5F,Level.ExplosionInteraction.NONE);
            for(int i=0;i<25;i++){BlockPos pos=worldPosition.offset(level.random.nextInt(8)-level.random.nextInt(8),level.random.nextInt(8)-level.random.nextInt(8),level.random.nextInt(8)-level.random.nextInt(8));if(level.hasChunkAt(pos)&&level.isEmptyBlock(pos))level.setBlockAndUpdate(pos,(pos.getY()<worldPosition.getY()?thaumcraft.registry.ModBlocks.FLUX_GOO.get():thaumcraft.registry.ModBlocks.FLUX_GAS.get()).defaultBlockState());}
        }
        subject=null;pendingSubject=null;playerSubject=null;shellInfusions=new int[12];shellHealth=20;recipeKey="";remaining.clear();instability=0;cycles=0;itemCount=0;resetWork();
        if(level instanceof ServerLevel server){server.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE,worldPosition.getX()+.5,worldPosition.getY()-1,worldPosition.getZ()+.5,25,1,1,1,.05);}
    }
    private void resetWork(){mode=0;progress=0;stored=new AspectList();demand=new AspectList();sync();}
    public void dismantle(BlockPos broken){
        if(dismantling||level==null||level.isClientSide())return;dismantling=true;
        if(mode==2||subject!=null)failInfusion();playerSubject=null;
        for(int i=0;i<2;i++){ItemStack stack=inventory.getStackInSlot(i);inventory.setStackInSlot(i,ItemStack.EMPTY);Containers.dropItemStack(level,worldPosition.getX()+.5,worldPosition.getY()+1.5,worldPosition.getZ()+.5,stack);}
        BlockPos origin=worldPosition.offset(-1,-3,-1);
        for(BlockPos off:BlockPos.betweenClosed(0,0,0,2,3,2)){BlockPos p=origin.offset(off);if(p.equals(worldPosition)||p.equals(broken)||!level.hasChunkAt(p))continue;if(level.getBlockEntity(p) instanceof VatEntity v){v.dismantling=true;level.setBlock(p,Vats.raw(off.getX(),off.getY(),off.getZ()),3);}}
        if(!worldPosition.equals(broken))level.setBlock(worldPosition,Vats.raw(1,3,1),3);
    }
    public IItemHandler automation(){VatEntity master=master();if(master==null)return null;return new IItemHandler(){
        public int getSlots(){return 2;}public ItemStack getStackInSlot(int s){return master.inventory.getStackInSlot(s);}public ItemStack insertItem(int s,ItemStack stack,boolean sim){return master.inventory.insertItem(s,stack,sim);}public ItemStack extractItem(int s,int count,boolean sim){return ItemStack.EMPTY;}public int getSlotLimit(int s){return 64;}public boolean isItemValid(int s,ItemStack stack){return master.inventory.isItemValid(s,stack);}
    };}
    @Override public Component getDisplayName(){return Component.translatable("container.vat");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inv,Player player){return new VatMenu(id,inv,this);}
    @Override public boolean isConnectable(Direction face){return part()==4||isController()&&mode==1&&face==Direction.UP;}
    @Override public boolean canInputFrom(Direction face){return isConnectable(face);}
    @Override public boolean canOutputTo(Direction face){return false;}
    @Override public void setSuction(Aspect a,int n){}
    @Override public Aspect getSuctionType(Direction face){var m=master();return m!=null&&m.mode==2?m.needed():null;}
    @Override public int getSuctionAmount(Direction face){var m=master();return m!=null&&m.needed()!=null?128:0;}
    @Override public int takeEssentia(Aspect a,int n,Direction face){return 0;}
    @Override public int addEssentia(Aspect a,int n,Direction face){var m=master();return m!=null&&isConnectable(face)?m.receive(a,n):0;}
    @Override public Aspect getEssentiaType(Direction face){return null;}
    @Override public int getEssentiaAmount(Direction face){return 0;}
    @Override public int getMinimumSuction(){return 0;}
    @Override public boolean renderExtendedTube(){return false;}
    @Override public AspectList getAspects(){var m=master();return m==null?new AspectList():(m.mode==2?m.demand:m.stored).copy();}
    @Override public void setAspects(AspectList a){}
    @Override public boolean doesContainerAccept(Aspect a){var m=master();return m!=null&&m.needed()==a;}
    @Override public int addToContainer(Aspect a,int n){var m=master();return m==null?n:n-m.receive(a,n);}
    @Override public boolean takeFromContainer(Aspect a,int n){return false;}
    @Override public boolean doesContainerContainAmount(Aspect a,int n){return getAspects().getAmount(a)>=n;}
    @Override public int containerContains(Aspect a){return getAspects().getAmount(a);}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider r){
        super.saveAdditional(tag,r);if(masterPos!=null)tag.putLong("master",masterPos.asLong());if(!isController())return;
        tag.putInt("mode",mode);tag.putInt("progress",progress);tag.putInt("instability",instability);tag.putInt("symmetry",symmetry);tag.putInt("cycles",cycles);tag.putInt("itemCount",itemCount);tag.putFloat("shellHealth",shellHealth);tag.putIntArray("shellInfusions",shellInfusions);tag.put("items",inventory.serializeNBT(r));
        tag.put("stored",BloodInfuser.saveAspects(stored));tag.put("demand",BloodInfuser.saveAspects(demand));tag.put("cost",BloodInfuser.saveAspects(recipeCost));tag.putString("recipe",recipeKey);tag.putIntArray("remaining",remaining.stream().mapToInt(Integer::intValue).toArray());
        if(operator!=null)tag.putUUID("operator",operator);if(playerSubject!=null)tag.putUUID("player",playerSubject);
        if(subject!=null){var nbt=new CompoundTag();subject.save(nbt);tag.put("subject",nbt);}else if(pendingSubject!=null)tag.put("subject",pendingSubject.copy());
        if(suckingFrom!=null)tag.putLong("source",suckingFrom.asLong());tag.putInt("color",essentiaColor);tag.putLong("fx",effectTime);
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider r){
        super.loadAdditional(tag,r);masterPos=tag.contains("master")?BlockPos.of(tag.getLong("master")):null;if(!isController())return;loading=true;
        mode=Math.clamp(tag.getInt("mode"),0,4);progress=Math.max(0,tag.getInt("progress"));instability=tag.getInt("instability");symmetry=tag.getInt("symmetry");cycles=tag.getInt("cycles");itemCount=tag.getInt("itemCount");shellHealth=tag.getFloat("shellHealth");shellInfusions=Arrays.copyOf(tag.getIntArray("shellInfusions"),12);inventory.deserializeNBT(r,tag.getCompound("items"));
        stored=BloodInfuser.loadAspects(tag.getCompound("stored"));demand=BloodInfuser.loadAspects(tag.getCompound("demand"));recipeCost=BloodInfuser.loadAspects(tag.getCompound("cost"));recipeKey=tag.getString("recipe");remaining.clear();for(int i:tag.getIntArray("remaining"))remaining.add(i);
        operator=tag.hasUUID("operator")?tag.getUUID("operator"):null;playerSubject=tag.hasUUID("player")?tag.getUUID("player"):null;
        var incoming=tag.contains("subject")?tag.getCompound("subject").copy():null;
        if(level!=null&&level.isClientSide()&&subject!=null&&incoming!=null&&incoming.hasUUID("UUID")&&subject.getUUID().equals(incoming.getUUID("UUID"))&&net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(subject.getType()).toString().equals(incoming.getString("id"))){subject.load(incoming);pendingSubject=null;}
        else{subject=null;pendingSubject=incoming;}
        suckingFrom=tag.contains("source")?BlockPos.of(tag.getLong("source")):null;essentiaColor=tag.getInt("color");effectTime=tag.getLong("fx");loading=false;
        if(level!=null&&!level.isClientSide()&&mode==2)scanPedestals();
    }
}
