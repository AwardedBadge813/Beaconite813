package net.awardedbadge813.beaconite813.entity;

import jdk.dynalink.linker.support.Lookup;
import net.awardedbadge813.beaconite813.Fluids.ModFluids;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.recipe.*;
import net.awardedbadge813.beaconite813.screen.custom.ReactorMenu;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.extensions.IHolderLookupProviderExtension;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.LookupTable;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.*;

public class ZwoopReactorBlockEntity extends BlockEntity implements MenuProvider {
    protected final ContainerData data;
    public int maxProgress = 1000;
    private int progress = 0;
    private boolean opened=false;
    private String recipe;
    boolean foundRecipe;

    public ZwoopReactorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REACTOR_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return progress;
                    }
                    case 1 -> {
                        return tank.getFluidAmount();
                    }
                    case 2-> {
                        return opened?1:0; //boolean to 0-1 conversion
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
                    case 2 -> {
                        opened=i1==1; //0-1 to boolean conversion
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

    public float getProgressPct() {
        return (float) progress / (float) maxProgress;
    }

    public boolean isDisabled(ToggleableBlockItem blockItem) {
        return blockItem.isDisabled();
    }

    public ItemStackHandler operatingSlot = new ItemStackHandler(2) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }



        //@Override
        /*public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {
                    return stack.is(Blocks.CHEST.asItem())||stack.is(ModItems.DIM_LATTICE.asItem());
                }
                case 1 -> {
                    return false;
                }

            }
            return false;
        }*/

        //first slot is the input, second is output
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot==1) {
                return super.extractItem(slot, amount, simulate);
            }
            if (slot<0) {
                return super.extractItem(slot+2, amount, simulate);
            }

            return ItemStack.EMPTY;

        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot==0) {
                return super.insertItem(slot, stack, simulate);
            }
            //override for this block to still use insert.
            // it is a guaranteed crash to reference negative numbers to a normal itemstackhandler, so any modded pipe, etc. will never use this to insert/extract.
            // since this block always has this special code in it, it can safely do this within this block only.
            if (slot<0) {
                slot+=2;
                ItemStack stackPresent = getStackInSlot(slot);
                if (stack.is(stackPresent.getItem())&& stackPresent.getMaxStackSize()-stack.getCount()-stackPresent.getCount()>=0) {
                    ItemStack result = new ItemStack(stackPresent.getItem(), stack.getCount()+stackPresent.getCount());
                    if (!simulate) {
                        setStackInSlot(slot, result);
                    }
                    return result;
                }
            }


            return stack;
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
    public ItemStackHandler manualSlots = new ItemStackHandler(2) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot==0) {
                return stack.is(Items.IRON_BARS);
            }
            return super.isItemValid(slot, stack);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot==0||opened) {
                return super.insertItem(slot, stack, simulate);
            }
            return stack;
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


    private FluidTank tank = new FluidTank(4000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getAmount()+fluid.getAmount()<=4000;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            if (maxDrain < 0) {
                super.drain(-maxDrain, action);
            }
            return FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
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
    //too lazy to not hardcode so this is my compromise, if this mod ever becomes popular god help my soul its easy to migrate
    public List<Item> ingredients = List.of(ModItems.DIM_LATTICE.get(), Items.CHEST, Items.GOLD_INGOT,Items.REDSTONE_BLOCK,Items.EMERALD,Items.DIAMOND);
    public HashMap<Item, ItemStack> recipes = getZwoopRecipes();
    public HashMap<Item, Integer> consumed = getConsumed();
    public HashMap<Item, Integer> zwoopCost = zwoopCost();
    public static HashMap<Item, ItemStack> getZwoopRecipes() {
        HashMap<Item, ItemStack> map = new HashMap<>();
        map.put(Items.CHEST, new ItemStack(ModBlocks.STORAGE_BEACON_BLOCK.asItem(), 1));
        map.put(ModItems.DIM_LATTICE.get(), new ItemStack(ModItems.DIM_LATTICE.get(),2));
        map.put(Items.GOLD_INGOT, new ItemStack(ModItems.STORAGE_FOCUS_DISTRIBUTE.get(),1));
        map.put(Items.REDSTONE_BLOCK, new ItemStack(ModItems.STORAGE_FOCUS_DEPOSIT.get(),1));
        map.put(Items.EMERALD, new ItemStack(ModItems.STORAGE_FOCUS_COLLECT.get(),1));
        map.put(Items.DIAMOND, new ItemStack(ModItems.STORAGE_FOCUS_CONC.get(),1));
        return map;
    }
    public static HashMap<Item, Integer> getConsumed() {
        HashMap<Item, Integer> map = new HashMap<>();
        map.put(Items.CHEST, 16);
        map.put(ModItems.DIM_LATTICE.get(), 1);
        map.put(Items.GOLD_INGOT, 1);
        map.put(Items.REDSTONE_BLOCK, 1);
        map.put(Items.EMERALD, 1);
        map.put(Items.DIAMOND, 1);
        return map;
    }
    public static HashMap<Item, Integer> zwoopCost() {
        HashMap<Item, Integer> map = new HashMap<>();
        map.put(Items.CHEST, 1000);
        map.put(ModItems.DIM_LATTICE.get(), 125);
        map.put(Items.GOLD_INGOT, 500);
        map.put(Items.REDSTONE_BLOCK, 500);
        map.put(Items.EMERALD, 500);
        map.put(Items.DIAMOND, 500);
        return map;
    }
    private Optional<RecipeHolder<ReactorRecipe>> getCurrentRecipe() {
        assert this.level != null;
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.REACTOR_TYPE.get(), new ReactorRecipeInput(operatingSlot.getStackInSlot(0), tank.getFluid()), level);
    }


    @Override
    public @NotNull BlockEntityType<ZwoopReactorBlockEntity> getType() {
        return ModBlockEntities.REACTOR_BE.get();
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("reactor_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        ItemStack M0 = manualSlots.getStackInSlot(0);
        //manual bar slot check
        opened = M0.getCount()>0;
        //manual slot input check and move it over to op slot 1 if valid item and closed
        ItemStack M1 = manualSlots.getStackInSlot(1);
        ItemStack OP0 = operatingSlot.getStackInSlot(0);
        ItemStack OP1 = operatingSlot.getStackInSlot(1);


        //conga line
        if (!opened) {
            if (!M1.isEmpty()) {
                //if the items are the same, combine them and input. if they are different send the one in input to OP1 and send the one in manual to OP0
                //this is to cycle inputs since if not OP0 is stuck forever until an item is made
                //adding to OP1 can be destructive, needs a second if.

                if (M1.getItem()==OP0.getItem()&& OP0.isEmpty()) {
                    manualSlots.setStackInSlot(1, ItemStack.EMPTY);
                    operatingSlot.setStackInSlot(0, new ItemStack(M1.getItem(), M1.getCount() + OP0.getCount()));
                } else if (OP1.is(ItemStack.EMPTY.getItem())){
                    manualSlots.setStackInSlot(1, ItemStack.EMPTY);
                    operatingSlot.setStackInSlot(0, M1);
                    operatingSlot.setStackInSlot(1, OP0);
                }
            }

            Optional<RecipeHolder<ReactorRecipe>> mayberecipe = getCurrentRecipe();
            OP0 = operatingSlot.getStackInSlot(0);
            M1 = manualSlots.getStackInSlot(1);
            OP0 = operatingSlot.getStackInSlot(0);
            OP1 = operatingSlot.getStackInSlot(1);
            if (mayberecipe.isPresent()) {
                ReactorRecipe recipe = mayberecipe.get().value();
                ItemStack inputItem = recipe.getIngredient();
                FluidStack fluidStack = recipe.fluidInput().getFluids()[0];
                ItemStack output = recipe.getResultItem(null);
                //prevents overwrites
                if (recipeAllowed(OP1, output)) {
                    progress++;
                    //if progress maxed, complete craft by finding the product and replacing op slot 2 with it.
                    if (progress>=maxProgress) {
                        progress=0;
                        operatingSlot.setStackInSlot(1, new ItemStack(OP1.getItem(), OP1.getCount()+output.getCount()));
                        operatingSlot.extractItem(-2, inputItem.getCount(), false);
                        tank.setFluid(new FluidStack(tank.getFluid().getFluid(), tank.getFluidAmount()-fluidStack.getAmount()));

                    }
                }



            }else {
                progress=0;
            }
        }else {
            progress=0;
        }








        /*
        if (!opened&&ingredients.contains(M1.getItem())) {
            //if the items are the same, combine them and input. if they are different send the one in input to OP1 and send the one in manual to OP0
            //this is to cycle inputs since if not OP0 is stuck forever until an item is made
            //adding to OP1 can be destructive, needs a second if statement
            manualSlots.setStackInSlot(1, ItemStack.EMPTY);
            if (M1.getItem()==OP0.getItem()) {
                operatingSlot.setStackInSlot(0, new ItemStack(M1.getItem(), M1.getCount() + OP0.getCount()));
            } else if (OP1.is(ItemStack.EMPTY.getItem())){
                operatingSlot.setStackInSlot(0, M1);
                operatingSlot.setStackInSlot(1, OP0);
            }
        }

         */













    }

    private boolean recipeAllowed(ItemStack op1, ItemStack output) {
        return op1.isEmpty()||(op1.is(output.getItem())&&op1.getMaxStackSize()-op1.getCount()-output.getCount()>=0);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(operatingSlot.getSlots());
        for (int i = 0; i< operatingSlot.getSlots(); i++) {
            inventory.setItem(i, operatingSlot.getStackInSlot(i));
        }
        SimpleContainer inventory2 = new SimpleContainer(manualSlots.getSlots());
        for (int i = 0; i< manualSlots.getSlots(); i++) {
            inventory2.setItem(i, manualSlots.getStackInSlot(i));
        }

        assert this.level != null;
        Containers.dropContents(this.level, this.worldPosition, inventory);
        Containers.dropContents(this.level, this.worldPosition, inventory2);
        level.invalidateCapabilities(getBlockPos());
    }

    private boolean ValidRecipe(ItemStack stack) {
        return recipes.get(stack.getItem())!=null
                && stack.getCount()>=consumed.get(stack.getItem())
                && tank.getFluidAmount()>=zwoopCost.get(stack.getItem());
    }


    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public FluidTank getTank() {
        return tank;
    }

    public IFluidHandler getCapabilityHandler(ZwoopReactorBlockEntity be, @Nullable Direction side) {
        return tank;
    }
    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new ReactorMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        pTag.put("reactor_inventory", operatingSlot.serializeNBT(pRegistries));
        pTag.put("reactor_manual", manualSlots.serializeNBT(pRegistries));
        pTag.putInt("fluid_inv", tank.getFluidAmount());
        tank.writeToNBT(pRegistries, pTag);
        pTag.putInt("reactor_progress", progress);
        pTag.putInt("reactor_max_progress", maxProgress);
        pTag.putBoolean("open", opened);
        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        operatingSlot.deserializeNBT(pRegistries, pTag.getCompound("reactor_inventory"));
        manualSlots.deserializeNBT(pRegistries, pTag.getCompound("reactor_manual"));
        tank.readFromNBT(pRegistries, pTag);
        progress = pTag.getInt("reactor_progress");
        maxProgress = pTag.getInt("reactor_max_progress");
        opened=pTag.getBoolean("open");


    }

    public IItemHandler getItemHandler(ZwoopReactorBlockEntity be, @Nullable Direction side) {
        if (side==Direction.DOWN) {
            return be.operatingSlot;
        }
        return be.operatingSlot;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public IFluidHandler getFluidHandler(ZwoopReactorBlockEntity zwoopEntity, Direction direction) {
        return tank;
    }
}