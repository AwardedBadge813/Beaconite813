package net.awardedbadge813.beaconite813.Fluids;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFluids {
    public static DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, beaconite813.MOD_ID);
    public static void register(IEventBus eventbus) {
        FLUIDS.register(eventbus);
    }
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SOURCE_ZWOOP =
            FLUIDS.register("zwoop_source", () -> new BaseFlowingFluid.Source(ModFluids.ZWOOP_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_ZWOOP =
            FLUIDS.register("zwoop_flowing", () -> new BaseFlowingFluid.Flowing(ModFluids.ZWOOP_PROPERTIES));
    public static final BaseFlowingFluid.Properties ZWOOP_PROPERTIES = new BaseFlowingFluid.Properties(ModFluidTypes.ZWOOP_TYPE, SOURCE_ZWOOP, FLOWING_ZWOOP).slopeFindDistance(4).levelDecreasePerBlock(3).block(ModBlocks.ZWOOP_BLOCK).bucket(ModItems.BUCKET_ZWOOP);
}