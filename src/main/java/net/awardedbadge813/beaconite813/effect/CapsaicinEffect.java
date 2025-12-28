package net.awardedbadge813.beaconite813.effect;

import net.awardedbadge813.beaconite813.damagesource.ModDamageTypes;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.AttributeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.jar.Attributes;

import static java.lang.Math.max;

public class CapsaicinEffect extends MobEffect {



    public CapsaicinEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // I wanted to make it so
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        if (BeaconiteLib.effectDisabled(ModEffects.CAPSAICIN.value())) {
            livingEntity.removeEffect(ModEffects.CAPSAICIN);
        }
        //scaling algorithm that feels reasonable.
        if (livingEntity.level().getGameTime()%(max(20-2*amplifier, 1))==0L || max(20-2*amplifier, 1)==1) {
            livingEntity.hurt(new DamageSource(livingEntity.level().registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageTypes.CAPSAICIN)), 0.5f);
        }
        if (!livingEntity.isOnFire() && Objects.requireNonNull(livingEntity.getEffect(ModEffects.CAPSAICIN)).isAmbient()){
            livingEntity.removeEffect(ModEffects.CAPSAICIN);
        }

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
