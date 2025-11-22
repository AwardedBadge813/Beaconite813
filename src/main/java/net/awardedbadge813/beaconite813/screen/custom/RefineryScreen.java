package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class RefineryScreen extends AbstractContainerScreen<RefineryMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/refinery_gui_test.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/refinery_progress_template.png");
    private static final ResourceLocation FLUID_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/inner_chamber_refinery.png");
    private static final ResourceLocation OUTER_CHAMBER =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/outer_chamber.png");

    protected int imageWidth = 256;
    /**
     * The Y size of the inventory window in pixels.
     */
    protected int imageHeight =256;


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public RefineryScreen(RefineryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -10;
        this.titleLabelY = -30;
        this.inventoryLabelX = -10;
        this.inventoryLabelY = this.imageHeight - 154;
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x=(width-imageWidth)/2;
        int y=(height-imageHeight)/2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, x, y);


    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
       if(menu.isCrafting()) {
           guiGraphics.blit(ARROW_TEXTURE, x+120, y+79-menu.getScaledArrowProgress(69), menu.getBeaconProgressParameter(), -20-menu.getScaledArrowProgress(69), 16, menu.getScaledArrowProgress(69), 216, 80);
           guiGraphics.blit(FLUID_TEXTURE, x+118, y+81, menu.getScaledArrowProgress(60), 2, 16, 16, 80, 20);
       }
    }
    }//y+43


