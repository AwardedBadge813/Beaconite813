package net.awardedbadge813.beaconite813.screen.custom;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.entity.ConstructorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import static net.minecraft.util.Mth.floor;

public class ConstructorMenu extends AbstractContainerMenu {

    public final ConstructorBlockEntity blockEntity;
    private final Level level;
    protected final ContainerData data;

    public ConstructorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(7));
    }

    public ConstructorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CONSTRUCTOR_MENU.get(), pContainerId);
        this.blockEntity = ((ConstructorBlockEntity) entity);
        this.level = inv.player.level();
                this.data=data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addInventory(blockEntity.inputItemHandler, 3, 5, 8, 17);
        addInventory(blockEntity.outputItemHandler, 3, 5, 116, 17);
        int leftXpos = 25;
        int lowYpos  =84;





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
    private static final int TE_INVENTORY_SLOT_COUNT = 30;  // must be the number of slots you have!
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
                    + TE_INVENTORY_SLOT_COUNT, false)) {
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

    public int getScaledArrowProgress(int arrowPixelSize) {
        int progress = this.data.get(6);
        int maxProgress = 40;
        if (!isPlacing()) {
            return 0;
        } else {
            return progress * arrowPixelSize/maxProgress;
        }
    }
    public boolean isPlacing() {
        return this.data.get(6)>0;

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
        pPlayer, ModBlocks.CONSTRUCTOR.get());
    }
}
