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
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;

public record ReactorRecipe(SizedIngredient itemInput, SizedFluidIngredient fluidInput,
                            ItemStack output) implements Recipe<ReactorRecipeInput>{
    //RefineryRecipeInput=from BlockEntity inventory
    //ingredient, output read from JSON
    //Don't @ me I didn't want to fuck with a non-null list in the JSON

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ing= NonNullList.create();
        ing.add(itemInput.ingredient());
        return ing;
    }
    public ItemStack getIngredient() {
        return itemInput.getItems()[0];
    }
    public FluidStack getFluid() {
        return fluidInput.getFluids()[0].copy();
    }

    @Override
    public boolean matches(@NotNull ReactorRecipeInput recipeInput, Level level) {
        if (level.isClientSide) {
            return false;
        }
        return itemInput.test(recipeInput.getItem(1))&& fluidInput().test(recipeInput.getFluid(1));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull ReactorRecipeInput refineryRecipeInput, HolderLookup.@NotNull Provider provider) {
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
        return ModRecipes.REACTOR_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.REACTOR_TYPE.get();
    }

    public int getInputAmount() {
        return itemInput.getItems()[0].getCount();
    }


    public static class Serializer implements RecipeSerializer<ReactorRecipe> {
        //might make this not horrible later, but it's my first mod cut me some slack lmao
        //only 1 item because all the codecs break when more than 1 item is added fml
        public static final MapCodec<ReactorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SizedIngredient.FLAT_CODEC.fieldOf("item_input").forGetter(ReactorRecipe::itemInput),
                SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid_input").forGetter(ReactorRecipe::fluidInput),
                ItemStack.CODEC.fieldOf("result").forGetter(ReactorRecipe::output))
                .apply(inst, ReactorRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ReactorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        SizedIngredient.STREAM_CODEC,
                        ReactorRecipe::itemInput,
                        SizedFluidIngredient.STREAM_CODEC,
                        ReactorRecipe::fluidInput,
                        ItemStack.STREAM_CODEC,
                        ReactorRecipe::output,
                        ReactorRecipe::new);
        
        @Override
        public @NotNull MapCodec<ReactorRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, ReactorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
