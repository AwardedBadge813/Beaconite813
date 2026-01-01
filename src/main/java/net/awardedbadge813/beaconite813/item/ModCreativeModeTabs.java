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
            .icon(() -> new ItemStack(ModItems.PURE_BEACONITE.get())).title(Component.translatable("creativetab.awardedbadge813.beaconite_tab"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.BEACONITE);
                output.accept(ModItems.REFINED_BEACONITE);
                output.accept(ModItems.PURE_BEACONITE);
                output.accept(ModItems.BEACON_POWDER);
                output.accept(ModItems.CATALYST);
                output.accept(ModItems.BEACONITE_SEED);
                output.accept(ModItems.BEACON_BEAM_SHARD);
                output.accept(ModItems.BEACON_BEAM_ITEM);
                output.accept(ModItems.QUARRY_TALISMAN);
                output.accept(ModItems.INVERT_TALISMAN);
                output.accept(ModItems.ETHER_FILTER);
                output.accept(ModItems.AURA_MODULE);
                output.accept(ModItems.DIFFUSE_MODULE);
                output.accept(ModItems.TRANSMUTE_MODULE);
                output.accept(ModItems.INFUSE_MODULE);
                output.accept(ModItems.BOTTOMLESS_BOTTLE);
                output.accept(ModItems.DORMANT_BOTTLE);
                output.accept(ModBlocks.DORMANT_EGG);


                output.accept(ModBlocks.BEACONITE_BLOCK);
                output.accept(ModBlocks.BEACONITE_GLASS);
                output.accept(ModBlocks.CONDENSED_BEACONITE);
                output.accept(ModBlocks.UNSTABLE_BEACON);
                output.accept(ModBlocks.REFINERY);
                output.accept(ModBlocks.ULTRA_DENSE_BEACONITE);
                output.accept(ModBlocks.CONSTRUCTOR);
                output.accept(ModBlocks.POLYMORPH_BEACONITE);
                output.accept(ModBlocks.BASE_BEACON_BLOCK);
                output.accept(ModBlocks.WRATHFUL_FLESH);
                output.accept(ModBlocks.BLAZING_MAGMA);
                output.accept(ModBlocks.COINS_BLOCK);
                output.accept(ModBlocks.LIVING_BLOCK);
                output.accept(ModBlocks.ANTIMATTER_BLOCK);
                output.accept(ModBlocks.ENGORGED_HEART);
                output.accept(ModBlocks.INFUSED_OBSIDIAN);

                output.accept(ModBlocks.VENGEANT_BEACON_BLOCK);
                output.accept(ModBlocks.IGNEOUS_BEACON_BLOCK);
                output.accept(ModBlocks.REGAL_BEACON_BLOCK);
                output.accept(ModBlocks.LIVING_BEACON);
                output.accept(ModBlocks.NEGATIVE_BEACON_BLOCK);
                output.accept(ModBlocks.AMORPH_BEACON_BLOCK);
                output.accept(ModBlocks.ETHER_BEACON_BLOCK);


            }).build());

    //this allows the icons for effects to be visible in JEI.
    public static final Supplier<CreativeModeTab> ICONS_TAB = CREATIVE_MODE_TAB.register("icons_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.AURIC_INTERFERENCE_ICON.get())).title(Component.translatable("creativetab.awardedbadge813.icons_tab"))
            .displayItems((itemDisplayParameters, output) -> {
                output.accept(ModItems.AURIC_INTERFERENCE_ICON);
                output.accept(ModItems.WRATH_ICON);
                output.accept(ModItems.CAPSAICIN_ICON);
                output.accept(ModItems.MIDAS_ROT_ICON);
                output.accept(ModItems.HYPERTROPHY_ICON);

            }).build());



    public static void register(IEventBus eventbus) {
        CREATIVE_MODE_TAB.register(eventbus);
    }

}
