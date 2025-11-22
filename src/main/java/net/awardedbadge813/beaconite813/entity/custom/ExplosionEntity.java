package net.awardedbadge813.beaconite813.entity.custom;

import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import static net.awardedbadge813.beaconite813.entity.ModEntities.EXPLOSION;

public class ExplosionEntity extends Entity implements TraceableEntity {



    public ExplosionEntity(EntityType<? extends ExplosionEntity> entityType, Level level) {
        super(entityType,  level);
        this.blocksBuilding = false;
    }

    @Override
    public void tick() {
        super.tick();
        this.explodeCreeper();
    }

    //vanilla creeper explosion code.
    private void explodeCreeper() {
        if (!this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), Config.EXPLOSION_RADIUS.getAsInt(), Level.ExplosionInteraction.NONE);
            this.discard();
        }

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public @Nullable Entity getOwner() {
        return null;
    }
}
