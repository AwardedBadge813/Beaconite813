package net.awardedbadge813.beaconite813.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.recipe.RefineryRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RefineryRecipeCategory implements IRecipeCategory<RefineryRecipe> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/refinery_gui_test.png");
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "refinery");

    //The RefineryRecipe has already been created so it will only crash if it has been removed. If it is removed, that is bad and it SHOULD crash.
    public static final RecipeType<RefineryRecipe> REFINERY_RECIPE_RECIPE_TYPE = new RecipeType<>(UID, RefineryRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public RefineryRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0,0,256,256);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.REFINERY));
    }

    @Override
    public @NotNull RecipeType<RefineryRecipe> getRecipeType() {
        return REFINERY_RECIPE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Beaconite Refinery");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RefineryRecipe recipe, @NotNull IFocusGroup focuses) {
        int leftXpos = 65;
        int lowYpos  =129;
        Ingredient ingredient = recipe.getIngredients().getFirst();
        builder.addSlot(RecipeIngredientRole.OUTPUT, leftXpos+53, lowYpos-48).addItemStack(recipe.getResultItem(null));
        builder.addSlot(RecipeIngredientRole.INPUT, leftXpos, lowYpos-74).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+24, lowYpos-97).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+107, lowYpos-74).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+83, lowYpos-97).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+83, lowYpos).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+107, lowYpos-23).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos, lowYpos-23).addIngredients(ingredient);
        builder.addSlot(RecipeIngredientRole.INPUT,  leftXpos+24, lowYpos).addIngredients(ingredient);
    }

    @SuppressWarnings("removal")
    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
