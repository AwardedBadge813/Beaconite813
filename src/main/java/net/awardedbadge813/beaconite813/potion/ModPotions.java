package net.awardedbadge813.beaconite813.potion;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, beaconite813.MOD_ID);

    public static final Holder<Potion> ETHEREAL_POTION = POTIONS.register("ethereal_potion", () -> new Potion());

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
