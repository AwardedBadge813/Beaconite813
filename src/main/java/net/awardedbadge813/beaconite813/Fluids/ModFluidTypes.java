package net.awardedbadge813.beaconite813.Fluids;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.joml.Vector3f;
public class ModFluidTypes {
    //public static final ResourceLocation WATER_STILL_RL = ResourceLocation.withDefaultNamespace( "block/water_still");
    public static final ResourceLocation WATER_STILL_RL = ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "block/fluid/base_still2");
    public static final ResourceLocation WATER_FLOW_RL = ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "block/fluid/base_flow2");
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, beaconite813.MOD_ID);
    public static void register(IEventBus eventbus) {
        FLUID_TYPES.register(eventbus);
    }

    private static DeferredHolder<FluidType, BaseFluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(
                WATER_STILL_RL,
                WATER_FLOW_RL,
                0xFF6c0398,
                new Vector3f((float) (42/255), (float) (1.4/255), (float) (59.6/255)),
                properties));
    }

    public static final DeferredHolder<FluidType, BaseFluidType> ZWOOP_TYPE =
            register("zwoop_type", FluidType.Properties.create().canPushEntity(false).viscosity(0).canSwim(false).density(0));
}