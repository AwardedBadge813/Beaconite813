package net.awardedbadge813.beaconite813.effect;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, beaconite813.MOD_ID);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
    public static final Holder<MobEffect> MARKED_SPLASH = MOB_EFFECTS.register("fading_effect", () -> new MarkedSplashEffect(MobEffectCategory.NEUTRAL, 0));

    public static final Holder<MobEffect> CAPSAICIN = MOB_EFFECTS.register("capsaicin_effect", () -> new CapsaicinEffect(MobEffectCategory.BENEFICIAL, 0)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_speed"), 0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_damage"), 1f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_jump"), 0.5f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.GRAVITY, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_grav"), 10f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_safe_fall"), 2f, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.BURNING_TIME, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "capsaicin_fire_resist"), 2f, AttributeModifier.Operation.ADD_VALUE));

    public static final Holder<MobEffect> WRATH = MOB_EFFECTS.register("wrath_effect", () -> new WrathEffect(MobEffectCategory.BENEFICIAL, 0)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "wrath_damage"), 0.5f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    //mixin included for this effect
    public static final Holder<MobEffect> HYPERTROPHY = MOB_EFFECTS.register("hypertrophy_effect", () -> new HypertrophyEffect(MobEffectCategory.NEUTRAL, DyeColor.BLUE.getTextureDiffuseColor()));

    //mixin included for this effect
    public static final Holder<MobEffect> MIDAS_ROT = MOB_EFFECTS.register("midas_rot_effect", () -> new MidasRotEffect(MobEffectCategory.NEUTRAL, DyeColor.YELLOW.getTextureDiffuseColor()));

}
