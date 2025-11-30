package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.block.custom.BeaconiteCropBlock;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

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
        dropSelf(ModBlocks.ENGORGED_HEART.get());
        dropSelf(ModBlocks.AMORPH_BEACON_BLOCK.get());


        LootItemCondition.Builder lootItemConditionBuilder =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.BEACONITE_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(BeaconiteCropBlock.AGE, BeaconiteCropBlock.MAX_AGE));
        this.add(ModBlocks.BEACONITE_CROP.get(), this.createBeaconCropDrops(ModBlocks.BEACONITE_CROP.get(), ModItems.BEACON_BEAM_SHARD.get(), ModItems.BEACONITE_SEED.get(), lootItemConditionBuilder));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    protected LootTable.Builder createBeaconCropDrops(Block cropBlock, Item grownCropItem, Item seedsItem, LootItemCondition.Builder dropGrownCropCondition) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.applyExplosionDecay(cropBlock, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem
                        .lootTableItem(grownCropItem).when(dropGrownCropCondition)
                                .otherwise(LootItem.lootTableItem(seedsItem))))
                .withPool(LootPool.lootPool()
                        .when(dropGrownCropCondition)
                        .add(LootItem.lootTableItem(seedsItem)
                                .apply(ApplyBonusCount
                                        .addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE),
                                                0.2714286F, 1))))
                .withPool(LootPool.lootPool()
                        .when(dropGrownCropCondition)
                        .add(LootItem.lootTableItem(grownCropItem)
                                .apply(ApplyBonusCount
                                        .addBonusBinomialDistributionCount(registrylookup.getOrThrow(Enchantments.FORTUNE),
                                                0.5714286F, 3)))));
    }
}
