package net.awardedbadge813.beaconite813.entity.custom;

import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.item.ToggleableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class BeaconBeamHolder extends BlockEntity {
    public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
    public BeaconBeamHolder(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    public boolean isDisabled(ToggleableBlockItem blockItem) {
        return blockItem.isDisabled();
    }

    public BlockPos getBeamBoundingBoxStart() {
        return this.getBlockPos();
    }
    public BlockPos getBeamBoundingBoxEnd(){
        return new BlockPos (getBlockPos().getX(), 1024, getBlockPos().getZ());
    }

    public ResourceLocation getBeamTexture() {
        return BEAM_LOCATION;
    }
    public int getLastBeamHeight(){
        //this defines the height of the last beacon beam, NOT the maximum render height of beacon beams.
        return 1024;
    }


    public int getColor() {
        return 11546150;
    }

    public int getYOffset(){return 0;}

    public float getBeamDirection() {return 1f;
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
        beamSection.setParams(getColor(), level.getMaxBuildHeight() - getBlockPos().getY());
        this.beamSections=List.of(beamSection);
        return List.of(beamSection);
    }



}
