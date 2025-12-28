package net.awardedbadge813.beaconite813.damagesource;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> CAPSAICIN =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin"));
    public static final ResourceKey<DamageType> MIDAS_ROT =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "midas_rot"));
}
