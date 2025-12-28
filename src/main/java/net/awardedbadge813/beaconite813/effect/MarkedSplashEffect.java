package net.awardedbadge813.beaconite813.effect;

import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.abs;

public class MarkedSplashEffect extends MobEffect {

    //the MobEffect stores an active hashmap of all livingEntities that have been marked and their linked effects & timers.
    private HashMap<LivingEntity, ArrayList<MobEffectInstance>> linkedEffects = new HashMap<>();
    private HashMap<LivingEntity, Integer> linkTimer = new HashMap<>();
    private HashMap<LivingEntity, Integer> checkerTimer = new HashMap<>();
    //may add a separate effect for this later.
    private HashMap<LivingEntity, Boolean> naughtyList = new HashMap<>();

    // Bear with me, this is a long one.
    // This empty effect exists to counter a massive bug inherent to the way minecraft handles potions.
    // The Aura effect is not in minecraft for a reason: It's REALLY complicated to implement it in a way that doesn't create a horrible dupe!
    // ALL area-based aura can be gamed by simply putting players outside it, splashing them, then entering one by one.
    // It is (not theoretically, ACTUALLY!) impossible to fix this solely in the ethereal beacon itself,
    // Since someone can destroy the beacon to undo any effect windup you add there.
    // Therefore: my solution is to make them all non-aura-able and tell the player that they can't do that (maybe change this later, but not for now)
    // MarkedSplash is an effect that in practice will not do anything to the player.
    // The effect is inherent to ethereal splash and lingering potions i.e. the ones that don't give an ambient effect but should.
    // this exists as a marker to show that you got your effect from a splash potion, and that you should feel bad.
    // Not really, but the ethereal beacon will snub your effects that the splash potion gave you since they are ambient.
    // This opens the door for people to accidentally lose potion time if they didn't know and try to re-aura,
    // but they likely are not concerned if they're making splash potions which have a lower duration (also transmutation exists!)
    // what this means is that if you use MY splash potions, you won't be able to re-aura them.
    // But you can re-aura ethereal potions and most importantly vanilla potions.
    // This offers a way to multiply potion time, which is importantly NOT a dupe! it's just an exploit(?)
    // that allows you to get more time if you use potions efficiently, which isn't something I have a problem with.
    public MarkedSplashEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // this logic makes the master toggle work. it is present in every effect but the main logic is: if disabled, then effect gets removed.
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amplifier) {
        livingEntity.removeEffect(ModEffects.MARKED_SPLASH);
        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return BeaconiteLib.effectDisabled(ModEffects.MARKED_SPLASH.value());
    }
}
