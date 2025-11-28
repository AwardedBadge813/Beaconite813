package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.screen.custom.ConstructorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConstructorBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {


    public ConstructorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CONSTRUCTOR_BE.get(),
                pos,
                blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return userSelectedLevel;
                    }
                    case 1 -> {
                        return MaxPlacingLevel;
                    }
                    case 2 -> {
                        return xCurrent;
                    }
                    case 3 -> {
                        return yCurrent;
                    }
                    case 4 -> {
                        return zCurrent;
                    }
                    case 5 -> {
                        return updatedLevel;
                    }
                    case 6 -> {
                        return counter;
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
                        userSelectedLevel = i1;
                    }
                    case 1 -> {
                        MaxPlacingLevel = i1;
                    }
                    case 2 -> {
                        xCurrent = i1;
                    }
                    case 3 -> {
                        yCurrent = i1;
                    }
                    case 4 -> {
                        zCurrent = i1;
                    }
                    case 5 -> {
                        updatedLevel = i1;
                    }
                    case 6 -> {
                        counter = i1;
                    }
                    default -> {
                    }
                }

            }

            @Override
            public int getCount() {
                return 7;
            }

        };
    }


    public final ItemStackHandler inputItemHandler = new ItemStackHandler(15) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
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

    public void placeItemsInContainer(ItemStackHandler itemHandler, List<ItemStack> itemStacks) {
        for (ItemStack itemstack : itemStacks) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (itemstack.isEmpty()) {
                    break;
                }
                itemstack = itemHandler.insertItem(i, itemstack, false);
            }
        }
    }


    private void updateSelectedLevel(Level level) {
        int yLevel=0;
        for(int i=getBlockPos().getY()-1; i>level.getMinBuildHeight(); i--) {
            if(level.getBlockState(new BlockPos(getBlockPos().getX(), i, getBlockPos().getZ())).is(BlockTags.BEACON_BASE_BLOCKS)) {
                yLevel++;
            } else {
                break;
            }
        }
        userSelectedLevel=yLevel;
    }




    //I rewrote this so many times before figuring out how simulate works...
    public boolean hasSpaceForItems(ItemStackHandler itemHandler,  List<ItemStack> itemStacks) {
        for(ItemStack itemstack : itemStacks) {
            for(int i=0; i<itemHandler.getSlots(); i++) {
                itemstack=itemHandler.insertItem(i, itemstack, true);
            }
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
            //item stacks wil all have been distributed, so true.
        return true;

    }








        public final ItemStackHandler outputItemHandler = new ItemStackHandler(15) {
            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return stack.getMaxStackSize();
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



        public void drops() {
            SimpleContainer inventory = new SimpleContainer(outputItemHandler.getSlots()+inputItemHandler.getSlots());
            for (int i = 0; i< outputItemHandler.getSlots(); i++) {
                inventory.setItem(i, outputItemHandler.getStackInSlot(i));
            }
            for (int i=0; i<inputItemHandler.getSlots(); i++) {
                inventory.setItem(i+ outputItemHandler.getSlots(), inputItemHandler.getStackInSlot(i));
            }

            assert level != null;
            Containers.dropContents(level, this.worldPosition, inventory);
            this.remove();
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.literal("block.beaconite813.unstable_beacon_be");
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
            return new ConstructorMenu(i, inventory, this, this.data);
        }


        @Override
        public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
            return saveWithoutMetadata(pRegistries);
        }

        public boolean checkBeaconBase(Item item) {
            return Block.byItem(item).defaultBlockState().is(BlockTags.BEACON_BASE_BLOCKS);
        }




    public void tick(Level level, BlockPos pos) {
        BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
        this.updatedLevel=getLayers(level, pos);
        updateSelectedLevel(level);
        this.counter%=40;
        int lastCounter = counter;
        if (Dev) {
            for(int i=0; i<20; i++) {
                placeBlock(Blocks.NETHERITE_BLOCK.defaultBlockState().getBlock(), null);
            }

        }
        if (BlockValidForDestruction(currentPos)
                && hasSpaceForItems(outputItemHandler,
                Block.getDrops(level.getBlockState(currentPos),
                        (ServerLevel) level,
                        currentPos,
                        level.getBlockEntity(currentPos)))) {

            for (int i = inputItemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack inputItem = inputItemHandler.getStackInSlot(i);
                if (!inputItem.isEmpty()
                        && checkBeaconBase(inputItem.getItem()) && !inputItem.is(ModItems.QUARRY_TALISMAN.get())) {

                    inputItemHandler.extractItem(i, 1, testBlockItem(level.getBlockState(currentPos).getBlock(), inputItemHandler.getStackInSlot(i).getItem()));
                    if(Dev||!testBlockItem(level.getBlockState(currentPos).getBlock(), inputItemHandler.getStackInSlot(i).getItem())) {
                        placeItemsInContainer(outputItemHandler, Block.getDrops(level.getBlockState(currentPos), (ServerLevel) level, currentPos, level.getBlockEntity(currentPos)));

                        //fix this sound logic with a counter eventually
                        if(counter%20==1) {
                            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 0.5f);
                        }
                    }
                    placeBlock(Block.byItem(inputItem.getItem()), null);
                    this.counter++;
                    this.isPlacing = true;
                    break;
                } else if (inputItem.is(ModItems.QUARRY_TALISMAN.get())) {
                    if(pos.getX()!=xCurrent || pos.getZ()!=zCurrent) {
                        placeItemsInContainer(outputItemHandler, Block.getDrops(level.getBlockState(currentPos), (ServerLevel) level, currentPos, level.getBlockEntity(currentPos)));
                    }
                    placeBlock(Blocks.AIR.defaultBlockState().getBlock(), ModItems.QUARRY_TALISMAN.get());
                    if(counter%20==1) {
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 0.5f);
                    }
                    this.counter++;
                    this.isPlacing = true;
                    break;
                }

            }
        }
        if (lastCounter==counter && counter!=0) {
            this.counter=0;
            this.isPlacing = false;
        }
    }
    private boolean isPlacing;

    private boolean testBlockItem(Block block, Item item) {
            return block==Block.byItem(item);
    }

    private boolean BlockValidForDestruction(BlockPos blockPos) {
            //may change this later but is a decent 'should not destroy this block' placeholder for now
        assert level != null;
        return !level.getBlockState(blockPos).is(BlockTags.WITHER_IMMUNE);

    }


    public void placeBlock (@NotNull Block block, @Nullable Item item) {
        // Initial declaration of positions, etc. that are used often in this method
        BlockPos pos = getBlockPos();
        int x=pos.getX();
        int y=pos.getY();
        int z=pos.getZ();
        int currentSize = y-yCurrent;
        BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
        //places the block
        assert level != null;
        level.getBlockState(currentPos);
        if(isPlacing) {
            level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.BLOCKS);
        }

        // I cannot be bothered to sort out this negation.
        if(!(currentPos.getX()==pos.getX() && currentPos.getZ()==pos.getZ() && item == ModItems.QUARRY_TALISMAN.get())) {
            level.setBlockAndUpdate(currentPos, block.defaultBlockState());
        } else if (Dev) {
            level.setBlockAndUpdate(currentPos, Blocks.NETHERITE_BLOCK.defaultBlockState());
        }

        //the majority of this method is literally just updating the current position.
        // This references the shape the pyramid should take and finds the next position in it.
        if (xCurrent+1<=x+currentSize) {
            xCurrent++;
        } else if (zCurrent+1<=z+currentSize) {
            zCurrent++;
            xCurrent=getX(pos, currentSize);
        } else if(currentSize+1> this.userSelectedLevel) {
            xCurrent = x - 1;
            yCurrent = y - 1;
            zCurrent = z - 1;
        } else {
            yCurrent--;
            xCurrent=getX(pos, currentSize+1);
            zCurrent=getZ(pos, currentSize+1);
        }
    }
    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }


    private int getX(BlockPos pos, int n) {
        return pos.getX()-n;
    }
    private int getZ(BlockPos pos, int n) {
        return pos.getZ()-n;
    }




    @Override
        protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
            pTag.put("input_inv", inputItemHandler.serializeNBT(pRegistries));
            pTag.put("output_inv", outputItemHandler.serializeNBT(pRegistries));
            pTag.putInt("selected_level", userSelectedLevel);
            pTag.putInt("current_level", MaxPlacingLevel);
            pTag.putInt("x_current", xCurrent);
            pTag.putInt("y_current", yCurrent);
            pTag.putInt("z_current", zCurrent);

            super.saveAdditional(pTag, pRegistries);
        }
        @Override
        protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
            super.loadAdditional(pTag, pRegistries);
            inputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("input_inv"));
            outputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("output_inv"));
            userSelectedLevel = pTag.getInt("selected_level");
            MaxPlacingLevel = pTag.getInt("current_level");
            xCurrent = pTag.getInt("x_current");
            yCurrent = pTag.getInt("y_current");
            zCurrent = pTag.getInt("z_current");


        }
        private final boolean Dev = true;


        protected final ContainerData data;
        private int userSelectedLevel=0;
        private int MaxPlacingLevel = 20;
        private int xCurrent = getBlockPos().getX()-1;
        private int yCurrent= getBlockPos().getY()-1;
        private int zCurrent= getBlockPos().getZ()-1;
        private int updatedLevel=0;
        private int counter;


    @Override
    public boolean IsBeaconActive() {
        return isPlacing;
    }

    @Override
    public List<BeaconBeamHolder.BeaconBeamSection> getBeamSections() {
        BeaconBeamHolder.BeaconBeamSection beamSection = new BeaconBeamHolder.BeaconBeamSection();
        assert level != null;
        beamSection.setParams(16383998, level.getMaxBuildHeight() - getBlockPos().getY());
        this.beamSections=List.of(beamSection);

        return this.beamSections;
    }

}
