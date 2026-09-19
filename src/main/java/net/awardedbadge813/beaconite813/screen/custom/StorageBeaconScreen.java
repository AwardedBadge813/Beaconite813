package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.Config;
import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StorageBeaconScreen extends AbstractContainerScreen<StorageBeaconMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/storage_gui.png");
    private static final ResourceLocation EXTRA =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/extra.png");
    private static final ResourceLocation CHEST_ICON =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/chesticon.png");
    private static final ResourceLocation SLOT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/itemslot.png");
    private static final ResourceLocation CHIP_SLOT =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/chip_slot.png");


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

    public StorageBeaconScreen(StorageBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -10;
        this.titleLabelY =-43;
        this.inventoryLabelX = 7;
        this.inventoryLabelY = this.imageHeight - 140;
    }
    private void addInventory(int width, int height, int x, int y, GuiGraphics guiGraphics, ResourceLocation texture, int tWidth, int tHeight){
        for (int i=0; i<height; ++i){
            for (int l=0; l<width; ++l){
                int index = l+i*width;
                int offsetX= x+l*18;
                int offsetY= y+i*18;
                guiGraphics.blit(texture, offsetX, offsetY, 0, 0, tWidth, tHeight, tWidth, tHeight);

            }//new SlotItemHandler(itemHandler, l+i*width, x+l*18, y+i*18));
        }
    }
    private void addInventory(int width, int height, int x, int y, GuiGraphics guiGraphics){
        addInventory(width, height, x, y, guiGraphics, SLOT_TEXTURE, 18, 18);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        int stackSize = menu.getSlot(36).getItem().getCount()+16;


        guiGraphics.blit(EXTRA, x-180, y-200, 0, 0, 512, 512, 450, 450);
        guiGraphics.blit(GUI_TEXTURE, x-116, y-237, 0, 0, 512, 512, 512, 512);
        addInventory(10, 6, x+38, y+52, guiGraphics);
        addInventory(1, 1, x-30, y+30, guiGraphics, CHIP_SLOT, 30, 30);
        guiGraphics.blit(CHEST_ICON, x, y+40, 0, 0, 20, 20, 20, 20);
        guiGraphics.drawString(this.font, new StringBuilder().append("Stack Size: ").append(stackSize).append("/").append(Config.MAX_STORAGE_SIZE.getAsInt()).toString(),
                x+30, y+13, 4210752, false);
    }

}

