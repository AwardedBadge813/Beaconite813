package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Fluids.ModFluids;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.recipe.*;
import net.awardedbadge813.beaconite813.screen.custom.DistilleryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static java.lang.Math.*;

public class DistilleryBlockEntity extends BlockEntity implements MenuProvider {
    private Direction facing;
    protected final ContainerData data;
    public int maxProgress = 1000;
    public int maxHeat = 5000;
    private int progress = 0;
    private int heat = 0;
    public int consumeAmount;

    public DistilleryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.DISTILLERY_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return progress;
                    }
                    case 1 -> {
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

                switch (i) {
                    case 0 -> {
                        progress = i1;
                    }
                    case 1 -> {
                        heat = i1;
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
        return (float) heat / (float) maxHeat;
    }

    public float getProgressPct() {
        return (float) progress / (float) maxProgress;
    }

    public boolean isDisabled(ToggleableBlockItem blockItem) {
        return blockItem.isDisabled();
    }

    public ItemStackHandler itemInputs = new ItemStackHandler(3) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }


        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {
                    return stack.is(ModItems.REACTIVE_CONCOCTION.get());
                }
                case 1 -> {
                    return stack.getBurnTime(RecipeType.SMELTING) > 0;
                }
                case 2 -> {
                    return stack.is(Items.BUCKET);
                }

            }
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public ItemStackHandler itemOutputs = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }


        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;

        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemInputs.getSlots());
        for (int i=0; i<itemInputs.getSlots(); i++) {
            inventory.setItem(i, itemInputs.getStackInSlot(i));
        }
        SimpleContainer inventoryOP = new SimpleContainer(itemOutputs.getSlots());
        for (int i=0; i<itemOutputs.getSlots(); i++) {
            inventoryOP.setItem(i, itemOutputs.getStackInSlot(i));
        }

        assert level != null;
        Containers.dropContents(level, this.worldPosition, inventory);
        Containers.dropContents(level, this.worldPosition, inventoryOP);
    }


    //this is the traditional code for extractItem, changing the name so items cant be extracted from input slots.
    public ItemStack takeItem(ItemStackHandler itemHandler, int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            //extract
            Item item = itemHandler.getStackInSlot(slot).getItem();
            itemHandler.setStackInSlot(slot, new ItemStack(itemHandler.getStackInSlot(slot).getItem(), max(itemHandler.getStackInSlot(slot).getCount()-1, 0)));
            return new ItemStack(item, 1);
        }
    }

    private FluidTank tank = new FluidTank(4000) {

        @Override
        protected void onContentsChanged() {
            setChanged();
            assert level != null;
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }
    };
    /*private Optional<RecipeHolder<DistilleryRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.DISTILLERY_TYPE.get(), new DistilleryRecipeInput(itemInputs.getStackInSlot(0)), level);
    }

     */


    @Override
    public @NotNull BlockEntityType<DistilleryBlockEntity> getType() {
        return ModBlockEntities.DISTILLERY_BE.get();
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("distillery_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        FluidStack output = new FluidStack(ModFluids.SOURCE_ZWOOP, 250);

        //heat loop
        float oldHeat=heat;
        heat--;
        float pct=this.getHeatPct();

        if (itemInputs.getStackInSlot(1).getCount()>0) {
            int burnTime = itemInputs.getStackInSlot(1).getBurnTime(RecipeType.SMELTING)/4;
            if (burnTime+heat<=maxHeat) {
                heat+=burnTime;
                takeItem(itemInputs, 1, 1, false);
            }

        }
        heat=clamp(heat,0, maxHeat);
        if ((int)(oldHeat/(float)maxHeat*15f)!=(int)((float)heat/(float)maxHeat*15f)) {
            setChanged();
        }

        //end heat loop

        //progress loop
        if (itemInputs.getStackInSlot(0).is(ModItems.REACTIVE_CONCOCTION.get())&&pct>=0.580f) {
            //RecipeHolder<DistilleryRecipe> recipe = recipeOptional.get();
            consumeAmount = 1; //recipe.value().ingredient().getItems()[0].getCount();
            progress+=(int)((1f-(abs(pct)-0.650f))*10f);
            if (progress>=maxProgress) {
                if (this.getHeatPct()<0.82) {
                    //there are various factors that can cause or prevent the item from getting consumed.
                    //if you succeed in managing the heat effectively, you will get product, but if you don't your item will simply get consumed.
                    if (canDepositFluid(output)) {
                        tank.setFluid(new FluidStack(output.getFluid(), output.getAmount()+tank.getFluidAmount()));
                        takeItem(itemInputs,0,consumeAmount,false);
                        progress=0;
                    }

                } else {
                    takeItem(itemInputs,0,1,false);
                    progress=0;
                }
            }
        } else {
            progress--;
        }
        progress=clamp(progress, 0, maxProgress);

        //bucket grabs zwoop from the itemslot
        if (tank.getFluidAmount()>=1000&&itemInputs.getStackInSlot(2).is(Items.BUCKET)&&itemOutputs.getStackInSlot(0).isEmpty()) {
            takeItem(itemInputs,2,1,false);
            itemOutputs.setStackInSlot(0, new ItemStack(ModItems.BUCKET_ZWOOP.get(), 1));
            tank.drain(1000, IFluidHandler.FluidAction.EXECUTE);
        }

        //deposits to block below
        IFluidHandler depositTank = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.offset(0,-1,0), Direction.UP);
        if (depositTank!=null) {
            int remaining = depositTank.fill(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
            tank.drain(remaining, IFluidHandler.FluidAction.EXECUTE);
        }





    }
    private boolean canDepositFluid(FluidStack stack) {
        //simple check for amount and type to make sure fluids can be combined.
        FluidStack tankFluid = tank.getFluid();
        boolean isSame = stack.getFluid().isSame(tankFluid.getFluid())||tank.isEmpty();
        int fillable = tank.getCapacity()-tankFluid.getAmount();
        int stackAmt = stack.getAmount();
        boolean canFill = fillable>=stackAmt;
        return isSame && canFill;
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
        pTag.put("distillery_output", itemOutputs.serializeNBT(pRegistries));
        pTag.putInt("fluid_inv", tank.getFluidAmount());
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
        itemOutputs.deserializeNBT(pRegistries, pTag.getCompound("distillery_output"));
        tank.setFluid(new FluidStack(ModFluids.SOURCE_ZWOOP, pTag.getInt("fluid_inv")));
        progress = pTag.getInt("distillery_progress");
        maxProgress = pTag.getInt("distillery_max_progress");
        heat = pTag.getInt("distillery_heat");
        maxHeat = pTag.getInt("distillery_max_heat");

    }

    public IItemHandler getItemHandler(DistilleryBlockEntity be, @Nullable Direction side) {
        if (side==Direction.DOWN) {
            return be.itemOutputs;
        }
        return be.itemInputs;
    }
}