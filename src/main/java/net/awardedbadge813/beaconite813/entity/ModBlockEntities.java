package net.awardedbadge813.beaconite813.entity;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, beaconite813.MOD_ID);

    public static final Supplier<BlockEntityType<RefineryBlockEntity>> REFINERY_BE =
            BLOCK_ENTITIES.register("refinery_be", () -> BlockEntityType.Builder.of(
                    RefineryBlockEntity::new, ModBlocks.REFINERY.get())
                    .build(null));
    public static final Supplier<BlockEntityType<UnstableBeaconBlockEntity>> UNSTABLE_BEACON_BE =
            BLOCK_ENTITIES.register("unstable_beacon_be", () -> BlockEntityType.Builder.of(
                            UnstableBeaconBlockEntity::new, ModBlocks.UNSTABLE_BEACON.get())
                    .build(null));
    public static final Supplier<BlockEntityType<ConstructorBlockEntity>> CONSTRUCTOR_BE =
            BLOCK_ENTITIES.register("constructor_be", () -> BlockEntityType.Builder.of(
                            ConstructorBlockEntity::new, ModBlocks.CONSTRUCTOR.get())
                    .build(null));
    public static final Supplier<BlockEntityType<LivingBeaconBlockEntity>> LIVING_BEACON_BE =
            BLOCK_ENTITIES.register("living_beacon_be", () -> BlockEntityType.Builder.of(
                            LivingBeaconBlockEntity::new, ModBlocks.LIVING_BEACON.get())
                    .build(null));
    public static final Supplier<BlockEntityType<BaseBeaconBlockEntity>> BASE_BEACON_BLOCK_BE =
            BLOCK_ENTITIES.register("base_beacon_block_be", () -> BlockEntityType.Builder.of(
                            BaseBeaconBlockEntity::new, ModBlocks.BASE_BEACON_BLOCK.get())
                    .build(null));

    public static final Supplier<BlockEntityType<AmorphousBeaconBlockEntity>> AMORPH_BEACON_BE =
            BLOCK_ENTITIES.register("amorph_beacon_be", () -> BlockEntityType.Builder.of(
                            AmorphousBeaconBlockEntity::new, ModBlocks.AMORPH_BEACON_BLOCK.get())
                    .build(null));

    public static final Supplier<BlockEntityType<IgneousBeaconBlockEntity>> IGNEOUS_BEACON_BE =
            BLOCK_ENTITIES.register("igneous_beacon_be", () -> BlockEntityType.Builder.of(
                            IgneousBeaconBlockEntity::new, ModBlocks.IGNEOUS_BEACON_BLOCK.get())
                    .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
