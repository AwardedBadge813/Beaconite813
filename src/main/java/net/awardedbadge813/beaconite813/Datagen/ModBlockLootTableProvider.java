package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.LIVING_BLOCK.get());
        dropSelf(ModBlocks.BASE_BEACON_BLOCK.get());
        dropSelf(ModBlocks.LIVING_BEACON.get());
        dropSelf(ModBlocks.CONSTRUCTOR.get());
        dropSelf(ModBlocks.UNSTABLE_BEACON.get());
        dropSelf(ModBlocks.BEACONITE_GLASS.get());
        dropSelf(ModBlocks.CONDENSED_BEACONITE.get());
        dropSelf(ModBlocks.BEACONITE_BLOCK.get());
        dropSelf(ModBlocks.POLYMORPH_BEACONITE.get());
        dropSelf(ModBlocks.REFINERY.get());
        dropSelf(ModBlocks.ULTRA_DENSE_BEACONITE.get());
/*
        LootItemCondition.Builder lootItemConditionBuilder =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.BEACONITE_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BeaconiteCropBlock.AGE, BeaconiteCropBlock.MAX_AGE));
        this.add(ModBlocks.BEACONITE_CROP.get(), this.createCropDrops(ModBlocks.BEACONITE_CROP.get(), ModItems.BEACON_BEAM_SHARD.get(), ModItems.BEACONITE_SEED.get(), lootItemConditionBuilder));
*/
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
