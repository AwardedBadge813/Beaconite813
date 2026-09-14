package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.awardedbadge813.beaconite813.screen.custom.StorageBeaconMenu;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.*;

public class StorageBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {

    private boolean currentInverted= false;
    private int MaxPlacingLevel=20;
    private boolean configoffset = true;
    private int activeSlots = 1;
    private int stackSize = 16;
    public StorageBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.STORAGE_BEACON_BE.get(),
                pos,
                blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                switch (i) {
                    case 0 -> {
                        return updatedLevel;
                    }
                    case 1 -> {
                        return counter;
                    }
                    case 2 -> {
                        return activeSlots;
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
                        updatedLevel = i1;
                    }
                    case 1 -> {
                        counter = i1;
                    }
                    case 3 -> {
                        activeSlots = i1;
                    }
                }

            }

            @Override
            public int getCount() {
                return 3;
            }

        };
    }



    public final ItemStackHandler itemStorage = new ItemStackHandler(60) {
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
        public @NotNull ItemStack getStackInSlot(int slot) {
            this.validateSlotIndex(slot);
            if (this.stacks.get(slot).getItem() instanceof ToggleableItem && ((ToggleableItem) this.stacks.get(slot).getItem()).isDisabled()) {
                return ItemStack.EMPTY;
            }
            return this.stacks.get(slot);
        }
    };


    public ItemStack placeItemsInContainer(ItemStackHandler itemHandler, ItemStack itemStack) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (itemStack.isEmpty()) {
                break;
            }
            itemStack = itemHandler.insertItem(i, itemStack, false);
        }
        return itemStack;
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemStorage.getSlots());
        for (int i = 0; i< itemStorage.getSlots(); i++) {
            inventory.setItem(i, itemStorage.getStackInSlot(i));
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
        return new StorageBeaconMenu(i, inventory, this, this.data);
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

    @Override
    public int getLayers(Level level, BlockPos pos) {
        int currentLayer;
        int y=pos.getY();
        for (currentLayer = 1; currentLayer <=20; currentLayer++){
            for (int x = getX(pos, currentLayer); x < getX(pos, currentLayer) + levelSize(currentLayer); x++) {
                for (int z = getZ(pos, currentLayer); z < (getZ(pos, currentLayer) + levelSize(currentLayer)); z++) {
                    if (!checkBlockStateForBeaconBlock(level, new BlockPos(x,y,z))){
                        return currentLayer -1;
                    }
                }
            }
            y-=1;
        }
        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
    }

    private float collectRange = 10;

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        updatedLevel=getLayers(level, pos);
        if (counter>20&&(random()*2<=1||configoffset)) {
            collectItems(pos);
            counter=0;
        }
        counter=max(counter+1, 0); //in case the value gets extremely negative this resets it
        //hopefully this prevents lag spikes by making it so the collections are both approximately 1 second and also offset from other operations.

    }

    private void collectItems(BlockPos pos) {
        AABB collectionRange = new AABB(pos).inflate(collectRange);
        List<ItemEntity> droppedItems = level.getEntitiesOfClass(ItemEntity.class, collectionRange);
        for (ItemEntity item:droppedItems) {
            ItemStack itemStack = item.getItem();
            if (ShouldDeposit(itemStorage, itemStack)) {
                itemStack = placeItemsInContainer(itemStorage, itemStack);
                if (itemStack.getCount() == 0) {
                    item.remove(Entity.RemovalReason.DISCARDED);
                } else {
                    item.setItem(itemStack);
                }

            }
        }
    }

    private boolean ShouldDeposit(ItemStackHandler itemHandler, ItemStack itemStack) {
        int beginCount = itemStack.getCount();
        for(int i=0; i<itemHandler.getSlots(); i++) {
            itemStack=itemHandler.insertItem(i, itemStack, true);
        }if (itemStack.getCount()==beginCount) {
            return false;
        }
        //item stacks will all have been distributed, so true.
        return true;
    }


    private boolean isPlacing;

    private boolean BlockValidForDestruction(BlockState blockState) {
            //may change this later but is a decent 'should not destroy this block' placeholder for now
        assert level != null;
        return !blockState.is(BlockTags.WITHER_IMMUNE) || blockState.is(ModBlocks.ULTRA_DENSE_BEACONITE.get());

    }

    //if there is an inversion talisman anywhere in the constructor, the constructor should become inverted.
    private boolean isInverted() {
        for (int slot = 0; slot < itemStorage.getSlots(); slot++) {
            if(itemStorage.getStackInSlot(slot).getItem()==ModItems.INVERT_TALISMAN.get()) {
                return true;
            }
        }
        return false;
    }

    public void remove() {
        assert level != null;
        level.removeBlockEntity(getBlockPos());
    }






    @Override
        protected void saveAdditional(CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
            pTag.put("store_inv", itemStorage.serializeNBT(pRegistries));
            pTag.putInt("current_level", updatedLevel);
            pTag.putInt("x_current", xCurrent);
            pTag.putInt("y_current", yCurrent);
            pTag.putInt("z_current", zCurrent);

            super.saveAdditional(pTag, pRegistries);
        }
        @Override
        protected void loadAdditional(@NotNull CompoundTag pTag, HolderLookup.@NotNull Provider pRegistries) {
            super.loadAdditional(pTag, pRegistries);
            itemStorage.deserializeNBT(pRegistries, pTag.getCompound("store_inv"));
            userSelectedLevel = pTag.getInt("selected_level");
            updatedLevel = pTag.getInt("current_level");
            xCurrent = pTag.getInt("x_current");
            yCurrent = pTag.getInt("y_current");
            zCurrent = pTag.getInt("z_current");


        }
        private final boolean Dev = Config.DEV_MODE.get();

    public ItemStackHandler getCapabilityHandler(StorageBeaconBlockEntity be, Direction side) {
        return be.itemStorage;
    }

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
