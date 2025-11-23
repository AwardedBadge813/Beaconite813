package net.awardedbadge813.beaconite813.item;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB=
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, beaconite813.MOD_ID);


    public static final Supplier<CreativeModeTab> MODTAB = CREATIVE_MODE_TAB.register("beaconite_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.PUREBEACONITE.get())).title(Component.translatable("creativetab.awardedbadge813.beaconite_tab"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.BEACONITE);
                output.accept(ModItems.REFINEDBEACONITE);
                output.accept(ModItems.PUREBEACONITE);
                output.accept(ModItems.BEACONPOWDER);
                output.accept(ModItems.CATALYST);
                output.accept(ModItems.QUARRY_TALISMAN);

                output.accept(ModBlocks.BEACONITEBLOCK);
                output.accept(ModBlocks.BEACONITEGLASS);
                output.accept(ModBlocks.CONDENSEDBEACONITE);
                output.accept(ModBlocks.UNSTABLE_BEACON);
                output.accept(ModBlocks.REFINERY);
                output.accept(ModBlocks.ULTRA_DENSE_BEACONITE);
                output.accept(ModBlocks.CONSTRUCTOR);



            }).build());


    public static void register(IEventBus eventbus) {
        CREATIVE_MODE_TAB.register(eventbus);
    }

}
