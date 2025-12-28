package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.custom.ToggleableBlockItem;
import net.awardedbadge813.beaconite813.entity.custom.BeaconBeamHolder;
import net.awardedbadge813.beaconite813.entity.custom.CanFormBeacon;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.*;
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

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NegativeBeaconBlockEntity extends BeaconBeamHolder implements CanFormBeacon {
    private final int effectRadius = 20;
    public static final List<MobEffect> effects = DefineEffects();
    public static final List<MobEffect> posEffects = getPositiveEffects();
    public static final List<MobEffect> negEffects = getNegativeEffects();
    public static final List<MobEffect> neutEffects = getNeutralEffects();
    public NegativeBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.NEGATIVE_BEACON_BE.get(), pos, blockState);
    }
    public void remove(Level level) {
        level.removeBlockEntity(getBlockPos());
    }


    @Override
    public @NotNull BlockEntityType<NegativeBeaconBlockEntity> getType() {
        return ModBlockEntities.NEGATIVE_BEACON_BE.get();
    }

    private AABB getEffectArea(Level level, BlockPos pos) {
        return new AABB(pos).inflate(effectRadius).expandTowards(pos.getX(), level.getMinBuildHeight(), pos.getZ());
    }

    public @NotNull Component getDisplayName() {
        return Component.literal("negative_beacon_be");
    }

    public void tick (Level level, BlockPos pos, BlockState blockState) {
        if (this.isDisabled((ToggleableBlockItem) blockState.getBlock().asItem())) {
            return;
        }
        if(isBeaconActive(level, pos)) {
            List<Player> players = level.getEntitiesOfClass(Player.class, getEffectArea(level, pos));
            for(Player player : players) {
                invertEffects(player, min(getLayers(level, pos)-8, 9));
            }
        }
    }

    private void invertEffects(Player player, int maxAmplifier) {
        for(MobEffectInstance effectInstance : player.getActiveEffects().stream().toList()) {
            if(!effectInstance.isAmbient()) {
                switch(effectInstance.getEffect().value().getCategory()) {
                    case MobEffectCategory.BENEFICIAL -> {
                        MobEffect effectToAdd=negEffects.get((int) Math.floor(Math.random()*negEffects.size()));
                        player.removeEffect(effectInstance.getEffect());
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effectToAdd), effectInstance.getDuration(), min(effectInstance.getAmplifier(), maxAmplifier), true, true));
                    }
                    case MobEffectCategory.HARMFUL -> {
                        MobEffect effectToAdd=posEffects.get((int) Math.floor(Math.random()*posEffects.size()));
                        player.removeEffect(effectInstance.getEffect());
                        player.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effectToAdd), effectInstance.getDuration(), min(effectInstance.getAmplifier(), maxAmplifier), true, true));
                    }
                    case MobEffectCategory.NEUTRAL -> {

                    }
                    default -> {

                    }
                }
            }
        }

    }

    public static List<MobEffect> DefineEffects(){
        List<ResourceKey<MobEffect>> keys = BuiltInRegistries.MOB_EFFECT.registryKeySet().stream().toList();
        ArrayList <MobEffect> effects = new ArrayList<>();
        for(ResourceKey<MobEffect> key : keys) {
            effects.add(BuiltInRegistries.MOB_EFFECT.get(key));
        }
        return effects;
    }
    public List<MobEffect>getEffects(){
        return effects;
    }
    public static List<MobEffect> getPositiveEffects (){
        ArrayList<MobEffect> returnable = new ArrayList<>();
        for(MobEffect effect: effects) {
            if(effect.isBeneficial()) {
                returnable.add(effect);
            }
        }
        return returnable;
    }
    public static List<MobEffect>getNegativeEffects (){
        ArrayList<MobEffect> returnable = new ArrayList<>();
        for(MobEffect effect: effects) {
            if(effect.getCategory().equals(MobEffectCategory.HARMFUL)) {
                returnable.add(effect);
            }
        }
        return returnable;
    }
    public static List<MobEffect>getNeutralEffects (){
        ArrayList<MobEffect> returnable = new ArrayList<>();
        for(MobEffect effect: effects) {
            if(effect.getCategory().equals(MobEffectCategory.NEUTRAL)) {
                returnable.add(effect);
            }
        }
        return returnable;
    }

    @Override
    public int getLastBeamHeight() {
        return 501;
    }

    @Override
    public List<BeaconBeamSection> getBeamSections() {
        BeaconBeamSection beamSection = null;
        if(isBeaconActive(getLevel(), getBlockPos())) {
            beamSection = new BeaconBeamSection();
            assert getLevel() != null;
            beamSection.setParams(DyeColor.WHITE.getTextureDiffuseColor(), 501);
            this.beamSections=List.of(beamSection);
        }
        return beamSection==null ? List.of(): List.of(beamSection);
    }

    public boolean isBeaconActive(Level level, BlockPos pos) {
        return getLayers(level, pos)>2 && getSkyStatus(level, pos)==1;
    }

    @Override
    public BlockPos getBeamBoundingBoxEnd() {
        return new BlockPos(getBlockPos().getX(), getBlockPos().getY()-500, getBlockPos().getZ());
    }

    //want the beam to go down instead of up, so the start of the beam is at 500 blocks lower.
    @Override
    public int getYOffset() {
        return -500;
    }

    @Override
    public int getSkyStatus(Level level, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean foundBadBlock=false;
        for (int heightclear = -1; j+heightclear > level.getMinBuildHeight(); heightclear--) {
            BlockPos pPos = new BlockPos(i, (j+heightclear), k);
            if(!checkBlockStateForBeaconPassable(level, pPos)) {
                foundBadBlock=true;
            }
        }
        return foundBadBlock? 0:1;
    }
    @Override
    public int getLayers(Level level, BlockPos pos) {
        int currentLayer;
        int y=pos.getY()+1;
        for (currentLayer = 1; currentLayer <=20; currentLayer++){
            for (int x = getX(pos, currentLayer); x < getX(pos, currentLayer) + levelSize(currentLayer); x++) {
                for (int z = getZ(pos, currentLayer); z < (getZ(pos, currentLayer) + levelSize(currentLayer)); z++) {
                    if (!checkBlockStateForBeaconBlock(level, new BlockPos(x,y,z))){
                        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
                    }
                }
            }
            y++;
        }
        return min(currentLayer-1, Config.MAX_LEVEL_BEACON.getAsInt());
    }

    @Override
    public float getBeamDirection() {
        return -1f;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider pRegistries) {
        return saveWithoutMetadata(pRegistries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public ResourceLocation getBeamTexture() {
        return ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/block/negative_beacon_beam.png");
    }

}