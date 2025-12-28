package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.awardedbadge813.beaconite813.screen.custom.ConstructorMenu;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
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
import net.minecraft.world.item.DyeColor;
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

import static java.lang.Math.min;
import static net.awardedbadge813.beaconite813.block.custom.ConstructorBlock.BASE_DOWN;

public class ConstructorBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {


    private boolean currentInverted= false;
    private int MaxPlacingLevel=20;

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

        @Override
        public ItemStack getStackInSlot(int slot) {
            this.validateSlotIndex(slot);
            if (this.stacks.get(slot).getItem() instanceof ToggleableItem && ((ToggleableItem) this.stacks.get(slot).getItem()).isDisabled()) {
                return ItemStack.EMPTY;
            }
            return this.stacks.get(slot);
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
    public boolean compareSelectedHeight(int height) {
        if(!isInverted()) {
            return height>getLevel().getMinBuildHeight();
        } else {
            return height<getLevel().getMaxBuildHeight();
        }
    }


    private void updateSelectedLevel(Level level) {
        int yLevel=0;
        for(int i=getBlockPos().getY()+getBuildYDirection(); compareSelectedHeight(i); i+=getBuildYDirection()) {
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
    private ItemStack extractFirstUsableItem(ItemStackHandler itemHandler, boolean simulate) {
        for(int slot=itemHandler.getSlots()-1; slot>=0; slot--) {
            if(checkBeaconBase(itemHandler.getStackInSlot(slot).getItem())) {
                itemHandler.extractItem(slot, 1, simulate);
                return itemHandler.getStackInSlot(slot);
            }
        }
        //if no block exists, just return air.
        return ItemStack.EMPTY;
    }
    public Block extractFirstUsableBlock(ItemStackHandler itemHandler, boolean simulate) {
        return Block.byItem(extractFirstUsableItem(itemHandler, simulate).getItem());
    }
    public int extractFirstUsableQuantity(ItemStackHandler itemHandler, boolean simulate) {
        return extractFirstUsableItem(itemHandler, simulate).getCount();
    }

    @Override
    public int getLayers(Level level, BlockPos pos) {
        int currentLayer;
        int y=pos.getY()+getBuildYDirection();
        for (currentLayer = 1; currentLayer <=20; currentLayer++){
            for (int x = getX(pos, currentLayer); x < getX(pos, currentLayer) + levelSize(currentLayer); x++) {
                for (int z = getZ(pos, currentLayer); z < (getZ(pos, currentLayer) + levelSize(currentLayer)); z++) {
                    if (!checkBlockStateForBeaconBlock(level, new BlockPos(x,y,z))){
                        return currentLayer -1;
                    }
                }
            }
            y+=getBuildYDirection();
        }
        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
    }



    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        //if the tick takes too long, don't re-tick till the system ends the operation.
        level.setBlockAndUpdate(pos, blockState.setValue(BASE_DOWN, !isInverted()));
        for (int i=0; i<20; i++) {
            BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
            updatedLevel=getLayers(level, pos);
            updateSelectedLevel(level);
            BlockState currentBlockState = level.getBlockState(currentPos);
            if (currentInverted!=isInverted()) {
                currentInverted = isInverted();
                yCurrent=pos.getY()+getBuildYDirection();
                xCurrent=pos.getX()-1;
                zCurrent=pos.getZ()-1;
            }



            this.counter%=40;
            Block toPlace = Blocks.AIR;
            if (!isQuarry()) {
                if(Dev) {
                    toPlace = level.getBlockState(new BlockPos(pos.getX(), yCurrent, pos.getZ())).getBlock();
                } else if (isViableReplacement(extractFirstUsableBlock(inputItemHandler, true), currentBlockState)) {
                    toPlace = extractFirstUsableBlock(inputItemHandler, false);
                }
            }
            //whatever the placing block is, it is good to place now.
            if (hasSpaceForItems(outputItemHandler, Block.getDrops(currentBlockState,
                    (ServerLevel) level,
                    currentPos,
                    level.getBlockEntity(currentPos)))) {
                if (isViableReplacement(toPlace, currentBlockState)) {
                    //retains center blocks.
                    if (!(currentPos.getX()==pos.getX() && currentPos.getZ()==pos.getZ() && isQuarry())) {
                        if (placeBlock(toPlace, isQuarry(), level)) {
                            placeItemsInContainer(outputItemHandler, Block.getDrops(currentBlockState,
                                    (ServerLevel) level,
                                    currentPos,
                                    level.getBlockEntity(currentPos)));
                        }
                    } else {
                        placeBlock(toPlace, isQuarry(), level);
                    }
                    isPlacing = true;
                    nextBlockLocation();
                } else if (userSelectedLevel != updatedLevel) {
                    nextBlockLocation();
                } else {
                    isPlacing = false;
                }
            }
            if (isPlacing) {
                counter++;
            } else {
                this.counter=0;
            }
        }
    }



    private boolean isViableReplacement(Block toPlace, BlockState blockState) {
        return (!blockState.is(toPlace) && !toPlace.defaultBlockState().is(Blocks.AIR) || (isQuarry())) && BlockValidForDestruction(blockState);
    }


    private boolean isPlacing;

    private boolean testBlockItem(Block block, Item item) {
            return block==Block.byItem(item);
    }

    private boolean BlockValidForDestruction(BlockState blockState) {
            //may change this later but is a decent 'should not destroy this block' placeholder for now
        assert level != null;
        return !blockState.is(BlockTags.WITHER_IMMUNE) || blockState.is(ModBlocks.ULTRA_DENSE_BEACONITE.get());

    }
    public BlockPos nextBlockLocation(){
        BlockPos pos = getBlockPos();
        int x=pos.getX();
        int y=pos.getY();
        int z=pos.getZ();
        int currentSize = (yCurrent-y)*getBuildYDirection();
        if (xCurrent+1<=x+currentSize) {
            xCurrent++;
        } else if (zCurrent+1<=z+currentSize) {
            zCurrent++;
            xCurrent=getX(pos, currentSize);
        } else if(currentSize+1> this.userSelectedLevel) {
            xCurrent = x - 1;
            yCurrent = y + getBuildYDirection();
            zCurrent = z - 1;
        } else {
            yCurrent+=getBuildYDirection();
            xCurrent=getX(pos, currentSize+1);
            zCurrent=getZ(pos, currentSize+1);
        }
        return new BlockPos(xCurrent, yCurrent, zCurrent);
    }

    //if the block is inverted, the constructor should place up instead of down.
    private int getBuildYDirection() {
        return this.isInverted() ? 1:-1;
    }

    //if there is an inversion talisman anywhere in the constructor, the constructor should become inverted.
    private boolean isInverted() {
        for (int slot=0; slot <inputItemHandler.getSlots(); slot++) {
            if(inputItemHandler.getStackInSlot(slot).getItem()==ModItems.INVERT_TALISMAN.get()) {
                return true;
            }
        }
        for (int slot=0; slot <outputItemHandler.getSlots(); slot++) {
            if(inputItemHandler.getStackInSlot(slot).getItem()==ModItems.INVERT_TALISMAN.get()) {
                return true;
            }
        }
        return false;
    }
    //if there is an inversion talisman anywhere in the constructor, the constructor should become inverted.
    private boolean isQuarry() {
        for (int slot=0; slot <inputItemHandler.getSlots(); slot++) {
            if(inputItemHandler.getStackInSlot(slot).getItem()==ModItems.QUARRY_TALISMAN.get()) {
                return true;
            }
        }
        for (int slot=0; slot <outputItemHandler.getSlots(); slot++) {
            if(inputItemHandler.getStackInSlot(slot).getItem()==ModItems.QUARRY_TALISMAN.get()) {
                return true;
            }
        }
        return false;
    }


    public boolean placeBlock(@NotNull Block block, boolean retainCenter, Level level) {
        // Initial declaration of positions, etc. that are used often in this method
        BlockPos pos = getBlockPos();
        BlockPos currentPos = new BlockPos(xCurrent, yCurrent, zCurrent);
        if(isPlacing) {
            assert level != null;
            level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.BLOCKS);
        }
        // I cannot be bothered to sort out this negation.
        //if the block is directly below the constructor, destroying it messes up the constructor mechanics, so those blocks should be retained.
        if(!(currentPos.getX()==pos.getX() && currentPos.getZ()==pos.getZ() && retainCenter)) {
            return BeaconiteLib.safeUpdateBlock(getLevel(), currentPos, block.defaultBlockState());
        }
        return false;
    }
    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
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
        private final boolean Dev = Config.DEV_MODE.get();


        protected final ContainerData data;
        private int userSelectedLevel=0;
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
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isPlacing) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.WHITE.getTextureDiffuseColor(), getLevel().getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

}
