package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class RegalBeaconScreen extends AbstractContainerScreen<RegalBeaconMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/regal_beacon/regal_beacon_gui.png");
    private static final ResourceLocation GOLD_PILE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/regal_beacon/gold_pile.png");
    private static final ResourceLocation LOOT_PILE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/regal_beacon/loot_pile.png");

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

    public RegalBeaconScreen(RegalBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -35;
        this.titleLabelY = -40;
        this.inventoryLabelX = -10;
        this.inventoryLabelY = this.imageHeight - 154;
    }

    //removing inventory label because there's not a good space for it in the GUI
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 1908001, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);


        //defining bases to move around gui parts more easily
        int goldX=70;
        int goldY=140;
        guiGraphics.drawString(this.font, "Gold Time: ", x+goldX, y+goldY, 4210752, false);
        guiGraphics.drawString(this.font, BeaconiteLib.getDurationCalc(menu.data.get(2)), x+goldX+5, y+goldY+10, 4210752, false);

        if(menu.goldActive()) {
             guiGraphics.blit(GOLD_PILE, x+61, y+139-menu.getScaledGoldTime(36), 0, 36-menu.getScaledGoldTime(36), 64, menu.getScaledGoldTime(36), 64, 36);

         }
        int lootX=125;
        int lootY=140;
        guiGraphics.drawString(this.font, "Bonus Loot Time: ", x+lootX, y+lootY, 4210752, false);
        guiGraphics.drawString(this.font, BeaconiteLib.getDurationCalc(menu.data.get(3)), x+lootX+15, y+lootY+10, 4210752, false);

        if(menu.lootActive()) {
            guiGraphics.blit(LOOT_PILE, x+131, y+139-menu.getScaledLootTime(34), 0, 34-menu.getScaledLootTime(34), 62, menu.getScaledLootTime(34), 62, 34);

        }


    }
}




