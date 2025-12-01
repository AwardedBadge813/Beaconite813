package net.awardedbadge813.beaconite813.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class BeaconBeamHolder extends BlockEntity {

    public BeaconBeamHolder(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public static class BeaconBeamSection {
        private int color;
        private int height;
        public void setParams(int color, int height) {
            this.color=color;
            this.height=height;
        }

        public int getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
    public boolean IsBeaconActive() {
        return true;
    };
    public List<BeaconBeamHolder.BeaconBeamSection> beamSections;

    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamHolder.BeaconBeamSection beamSection = new BeaconBeamHolder.BeaconBeamSection();
        beamSection.setParams(11546150, level.getMaxBuildHeight() - getBlockPos().getY());
        this.beamSections=List.of(beamSection);
        return List.of(beamSection);
    }



}
