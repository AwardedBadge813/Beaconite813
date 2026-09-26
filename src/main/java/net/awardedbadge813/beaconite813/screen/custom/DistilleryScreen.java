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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class DistilleryScreen extends AbstractContainerScreen<DistilleryMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/distillery_gui.png");
    private static final ResourceLocation BOTTLE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/bottle_gui.png");
    private static final ResourceLocation EXTRA =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/distillery_extra.png");
    private static final ResourceLocation SLOT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/storage/itemslot.png");
    private static final ResourceLocation ARROW_CURVED =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/arrow_curved.png");
    private static final ResourceLocation ZWOOP_FULL =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/zwoop_full.png");
    private static final ResourceLocation ARROW_CURVED_OP =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/arrow_curved_op.png");
    private static final ResourceLocation METERS =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/meters.png");
    private static final ResourceLocation HEAT_METER =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/heat_meter.png");
    private static final ResourceLocation ZWOOP_METER =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/distillery/zwoop_meter.png");

    private ResourceLocation direction;

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

    public DistilleryScreen(DistilleryMenu menu, Inventory playerInventory, Component title) {
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


        guiGraphics.blit(GUI_TEXTURE, x-116, y-237, 0, 0, 512, 512, 512, 512);
        guiGraphics.blit(EXTRA, x+70, y+20, 0, 0, 119,138,119,138);
        guiGraphics.blit(ARROW_CURVED, x+152, y+150, 0, 0, 7,7,7,7);
        guiGraphics.blit(ARROW_CURVED_OP, x+190, y+130, 0, 0, 7,7,7,7);
        guiGraphics.blit(SLOT_TEXTURE, x+186, y+141, 0, 0, 18,18,18,18);
        guiGraphics.blit(SLOT_TEXTURE, x+143, y+131, 0, 0, 18,18,18,18);
        guiGraphics.blit(METERS, x+103, y+105, 0, 0, 30,40,30,40);
        guiGraphics.blit(HEAT_METER, x+114, y+106+menu.getHeatMeter(), 0, menu.getHeatMeter(), 3,38-menu.getHeatMeter(),3,38);
        guiGraphics.blit(ZWOOP_METER, x+118, y+106+menu.getZwoopMeter(), 0, menu.getZwoopMeter(), 3,38-menu.getZwoopMeter(),3,38);
        guiGraphics.blit(ZWOOP_FULL, x+163, y+141+menu.getZwoopTank(), 0, menu.getZwoopTank(), 20,16-menu.getZwoopTank(),20,16);




    }

}

