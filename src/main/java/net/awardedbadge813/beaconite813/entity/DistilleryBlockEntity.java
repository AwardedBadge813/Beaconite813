package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Fluids.ModFluidTypes;
import net.awardedbadge813.beaconite813.Fluids.ModFluids;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.screen.custom.DistilleryMenu;
import net.awardedbadge813.beaconite813.screen.custom.StorageBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.datafix.fixes.FurnaceRecipeFix;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static net.awardedbadge813.beaconite813.block.custom.StorageBeaconBlock.FACING;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.restrict;

public class DistilleryBlockEntity extends BlockEntity implements MenuProvider {
    private Direction facing;
    protected final ContainerData data;
    public int maxProgress=1000;
    public int maxHeat=5000;
    private int progress=0;
    private int heat = 0;
    public DistilleryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DISTILLERY_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch(i) {
                    case 0-> {
                        return progress;
                    }
                    case 1-> {
                        return heat;
                    }
                    case 2 -> {
                        return tank.getFluidAmount();
                    }

                }
                return 0;
            }

            @Override
            public void set(int i, int i1) {

                switch(i) {
                    case 0->{
                        progress=i1;
                    }
                    case 1->{
                        heat=i1;
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
    public float getHeatPct() {
        return (float)heat/(float)maxHeat;
    }
    public float getProgressPct() {
        return (float)progress/(float)maxProgress;
    }
    public boolean isDisabled(ToggleableBlockItem blockItem) {
        return blockItem.isDisabled();
    }

    public ItemStackHandler itemInputs = new ItemStackHandler(4) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }


        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {
                    return stack.is(ModItems.REACTIVE_CONCOCTION);
                }
                case 1 -> {
                    return stack.getBurnTime(RecipeType.SMELTING)>0;
                }
                case 2 -> {
                    return stack.is(Items.BUCKET);
                }
                case 3 -> {
                    return false;
                }

            }
            return false;
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

    private FluidTank tank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return false;
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
            assert level != null;
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    @Override
    public @NotNull BlockEntityType<DistilleryBlockEntity> getType() {
        return ModBlockEntities.DISTILLERY_BE.get();
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("igneous_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        //heat loop
        heat--;
        float pct=this.getHeatPct();
        if (pct>=0.580&&pct<=1f) {
            progress+=(int)((1f-(abs(pct)-0.650f))*10f);
        }else {
            progress--;
        }
        if (itemInputs.getStackInSlot(1).getCount()>0) {
            int burnTime = itemInputs.getStackInSlot(1).getBurnTime(RecipeType.SMELTING)/4;
            if (burnTime+heat<=maxHeat) {
                heat+=burnTime;
                itemInputs.extractItem(1,1,false);
            }

        }
        heat=clamp(heat,0, maxHeat);
        //end heat loop

        //progress loop
        if (progress>=maxProgress) {
            boolean consume = true;
            if (this.getHeatPct()<0.82) {
                if (tank.getFluidAmount()+250<=tank.getCapacity()) {
                    tank.setFluid(new FluidStack(ModFluids.SOURCE_ZWOOP, 250+tank.getFluidAmount()));
                } else {
                    consume=false;
                }

            }
            //there are various factors that can cause or prevent the item from getting consumed.
            //if you succeed in managing the heat effectively, you will get product, but if you don't your item will simply get consumed.
            if (consume) {
                itemInputs.extractItem(0,1,false);
                progress=0;
            }
        }
        progress=clamp(progress, 0, maxProgress);

        if (tank.getFluidAmount()>=1000&&itemInputs.getStackInSlot(2).is(Items.BUCKET)&&itemInputs.getStackInSlot(3).isEmpty()) {
            itemInputs.extractItem(2,1,false);
            itemInputs.setStackInSlot(3, new ItemStack(ModItems.BUCKET_ZWOOP.get(), 1));
            tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        }



    }



    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public IFluidHandler getCapabilityHandler(DistilleryBlockEntity be, @Nullable Direction side) {
        return tank;
    }
    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new DistilleryMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("distillery_inventory", itemInputs.serializeNBT(pRegistries));
        pTag.putInt("distillery_progress", progress);
        pTag.putInt("distillery_max_progress", maxProgress);
        pTag.putInt("distillery_heat", heat);
        pTag.putInt("distillery_max_heat", maxHeat);
        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemInputs.deserializeNBT(pRegistries, pTag.getCompound("distillery_inventory"));
        progress = pTag.getInt("distillery_progress");
        maxProgress = pTag.getInt("distillery_max_progress");
        heat = pTag.getInt("distillery_heat");
        maxHeat = pTag.getInt("distillery_max_heat");

    }
}