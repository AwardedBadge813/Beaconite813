package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.recipe.ModRecipes;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipe;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipeInput;
import net.awardedbadge813.beaconite813.screen.custom.RefineryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.logging.ILogger;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.Optional;

public class RefineryBlockEntity extends BlockEntity implements MenuProvider {
    private static final Log log = LogFactory.getLog(RefineryBlockEntity.class);

    public RefineryBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.REFINERY_BE.get(), pos, blockState);
        data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> RefineryBlockEntity.this.progress;
                    case 1 -> RefineryBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int val) {
                switch (i) {
                    case 0 -> RefineryBlockEntity.this.progress = val;
                    case 1 -> RefineryBlockEntity.this.maxProgress = val;
                };

            }

            @Override
            public int getCount() {
                return 2;
            }




        };

    }
    private static final int INPUT_SLOT_1=1;
    private static final int INPUT_SLOT_2=2;
    private static final int INPUT_SLOT_3=3;
    private static final int INPUT_SLOT_4=4;
    private static final int INPUT_SLOT_5=5;
    private static final int INPUT_SLOT_6=6;
    private static final int INPUT_SLOT_7=7;
    private static final int INPUT_SLOT_8=8;
    private static final int OUTPUT_SLOT=0;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 1600;

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("inventory", itemHandler.serializeNBT(pRegistries));
        pTag.putInt("refinery.progress", progress);
        pTag.putInt("refinery.max_progress", maxProgress);
        super.saveAdditional(pTag, pRegistries);
    }
    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        itemHandler.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
        progress = pTag.getInt("refinery.progress");
        maxProgress = pTag.getInt("refinery.max_progress");

    }




    public final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 64;
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
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i=0; i<itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.beaconite813.refinery");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RefineryMenu(i, inventory, this, this.data);
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if(hasRecipe()) {
            progress++;
            setChanged(level, blockPos, blockState);
            if(hasCraftingFinished()) {
                craftItem();
                resetProgress();

            }

        }else {
            resetProgress();
        }

    }

    private boolean hasCraftingFinished() {
        return progress>=maxProgress;
    }

    private void craftItem() {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();
        for(int i=0; i<=inventory_max; i++){
            if(i==0) {
                itemHandler.setStackInSlot(0, new ItemStack(output.getItem(), itemHandler.getStackInSlot(0).getCount()+output.getCount()));
            } else{
                itemHandler.extractItem(i, 1,  false);
            }
        }
    }


    private void resetProgress() {
        progress=0;
        maxProgress= Config.REFINERYCOOKTIME.getAsInt();
    }

    public static int inventory_max=8; //hardcoded for now
    public static int maxMatchingItemsCount = 8;
    private boolean hasRecipe() {
        Optional<RecipeHolder<RefineryRecipe>> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }
        for (int i=1; i<9; i++) {
            if(itemHandler.getStackInSlot(i).getItem()!=itemHandler.getStackInSlot(i).getItem() || itemHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }


        ItemStack output=recipe.get().value().output();
        return canInsertAmountIntoOutputSlot(output.getCount())
                && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<RefineryRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.REFINERY_TYPE.get(), new RefineryRecipeInput(itemHandler.getStackInSlot(1)), level);
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(0).isEmpty() || itemHandler.getStackInSlot(0).getItem()==output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int maxCount = itemHandler.getStackInSlot(0).isEmpty() ? 64 : itemHandler.getStackInSlot(0).getMaxStackSize();
        int currentCount=itemHandler.getStackInSlot(0).getCount();
        return maxCount>=currentCount+count;
    }






}
