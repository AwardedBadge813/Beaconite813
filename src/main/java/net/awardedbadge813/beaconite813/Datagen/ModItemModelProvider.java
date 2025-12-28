package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider  extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, beaconite813.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.CATALYST.get());
        basicItem(ModItems.REFINED_BEACONITE.get());
        basicItem(ModItems.BEACONITE.get());
        basicItem(ModItems.PURE_BEACONITE.get());
        basicItem(ModItems.QUARRY_TALISMAN.get());
        basicItem(ModBlocks.REFINERY.asItem());
        basicItem(ModItems.BEACON_POWDER.get());
        basicItem(ModItems.BEACONITE_SEED.get());
        basicItem(ModItems.BEACON_BEAM_SHARD.get());
        basicItem(ModItems.BEACON_BEAM_ITEM.get());
        basicItem(ModItems.AURA_MODULE.get());
        basicItem(ModItems.DIFFUSE_MODULE.get());
        basicItem(ModItems.TRANSMUTE_MODULE.get());
        basicItem(ModItems.INFUSE_MODULE.get());
        basicItem(ModItems.ETHER_FILTER.get());
        basicItem(ModItems.BOTTOMLESS_BOTTLE.get());
        basicItem(ModItems.DORMANT_BOTTLE.get());
        basicItem(ModItems.AURIC_INTERFERENCE_ICON.get());
        basicItem(ModItems.WRATH_ICON.get());
        basicItem(ModItems.CAPSAICIN_ICON.get());
        basicItem(ModItems.MIDAS_ROT_ICON.get());
        basicItem(ModItems.HYPERTROPHY_ICON.get());
    }
}
