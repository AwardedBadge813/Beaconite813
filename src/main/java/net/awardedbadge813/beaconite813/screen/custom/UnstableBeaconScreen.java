package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.UnstableBeaconBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static java.lang.Math.floor;
import static java.lang.Math.random;

public class UnstableBeaconScreen extends AbstractContainerScreen<UnstableBeaconMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/unstable_beacon/unstable_beacon.png");
    private static final ResourceLocation OOPS =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/unstable_beacon/oops.png");

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

    public UnstableBeaconScreen(UnstableBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -10;
        this.titleLabelY = -15;
        this.inventoryLabelX = 8;
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
        if(menu.ExplosionActive()==1) {
            for(int j=0; j<11; j++){
                guiGraphics.blit(OOPS, x+70+ ((int) ((random()-0.5)*300)), y+100+((int) ((random()-0.5)*300)), 0, 0, 128, 51, 128, 51);
            }
            guiGraphics.blit(OOPS, x+70, y+100, 0, 0, 128, 51, 128, 51);


        }


    }

    }//y+43


