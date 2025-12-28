package net.awardedbadge813.beaconite813.entity.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Math.*;

public class BubbleEntity extends Animal {
    public final AnimationState animation = new AnimationState();
    private double ySeed = -1000;
    private int idleAnimationTimeout= 20;
    private int powerLevel = 0;
    private int timer=400;
    private final double bubbleSeed = random()*7.28;
    private int bubbleTimer = 0;

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.BUBBLE_COLUMN_BUBBLE_POP;
    }

    public BubbleEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }
    public void setAttributes (int x, int y, int z, int powerLevel) {
        this.setPos(x, y, z);
        this.ySeed = y;
        this.powerLevel=powerLevel;
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        this.remove(RemovalReason.KILLED);
    }

    public void setPowerLevel(int i) {
        this.powerLevel = i;
    }
    public void setTimer (int i) {
        this.timer = i;
    }
    public int getPowerLevel() {
        return this.powerLevel;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <=0) {
            this.idleAnimationTimeout = 20;
            this.animation.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }
    private int getPlayerConduitDuration(Player player) {
        List<MobEffectInstance> list = player.getActiveEffects().stream().toList();
        int duration = 0;
        for(MobEffectInstance effectInstance : list) {
            if(effectInstance.getEffect()==MobEffects.CONDUIT_POWER) {
                duration = effectInstance.getDuration();
            }
        }
        return duration;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BUBBLE_COLUMN_BUBBLE_POP;
    }

    public void tick () {
        //if the super is removed the animations no longer work ;-;
        super.tick();
        //this is commented out since it does not work, I will need to update the model I think.
        if (this.level().isClientSide()) {
            //this.setupAnimationStates();
        }
        if (ySeed==-1000) {
            ySeed=getY();
        }

        //7.28==2*pi
        //we want the bubble to oscillate up and down which looks nice
        this.setPos(getX(), (double) ySeed + 0.3F*sin((double)(bubbleTimer*4+62)/31+bubbleSeed), getZ());


        //we want the bubble to give the player the conduit power effect when they touch it, so we make an AABB beforehand and check if the player is within it.
        AABB range = new AABB(this.getOnPos()).inflate(1);
        if (!this.level().getEntitiesOfClass(Player.class, range).isEmpty()) {
            Player player = this.level().getEntitiesOfClass(Player.class, range).getFirst();
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, min(getPlayerConduitDuration(player) + (400 / max(powerLevel, 1)), 12000), powerLevel, true, true));
            level().playSound(null, getOnPos(), getDeathSound(), SoundSource.MASTER, 5f, 1f);
            this.remove(RemovalReason.DISCARDED);
        }

        //if the bubbles are in a remote location, we want the bubble to get removed after a while so new ones can spawn.
        if (this.timer <= 0) {
            this.remove(RemovalReason.DISCARDED);
        }


        //updating timers.
        this.timer--;
        this.bubbleTimer++;
    }

    //all of these overrides are here since I have no idea which one of them is causing the problem of the bubble oscillating rapidly in the water.

    //if the bubble is underwater or under other circumstances, the oscillations should not change.
    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public void lavaHurt() {
        //the bubble should pop when in lava.
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        //this prevents the attack sound from playing while still allowing the player to manually destroy bubbles.
        this.remove(RemovalReason.KILLED);
        level().playSound(null, getOnPos(), getDeathSound(), SoundSource.MASTER, 5f, 1f);
        return true;
    }
    @Override
    protected void doPush(Entity entity) {
        //super.doPush(entity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}
