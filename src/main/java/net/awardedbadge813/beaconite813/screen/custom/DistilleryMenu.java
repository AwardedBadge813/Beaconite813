package net.awardedbadge813.beaconite813.screen.custom;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.entity.DistilleryBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class DistilleryMenu extends AbstractContainerMenu {

    public final DistilleryBlockEntity blockEntity;
    private final Level level;
    protected ContainerData data;

    public DistilleryMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(9));
    }

    public DistilleryMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.DISTILLERY_MENU.get(), pContainerId);
        this.blockEntity = ((DistilleryBlockEntity) entity);
        this.level = inv.player.level();
        this.data=data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.addSlot(new SlotItemHandler(blockEntity.itemInputs, 0, 43, 28));
        this.addSlot(new SlotItemHandler(blockEntity.itemInputs, 1, 44, 78));
        this.addSlot(new SlotItemHandler(blockEntity.itemInputs, 2, 104, 87));
        this.addSlot(new SlotItemHandler(blockEntity.itemInputs, 3, 147, 97) {
            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });







        addDataSlots(data);
    }
    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT-1, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
    private void addInventory(ItemStackHandler itemHandler, int width, int height, int x, int y){
        for (int i=0; i<height; ++i){
            for (int l=0; l<width; ++l){
                this.addSlot(new SlotItemHandler(itemHandler, l+i*width, x+l*18, y+i*18));

            }
        }
    }






    private void addPlayerInventory(Inventory inventory){
        for (int i=0; i<3; ++i){
            for (int l=0; l<9; ++l){
                this.addSlot(new Slot(inventory, l+i*9+9, xHotbar +l*18, yHotbar -57+i*18));

            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i=0; i<9; ++i){
            this.addSlot(new Slot(inventory, i, xHotbar +i*18, yHotbar));
        }
    }
    public static int yHotbar =186;
    public static int xHotbar =8;

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
        pPlayer, ModBlocks.DISTILLERY_BLOCK.get());
    }

    public int getHeatMeter() {
        return (int)(((1f-(float)(data.get(1))/(float)blockEntity.maxHeat)*40f));
    }

    public int getZwoopMeter() {
        return (int)(((1f-(float)(data.get(0))/(float)blockEntity.maxProgress)*40f));
    }

    public int getZwoopTank() {
        return (int)(((1f-(data.get(2)/4000f))*16f));
    }
}
