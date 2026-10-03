package net.awardedbadge813.beaconite813.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record ReactorRecipeInput(ItemStack input, FluidStack fluid) implements RecipeInput {
    @Override
    public @NotNull ItemStack getItem(int i) {
        return input;
    }
    public @NotNull FluidStack getFluid(int i) {
        return fluid;
    }

    @Override
    public int size() {
        return 2;
    }
    public int getConsumedInput() {
        return input.getCount();
    }
}
