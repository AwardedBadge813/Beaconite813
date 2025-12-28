package net.awardedbadge813.beaconite813.screen.custom;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.entity.RegalBeaconBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;

import static java.lang.Math.min;

public class RegalBeaconMenu extends AbstractContainerMenu {
    private static final int PAYMENT_SLOT = 0;
    private static final int SLOT_COUNT = 1;
    private final RegalBeaconBlockEntity blockEntity;
    protected final ContainerData data;
    public RegalBeaconMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }



    public RegalBeaconMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.REGAL_BEACON_MENU.get(), pContainerId);
        this.blockEntity = ((RegalBeaconBlockEntity) entity);
        this.data=data;
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        int leftXpos = 25;
        int lowYpos  =84;


        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, leftXpos+26, lowYpos-44));
        this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, leftXpos+86, lowYpos-44));

        addDataSlots(data);
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public boolean goldActive() {
        return this.data.get(2)>0;
    }
    public boolean lootActive() {
        return this.data.get(3)>0;
    }

    public int getScaledGoldTime(int arrowPixelSize) {
        int progress=this.data.get(2);
        int maxProgress=Config.MAX_GOLD_TIME.getAsInt();
        return maxProgress !=0 && progress !=0 ? progress * arrowPixelSize/maxProgress:0;
    }
    public int getScaledLootTime(int arrowPixelSize) {
        int progress=this.data.get(3);
        int maxProgress=Config.MAX_LOOT_TIME.getAsInt();
        return maxProgress !=0 && progress !=0 ? progress * arrowPixelSize/maxProgress:0;
    }

    private void addPlayerInventory(Inventory inventory){
        for (int i=0; i<3; ++i){
            for (int l=0; l<9; ++l){
                this.addSlot(new Slot(inventory, l+i*9+9, xHotbar+l*18, yHotbar-57+i*18));

            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i=0; i<9; ++i){
            this.addSlot(new Slot(inventory, i, xHotbar+i*18, yHotbar));

        }
    }
    int xHotbar = 8;
    int yHotbar = 186;

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
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
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


}

