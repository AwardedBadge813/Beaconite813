package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.BubbleEntity;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.screen.custom.RegalBeaconMenu;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.*;

public class RegalBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {

    private final ContainerData data;
    private int beaconLevels;
    private int canSeeSky;
    private int goldTime;
    private int lootTime;

    public RegalBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REGAL_BEACON_BE.get(), pos, blockState);
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
                        return goldTime;
                    }
                    case 3 -> {
                        return lootTime;
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
                        beaconLevels=i1;
                    }
                    case 1 -> {
                        canSeeSky=i1;
                    }
                    case 2 -> {
                        goldTime = i1;
                    }
                    case 3 -> {
                        lootTime = i1;
                    }
                    default -> {

                    }
                }

            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }


    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
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

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return BeaconiteLib.midasValidEntries.contains(stack.getItem());
            }
            if (slot == 1) {
                return BeaconiteLib.hypertrophyValidEntries.contains(stack.getItem());
            }
            return false;
        }
    };

    @Override
    public @NotNull BlockEntityType<RegalBeaconBlockEntity> getType() {
        return ModBlockEntities.REGAL_BEACON_BE.get();
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("regal_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        int amplifier = BeaconiteLib.restrict(getLayers(level, pos) -8, 0, 9);
        addGoldTime(itemHandler, 0);
        addLootTime(itemHandler, 1);
        if (goldTime>0) {
            if (applyLootEffect(ModEffects.MIDAS_ROT, amplifier, level, pos) && level.getGameTime()%2L==0L) {
                goldTime-=2;
            }
        }
        if (lootTime>0) {
            if (applyLootEffect(ModEffects.HYPERTROPHY, amplifier, level, pos) && level.getGameTime()%2L==0L) {
                lootTime-=2;
            }
        }



    }
    public void addGoldTime(ItemStackHandler itemHandler, int slot) {
        //the pending time should be based on the time specified in the gold map config for the item you inputted.
        Item pendingItem = itemHandler.getStackInSlot(slot).getItem();
        int pendingTime = 0;
        if (goldMap.containsKey(pendingItem)) {
            pendingTime = goldMap.get(pendingItem) * Config.GOLD_TIME_MOD.getAsInt();
        }
        //if the time is 0, the item should not be consumed since it won't actually do anything.
        //if the time exceeds the maximum gold time, it should not be unusable even though the extra time will be wasted.
        //However, in the spirit of keeping the time near max but not wasting any, the beacon should wait to consume an item until there is space for its time.
        if (pendingTime>0 && (pendingTime+goldTime<=Config.MAX_GOLD_TIME.getAsInt() || pendingTime>Config.MAX_GOLD_TIME.getAsInt())) {
            itemHandler.extractItem(slot, 1, false);
            goldTime=BeaconiteLib.restrict(goldTime+pendingTime, 0, Config.MAX_GOLD_TIME.getAsInt());
        }
    }
    public void addLootTime(ItemStackHandler itemHandler, int slot) {
        //the pending time should be based on the time specified in the gold map config for the item you inputted.
        Item pendingItem = itemHandler.getStackInSlot(slot).getItem();
        int pendingTime = 0;
        if (lootMap.containsKey(pendingItem)) {
            pendingTime = lootMap.get(pendingItem) * Config.LOOT_TIME_MOD.getAsInt();
        }
        //if the time is 0, the item should not be consumed since it won't actually do anything.
        //if the time exceeds the maximum gold time, it should not be unusable even though the extra time will be wasted.
        //However, in the spirit of keeping the time near max but not wasting any, the beacon should wait to consume an item until there is space for its time.
        if (pendingTime>0 && (pendingTime+lootTime<=Config.MAX_LOOT_TIME.getAsInt() || pendingTime>Config.MAX_LOOT_TIME.getAsInt())) {
            itemHandler.extractItem(slot, 1, false);
            lootTime=BeaconiteLib.restrict(lootTime+pendingTime, 0, Config.MAX_LOOT_TIME.getAsInt());
        }
    }
    public boolean applyLootEffect(Holder<MobEffect> mobEffect, int amplifier, Level level, BlockPos pos) {
        int radius = 30;
        boolean applied = false;
        AABB checkerRange = new AABB(pos).inflate(radius).expandTowards(pos.getX(), level.getMaxBuildHeight(), pos.getZ());
        for(LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, checkerRange)) {
            //bubbles get spawned often so the regal beacon kinda breaks if you synergize with them.
            if (!(entity instanceof BubbleEntity) && !entity.hasCustomName() && !(entity instanceof ServerPlayer) && level.getGameTime()%2L==0L) {
                if (entity.addEffect(new MobEffectInstance(mobEffect, 2, amplifier, true, true))) {
                    applied =true;
                }
            }
        }
        return applied;
    }

    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i< itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
        //the entity should be removed after you destroy the block.
        this.remove();
    }



    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new RegalBeaconMenu(i, inventory, this, this.data);
    }
    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert level != null;
            beamSection.setParams(DyeColor.YELLOW.getTextureDiffuseColor(), level.getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>7 && getSkyStatus(level, pos)==1;
    }




    //vanilla beacon code, credit to mojang
    private void applyEffect(Level level, BlockPos pos, int range, MobEffectInstance mobEffect) {
        int duration = 150;
        AABB aabb = (new AABB(pos)).inflate(range).expandTowards(0.0F, level.getMaxBuildHeight(), 0.0F);
        List<Player> list = level.getEntitiesOfClass(Player.class, aabb);
        for(Player player : list) {
            if (!player.hasEffect(mobEffect.getEffect()) || Objects.requireNonNull(player.getEffect(mobEffect.getEffect())).getDuration()<=max(mobEffect.getDuration()*0.25, 2)) {
                player.addEffect(mobEffect);
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
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("beacon_levels", beaconLevels);
        pTag.putInt("can_see_sky", canSeeSky);
        pTag.putInt("goldTime", goldTime);
        pTag.putInt("lootTime", lootTime);


        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        beaconLevels = pTag.getInt("beacon_levels");
        canSeeSky = pTag.getInt("can_see_sky");
        goldTime = pTag.getInt("goldTime");
        lootTime = pTag.getInt("lootTime");
    }


}