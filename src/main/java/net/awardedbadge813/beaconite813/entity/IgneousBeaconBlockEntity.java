package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Thread.sleep;
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
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<Player> inRangePlayers = new ArrayList<>(level.getEntitiesOfClass(Player.class, range));
            for (Player player : inRangePlayers) {
                if(player.isOnFire()) {
                    player.addEffect(new MobEffectInstance(ModEffects.CAPSAICIN, 40, beaconLayers -6, true, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, beaconLayers -6, true, false, false));

                    if(player.getRemainingFireTicks()<=40) {
                        player.setRemainingFireTicks(60);
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