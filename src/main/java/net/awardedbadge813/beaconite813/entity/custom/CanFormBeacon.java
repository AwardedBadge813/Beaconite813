package net.awardedbadge813.beaconite813.entity.custom;

import net.awardedbadge813.beaconite813.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface CanFormBeacon {

    default int getSkyStatus(Level level, BlockPos pos) {
        int canSeeSky = 0;
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
        if (!foundBadBlock||!Config.UNSTABLEBEACOONSEESSKY.getAsBoolean()) {
            canSeeSky = 1;
        }
        return canSeeSky;
    }

    private int getX(BlockPos pos, int n) {
        return pos.getX()-n;
    }
    private int getZ(BlockPos pos, int n) {
        return pos.getZ()-n;
    }

    private int levelSize(int n) {
        return 1+2*n;
    }

    private boolean checkBlockStateForBeaconBlock(Level level, BlockPos pPos) {
        return level.getBlockState(pPos).is(BlockTags.BEACON_BASE_BLOCKS);
    }
    private boolean checkBlockStateForBeaconPassable(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.AIR.defaultBlockState().getBlock())||level.getBlockState(pos).is(Blocks.BEDROCK.defaultBlockState().getBlock())||level.getBlockState(pos).is(Blocks.GLASS.defaultBlockState().getBlock());
    }

    default int getLayers(Level level, BlockPos pos) {
        int currentLayer =1;
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
        int goodLayers = currentLayer-1;
        return goodLayers;
    }

}
