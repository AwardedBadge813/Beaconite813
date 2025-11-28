package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BEACONITEBLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BEACONITE.get())
                .unlockedBy("has_beaconite", has(ModItems.BEACONITE)).save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BEACONITE.get())
                .pattern("BCB")
                .pattern("CDC")
                .pattern("BCB")
                .define('B', Items.DIAMOND.asItem())
                .define('C', Items.NETHERITE_SCRAP.asItem())
                .define('D', Items.NETHER_STAR.asItem())
                .unlockedBy("has_beaconite", has(ModItems.BEACONITE))
                .save(recipeOutput, "beaconite813:beaconite_synthesis");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.QUARRY_TALISMAN.get())
                .pattern("BCB")
                .pattern("CDC")
                .pattern("BCB")
                .define('B', ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .define('C', Items.NETHERITE_PICKAXE.asItem())
                .define('D', ModItems.CATALYST.asItem())
                .unlockedBy("has_pure_beaconite", has(ModItems.PUREBEACONITE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CATALYST.get())
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', ModItems.PUREBEACONITE.get())
                .define('C', Items.NETHER_STAR.asItem())
                .unlockedBy("has_pure_beaconite", has(ModItems.PUREBEACONITE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.NETHERITE_BLOCK.asItem())
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', ModItems.BEACONITE.get())
                .define('C', Blocks.GOLD_BLOCK.asItem())
                .unlockedBy("has_beaconite", has(ModItems.BEACONITE))
                .save(recipeOutput, "beaconite813:netherite_synthesis");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BEACONITEGLASS.get(), 4)
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', Items.GLASS.asItem())
                .define('C', ModItems.BEACONPOWDER.get())
                .unlockedBy("has_beacon_powder", has(ModItems.BEACONPOWDER))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LIVING_BLOCK.get(), 8)
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('C', Items.MOSS_BLOCK.asItem())
                .define('B', ModItems.BEACON_BEAM_ITEM.get())
                .unlockedBy("has_beacon_beam_item", has(ModItems.BEACON_BEAM_ITEM))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.POLYMORPH_BEACONITE.get(), 1)
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('C', ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .define('B', ModItems.BEACON_BEAM_ITEM.get())
                .unlockedBy("has_beacon_beam_item", has(ModItems.BEACON_BEAM_ITEM))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONDENSEDBEACONITE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.REFINEDBEACONITE.get())
                .unlockedBy("has_refined_beaconite", has(ModItems.REFINEDBEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.PUREBEACONITE.get())
                .unlockedBy("has_pure_beaconite", has(ModItems.PUREBEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONSTRUCTOR.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITEGLASS.get())
                .define('D', ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_pure_beaconite", has(ModItems.PUREBEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BASE_BEACON_BLOCK.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITEGLASS.get())
                .define('D', ModBlocks.POLYMORPH_BEACONITE.get())
                .define('C', ModBlocks.ULTRA_DENSE_BEACONITE.asItem())
                .unlockedBy("has_polymorph_beaconite", has(ModBlocks.POLYMORPH_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.REFINERY.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITEGLASS.get())
                .define('D', ModItems.CATALYST.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_catalyst", has(ModItems.CATALYST)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.UNSTABLE_BEACON.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITEGLASS.get())
                .define('D', ModBlocks.CONDENSEDBEACONITE.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_refined_beaconite", has(ModItems.REFINEDBEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BEACON_BEAM_ITEM.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BEACON_BEAM_SHARD.get())
                .unlockedBy("has_beacon_shard", has(ModItems.BEACON_BEAM_SHARD)).save(recipeOutput);





        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BEACONITE.get(), 9)
                .requires(ModBlocks.BEACONITEBLOCK)
                .unlockedBy("has_beaconite_block", has(ModBlocks.BEACONITEBLOCK)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.REFINEDBEACONITE.get(), 9)
                .requires(ModBlocks.CONDENSEDBEACONITE)
                .unlockedBy("has_refined_beaconite_block", has(ModBlocks.CONDENSEDBEACONITE)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PUREBEACONITE.get(), 9)
                .requires(ModBlocks.ULTRA_DENSE_BEACONITE)
                .unlockedBy("has_pure_beaconite_block", has(ModBlocks.ULTRA_DENSE_BEACONITE)).save(recipeOutput);

        List<ItemLike> SMELTABLE = List.of();





    }

    //credit to kaupenjoe for this code
    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, beaconite813.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
