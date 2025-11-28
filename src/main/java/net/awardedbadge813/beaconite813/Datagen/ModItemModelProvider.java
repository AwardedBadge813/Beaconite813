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
        basicItem(ModItems.REFINEDBEACONITE.get());
        basicItem(ModItems.BEACONITE.get());
        basicItem(ModItems.PUREBEACONITE.get());
        basicItem(ModItems.QUARRY_TALISMAN.get());
        basicItem(ModBlocks.REFINERY.asItem());
        basicItem(ModItems.BEACONPOWDER.get());
        basicItem(ModItems.BEACONITE_SEED.get());
        basicItem(ModItems.BEACON_BEAM_SHARD.get());
        basicItem(ModItems.BEACON_BEAM_ITEM.get());
    }
}
