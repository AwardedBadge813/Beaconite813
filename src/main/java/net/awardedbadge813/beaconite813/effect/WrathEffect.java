package net.awardedbadge813.beaconite813.effect;

import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class WrathEffect extends MobEffect {



    public WrathEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // I wanted to make it so
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {

        livingEntity.removeEffect(ModEffects.WRATH);

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return BeaconiteLib.effectDisabled(ModEffects.WRATH.value());
    }
}
