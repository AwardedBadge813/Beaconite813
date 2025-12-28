package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, beaconite813.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(ModTags.Items.ETHEREAL_MODULES)
                .add(ModItems.AURA_MODULE.get())
                .add(ModItems.DIFFUSE_MODULE.get())
                .add(ModItems.TRANSMUTE_MODULE.get())
                .add(ModItems.INFUSE_MODULE.get())
                .add(ModItems.BOTTOMLESS_BOTTLE.get());
        tag(ModTags.Items.ETHEREAL_INFUSIBLE)
                .add(ModBlocks.DORMANT_EGG.get().asItem())
                .add(ModItems.DORMANT_BOTTLE.get());
    }
}
