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

public class AmorphousBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
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
        int beaconLayers = getLayers(level, pos);

        if(isBeaconActive(level, pos)) {
            int radius = clamp(65-5*beaconLayers, 20, 40);
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<BubbleEntity> inRangeBubbles = new ArrayList<>(level.getEntitiesOfClass(BubbleEntity.class, range));
            int bubbleCount=inRangeBubbles.size();
            for (BubbleEntity bubble : inRangeBubbles) {
                bubble.setPowerLevel(clamp(beaconLayers -6, 0, 9));
            }
            //delay so the latter happens more rarely
            if (level.getGameTime()%20!=0) {
                return;
            }

            //spawns bubbles in the radius rarely that give the conduit effect.
            if(bubbleCount<= beaconLayers) {
                int xPos = (int)(random()*radius*2f-radius);
                int zPos = (int)(random()*radius*2f-radius);
                int yPos;

                if(random()< (double) 10/radius) {
                    if(pos.getY()<level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX()+xPos, pos.getZ()+zPos)){
                        yPos = level.getHeight(Heightmap.Types.OCEAN_FLOOR, pos.getX()+xPos, pos.getZ()+zPos);
                    } else {
                        yPos = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX()+xPos, pos.getZ()+zPos);
                    }
                    BubbleEntity bubble = new BubbleEntity(ModEntities.BUBBLE.get(), level);
                    bubble.setAttributes(pos.getX()+xPos, yPos+(int)(random()*5)+1, pos.getZ()+zPos, clamp(beaconLayers -8, 0, 9));
                    level.addFreshEntity(bubble);
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