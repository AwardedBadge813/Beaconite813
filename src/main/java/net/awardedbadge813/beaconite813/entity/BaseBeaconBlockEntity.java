package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.block.ModBlocks;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;

import static java.lang.Thread.sleep;

public class BaseBeaconBlockEntity extends BlockEntity {
    private static final Log log = LogFactory.getLog(BaseBeaconBlockEntity.class);

    public BaseBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BASE_BEACON_BLOCK_BE.get(), pos, blockState);
    }
    public void kill() {
        getLevel().removeBlockEntity(getBlockPos());
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
        AABB aabb = new AABB(blockPos).inflate(1);
        checkForInput(aabb, blockPos, ModBlocks.LIVING_BLOCK.get(), ModBlocks.LIVING_BEACON.get());

    }

    //abstracted to support additional multiblock recipes. planing on adding more than just 1 beacon for the polymorphic beacon.
    public void checkForInput(AABB aabb, BlockPos blockPos, Block inputBlock, Block outputBlock) {
        int input = 0;
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
            level.setBlockAndUpdate(new BlockPos(blockPos.getX(), blockPos.getY()-1, blockPos.getZ()), outputBlock.defaultBlockState());
            EntityType.LIGHTNING_BOLT.spawn((ServerLevel) level, blockPos, MobSpawnType.EVENT);
            level.playSound(null, blockPos, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.AMBIENT);
        }
    }

}
