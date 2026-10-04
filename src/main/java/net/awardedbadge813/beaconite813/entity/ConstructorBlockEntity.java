package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ConstructorBlock;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.awardedbadge813.beaconite813.screen.custom.ConstructorMenu;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.min;
import static net.awardedbadge813.beaconite813.block.custom.ConstructorBlock.BASE_DOWN;
import static net.neoforged.neoforge.capabilities.BlockCapability.createVoid;

public class ConstructorBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {
    private BlockCapabilityCache<IItemHandler, @Nullable Direction> capCache;
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

    @Override
    public void onLoad() {
        // Later, for example in `onLoad` for a block entity:
        if (level instanceof ServerLevel serverLevel) {
            this.capCache = BlockCapabilityCache.create(
                    Capabilities.ItemHandler.BLOCK, // capability to cache
                    serverLevel, // level
                    getBlockPos(), // target position
                    Direction.NORTH // context
            );
        }
        super.onLoad();
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
        public @NotNull ItemStack getStackInSlot(int slot) {
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
            assert getLevel() != null;
            return height>getLevel().getMinBuildHeight();
        } else {
            assert getLevel() != null;
            return height<getLevel().getMaxBuildHeight();
        }
    }


    private void updateSelectedLevel(BlockPos pos, Level level) {
        int yLevel=0;
        for(int i=pos.getY()+getBuildYDirection(); compareSelectedHeight(i); i+=getBuildYDirection()) {
            if(level.getBlockState(new BlockPos(pos.getX(), i, pos.getZ())).is(BlockTags.BEACON_BASE_BLOCKS)) {
                yLevel++;
            } else {
                break;
            }
        }
        userSelectedLevel=yLevel;
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


    public int getAnything(Level level, BlockPos pos) {
        int currentLayer;
        int y=getBuildYDirection();
        for (currentLayer = 1; currentLayer <=20; currentLayer++){
            for (int x = -1*currentLayer; x <currentLayer; x++) {
                for (int z = -1*currentLayer; z < currentLayer; z++) {
                    if (!level.getBlockState(pos.offset(x,y,z)).is(Blocks.AIR)&&!(x==0&&z==0)){
                        return currentLayer -1;
                    }
                }
            }
            y+=getBuildYDirection();
        }
        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
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

        //rewritten code because it was kinda awful
        updateSelectedLevel(pos, level);
        updatedLevel = getLayers(level, pos);
        level.setBlockAndUpdate(pos, blockState.setValue(BASE_DOWN, !isInverted()));
        //will have it so it checks any unfinished layer every 20 ticks.
        int layerLevel = updatedLevel + 1;
        if (isQuarry()) {
            layerLevel = getAnything(level, pos)+1;
        }
        int dy = layerLevel * getBuildYDirection();
        BlockPos currentPos;
        if (level.getGameTime()%40!=0) {
            return;
        }
        if (userSelectedLevel>updatedLevel) {
            isPlacing=true;
            counter=40;
        } else {
            counter=0;
        }

        if (layerLevel <= userSelectedLevel) {
            for (int x = -1*layerLevel; x < layerLevel + 1; x++) {
                for (int z = -1*layerLevel; z < layerLevel + 1; z++) {
                    currentPos = pos.offset(x, dy, z);
                    Block testBlock;
                    if (!isQuarry()) {
                        testBlock = extractFirstUsableBlock(inputItemHandler, true);
                    }else {
                        testBlock = Blocks.AIR;
                    }
                    if (level instanceof ServerLevel serverLevel) {
                        List<ItemStack> drops = Block.getDrops(serverLevel.getBlockState(currentPos), serverLevel, currentPos, serverLevel.getBlockEntity(currentPos));
                        if (!testBlock.defaultBlockState().is(serverLevel.getBlockState(currentPos).getBlock())
                                && (!testBlock.defaultBlockState().is(Blocks.AIR) || isQuarry())
                                && BlockValidForDestruction(serverLevel.getBlockState(currentPos))
                                && canDepositItems(drops, outputItemHandler)
                                && !(x == 0 && z == 0)) {

                            if (!Config.MASTER_DESTROY_TOGGLE.getAsBoolean()) {
                                if (serverLevel.setBlockAndUpdate(currentPos, testBlock.defaultBlockState())) {
                                    placeItemsInContainer(outputItemHandler, drops);
                                    if (!isQuarry()) {
                                        extractFirstUsableBlock(inputItemHandler, false);
                                    }
                                }


                            }
                        }

                    }

                }

            }
        }
    }

    private boolean canDepositItems(BlockState blockState, ItemStackHandler outputItemHandler, BlockPos pos, Level level) {
        if (level instanceof ServerLevel serverLevel) {
            List<ItemStack> items = Block.getDrops(blockState, serverLevel, pos, serverLevel.getBlockEntity(pos));
            return canDepositItems(items, outputItemHandler);
        }
        return false;
    }

    private boolean canDepositItems(List<ItemStack> items, ItemStackHandler outputItemHandler) {
            //initialize a new container, and try storing the items in it. if successful, the actual items can be stored.
            ItemStackHandler container = new ItemStackHandler(outputItemHandler.getSlots());
            for (int i=0; i<outputItemHandler.getSlots(); i++) {
                container.setStackInSlot(i,outputItemHandler.getStackInSlot(i));
            }

            for (ItemStack itemStack : items) {
                ItemStack item = itemStack;
                for (int i=0; i<outputItemHandler.getSlots(); i++) {
                    item = container.insertItem(i,item, false);
                }
                if (!item.isEmpty()) {
                    return false;
                }
            }
            return true;
    }

    private boolean isPlacing;

    private boolean BlockValidForDestruction(BlockState blockState) {
            //may change this later but is a decent 'should not destroy this block' placeholder for now
        assert level != null;
        return !blockState.is(BlockTags.WITHER_IMMUNE) || blockState.is(ModBlocks.ULTRA_DENSE_BEACONITE.get());

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

    public ItemStackHandler getinputItemHandler() {
        return this.inputItemHandler;
    }
    public ItemStackHandler getoutputItemHandler() {
        return this.outputItemHandler;
    }

    public ItemStackHandler getCapabilityHandler(BlockEntity be, Direction side) {
        if (be instanceof ConstructorBlockEntity constructorBlockEntity) {
            if (side == Direction.DOWN) {
                return constructorBlockEntity.getoutputItemHandler();
            }
            return constructorBlockEntity.getinputItemHandler();
        }
        return null;
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
