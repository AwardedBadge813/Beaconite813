package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.restrict;

public class IgneousBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    public IgneousBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.IGNEOUS_BEACON_BE.get(), pos, blockState);
    }


    @Override
    public @NotNull BlockEntityType<IgneousBeaconBlockEntity> getType() {
        return ModBlockEntities.IGNEOUS_BEACON_BE.get();
    }



    public @NotNull Component getDisplayName() {
        return Component.literal("igneous_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        int beaconLayers =getLayers(level, pos);

        if(isBeaconActive(level, pos)) {
            int radius = restrict(20+5* (beaconLayers-6), 20, 60);
            int cropRadius = radius/5;
            if (level.getGameTime()%120==0) {
                for (int x=-cropRadius;x<cropRadius;x++ ) {
                    for (int y=-cropRadius;y<cropRadius;y++ ) {
                        for (int z=-cropRadius;z<cropRadius;z++ ) {
                            BlockPos cropCheck = pos.offset(x,y,z);
                            boolean viable = level.getBlockState(cropCheck.above(1)).isAir()&&level.getBlockState(cropCheck).is(Tags.Blocks.VILLAGER_FARMLANDS);
                            if (viable&& random()<0.01f) {
                                level.setBlockAndUpdate(cropCheck.above(1), ModBlocks.CREAPER_CROP.get().defaultBlockState());
                            }
                        }
                    }
                }
            }


            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<LivingEntity> inRangeEntities = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class, range));
            for (LivingEntity entity : inRangeEntities) {
                if(entity.isOnFire()) {
                    entity.addEffect(new MobEffectInstance(ModEffects.CAPSAICIN, 40, beaconLayers -6, true, true, true));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, beaconLayers -6, true, false, false));

                    if(entity.getRemainingFireTicks()<=40) {
                        entity.setRemainingFireTicks(60);
                    }
                }
            }
        }
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.ORANGE.getTextureDiffuseColor(), getLevel().getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>5 && getSkyStatus(level, pos)==1;
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