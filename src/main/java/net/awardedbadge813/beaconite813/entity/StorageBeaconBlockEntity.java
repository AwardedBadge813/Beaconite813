package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.awardedbadge813.beaconite813.screen.custom.StorageBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.lang.Math.*;
import static net.awardedbadge813.beaconite813.block.custom.StorageBeaconBlock.FACING;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.getClockwise;

public class StorageBeaconBlockEntity extends BeaconBeamHolder implements MenuProvider, CanFormBeacon {
    private static final Log log = LogFactory.getLog(StorageBeaconBlockEntity.class);
    private BlockCapabilityCache<IItemHandler, @Nullable Direction> capCache;
    private boolean currentInverted= false;
    private int MaxPlacingLevel=20;
    private boolean configoffset = true;
    private int activeSlots = 1;
    private int stackSize = 0;
    private Direction facing=getBlockState().getValue(FACING);
    public final HashMap<Direction, Direction> getChip = getClockwise();
    private StorageMode storageMode = StorageMode.NONE;
    private SafeCollect collectTarget = SafeCollect.PICKUP;
    private BlockEntity focusTarget;
    public static HashMap<StorageMode, String> storageModeNames = getModeNames();
    public static HashMap<SafeCollect, String> safeCollectNames = getCollectNames();
    public static HashMap<Item, StorageMode> modeMap = getModeMap();
    public static HashMap<Item, SafeCollect> collectMap = getCollectMap();
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
                    case 3 -> {
                        return stackSize;
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
                    case 2 -> {
                        activeSlots = i1;
                    }
                    case 3 -> {
                        stackSize=i1;
                    }
                }

            }

            @Override
            public int getCount() {
                return 4;
            }

        };
    }
    public int getStackSize() {
        return stackSize+16;
    }



    public final ItemStackHandler itemStorage = new ItemStackHandler(60) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 16+stackSize;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            assert level != null;
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        //these have to be overridden to do some voodoo magic with itemstacks. if this is removes and the normal version used, the game will crash as 99 is the limit for itemstacks.
        //of course, nobody cares if you just give a single item a new 'count' variable and decode it later >:)
        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            ListTag nbtTagList = new ListTag();

            for(int i = 0; i < this.stacks.size(); ++i) {
                if (!((ItemStack)this.stacks.get(i)).isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    itemTag.putInt("Count", this.stacks.get(i).getCount());
                    nbtTagList.add(new ItemStack(this.stacks.get(i).getItem(), 1).save(provider, itemTag));
                }
            }


            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", this.stacks.size());
            return nbt;
        }
        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.setSize(nbt.contains("Size", 4) ? nbt.getInt("Size") : this.stacks.size());
            ListTag tagList = nbt.getList("Items", 10);

            for(int i = 0; i < tagList.size(); ++i) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                int count = itemTags.getInt("Count");
                if (slot >= 0 && slot < this.stacks.size()) {
                    ItemStack.parse(provider, itemTags).ifPresent((stack) -> this.stacks.set(slot, new ItemStack(stack.getItem(), count)));
                }
            }

            this.onLoad();
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
    public final ItemStackHandler chipSlot = new ItemStackHandler(3) {
        //slot 0 defines the dimensional lattice, which increases storage size of the block.
        //slot 1 defines the foci slot, which defines the behaviour of the beacon.
        //slot 2 defines the trim slot, which defines whether the beacon can pickup items
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            if (slot==0) {
                int returnValue = Config.MAX_STORAGE_SIZE.getAsInt();
                if (returnValue==-1) {
                    return Integer.MAX_VALUE-16;
                }
                return max(returnValue-16, 0);
            } else {
                return 1;
            }

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
        public boolean isItemValid(int slot, ItemStack stack) {
            switch (slot) {
                case 0 -> {
                    return stack.is(ModItems.DIM_LATTICE);
                }
                case 1 -> {
                    return stack.is(ModItems.STORAGE_FOCUS_COLLECT)
                            ||stack.is(ModItems.STORAGE_FOCUS_DEPOSIT)
                            ||stack.is(ModItems.STORAGE_FOCUS_CONC)
                            ||stack.is(ModItems.STORAGE_FOCUS_DISTRIBUTE);
                }
                case 2 -> {
                    return stack.is(ModItems.STORAGE_TRIM_ADOWN)
                            ||stack.is(ModItems.STORAGE_TRIM_AUP)
                            ||stack.is(ModItems.STORAGE_TRIM_ALL)
                            ||stack.is(ModItems.STORAGE_TRIM_BDOWN)
                            ||stack.is(ModItems.STORAGE_TRIM_BUP)
                            ||stack.is(ModItems.STORAGE_TRIM_PU);
                }
                default -> {
                    return false;
                }
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
        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            ListTag nbtTagList = new ListTag();

            for(int i = 0; i < this.stacks.size(); ++i) {
                if (!((ItemStack)this.stacks.get(i)).isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    itemTag.putInt("Slot", i);
                    itemTag.putInt("Count", this.stacks.get(i).getCount());
                    nbtTagList.add(new ItemStack(this.stacks.get(i).getItem(), 1).save(provider, itemTag));
                }
            }


            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", nbtTagList);
            nbt.putInt("Size", this.stacks.size());
            return nbt;
        }
        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            this.setSize(nbt.contains("Size", 4) ? nbt.getInt("Size") : this.stacks.size());
            ListTag tagList = nbt.getList("Items", 10);

            for(int i = 0; i < tagList.size(); ++i) {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");
                int count = itemTags.getInt("Count");
                if (slot >= 0 && slot < this.stacks.size()) {
                    ItemStack.parse(provider, itemTags).ifPresent((stack) -> this.stacks.set(slot, new ItemStack(stack.getItem(), count)));
                }
            }

            this.onLoad();
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
    public List<ItemStack> getStacks(ItemStackHandler itemHandler) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i=0; i<itemHandler.getSlots(); i++) {
            itemStacks.add(itemHandler.getStackInSlot(i));
        }
        return itemStacks;
    }
    public SimpleContainer prepareStackForDisposal(ItemStack stack) {
        int Remainder = stack.getCount()%64;
        int stacks = max((stack.getCount()-Remainder)/64+1, 1);
        SimpleContainer container = new SimpleContainer(stacks);
        for (int i = 0; i<stacks-1; i++) {
            container.setItem(i, new ItemStack(stack.getItem(), 64));
        }
        container.setItem(stacks-1, new ItemStack(stack.getItem(), Remainder));
        return container;
    }
    public List<SimpleContainer> getDropContainers (ItemStackHandler handler) {
        List<SimpleContainer> containers = new ArrayList<>();
        for (ItemStack stack : getStacks(handler)) {
            SimpleContainer inventory = prepareStackForDisposal(stack);
            containers.add(inventory);
        }
        return containers;

    }
    public void drops() {
        List<SimpleContainer> container1 =  getDropContainers(itemStorage);
        List<SimpleContainer> container2 =  getDropContainers(chipSlot);
        for (SimpleContainer container : container1 ) {
            Containers.dropContents(level, this.worldPosition, container);

        }
        for (SimpleContainer container : container2 ) {
            Containers.dropContents(level, this.worldPosition, container);

        }
        invalidateCapabilities();
        this.remove();
    }
    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("block.beaconite813.storage_beacon_be");
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
    private int checkDirection = 20;

    private float collectRange = 10;

    public void tick(Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        updatedLevel=getLayers(level, pos);
        int blockRadius = updatedLevel; //just for easier notation



        counter=max(counter+1, 0); //in case the value gets extremely negative this resets it
        //hopefully this prevents lag spikes by making it so the collections are both approximately 1 second and also offset from other operations.

        //update settings 2 and 3
        storageMode = modeMap.get(chipSlot.getStackInSlot(1).getItem());
        collectTarget = collectMap.get(chipSlot.getStackInSlot(2).getItem());



        //update chipslot data
        stackSize=chipSlot.getStackInSlot(0).getCount();
        //determine which blocks are exempt from the requested operation

        //beacon actions, must be done last since it requires updated data
        if (counter>20&&(random()*2<=1||configoffset)) {
            getFocusTarget(level, pos);
            counter=0;
            RedirectTick(level, pos, blockState);
        }


    }

    private void ConcentrateHelper(Level level) {
        if (focusTarget!=null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, focusTarget.getBlockPos(), facing.getOpposite());
            if (handler != null) {
                int handlerSize = handler.getSlots();
                int storageSize = itemStorage.getSlots();

                for (int i=0; i<storageSize;i++) { //handler
                    ItemStack collected = itemStorage.extractItem(i,itemStorage.getSlotLimit(i),false);
                    for (int j=0;j<handlerSize;j++) {//itemStorage
                        collected = handler.insertItem(j,collected,false);
                        if (collected == ItemStack.EMPTY) {
                            break;
                        }
                    }
                    if (collected!=ItemStack.EMPTY) {
                        itemStorage.insertItem(i,collected,false);
                    }
                }
                //this is goofy so I'll annotate. removes 64 of an item from slot, then adds it to focused block (on the specified face!), then adds remainder back to slot.
            }
        }
    }
    private void RedirectHelper(Level level) {
        if (focusTarget!=null) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, focusTarget.getBlockPos(), facing.getOpposite());
            if (handler != null) {
                int handlerSize = handler.getSlots(); //i
                int storageSize = itemStorage.getSlots();//j
                for (int i=0; i<storageSize;i++) { //itemStorage
                    ItemStack collected = itemStorage.extractItem(i,itemStorage.getSlotLimit(i),false);
                    for (int j=0;j<handlerSize;j++) {//handler
                        collected = handler.insertItem(j,collected,false);
                        if (collected == ItemStack.EMPTY) {
                            break;
                        }
                    }
                    if (collected!=ItemStack.EMPTY) {
                        itemStorage.insertItem(i,collected,false);
                    }
                }
                //reversed version of ConcentrateHelper. goofy ahh function.
            }
        }
    }

    //determine which block is being 'focused' on. should deposit in the face the beacon is 'looking' at.
    public @Nullable BlockEntity getFocusTarget(Level level, BlockPos pos) {
        for (int i=0; i<checkDirection; i++) {
            pos=pos.relative(facing, 1);
            IItemHandler maybeHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, facing.getOpposite());
            if (maybeHandler!=null) {
                BlockEntity focus=level.getBlockEntity(pos);
                focusTarget = focus;
                return focus;
            }
        }
        return null;
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

    //mother function that sends the tick to the right function given the block states and enums. there's a lot here...
    private void RedirectTick(Level level, BlockPos pos, BlockState blockState) {
        boolean extract = storageMode==StorageMode.COLLECT||storageMode==StorageMode.FOCUS;
        boolean insert = storageMode==StorageMode.DEPOSIT||storageMode==StorageMode.REDIRECT;

        List<Direction> validFaces;
        boolean pickup = false;
        switch (collectTarget) {
            case SafeCollect.ALL -> {
                validFaces = List.of(
                        Direction.DOWN,
                        Direction.UP,
                        Direction.NORTH,
                        Direction.SOUTH,
                        Direction.EAST,
                        Direction.WEST);
                pickup = true;
            }
            case SafeCollect.ALL_UP -> {
                validFaces = List.of(
                        Direction.UP);
                pickup = true;

            }
            case SafeCollect.BLOCK_UP -> {
                validFaces = List.of(
                        Direction.UP);
                pickup = false;

            }
            case SafeCollect.ALL_DOWN -> {
                validFaces = List.of(
                        Direction.DOWN);
                pickup = true;
            }
            case SafeCollect.BLOCK_DOWN ->{
                validFaces = List.of(
                        Direction.DOWN);
                pickup = false;
            }
            case SafeCollect.PICKUP ->{
                validFaces = List.of();
                pickup = true; //counterintuitive but this means it won't immediately pickup items it drops
            }
            default -> {
                validFaces = List.of();
            }
        }
        if (pickup && (storageMode != StorageMode.DEPOSIT)) {
            collectItems(pos);
        } else if ((collectTarget == SafeCollect.PICKUP)&&(storageMode == StorageMode.DEPOSIT)) {
            List<SimpleContainer> container1 =  getDropContainers(itemStorage);
            for (int i=0;i<itemStorage.getSlots();i++) {
                itemStorage.setStackInSlot(i, ItemStack.EMPTY);
            }
            for (SimpleContainer container : container1 ) {
                Containers.dropContents(level, this.worldPosition.above(1), container);
            }
        }
        ArrayList<IItemHandler> operableHandlers = getListOfIItemHandlers(level, pos, updatedLevel, validFaces, focusTarget);
        log.debug(operableHandlers.toString());

        //the main function to determine what the beacon is ACTUALLY doing. most of the previous stuff is just prep for this.
        switch (storageMode) {
            case StorageMode.COLLECT ->  {
                //code to collect from block faces
                for (IItemHandler handler : operableHandlers) {
                    int handlerSize = handler.getSlots(); //i
                    int storageSize = itemStorage.getSlots();//j
                    for (int i=0; i<handlerSize;i++) { //handler
                        ItemStack collected = handler.extractItem(i,handler.getSlotLimit(i),false);
                        for (int j=0;j<storageSize;j++) {//itemStorage
                            collected = itemStorage.insertItem(j,collected,false);
                            if (collected == ItemStack.EMPTY) {
                                break;
                            }
                        }
                        if (collected!=ItemStack.EMPTY) {
                            handler.insertItem(i,collected,false);
                        }
                    }
                }
            }
            case StorageMode.DEPOSIT ->  {
                //code to deposit to block faces
                for (IItemHandler handler : operableHandlers) {
                    int handlerSize = handler.getSlots(); //i
                    int storageSize = itemStorage.getSlots();//j
                    for (int i=0; i<storageSize;i++) { //handler
                        ItemStack collected = itemStorage.extractItem(i,itemStorage.getSlotLimit(i),false);
                        for (int j=0;j<handlerSize;j++) {//itemStorage
                            collected = handler.insertItem(j,collected,false);
                            if (collected == ItemStack.EMPTY) {
                                break;
                            }
                        }
                        if (collected!=ItemStack.EMPTY) {
                            itemStorage.insertItem(i,collected,false);
                        }
                    }
                }
            }
            case StorageMode.FOCUS ->  {
                //code to collect from block faces
                for (IItemHandler handler : operableHandlers) {
                    int handlerSize = handler.getSlots(); //i
                    int storageSize = itemStorage.getSlots();//j
                    for (int i=0; i<handlerSize;i++) { //handler
                        ItemStack collected = handler.extractItem(i,handler.getSlotLimit(i),false);
                        for (int j=0;j<storageSize;j++) {//itemStorage
                            collected = itemStorage.insertItem(j,collected,false);
                            if (collected == ItemStack.EMPTY) {
                                break;
                            }
                        }
                        if (collected!=ItemStack.EMPTY) {
                            handler.insertItem(i,collected,false);
                        }
                    }
                }
                ConcentrateHelper(level);
            }
            case StorageMode.REDIRECT ->  {
                //code to deposit to block faces
                for (IItemHandler handler : operableHandlers) {
                    int handlerSize = handler.getSlots(); //i
                    int storageSize = itemStorage.getSlots();//j
                    for (int i=0; i<storageSize;i++) { //handler
                        ItemStack collected = itemStorage.extractItem(i,itemStorage.getSlotLimit(i),false);
                        for (int j=0;j<handlerSize;j++) {//itemStorage
                            collected = handler.insertItem(j,collected,false);
                            if (collected == ItemStack.EMPTY) {
                                break;
                            }
                        }
                        if (collected!=ItemStack.EMPTY) {
                            itemStorage.insertItem(i,collected,false);
                        }
                    }
                }

                RedirectHelper(level);
            }
        }



    }

    private ArrayList<IItemHandler> getListOfIItemHandlers(Level level, BlockPos pos, int updatedLevel, List<Direction> validFaces, BlockEntity focusTarget) {
        List<BlockPos> reject = List.of(focusTarget==null?pos:focusTarget.getBlockPos(), pos);
        ArrayList<IItemHandler> list = new ArrayList<>();
        for (int x = -updatedLevel; x<=updatedLevel;x++) {
            for (int y = -updatedLevel; y<=updatedLevel;y++) {
                for (int z = -updatedLevel; z<=updatedLevel;z++) {
                    //inside this is the block logic code, outside just identifies the loop through a radius of the beacon level.

                    BlockPos here = pos.offset(x,y,z);
                    if (!reject.contains(here)) {
                        for (Direction dir : validFaces) {
                            IItemHandler maybeHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, here, dir);
                            if ((maybeHandler!=null) && (!list.contains(maybeHandler))) {
                                list.add(maybeHandler);
                                //theoretically if the handlers are iterated over, it shouldn't matter if there are duplicates, but it doesn't sound very optimized.
                            }
                        }
                    }
                }
            }
        }
        return list;
        //this feels very resource-hungry.
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
            pTag.put("chip_inv", chipSlot.serializeNBT(pRegistries));
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
            chipSlot.deserializeNBT(pRegistries, pTag.getCompound("chip_inv"));
            userSelectedLevel = pTag.getInt("selected_level");
            updatedLevel = pTag.getInt("current_level");
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
        return true;
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(IsBeaconActive()) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.WHITE.getTextureDiffuseColor(), getLevel().getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
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


    public @Nullable ItemStackHandler getCapability(BlockEntity entity, @Nullable Direction direction) {
        //when cap is being made sends 1 to force the check to fail
        StorageBeaconBlockEntity be = entity instanceof StorageBeaconBlockEntity casted? casted:null;
        Direction dir = direction instanceof Direction casted?casted:null;
        //only expose the chip slot if the proper face is being accessed, which is directly clockwise to the facing direction.
        if (dir == null) {
            return be==null?null:be.itemStorage;
        }
        if (dir==getChip.get(facing)) {
            return chipSlot;
        }
        return be.itemStorage;
    }

    public BlockEntity getFocus() {
        return focusTarget;
    }

    //StorageMode defines what the beacon is doing, safecollect defines what it's doing it to. focus mode is slightly different since it always outputs to a block,
    // and the focus target is always omitted from extraction.
    public enum StorageMode {
        NONE,
        COLLECT,
        DEPOSIT,
        FOCUS,
        REDIRECT
    }
    public enum SafeCollect {
        NONE,
        PICKUP,
        BLOCK_DOWN,
        BLOCK_UP,
        ALL_DOWN,
        ALL_UP,
        ALL
        //ALL_DOWN means pickup and the bottom of blocks. ALL means ALL faces of blocks and pickup. note the exception for FOCUS target if applicable.
    }
    //slot 1 of chipSlot determines what mode the beacon is in.
    public static HashMap<StorageMode, String> getModeNames() {
        HashMap<StorageMode, String> map = new HashMap<>();
        map.put(StorageMode.NONE, "None");
        map.put(StorageMode.COLLECT, "Collect");
        map.put(StorageMode.DEPOSIT, "Deposit");
        map.put(StorageMode.FOCUS, "Concentrate");
        map.put(StorageMode.REDIRECT, "Redirect");
        return map;
    }
    //slot 1 of chipSlot determines what mode the beacon is in.
    public static HashMap<Item, StorageMode> getModeMap() {
        HashMap<Item, StorageMode> map = new HashMap<>();
        map.put(ItemStack.EMPTY.getItem(), StorageMode.NONE);
        map.put(ModItems.STORAGE_FOCUS_COLLECT.get(), StorageMode.COLLECT);
        map.put(ModItems.STORAGE_FOCUS_DEPOSIT.get(), StorageMode.DEPOSIT);
        map.put(ModItems.STORAGE_FOCUS_CONC.get(), StorageMode.FOCUS);
        map.put(ModItems.STORAGE_FOCUS_DISTRIBUTE.get(), StorageMode.REDIRECT);
        return map;
    }
    //slot 2 of chipSlot determines what the beacon collects.
    public static HashMap<SafeCollect, String> getCollectNames() {
        HashMap<SafeCollect, String> map = new HashMap<>();
        map.put(SafeCollect.NONE, "None");
        map.put(SafeCollect.PICKUP, "Pickup (Can Drop Items!)");
        map.put(SafeCollect.BLOCK_DOWN, "Bottom of Blocks");
        map.put(SafeCollect.BLOCK_UP, "Top of Blocks");
        map.put(SafeCollect.ALL_DOWN, "Pickup & Bottom of Blocks");
        map.put(SafeCollect.ALL_UP, "Pickup & Top of Blocks");
        map.put(SafeCollect.ALL, "EVERYTHING!");


        return map;
    }

    public static HashMap<Item, SafeCollect> getCollectMap() {
        HashMap<Item, SafeCollect> map = new HashMap<>();
        map.put(ItemStack.EMPTY.getItem(), SafeCollect.NONE);
        map.put(ModItems.STORAGE_TRIM_PU.get(), SafeCollect.PICKUP);
        map.put(ModItems.STORAGE_TRIM_BDOWN.get(),SafeCollect.BLOCK_DOWN);
        map.put(ModItems.STORAGE_TRIM_BUP.get(),SafeCollect.BLOCK_UP);
        map.put(ModItems.STORAGE_TRIM_ADOWN.get(),SafeCollect.ALL_DOWN);
        map.put(ModItems.STORAGE_TRIM_AUP.get(),SafeCollect.ALL_UP);
        map.put(ModItems.STORAGE_TRIM_ALL.get(),SafeCollect.ALL);


        return map;
    }

    public StorageMode getMode() {
        return storageMode;
    }
}
