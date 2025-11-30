package net.awardedbadge813.beaconite813.entity;

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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;

public class AmorphousBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    private int beaconLevels;
    private boolean canSeeSky;
    public AmorphousBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AMORPH_BEACON_BE.get(), pos, blockState);
    }

    @Override
    public @NotNull BlockEntityType<AmorphousBeaconBlockEntity> getType() {
        return ModBlockEntities.AMORPH_BEACON_BE.get();
    }

    public static int restrict (int i, int j, int k) {
        //i should be between j(low) and k(high)
        return min(max(i, j), k);
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("amorph_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        this.beaconLevels=getLayers(level, pos);
        this.canSeeSky=getSkyStatus(level, pos)==1;

        if(beaconLevels>8 && canSeeSky) {
            int radius = restrict(80-5*beaconLevels, 20, 60);
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<BubbleEntity> inRangeBubbles = new ArrayList<>(level.getEntitiesOfClass(BubbleEntity.class, range));
            int bubbleCount=inRangeBubbles.size();
            for (BubbleEntity bubble : inRangeBubbles) {
                bubble.setPowerLevel(restrict(beaconLevels-8, 0, 9));
            }
            if(bubbleCount<=beaconLevels) {
                int xPosStart = pos.getX()-radius;
                int zPosStart = pos.getZ()-radius;
                while (xPosStart <= (pos.getX() + radius)) {
                    while (zPosStart <= (pos.getZ() + radius)) {
                        if(random()< (double) 1 /(2*pow(radius, 2))) {
                            int yPos = level.getHeight(Heightmap.Types.WORLD_SURFACE, xPosStart, zPosStart);
                            BubbleEntity bubble = new BubbleEntity(ModEntities.BUBBLE.get(), level);
                            bubble.setAttributes(xPosStart, yPos+(int)(random()*5)+1, zPosStart, restrict(beaconLevels-8, 0, 9));
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
    public boolean IsBeaconActive() {
        return true;
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = new BeaconBeamSection();
        assert level != null;
        beamSection.setParams(3949738, level.getMaxBuildHeight() - getBlockPos().getY());

        return List.of(beamSection);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}