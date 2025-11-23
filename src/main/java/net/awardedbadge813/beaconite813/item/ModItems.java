package net.awardedbadge813.beaconite813.item;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS=DeferredRegister
            .createItems(beaconite813.MOD_ID);
    public static void register(IEventBus eventbus) {
        ITEMS.register(eventbus);
    }

    public static final DeferredItem<Item> BEACONITE = ITEMS.register("beaconite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> REFINEDBEACONITE = ITEMS.register("refinedbeaconite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> PUREBEACONITE = ITEMS.register("purebeaconite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BEACONPOWDER = ITEMS.register("powdered_beaconite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> CATALYST = ITEMS.register("refinery_catalyst",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> QUARRY_TALISMAN = ITEMS.register("quarry_talisman",
            () -> new Item(new Item.Properties()));





}
