package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.screen.custom.LivingBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;

public class LivingBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {

    private final ContainerData data;
    private int beaconLevels;
    private int canSeeSky;
    private int satiation;
    private ArrayList<Holder<MobEffect>> effects;
    public LivingBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LIVING_BEACON_BE.get(), pos, blockState);
        effects = new ArrayList<>();
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return beaconLevels;
                    }
                    case 1 -> {
                        return canSeeSky;
                    }
                    case 2 -> {
                        return satiation;
                    }
                    default -> {
                        try {
                            return encodeEffect(effects.get(i-3).getDelegate());
                        }catch (Exception ignored) {
                            //the amount of ContainerData slots is static, so I have to just store 0 for all the extra ones.
                            return 0;
                        }
                    }
                }
            }

            @Override
            public void set(int i, int i1) {
                switch (i) {
                    case 0 -> {
                        beaconLevels=i1;
                    }
                    case 1 -> {
                        canSeeSky=i1;
                    }
                    case 2 -> {
                        satiation = i1;
                    }
                    default -> {
                        if(i1!=0 && !effects.contains(decodeEffect(i1))) {
                            effects.add(decodeEffect(i1));
                        }
                    }
                }

            }

            @Override
            public int getCount() {
                return 20;
            }
        };
    }
    public ArrayList<Holder<MobEffect>> getEffects() {
        return this.effects;
    }


    public final ItemStackHandler payment_slot = new ItemStackHandler(2) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 64;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    @Override
    public @NotNull BlockEntityType<LivingBeaconBlockEntity> getType() {
        return ModBlockEntities.LIVING_BEACON_BE.get();
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("living_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        this.beaconLevels=getLayers(level, pos);
        this.canSeeSky=getSkyStatus(level, pos);

        if(beaconLevels>5) {
            if(this.satiation<=0 || this.data.get(3)==0) {
                if(TryEatItem()) {
                    satiation += 1000;
                }
            } else {
                applyEffects(level, pos, satiation/10, min(max(beaconLevels-6, 0), 9));
                satiation-= beaconLevels/5;
            }
            if(satiation <= 10000 && !payment_slot.getStackInSlot(0).isEmpty() && !effects.isEmpty()) {
                tryFeed();
            }

        }



    }
    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(payment_slot.getSlots());
        for (int i=0; i<payment_slot.getSlots(); i++) {
            inventory.setItem(i, payment_slot.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
        this.remove();
    }
    public void tryFeed() {
        try {
            int saturation = (int) (Objects.requireNonNull(payment_slot.getStackInSlot(0).getFoodProperties(null)).nutrition()+ Objects.requireNonNull(payment_slot.getStackInSlot(0).getFoodProperties(null)).saturation());
            satiation += (int) (saturation*100/pow(2, effects.size()));
            payment_slot.extractItem(0, 1, false);
            assert level != null;
            level.playSound(null, getBlockPos(), SoundEvents.GENERIC_EAT, SoundSource.AMBIENT);
        } catch (Exception ignored) {
            //if the item has unsupported food effects, nothing will happen.
        }
    }

    public boolean TryEatItem() {
        // takes effects from the food items and stores them in the form of mobEffects.
        // I believe this will also get effects like poison from pufferfish and hunger from rotten flesh, which would be terribly funny.
        this.effects= new ArrayList<>();
        List<FoodProperties.PossibleEffect> storedPossibleEffects;
        try{
            storedPossibleEffects = Objects.requireNonNull(payment_slot.getStackInSlot(1).getFoodProperties(null)).effects();
        } catch(Exception ignored) {
            //if the item has foodEffects that are unsupported, the list will be cleared since this is called when saturation is 0.
            storedPossibleEffects = NonNullList.create();
        }


        for(FoodProperties.PossibleEffect effect : storedPossibleEffects) {
            try {
                this.effects.add(effect.effect().getEffect());
            } catch (Exception ignored) {
                // if the possibleEffect is not an effect or a compatible effect, it will not be stored instead of crashing.
            }
        }


        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        if(!effects.isEmpty()) {
            payment_slot.extractItem(1, 1, false);
            level.playSound(null, getBlockPos(), SoundEvents.GENERIC_EAT, SoundSource.AMBIENT);
            return true;
        }
        return false;
    }


    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new LivingBeaconMenu(i, inventory, this, this.data);
    }
    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert level != null;
            beamSection.setParams(DyeColor.GREEN.getTextureDiffuseColor(), level.getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>7 && getSkyStatus(level, pos)==1;
    }




    //vanilla beacon code, credit to mojang
    private void applyEffects(Level level, BlockPos pos, int range, int amplifier) {
        if (!level.isClientSide && (this.effects != null && !this.effects.isEmpty())) {
            int duration = 150;
            AABB aabb = (new AABB(pos)).inflate(range).expandTowards(0.0F, level.getHeight(), 0.0F);
            List<Player> list = level.getEntitiesOfClass(Player.class, aabb);

            for(Player player : list) {
                for(Holder<MobEffect> effect: this.effects) {
                    player.addEffect(new MobEffectInstance(effect, duration, amplifier, true, true));
                }

            }
        }

    }
    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveCustomAndMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("inventory", payment_slot.serializeNBT(pRegistries));
        pTag.putInt("beacon_levels", beaconLevels);
        pTag.putInt("can_see_sky", canSeeSky);
        pTag.putInt("satiation", satiation);

        for(int i=0; i<effects.size(); i++){
            pTag.putInt("effect"+i, encodeEffect(effects.get(i)));
        }

        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        payment_slot.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        beaconLevels = pTag.getInt("beacon_levels");
        canSeeSky = pTag.getInt("can_see_sky");
        satiation = pTag.getInt("satiation");
        for(int i=0; pTag.getInt("effect" + i) != 0; i++) {
            effects.add(decodeEffect(pTag.getInt("effect"+i)));
        }
    }


    // encode/decode also mojang
    public static int encodeEffect(@javax.annotation.Nullable Holder<MobEffect> effect) {
        return effect == null ? 0 : BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(effect) + 1;
    }

    @javax.annotation.Nullable
    public static Holder<MobEffect> decodeEffect(int effectId) {
        return effectId == 0 ? null : BuiltInRegistries.MOB_EFFECT.asHolderIdMap().byId(effectId - 1);
    }
}