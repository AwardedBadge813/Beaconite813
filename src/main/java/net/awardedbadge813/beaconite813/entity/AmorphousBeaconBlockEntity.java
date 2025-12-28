package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.BubbleEntity;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.restrict;

public class AmorphousBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    private int beaconLayers;
    private boolean canSeeSky;
    public AmorphousBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AMORPH_BEACON_BE.get(), pos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<AmorphousBeaconBlockEntity> getType() {
        return ModBlockEntities.AMORPH_BEACON_BE.get();
    }


    public @NotNull Component getDisplayName() {
        return Component.literal("amorph_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        this.beaconLayers=getLayers(level, pos);
        this.canSeeSky=getSkyStatus(level, pos)==1;

        if(isBeaconActive(level, pos)) {
            int radius = restrict(80-5* beaconLayers, 20, 60);
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<BubbleEntity> inRangeBubbles = new ArrayList<>(level.getEntitiesOfClass(BubbleEntity.class, range));
            int bubbleCount=inRangeBubbles.size();
            for (BubbleEntity bubble : inRangeBubbles) {
                bubble.setPowerLevel(restrict(beaconLayers -6, 0, 9));
            }


            if(bubbleCount<= beaconLayers) {
                int xPosStart = pos.getX()-radius;
                int zPosStart = pos.getZ()-radius;
                int yPos;

                while (xPosStart <= (pos.getX() + radius)) {
                    while (zPosStart <= (pos.getZ() + radius)) {

                        if(random()< (double) 1 /(2*pow(radius, 2))) {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            if(pos.getY()<level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ())){
                                yPos = level.getHeight(Heightmap.Types.OCEAN_FLOOR, xPosStart, zPosStart);
                            } else {
                                yPos = level.getHeight(Heightmap.Types.WORLD_SURFACE, xPosStart, zPosStart);
                            }
                            BubbleEntity bubble = new BubbleEntity(ModEntities.BUBBLE.get(), level);
                            bubble.setAttributes(xPosStart, yPos+(int)(random()*5)+1, zPosStart, restrict(beaconLayers -8, 0, 9));
                            level.addFreshEntity(bubble);
                            bubbleCount++;
                        }

                        zPosStart++;
                    }
                    xPosStart++;
                    zPosStart=pos.getZ()-radius;
                }
            }
        }
    }


    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }


    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert level != null;
            beamSection.setParams(DyeColor.BLUE.getTextureDiffuseColor(), level.getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>5 && getSkyStatus(level, pos)==1;
    }
    @Override
    public int getSkyStatus(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean foundBadBlock=false;
        for (int heightclear = 1; j+heightclear < level.getMaxBuildHeight(); heightclear++) {
            BlockPos pPos = new BlockPos(i, (j+heightclear), k);
            if(!(level.getBlockState(pPos)== Blocks.WATER.defaultBlockState()||level.getBlockState(pPos)== Blocks.AIR.defaultBlockState() || level.getBlockState(pPos)== Blocks.GLASS.defaultBlockState())) {
                foundBadBlock=true;
            }
        }
        if (!foundBadBlock||!Config.UNSTABLE_BEACON_SEES_SKY.getAsBoolean()) {
            return 1;
        }
        return 0;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}