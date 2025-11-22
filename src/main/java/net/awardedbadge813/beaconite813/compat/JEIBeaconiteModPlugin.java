package net.awardedbadge813.beaconite813.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.recipe.ModRecipes;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipe;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipeInput;
import net.awardedbadge813.beaconite813.screen.custom.RefineryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIBeaconiteModPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RefineryRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<RefineryRecipe> refineryRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.REFINERY_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(RefineryRecipeCategory.REFINERY_RECIPE_RECIPE_TYPE, refineryRecipes);
    }


    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(RefineryScreen.class, 75, -45, 20, 70, RefineryRecipeCategory.REFINERY_RECIPE_RECIPE_TYPE);
    }
}
