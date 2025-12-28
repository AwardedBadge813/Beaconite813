package net.awardedbadge813.beaconite813.mixin;

import net.awardedbadge813.beaconite813.effect.ModEffects;
import net.minecraft.core.Holder;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Stack;

//to forge: I'm sorry, i need a mixin here ;-;
// this isn't really something I can use an AttributeModifier for since the resistance effect doesn't use one either.
// so this just alters the variables at the time of storage.
// If any other modder adds values in a mixin like this, there should be no issues snd both should coexist peacefully.
@Mixin(value = LivingEntity.class)
public abstract class addResistanceEffects {


    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract void remove(Entity.RemovalReason reason);

    @Shadow
    protected Stack<DamageContainer> damageContainers;

    // based on the levels of wrath, we want to nullify TWO levels of resistance.
    // resistance adds 5 times the resistance level, and this reverses that x2.
    @ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "HEAD", ordinal = 0), argsOnly = true)
    public float ResistanceModifiers(float damageAmount) {
        int i = 0;
        // midas rot should bypass any resistances here. it will in the other section too since it has the right tag,
        // but I can't pull the damagesource in this method for some reason.
        if (this.hasEffect(ModEffects.MIDAS_ROT)) {
            return damageAmount;
        }
        // based on the levels of hypertrophy, we want to add a bit of resistance(I mean, have you SEEN those cows?). but they also shouldn't be immune to damage, this gives them 80% resist at level X.
        if (this.hasEffect(ModEffects.HYPERTROPHY)) {
            i += (Objects.requireNonNull(getEffect(ModEffects.HYPERTROPHY)).getAmplifier() + 1) * 2;
        }
        //wrath could apply to these effects, but mixin mechanics make it more complicated than 'let's just put it here!'
        //and entities can't normally get wrath anyway so ima say it doesn't matter.
        int j = 25 - i;
        if (this.hasEffect(ModEffects.WRATH) && j > 25) {
            j *= 50 * (j - 25);
        }
        float f = damageAmount * (float) j;
        float f1 = damageAmount;
        damageAmount = Math.max(f / 25.0F, 0.0F);
        float f2 = f1 - damageAmount;
        if (f2 > 0.0F && f2 < 3.4028235E37F) {
            ((DamageContainer)this.damageContainers.peek()).setReduction(DamageContainer.Reduction.MOB_EFFECTS, f2);
        }

        if (damageAmount<=0) {
            return 0.0F;
        }
        return damageAmount;
    }



    // the above treats wrath as a separate effect from resistance debuffing, this affects the resistance effect separately.
    @ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "STORE"), name = "i")
    public int WrathDebuff(int i) {
        if (this.hasEffect(ModEffects.WRATH)) {
            i -= (Objects.requireNonNull(getEffect(ModEffects.WRATH)).getAmplifier() + 1) * 10;
        }
        return i;
    }

    // this scales wrath debuff so your damage should be magnified. does it get magnified? no clue.
    @ModifyVariable(method = "getDamageAfterMagicAbsorb", at = @At(value = "STORE"), name = "j")
    public int WrathScaling(int j) {
        if (j>=25) {
            j*=5;
        }
        return j;
    }




    // God I hate mixins
}
