package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Thread.sleep;

public class BaseBeaconBlockEntity extends BlockEntity {

    public BaseBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BASE_BEACON_BLOCK_BE.get(), pos, blockState);
    }
    public void kill() {
        assert getLevel() != null;
        getLevel().removeBlockEntity(getBlockPos());
    }



    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    public boolean isDisabled(ToggleableBlockItem blockItem) {
        return blockItem.isDisabled();
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        AABB aabb = new AABB(blockPos).inflate(0.5);
        checkForInput(aabb, blockPos, ModBlocks.WRATHFUL_FLESH.get(), ModBlocks.VENGEANT_BEACON_BLOCK.get(), level, -1);
        checkForInput(aabb, blockPos, ModBlocks.BLAZING_MAGMA.get(), ModBlocks.IGNEOUS_BEACON_BLOCK.get(), level, -1);
        checkForInput(aabb, blockPos, ModBlocks.COINS_BLOCK.get(), ModBlocks.REGAL_BEACON_BLOCK.get(), level, -1);
        checkForInput(aabb, blockPos, ModBlocks.LIVING_BLOCK.get(), ModBlocks.LIVING_BEACON.get(), level, -1);
        checkForInput(aabb, blockPos, ModBlocks.ANTIMATTER_BLOCK.get(), ModBlocks.NEGATIVE_BEACON_BLOCK.get(), level, 1);
        checkForInput(aabb, blockPos, ModBlocks.ENGORGED_HEART.get(), ModBlocks.AMORPH_BEACON_BLOCK.get(), level, -1);
        checkForInput(aabb, blockPos, ModBlocks.INFUSED_OBSIDIAN.get(), ModBlocks.ETHER_BEACON_BLOCK.get(), level, -1);



    }

    //abstracted to support additional multiblock recipes. note that this ONLY works for a complete 3x3 cube.
    public void checkForInput(AABB aabb, BlockPos blockPos, Block inputBlock, Block outputBlock, Level level, Integer offsetY) {
        if (outputBlock.asItem() instanceof ToggleableBlockItem &&  isDisabled((ToggleableBlockItem) outputBlock.asItem())) {
            return;
        }
        int input = 0;
        assert level != null;
        for (BlockState blockstate : level.getBlockStates(aabb).toList()) {
            if(blockstate.getBlock() == inputBlock) {
                input++;
            }
        }
        if (input ==26) {
            try {
                sleep(1000);
            } catch(Exception ignored) {}
            for(int xVal = blockPos.getX()-1; xVal <=blockPos.getX()+1; xVal++) {
                for(int yVal = blockPos.getY()-1; yVal <=blockPos.getY()+1; yVal++) {
                    for(int zVal = blockPos.getZ()-1; zVal <=blockPos.getZ()+1; zVal++) {
                        BlockPos pos1 = new BlockPos(xVal, yVal, zVal);
                        level.setBlockAndUpdate(pos1, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            level.setBlockAndUpdate(new BlockPos(blockPos.getX(), blockPos.getY()+ offsetY, blockPos.getZ()), outputBlock.defaultBlockState());
            EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level, blockPos, MobSpawnType.EVENT);
            level.playSound(null, blockPos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.AMBIENT);
        }
    }

}
