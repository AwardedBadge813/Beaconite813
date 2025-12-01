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
        return ingredient.test(refineryRecipeInput.getItem(1));
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
