package net.awardedbadge813.beaconite813.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record RefineryRecipe (Ingredient ingredient,
                              ItemStack output) implements Recipe<RefineryRecipeInput> {
    //RefineryRecipeInput=from BlockEntity inventory
    //ingredient, output read from JSON
    //Don't @ me I didn't want to fuck with a non-null list in the JSON


    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull RefineryRecipeInput input) {
        return Recipe.super.getRemainingItems(input);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ing= NonNullList.create();
        ing.add(ingredient);
        return ing;
    }

    @Override
    public boolean matches(@NotNull RefineryRecipeInput refineryRecipeInput, Level level) {
        if (level.isClientSide) {
            return false;
        }
        int matches=0;
        NonNullList<ItemStack> itemInputs = NonNullList.create();
        boolean[] inputActive = new boolean[9];
        boolean[] ingredientActive = new boolean[9];

        //creates a list of items in each slot, and creates booleans that show if they are 'active' slots,
        //this just means they don't have a recipe match yet.
        for (int i=0; i<8; i++) {
            ItemStack itemInSlot = refineryRecipeInput.getItem(i + 1);

            itemInputs.add(itemInSlot);
            inputActive[i] = true;
            ingredientActive[i] = true;
        }

        // checks for both boolean indices if a match exists. if a match exists between item input and recipe input,
        // both indices are deactivated and the matching count increases by 1.
        // notably, inactive slots are not checked again.
        // this prevents multi-item recipes from fucking everything up.
        // at least it would if the codecs didn't ruin everything so only 1 item as an ingredient now, keeping the framework though
        for(int indexIngredients = 0; indexIngredients <9; indexIngredients++) {
            for (int indexItems=0; indexItems<9; indexItems++) {
                if(inputActive[indexItems]
                        && ingredientActive[indexIngredients]
                        && ingredient.test(itemInputs.get(indexItems))) {
                            inputActive[indexItems]=false;
                            ingredientActive[indexIngredients]= false;
                            matches++;
                }
            }
        }

        // now we know how many items match. we just need to figure out hoe many items we need.
        // this could probably be done easier, but I'm going to get 8 and deal with it.
        int totalItems=8;
        //in case some items are null ,add the number to matches

        // return true if you have enough items, dunno how you would get more than 8 but someone will find a way to
        return matches>=totalItems;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RefineryRecipeInput refineryRecipeInput, HolderLookup.@NotNull Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@Nullable HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.REFINERY_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.REFINERY_TYPE.get();
    }




    public static class Serializer implements RecipeSerializer<RefineryRecipe> {
        //might make this not horrible later, but it's my first mod cut me some slack lmao
        //only 1 item because all the codecs break when more than 1 item is added fml
        public static final MapCodec<RefineryRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RefineryRecipe::ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(RefineryRecipe::output))
                .apply(inst, RefineryRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RefineryRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC,
                        RefineryRecipe::ingredient,
                        ItemStack.STREAM_CODEC,
                        RefineryRecipe::output,
                        RefineryRecipe::new);
        
        @Override
        public @NotNull MapCodec<RefineryRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RefineryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
