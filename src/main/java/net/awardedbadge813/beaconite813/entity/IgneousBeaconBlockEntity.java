package net.awardedbadge813.beaconite813.entity;

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

public class IgneousBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    private int beaconLevels;
    private boolean canSeeSky;
    public IgneousBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.IGNEOUS_BEACON_BE.get(), pos, blockState);
    }


    @Override
    public @NotNull BlockEntityType<IgneousBeaconBlockEntity> getType() {
        return ModBlockEntities.IGNEOUS_BEACON_BE.get();
    }

    public static int restrict (int i, int j, int k) {
        //i should be between j(low) and k(high)
        return min(max(i, j), k);
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("igneous_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        this.beaconLevels=getLayers(level, pos);
        this.canSeeSky=getSkyStatus(level, pos)==1;

        if(beaconLevels>7 && canSeeSky) {
            int radius = restrict(80-5*beaconLevels, 20, 60);
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<Player> inRangePlayers = new ArrayList<>(level.getEntitiesOfClass(Player.class, range));
            for (Player player : inRangePlayers) {
                if(player.isOnFire()||player.wasOnFire && !player.fireImmune()) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, beaconLevels-8));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, beaconLevels-8));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, beaconLevels-8));
                    if(player.getRemainingFireTicks()<=40) {
                        player.setRemainingFireTicks(player.getRemainingFireTicks()+40);
                    }

                    player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
                    player.removeEffect(MobEffects.REGENERATION);
                }

            }
        }
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = new BeaconBeamSection();
        assert level != null;
        beamSection.setParams(DyeColor.ORANGE.getTextureDiffuseColor(), level.getMaxBuildHeight() - getBlockPos().getY());

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