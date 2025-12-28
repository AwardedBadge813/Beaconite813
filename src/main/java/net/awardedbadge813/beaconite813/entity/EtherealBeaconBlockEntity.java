package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.effect.MarkedSplashEffect;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.awardedbadge813.beaconite813.potion.ModPotions;
import net.awardedbadge813.beaconite813.screen.custom.EtherealBeaconMenu;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.awardedbadge813.beaconite813.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.*;


public class EtherealBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {

    private Operation currentOperation = Operation.NORMAL;
    private  @Nullable Holder<MobEffect> heldEffect=null;
    private int potionTime=0;
    private static final HashMap<Item, Operation> moduleMap = defineModuleMap();
    public EtherealBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ETHER_BEACON_BE.get(),
                pos,
                blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return encodeEffect(heldEffect);
                    }
                    case 1 -> {
                        return potionTime;
                    }
                    case 2 -> {
                        return encodeOperation(currentOperation);
                    }
                    default -> {
                        return 0;
                    }
                }
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
                    case 0 -> {
                        heldEffect = decodeEffect(i1);
                    }
                    case 1 -> {
                        potionTime=i1;
                    }
                    case 2 -> {
                        currentOperation= decodeOperation(i1);
                    }
                    default -> {
                    }
                }

            }

            @Override
            public int getCount() {
                return 3;
            }

        };
    }

    private int encodeOperation(Operation operation){
        int result;
        switch (operation) {
            case AURA -> {
                result = 1;
            }
            case DIFFUSION -> {
                result = 2;
            }
            case TRANSMUTATION -> {
                result = 3;
            }
            case INFUSION -> {
                result = 4;
            }
            case INACTIVE -> {
                result = 5;
            }
            case CLEAR -> {
                result = 6;
            }
            default -> {
                result=0;
            }

        }
        return result;
    }
    public Operation decodeOperation(int i) {
        switch (i) {
            case 1 -> {
                return Operation.AURA;
            }
            case 2 -> {
                return Operation.DIFFUSION;
            }
            case 3 -> {
                return Operation.TRANSMUTATION;
            }
            case 4 -> {
                return Operation.INFUSION;
            }
            case 5 -> {
                return Operation.INACTIVE;
            }
            case 6 -> {
                return Operation.CLEAR;
            }
            default -> {
                return Operation.NORMAL;
            }

        }

    }



    public final ItemStackHandler itemHandler = new ItemStackHandler(11) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        public int getSlotLimit(int slot) {
            if(itemHandler.getStackInSlot(slot).getItem()==ModItems.DIFFUSE_MODULE.get()) {
                return 10;
            }
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            this.validateSlotIndex(slot);
            if (this.stacks.get(slot).getItem() instanceof ToggleableItem && ((ToggleableItem) this.stacks.get(slot).getItem()).isDisabled()) {
                return ItemStack.EMPTY;
            }
            return this.stacks.get(slot);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if(slot==10) {
                return isPotionStorable(stack.getItem())|| stack.is(ModTags.Items.ETHEREAL_MODULES);
            } else if(currentOperation!=Operation.INFUSION || slot!=7){
                return isPotionStorable(stack.getItem());
            } else {
                return stack.is(ModTags.Items.ETHEREAL_INFUSIBLE);
            }
        }
    };



    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i=0; i<itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, this.worldPosition, inventory);
        this.remove();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("block.beaconite813.ethereal_beacon_be");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new EtherealBeaconMenu(i, inventory, this, this.data);
    }



    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }
    public enum Operation {
        NORMAL,
        AURA,
        DIFFUSION,
        TRANSMUTATION,
        INFUSION,
        CLEAR,
        INACTIVE
    }

    public static HashMap<Item, Operation> defineModuleMap() {
        HashMap<Item, Operation> operationHashMap = new HashMap<>();
        operationHashMap.put(ModItems.AURA_MODULE.get(), Operation.AURA);
        operationHashMap.put(ModItems.DIFFUSE_MODULE.get(), Operation.DIFFUSION);
        operationHashMap.put(ModItems.TRANSMUTE_MODULE.get(), Operation.TRANSMUTATION);
        operationHashMap.put(ModItems.INFUSE_MODULE.get(), Operation.INFUSION);
        operationHashMap.put(ModItems.BOTTOMLESS_BOTTLE.get(), Operation.CLEAR);
        return operationHashMap;
    }



    public void updateOperation(ItemStack inputStack, Level level, BlockPos pos) {
        SoundEvent toPlay = SoundEvents.EMPTY;
        if (moduleMap.get(inputStack.getItem())==Operation.CLEAR) {
            toPlay = SoundEvents.BREWING_STAND_BREW;
        } else {
            toPlay = SoundEvents.ANVIL_USE;
        }
        if(currentOperation==Operation.INACTIVE) {
            toPlay = SoundEvents.BEACON_ACTIVATE;
        }
        if(!isBeaconActive(level, pos)) {
            toPlay = SoundEvents.BEACON_DEACTIVATE;
            changeEnum(Operation.INACTIVE, level, pos, toPlay);
            return ;
        }
        if (inputStack.is(ModTags.Items.ETHEREAL_MODULES)) {
            changeEnum(moduleMap.get(inputStack.getItem()), level, pos, toPlay);
            return;
        }
        changeEnum(Operation.NORMAL, level, pos, SoundEvents.BEACON_DEACTIVATE);

    }

    public void changeEnum(Operation operation, Level level, BlockPos pos, SoundEvent toPlay) {
        if (currentOperation != operation) {
            currentOperation = operation;
            if(toPlay.equals(SoundEvents.BEACON_DEACTIVATE)) {
                level.playSound(null, pos, toPlay, SoundSource.AMBIENT, 1f, 1.5f);
            } else if(toPlay.equals(SoundEvents.BREWING_STAND_BREW)) {
                level.playSound(null, pos, toPlay, SoundSource.AMBIENT, 1f, 1f);
            } else if(!toPlay.equals(SoundEvents.EMPTY)) {
                level.playSound(null, pos, toPlay, SoundSource.AMBIENT, 0.2f, 1f);
            }
        }

    }
    public int findFading(LivingEntity entity) {
        //if fading is found, we want to know what the duration is so we can snub any matching effects.
        for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
            if(effectInstance.is(ModEffects.MARKED_SPLASH)) {
                return effectInstance.getDuration();
            }
        }
        return 0;
    }




    public void tick(Level level, BlockPos pos, BlockState state) {
        if (this.isDisabled((ToggleableBlockItem) state.getBlock().asItem())) {
            return;
        }
        if(potionTime <= 0){
            setEffect(null);
        }
        updateOperation(itemHandler.getStackInSlot(10), level, pos);
        if(this.isBeaconActive(level, pos)) {
            potionTime = max(min(potionTime, Config.MAX_BLOCK_POTION_TIME.getAsInt()), 0);
            switch (currentOperation) {
                case NORMAL -> {
                    consumePotionTime(10);
                    for (int slot = 0; slot < 10; slot++) {
                        if ((int) (20 * pow(2, slot)) <= potionTime && isPotionStorable(itemHandler.getStackInSlot(slot).getItem())) {
                            if (storeEffect(heldEffect, slot, slot)) {
                                potionTime -= (int) min((20 * pow(2, slot)), 2147483647);
                            }
                        }
                    }
                }
                case DIFFUSION -> {
                    for (int slot = 0; slot < 10; slot++) {
                        consumePotionTime(slot);
                    }
                    if (potionTime >= pow(1.5, itemHandler.getStackInSlot(10).getCount()-1) && potionTime>0) {
                        AABB aabb = new AABB(pos).inflate(Config.AURA_DIFFUSE_RADIUS.getAsInt());
                        List<Player> entities = level.getEntitiesOfClass(Player.class, aabb);
                        boolean used = false;
                        for (LivingEntity entity : entities) {
                            entity.addEffect(new MobEffectInstance(heldEffect, 2, itemHandler.getStackInSlot(10).getCount()-1, true, true));
                            used = true;
                        }
                        if (used) {
                            potionTime -= (int) (pow(1.5f, itemHandler.getStackInSlot(10).getCount()-1));
                        }


                    }

                }
                case AURA -> {
                    //cloudchecker fuckyou is bigger because if you have a lingering potion you could get the system to give you WAY more time than you put in, making a potion dupe.
                    //hence the fuck you for making my life harder mojang just make it an ambient effect ;-;
                    //
                    AABB aabb = new AABB(pos).inflate(Config.AURA_DIFFUSE_RADIUS.getAsInt());
                    AABB cloudchecker_fuckyou = new AABB(pos).inflate(Config.AURA_DIFFUSE_RADIUS.getAsInt()+10);
                    List<AreaEffectCloud> clouds = level.getEntitiesOfClass(AreaEffectCloud.class, cloudchecker_fuckyou);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
                    //we want to apply a gametime offset so markedsplash does not break everything.
                    if(clouds.isEmpty() && potionTime<=Config.MAX_BLOCK_POTION_TIME.getAsInt()) {
                        try {
                            pullEffects(entities);
                        } catch (Exception e) {
                            //don't know why this throws a concurrentmodificationexception very rarely. can't handle it since it is not consistent.
                            // so if it happens, the ethereal beacon will instead do nothing.
                        }

                    }
                    for (int slot = 0; slot < 10; slot++) {
                        if ((int) (20 * pow(2, slot)) <= potionTime && isPotionStorable(itemHandler.getStackInSlot(slot).getItem())) {
                            if (storeEffect(heldEffect, slot, slot)) {
                                potionTime -= (int) min((20 * pow(2, slot)), 2147483647);
                                if (potionTime<=20) {
                                    potionTime =0;
                                    setEffect(null);
                                }
                            }
                        }
                    }
                }
                case TRANSMUTATION -> {
                    for(int slot=0; slot < 5; slot++) {
                        consumePotionTime(slot);
                    }

                    for (int slot = 5; slot < 10; slot++) {
                        Holder<MobEffect> effectValue=null;
                        int effectAmplifier=-1;
                        for(MobEffectInstance effect : itemHandler.getStackInSlot(slot).getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects()) {
                            if(effect.getDuration()>1 && effect.getEffect()!=heldEffect) {
                                effectValue = effect.getEffect();
                                effectAmplifier=effect.getAmplifier();
                            }
                        }
                        if ((int) (40 * pow(2, effectAmplifier)) <= potionTime && isPotionStorable(itemHandler.getStackInSlot(slot).getItem())&& effectValue!=null) {
                            if (storeEffect(effectValue, effectAmplifier, slot)) {
                                potionTime -= (int) min((40 * pow(2, effectAmplifier)), 2147483647);
                            }
                        }
                    }
                }
                case INFUSION -> {
                    for(int slot=0; slot < 10; slot++) {
                        if(slot!=7) {
                            consumePotionTime(slot);
                        } else {
                            infuseItem(7, ModBlocks.DORMANT_EGG.asItem().getDefaultInstance(), Items.DRAGON_EGG.getDefaultInstance(), 36000, true);
                            infuseItem(7, ModItems.DORMANT_BOTTLE.get().getDefaultInstance(), ModItems.BOTTOMLESS_BOTTLE.get().getDefaultInstance(), 12000, false);
                        }

                    }
                }
                case INACTIVE -> {

                }
                case CLEAR -> {
                    potionTime=1;
                    for(int slot=0; slot < 10; slot++) {
                        consumePotionTime(slot);
                    }
                    if (potionTime==1) {
                        setEffect(null);
                        potionTime=0;
                    }

                }
                default -> {
                    currentOperation=Operation.NORMAL;
                }
            }
        }


    }

    public void setEffect (Holder<MobEffect> mobEffect) {
        if (mobEffect==null || !BeaconiteLib.effectDisabled(mobEffect.value())) {
            heldEffect = mobEffect;
        }
    }


    private void infuseItem(int slot, ItemStack recipeInput, ItemStack recipeOutput, int timeCost, boolean metadata) {
        boolean meta = itemHandler.getStackInSlot(slot).getComponents().equals(recipeInput.getComponents());
        boolean checker = itemHandler.getStackInSlot(slot).getItem() == recipeInput.getItem() && itemHandler.getStackInSlot(slot).getCount()==recipeInput.getCount() && (meta || !metadata);
        if (checker && potionTime>=min(Config.MAX_BLOCK_POTION_TIME.getAsInt(), timeCost)) {
            itemHandler.setStackInSlot(slot, recipeOutput);
            potionTime-=timeCost;
            assert getLevel() != null;
            getLevel().playSound(null, getBlockPos(), SoundEvents.BEACON_DEACTIVATE, SoundSource.AMBIENT, 0.5f, 0f);
        }
    }
    public boolean marginCompare(int value1, int value2, int deadband) {
        int margin = abs(value1-value2);
        return margin<=deadband;

    }

    public void pullEffects(List<LivingEntity> entities) {
        int extracted = 0;
        int extracted_last=0;
        int toAdd=0;
        int extractedTot=0;
        for(LivingEntity entity : entities) {
            extracted_last = extractedTot;
            for (MobEffectInstance effect : entity.getActiveEffects()) {
                //marked splash makes effects ambient so this system will snub them.
                //I may update it one day by dividing the potiontime by the markedsplash amplifier,
                // but it would take  a mixin i think and more effort than I want to dedicate right now.
                if(!effect.isAmbient() && heldEffect==null){
                    setEffect(effect.getEffect());
                }
                extractedTot = takeEffect(effect, entity, true);
            }
            if (extractedTot>extracted_last) {
                toAdd=extractedTot;
            }
            if (toAdd+potionTime<=Config.MAX_BLOCK_POTION_TIME.getAsInt()) {
                for (MobEffectInstance effect : entity.getActiveEffects()) {
                    takeEffect(effect, entity, false);
                }
                potionTime+=toAdd;
            }
        }

    }

    private int takeEffect(MobEffectInstance effect,  LivingEntity entity, boolean simulate) {
        int extracted = 0;
        int extractedtot=0;
        if (!effect.isAmbient() && effect.is(heldEffect)){
            extracted = min(effect.getDuration(), 100);
            if (!simulate) {
                entity.removeEffect(heldEffect);
                entity.addEffect(new MobEffectInstance(heldEffect, max(effect.getDuration()-100, 0), effect.getAmplifier()));
            }
            extractedtot=min((int) (extracted*pow(2, effect.getAmplifier())), Config.MAX_BLOCK_POTION_TIME.getAsInt());
        }
        return extractedtot;
    }

    @Override
    public int getColor() {
        return DyeColor.PURPLE.getTextureDiffuseColor();
    }

    public boolean storeEffect(Holder<MobEffect> storingEffect, int amplifier, int slot) {
            Iterable<MobEffectInstance> effects = itemHandler.getStackInSlot(slot).getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects();
            ArrayList<MobEffectInstance> effectsUpdated = new ArrayList<>();
            int excess;
            int tradeIn=0;
            for (MobEffectInstance effect : effects) {
                Holder<MobEffect> effectValue = effect.getEffect();
                if (effectValue == storingEffect) {
                    tradeIn+= (int) (effect.getDuration()*pow(2, effect.getAmplifier()));
                } else {
                    effectsUpdated.add(effect);
                }
            }
            excess = (int) (tradeIn%20*pow(2, amplifier));
            if (20+(int) ((tradeIn-excess)/pow(2, amplifier))<=Config.MAX_HELD_POTION_TIME.getAsInt()) {

                effectsUpdated.add(new MobEffectInstance(storingEffect, 20+(int) ((tradeIn-excess)/pow(2, amplifier)), amplifier));
                potionTime+=excess;

                setPotion(effectsUpdated, slot, itemHandler.getStackInSlot(slot).getItem());
                return true;
            }
            return false;
    }


    public Boolean isPotionStorable (Item potionItem) {
        return potionItem==Items.GLASS_BOTTLE || potionItem instanceof PotionItem;
    }

    public void consumePotionTime(int slot){
        if(potionTime < 20){
            potionTime = 0;
            heldEffect = null;
        }
        if(itemHandler.getStackInSlot(slot).getItem() instanceof PotionItem) {
            Iterable<MobEffectInstance> effects = itemHandler.getStackInSlot(slot).getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getAllEffects();
            ArrayList<MobEffectInstance> effectsUpdated = new ArrayList<>();
            boolean changed = false;

            for (MobEffectInstance effect : effects) {
                Holder<MobEffect> effectValue = effect.getEffect();
                if (!effect.is(ModEffects.MARKED_SPLASH)) {
                    int  effectDuration = effect.getDuration();
                    int  effectAmplifier = effect.getAmplifier();
                    int toAdd = 20*(int) min(pow(2, effectAmplifier), Config.MAX_BLOCK_POTION_TIME.getAsInt());


                    if (heldEffect == null && effectDuration>1) {
                        setEffect(effectValue);
                    }

                    if(effectValue==heldEffect && potionTime+toAdd<=Config.MAX_BLOCK_POTION_TIME.getAsInt() && effectDuration>=20) {
                        effect = new MobEffectInstance(effectValue, effectDuration-20, effectAmplifier);
                        this.potionTime+=toAdd;
                        changed = true;
                    }
                    if(effectValue==heldEffect && effectDuration-20<20){
                        effect = null;
                        changed = true;
                    }
                    if(effect!=null) {
                        effectsUpdated.add(effect);
                    }
                }
            }
            if (changed) {
                setPotion(effectsUpdated, slot, itemHandler.getStackInSlot(slot).getItem());
            }
        }
    }
    public ArrayList<MobEffectInstance> getMark(List<MobEffectInstance> effects) {
        ArrayList<MobEffectInstance> effectsUpdated= new ArrayList<>();
        for(MobEffectInstance effectInstance : effects){
            if (effectInstance.getEffect()!=ModEffects.MARKED_SPLASH){
                effectsUpdated.add(new MobEffectInstance(effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier(), true, true, true));
            }
        }
        int pendingDuration = 0;
        for (MobEffectInstance effectInstance : effectsUpdated) {
            int duration = effectInstance.getDuration();
            if (duration>pendingDuration) {
                pendingDuration=duration;
            }
        }
        MobEffectInstance marked = new MobEffectInstance(ModEffects.MARKED_SPLASH, pendingDuration, 0, true, true);
        effectsUpdated.add(marked);
        return effectsUpdated;
    }
    public void  setPotion(List<MobEffectInstance> effects, @Nullable Integer slotIndex, Item basePotionItem) {
        ItemStack potionItem;
        if(basePotionItem instanceof LingeringPotionItem) {
            potionItem = new ItemStack(Holder.direct(Items.LINGERING_POTION), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(ModPotions.ETHEREAL_POTION), Optional.of(16253890), getMark(effects))).build());
        } else if (basePotionItem instanceof SplashPotionItem) {
            //if this is a splash potion, we want to apply markedSplash equivalent to the maximum time across all effects.
            //if the time of the effect is less than the marked value, the effect cannot be stored.
            potionItem = new ItemStack(Holder.direct(Items.SPLASH_POTION), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(ModPotions.ETHEREAL_POTION), Optional.of(16253890), getMark(effects))).build());
        } else  {
            potionItem = new ItemStack(Holder.direct(Items.POTION), 1, DataComponentPatch.builder().set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.of(ModPotions.ETHEREAL_POTION), Optional.of(16253890), effects)).build());
        }

        if (effects.isEmpty()){
            potionItem = new ItemStack(Items.GLASS_BOTTLE, 1);
        }
        if (slotIndex != null) {
            itemHandler.setStackInSlot(slotIndex, potionItem);
        }
    }


    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.PURPLE.getTextureDiffuseColor(), getLevel().getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>9 && getSkyStatus(level, pos)==1;
    }

    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("effect", encodeEffect(heldEffect));
        pTag.putInt("potion_time", potionTime);

        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        heldEffect = decodeEffect(pTag.getInt("effect"));
        potionTime = pTag.getInt("potion_time");
    }


    // encode/decode also mojang
    public static int encodeEffect(@javax.annotation.Nullable Holder<MobEffect> effect) {
        return effect == null ? 0 : BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(effect) + 1;
    }

    @javax.annotation.Nullable
    public static Holder<MobEffect> decodeEffect(int effectId) {
        return effectId == 0 ? null : BuiltInRegistries.MOB_EFFECT.asHolderIdMap().byId(effectId - 1);
    }

    protected final ContainerData data;

}
