package net.awardedbadge813.beaconite813.compat;

import com.sun.jna.platform.unix.X11;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.block.ModBlocks;
import net.awardedbadge813.beaconite813.recipe.ReactorRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

import static net.awardedbadge813.beaconite813.recipe.ModRecipes.REACTOR_TYPE;

public class ReactorRecipeCategory implements IRecipeCategory<ReactorRecipe> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/jei/reactor/reactor_gui.png");
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "reactor");

    //The RefineryRecipe has already been created so it will only crash if it has been removed. If it is removed, that is bad and it SHOULD crash.
    public static final RecipeType<ReactorRecipe> REACTOR_RECIPE_TYPE = new RecipeType<>(UID, ReactorRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;


    public ReactorRecipeCategory(IGuiHelper helper) {
        //this.background = helper.createDrawable(TEXTURE, 0,0,173,156);
        this.background = helper.createDrawable(TEXTURE, 256-173,-256+100,173,156);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.REACTOR_BLOCK));
    }

    @Override
    public RecipeType<ReactorRecipe> getRecipeType() {
        return REACTOR_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Reactor");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactorRecipe recipe, @NotNull IFocusGroup focuses) {
        int leftXPos = 38;
        int lowYPos =68;
        SizedIngredient ingredient = recipe.itemInput();
        FluidStack fluid = recipe.getFluid();
        builder.addSlot(RecipeIngredientRole.OUTPUT, leftXPos +78, lowYPos ).addItemStack(recipe.getResultItem(null));
        builder.addSlot(RecipeIngredientRole.INPUT, leftXPos +39, lowYPos ).addFluidStack(fluid.getFluid()).addRichTooltipCallback(new IRecipeSlotRichTooltipCallback() {
            @Override
            public void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
                tooltip.add(Component.literal("§7"+ String.valueOf(fluid.getAmount())+" mB"));
            }
        });
        builder.addSlot(RecipeIngredientRole.INPUT, leftXPos, lowYPos ).addItemStack(ingredient.getItems()[0]);

    }

    @SuppressWarnings("removal")
    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
