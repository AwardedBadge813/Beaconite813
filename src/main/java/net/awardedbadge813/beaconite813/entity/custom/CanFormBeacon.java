package net.awardedbadge813.beaconite813.entity.custom;

import net.awardedbadge813.beaconite813.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.min;


public interface CanFormBeacon {

    default int getSkyStatus(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean foundBadBlock=false;
        for (int heightclear = 1; j+heightclear < level.getMaxBuildHeight(); heightclear++) {
            BlockPos pPos = new BlockPos(i, (j+heightclear), k);
            if(!checkBlockStateForBeaconPassable(level, pPos)) {
                foundBadBlock=true;
            }
        }
        return foundBadBlock? 0:1;
    }

    default int getX(BlockPos pos, int n) {
        return pos.getX()-n;
    }
    default int getZ(BlockPos pos, int n) {
        return pos.getZ()-n;
    }

    default int levelSize(int n) {
        return 1+2*n;
    }

    default boolean checkBlockStateForBeaconBlock(Level level, BlockPos pPos) {
        return level.getBlockState(pPos).is(BlockTags.BEACON_BASE_BLOCKS);
    }
    default boolean checkBlockStateForBeaconPassable(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.AIR.defaultBlockState().getBlock())||level.getBlockState(pos).is(Blocks.BEDROCK.defaultBlockState().getBlock())||level.getBlockState(pos).propagatesSkylightDown(level, pos);
    }

    default int getLayers(Level level, BlockPos pos) {
        int currentLayer;
        int y=pos.getY()-1;
        for (currentLayer = 1; currentLayer <=20; currentLayer++){
            for (int x = getX(pos, currentLayer); x < getX(pos, currentLayer) + levelSize(currentLayer); x++) {
                for (int z = getZ(pos, currentLayer); z < (getZ(pos, currentLayer) + levelSize(currentLayer)); z++) {
                    if (!checkBlockStateForBeaconBlock(level, new BlockPos(x,y,z))){
                        return currentLayer -1;
                    }
                }
            }
            y--;
        }
        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
    }

}
