package net.awardedbadge813.beaconite813.recipe;


import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidRecipeInput {
    FluidStack getFluid(int var1);

    int size();

    default boolean isEmpty() {
        for(int i = 0; i < this.size(); ++i) {
            if (!this.getFluid(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }
}