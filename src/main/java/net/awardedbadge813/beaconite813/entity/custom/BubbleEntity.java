package net.awardedbadge813.beaconite813.entity.custom;

import net.awardedbadge813.beaconite813.entity.client.BubbleRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class BubbleEntity extends Animal {
    public final AnimationState animation = new AnimationState();
    private int idleAnimationTimeout= 20;
    private int powerLevel = 0;
    private int timer=400;
    @Override
    public boolean isNoGravity() {
        return true;
    }

    public BubbleEntity(EntityType<? extends Animal> entityType,  Level level) {
        super(entityType, level);
    }
    public void setAttributes (int x, int y, int z, int powerLevel) {
        this.setPos(x, y, z);
        this.powerLevel=powerLevel;
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        this.remove(RemovalReason.KILLED);
    }

    public void setPowerLevel(int i) {
        this.powerLevel = i;
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

    public void tick () {
        super.tick();
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
        AABB range = new AABB(this.getOnPos()).inflate(1);
        if (!this.level().getEntitiesOfClass(Player.class, range).isEmpty()) {
            Player player = this.level().getEntitiesOfClass(Player.class, range).getFirst();
            player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, min(getPlayerConduitDuration(player) + (int) (400 / max(powerLevel, 1)), 12000), powerLevel));
            this.remove(RemovalReason.DISCARDED);
        }
        if (this.timer <= 0) {
            this.remove(RemovalReason.DISCARDED);
        }


        this.timer--;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }


    @Override
    protected void applyGravity() {
        super.applyGravity();
    }

    @Override
    protected void doPush(Entity entity) {
        //super.doPush(entity);
    }

}
