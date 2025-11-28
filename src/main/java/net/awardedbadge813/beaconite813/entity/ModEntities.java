package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.custom.ExplosionEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, beaconite813.MOD_ID);


    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }


    public static final Supplier<EntityType<ExplosionEntity>> EXPLOSION = ENTITY_TYPES.register("explosion", () -> EntityType.Builder.of(ExplosionEntity::new, MobCategory.MISC).build("explosion"));







}
