package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ConstructorScreen extends AbstractContainerScreen<ConstructorMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/refinery_gui_test.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/refinery_progress_template.png");
    private static final ResourceLocation FLUID_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/inner_chamber_refinery.png");
    private static final ResourceLocation OUTER_CHAMBER =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/outer_chamber.png");
    private static final ResourceLocation BUTTON_SPRITE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/outer_chamber.png");
    private static final ResourceLocation BUTTON_HIGHLIGHTED_SPRITE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/refinery/inner_chamber_refinery.png");


    protected void init() {
        super.init();
        //ConstructorButton upbutton = new ConstructorButton(100, 100, 1);
        //this.addRenderableWidget(upbutton);
        //ConstructorButton downbutton = new ConstructorButton(100, 120, -1);
        //this.addRenderableWidget(downbutton);

    }

    protected int imageWidth = 256;
    /**
     * The Y size of the inventory window in pixels.
     */
    protected int imageHeight = 256;

// not added since it is broken
    /*
    class ConstructorButton extends AbstractButton {
        private int increment = 0;
        protected ConstructorButton(int x, int y, int i) {
            super(x, y, 20, 20, Component.translatable("beaconite813:increment_up_button"));
            this.increment=i;

        }

        @Override
        public void onPress() {
            getMenu().setContainerData(0, getMenu().getContainerData(0)+this.increment);
        }


        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation resourcelocation;
            if (this.isHoveredOrFocused()) {
                resourcelocation = ConstructorScreen.BUTTON_HIGHLIGHTED_SPRITE;
            } else {
                resourcelocation = ConstructorScreen.BUTTON_SPRITE;
            }

            guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.width, this.height);
            this.renderIcon(guiGraphics, this.getX(), this.getY());
        }

        public void renderIcon(GuiGraphics guiGraphics, int x, int y) {
            guiGraphics.blit(BUTTON_SPRITE, x, y, 0, 0, 20, 20, 80, 80);
        }


        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

    }
    */


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public ConstructorScreen(ConstructorMenu menu, Inventory playerInventory, Component title) {
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
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;



        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        guiGraphics.drawString(this.font, "Beacon Level: " + String.valueOf(menu.data.get(0)) , 490, 283, 4210752, false);


    }

}

