package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.screen.custom.ConstructorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConstructorBlockEntity extends BlockEntity implements MenuProvider {

    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");

    private static final Log log = LogFactory.getLog(net.awardedbadge813.beaconite813.entity.ConstructorBlockEntity.class);


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
                    default -> {
                    }
                }

            }

            @Override
            public int getCount() {
                return 6;
            }

        };
    }


    public final ItemStackHandler inputItemHandler = new ItemStackHandler(6) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            };
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


    private void updateSelectedLevel() {
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
            //itemstacks wil all have been distributed, so true.
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
                if(!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                };
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

            Containers.dropContents(this.level, this.worldPosition, inventory);
            this.remove();
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("block.beaconite813.unstable_beacon_be");
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return new ConstructorMenu(i, inventory, this, this.data);
        }


        @Override
        public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
            return saveWithoutMetadata(pRegistries);
        }

        public boolean checkBeaconBase(Item item) {
            Block block = Block.byItem(item);
            if(block.defaultBlockState().is(BlockTags.BEACON_BASE_BLOCKS)) {
                return true;
            }
            return false;
        }




    public void tick(Level level, BlockPos pos, BlockState state) {
        BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
        updateSelectedLevel();
        int counter = 0;

        if (BlockValidForDestruction(currentPos)
                && hasSpaceForItems(outputItemHandler,
                Block.getDrops(level.getBlockState(currentPos),
                        (ServerLevel) level,
                        currentPos,
                        level.getBlockEntity(currentPos)))) {

            for (int i = inputItemHandler.getSlots() - 1; i >= 0; i--) {
                ItemStack inputItem = inputItemHandler.getStackInSlot(i);
                if (!inputItem.isEmpty()
                        && checkBeaconBase(inputItem.getItem())) {

                    inputItemHandler.extractItem(i, 1, testBlockItem(level.getBlockState(currentPos).getBlock(), inputItemHandler.getStackInSlot(i).getItem()));
                    if(!testBlockItem(level.getBlockState(currentPos).getBlock(), inputItemHandler.getStackInSlot(i).getItem())) {
                        placeItemsInContainer(outputItemHandler, Block.getDrops(level.getBlockState(currentPos), (ServerLevel) level, currentPos, level.getBlockEntity(currentPos)));
                        if(counter%20==0) {
                            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5f, 0.5f);
                        }
                    }
                    placeBlock(Block.byItem(inputItem.getItem()));
                    counter++;
                    break;
                }
            }
        }
    }

    private boolean testBlockItem(Block block, Item item) {
            Block blockver=Block.byItem(item);
            return block==blockver;
    }

    private boolean BlockValidForDestruction(BlockPos blockPos) {
            //may change this later but is a decent 'should not destroy this block' placeholder for now
            return !level.getBlockState(blockPos).is(BlockTags.WITHER_IMMUNE);

    }


    public void placeBlock (@NotNull Block block) {
        // Initial declaration of positions, etc. that are used often in this method
        BlockPos pos = getBlockPos();int x=pos.getX();int y=pos.getY();
        int z=pos.getZ();int currentSize = y-yCurrent;
        BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
        //places the block
        level.getBlockState(currentPos);

        //the majority of this method is literally just updating the current position.
        // This references the shape the pyramid should take and finds the next position in it.
        level.setBlockAndUpdate(currentPos, block.defaultBlockState());
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
        level.removeBlockEntity(getBlockPos());
    }


    private int getX(BlockPos pos, int n) {
        return pos.getX()-n;
    }
    private int getZ(BlockPos pos, int n) {
        return pos.getZ()-n;
    }




    @Override
        protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
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
        protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
            super.loadAdditional(pTag, pRegistries);
            inputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("input_inv"));
            outputItemHandler.deserializeNBT(pRegistries, pTag.getCompound("output_inv"));
            userSelectedLevel = pTag.getInt("selected_level");
            MaxPlacingLevel = pTag.getInt("current_level");
            xCurrent = pTag.getInt("x_current");
            yCurrent = pTag.getInt("y_current");
            zCurrent = pTag.getInt("z_current");


        }


        protected final ContainerData data;
        private int userSelectedLevel=0;
        private int MaxPlacingLevel = 20;
        private int xCurrent = getBlockPos().getX()-1;
        private int yCurrent= getBlockPos().getY()-1;
        private int zCurrent= getBlockPos().getZ()-1;
        private int updatedLevel=0;



}
