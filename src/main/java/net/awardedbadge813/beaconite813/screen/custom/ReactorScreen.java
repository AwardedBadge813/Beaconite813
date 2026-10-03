package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ReactorScreen extends AbstractContainerScreen<ReactorMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/reactor/reactor_gui.png");
    private static final ResourceLocation LOCK_U =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/reactor/zwoop_reactor_lock_u.png");
    private static final ResourceLocation LOCK =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/reactor/zwoop_reactor_lock.png");
    private static final ResourceLocation FULL_TANK =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/reactor/fulltank.png");
    private static final ResourceLocation ARROW_PROGRESS =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/reactor/arrow_progress.png");


    private ResourceLocation direction;

    protected void init() {
        super.init();

    }

    protected int imageWidth = 256;
    /**
     * The Y size of the inventory window in pixels.
     */
    protected int imageHeight = 256;


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public ReactorScreen(ReactorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = 25;
        this.titleLabelY =-35;
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



    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;


        guiGraphics.blit(GUI_TEXTURE, x-116, y-235, 0, 0, 512,512,512,512);
        guiGraphics.blit(FULL_TANK, x+57, y+6+menu.getZwoopTank(), 0, menu.getZwoopTank(), 141,141-menu.getZwoopTank(),141,141);

        ResourceLocation lockTexture = menu.data.get(2)==1?LOCK_U:LOCK;
        guiGraphics.blit(lockTexture, x+81, y+50, 0, 0, 10,11,10,11);
        guiGraphics.blit(lockTexture, x+159, y+50, 0, 0, 10,11,10,11);
        guiGraphics.blit(LOCK, x+120, y+50, 0, 0, 10,11,10,11);
        guiGraphics.blit(ARROW_PROGRESS, x+138, y+71, 0, 0, menu.getProgress(),5,15,5);




    }

}

