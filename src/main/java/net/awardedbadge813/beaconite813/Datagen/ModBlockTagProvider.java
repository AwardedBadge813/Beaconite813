package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, beaconite813.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.BASE_BEACON_BLOCK.get())
                .add(ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .add(ModBlocks.POLYMORPH_BEACONITE.get())
                .add(ModBlocks.REFINERY.get())
                .add(ModBlocks.LIVING_BLOCK.get())
                .add(ModBlocks.BEACONITE_BLOCK.get())
                .add(ModBlocks.CONDENSED_BEACONITE.get())
                .add(ModBlocks.BEACONITE_GLASS.get())
                .add(ModBlocks.UNSTABLE_BEACON.get())
                .add(ModBlocks.CONSTRUCTOR.get())
                .add(ModBlocks.LIVING_BEACON.get())
                .add(ModBlocks.ENGORGED_HEART.get())
                .add(ModBlocks.AMORPH_BEACON_BLOCK.get())
                .add(ModBlocks.BLAZING_MAGMA.get())
                .add(ModBlocks.IGNEOUS_BEACON_BLOCK.get());


        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.LIVING_BLOCK.get());

        tag(BlockTags.WITHER_IMMUNE)
                .add(ModBlocks.BEACONITE_GLASS.get())
                .add(ModBlocks.BEACONITE_BLOCK.get())
                .add(ModBlocks.CONDENSED_BEACONITE.get())
                .add(ModBlocks.ULTRA_DENSE_BEACONITE.get());

        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(ModBlocks.ULTRA_DENSE_BEACONITE.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .add(ModBlocks.BEACONITE_BLOCK.get())
                .add(ModBlocks.CONDENSED_BEACONITE.get());





    }
}
