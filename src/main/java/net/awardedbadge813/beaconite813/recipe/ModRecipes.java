package net.awardedbadge813.beaconite813.recipe;

import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, beaconite813.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, beaconite813.MOD_ID);


    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<RefineryRecipe>> REFINERY_SERIALIZER =
            SERIALIZERS.register("refinery", RefineryRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<RefineryRecipe>> REFINERY_TYPE =
            TYPES.register("refinery", () -> new RecipeType<RefineryRecipe>() {
                @Override
                public String toString() {
                    return "refinery";
                }
            });


    public static void register(IEventBus eventbus) {
        SERIALIZERS.register(eventbus);
        TYPES.register(eventbus);
    }
}
