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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;
import static net.awardedbadge813.beaconite813.util.BeaconiteLib.restrict;

public class VengeantBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    private int beaconLayers;
    private boolean canSeeSky;
    public VengeantBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VENGEANT_BEACON_BE.get(), pos, blockState);
    }


    @Override
    public @NotNull BlockEntityType<VengeantBeaconBlockEntity> getType() {
        return ModBlockEntities.VENGEANT_BEACON_BE.get();
    }



    public @NotNull Component getDisplayName() {
        return Component.literal("vengeant_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        this.beaconLayers =getLayers(level, pos);
        this.canSeeSky=getSkyStatus(level, pos)==1;
        int vengeanceModifier = getVengeanceModifier(pos, level, beaconLayers);

        if(isBeaconActive(level, pos)) {
            int radius = restrict(10+5* (beaconLayers-8), 20, 60);
            AABB range = new AABB(pos).inflate(radius).expandTowards(0, 300,0 );
            List<Player> inRangePlayers = new ArrayList<>(level.getEntitiesOfClass(Player.class, range));
            for (Player player : inRangePlayers) {
                if (!player.hasEffect(ModEffects.WRATH) || Objects.requireNonNull(player.getEffect(ModEffects.WRATH)).getDuration()<=100) {
                    player.addEffect(new MobEffectInstance(ModEffects.WRATH, 300, vengeanceModifier, true, true, true));
                }
            }
        }
    }

    private int getVengeanceModifier(BlockPos pos, Level level, int beaconLayers) {
        int updateSkulls = 0;
        AABB checkerRange = new AABB(pos).inflate(beaconLayers);
        for (BlockState blockState : level.getBlockStates(checkerRange).toList()) {
            //skulls is an item tag, not a block tag (even though it shows up as a blockTag in jei?)
            if (Item.byBlock(blockState.getBlock()).getDefaultInstance().is(ItemTags.SKULLS)) {
                updateSkulls+=1;
            }
        }
        //max is 7 for layers so skulls are required to get the last 2. layers included so level 10 is actually possible.
        //the second restrict requires that there be at least 10 skulls.
        return  (int)floor(log10(updateSkulls+1))+restrict(this.beaconLayers-8, 0, 8)*restrict((int)floor(log10(updateSkulls+1)), 0, 1)-1;
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.RED.getTextureDiffuseColor(), getLevel().getMaxBuildHeight() - getBlockPos().getY());
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    private boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>7 && getSkyStatus(level, pos)==1 && getVengeanceModifier(pos, level, getLayers(level, pos))>=0;
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