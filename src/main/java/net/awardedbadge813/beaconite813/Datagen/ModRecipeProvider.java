package net.awardedbadge813.beaconite813.Datagen;

import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.item.ModItems;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BEACONITE_BLOCK.get())
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
                .unlockedBy("has_pure_beaconite", has(ModItems.PURE_BEACONITE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CATALYST.get())
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', ModItems.PURE_BEACONITE.get())
                .define('C', Items.NETHER_STAR.asItem())
                .unlockedBy("has_pure_beaconite", has(ModItems.PURE_BEACONITE))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.NETHERITE_BLOCK.asItem())
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', ModItems.BEACONITE.get())
                .define('C', Blocks.GOLD_BLOCK.asItem())
                .unlockedBy("has_beaconite", has(ModItems.BEACONITE))
                .save(recipeOutput, "beaconite813:netherite_synthesis");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BEACONITE_GLASS.get(), 4)
                .pattern("BBB")
                .pattern("BCB")
                .pattern("BBB")
                .define('B', Items.GLASS.asItem())
                .define('C', ModItems.BEACON_POWDER.get())
                .unlockedBy("has_beacon_powder", has(ModItems.BEACON_POWDER))
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


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONDENSED_BEACONITE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.REFINED_BEACONITE.get())
                .unlockedBy("has_refined_beaconite", has(ModItems.REFINED_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.PURE_BEACONITE.get())
                .unlockedBy("has_pure_beaconite", has(ModItems.PURE_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CONSTRUCTOR.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITE_GLASS.get())
                .define('D', ModBlocks.ULTRA_DENSE_BEACONITE.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_pure_beaconite", has(ModItems.PURE_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BASE_BEACON_BLOCK.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITE_GLASS.get())
                .define('D', ModBlocks.POLYMORPH_BEACONITE.get())
                .define('C', ModBlocks.ULTRA_DENSE_BEACONITE.asItem())
                .unlockedBy("has_polymorph_beaconite", has(ModBlocks.POLYMORPH_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.REFINERY.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITE_GLASS.get())
                .define('D', ModItems.CATALYST.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_catalyst", has(ModItems.CATALYST)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.UNSTABLE_BEACON.get())
                .pattern("BBB")
                .pattern("BDB")
                .pattern("CCC")
                .define('B', ModBlocks.BEACONITE_GLASS.get())
                .define('D', ModBlocks.CONDENSED_BEACONITE.get())
                .define('C', Blocks.NETHERITE_BLOCK.asItem())
                .unlockedBy("has_refined_beaconite", has(ModItems.REFINED_BEACONITE)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BEACON_BEAM_ITEM.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BEACON_BEAM_SHARD.get())
                .unlockedBy("has_beacon_shard", has(ModItems.BEACON_BEAM_SHARD)).save(recipeOutput);




        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BEACONITE.get(), 9)
                .requires(ModBlocks.BEACONITE_BLOCK)
                .unlockedBy("has_beaconite_block", has(ModBlocks.BEACONITE_BLOCK)).save(recipeOutput, "b_from_block");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.REFINED_BEACONITE.get(), 9)
                .requires(ModBlocks.CONDENSED_BEACONITE)
                .unlockedBy("has_refined_beaconite_block", has(ModBlocks.CONDENSED_BEACONITE)).save(recipeOutput, "rb_from_block");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PURE_BEACONITE.get(), 9)
                .requires(ModBlocks.ULTRA_DENSE_BEACONITE)
                .unlockedBy("has_pure_beaconite_block", has(ModBlocks.ULTRA_DENSE_BEACONITE)).save(recipeOutput, "pb_from_block");


        //this can only be used to smelt single items, and the mod has no single-item smelting, but I would like to keep the compatibility in case anything new is added later.
        List<ItemLike> SMELTABLE = List.of();





    }

    //credit to kaupenjoe for this code
    protected static void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult,
                                      float pExperience, int pCookingTIme, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult,
                                      float pExperience, int pCookingTime, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.@NotNull Factory<T> factory,
                                                                       List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult, float pExperience, int pCookingTime, @NotNull String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, beaconite813.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
