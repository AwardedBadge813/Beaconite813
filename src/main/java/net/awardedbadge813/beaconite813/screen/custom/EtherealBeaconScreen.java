package net.awardedbadge813.beaconite813.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.awardedbadge813.beaconite813.beaconite813;
import net.awardedbadge813.beaconite813.entity.EtherealBeaconBlockEntity;
import net.awardedbadge813.beaconite813.util.BeaconiteLib;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EtherealBeaconScreen extends AbstractContainerScreen<EtherealBeaconMenu> {
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui.png");
    private static final ResourceLocation GUI_EMPTY =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/misc/empty_gui.png");
    private static final ResourceLocation GUI_AURA =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui_aura.png");
    private static final ResourceLocation GUI_DIFFUSE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui_diffuse.png");
    private static final ResourceLocation GUI_TRANSMUTE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui_transmute.png");
    private static final ResourceLocation GUI_INFUSE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui_infuse.png");
    private static final ResourceLocation GUI_INACTIVE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_gui_inactive.png");
    private static final ResourceLocation PROGRESS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(beaconite813.MOD_ID, "textures/gui/ethereal_beacon/ethereal_progress.png");

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

    public EtherealBeaconScreen(EtherealBeaconMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.titleLabelX = -34;
        this.titleLabelY = -39;
        this.inventoryLabelX = 8;
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
        int x=(width-imageWidth)/2;
        int y=(height-imageHeight)/2;
        String effectText;
        try {
            effectText = EtherealBeaconBlockEntity.decodeEffect(menu.data.get(0)).value().getDisplayName().getString();
        } catch (Exception e) {
            effectText = "none";
        }


        guiGraphics.blit(getGUI(menu.getOperation()), x, y, 0, 0, imageWidth, imageHeight);
        if(menu.getOperation()!=5) {
            guiGraphics.blit(GUI_EMPTY, x-128, y, 0, 0, 128, 51, 128, 51);
            guiGraphics.drawString(this.font, BeaconiteLib.getDurationCalc(menu.data.get(1))+" remaining", x-122, y+25, 0, false);
            guiGraphics.drawString(this.font, "Effect: "+effectText, x-122, y+5, 0, false);
            guiGraphics.drawString(this.font, OpToString(menu.getOperation()), x-122, y+15, 0, false);
            guiGraphics.blit(PROGRESS_TEXTURE, x+11, y+173, 0, 0, 28, menu.getScaledPotionTime(75), 28, 75);
        } else {
            guiGraphics.drawString(this.font, "Requires sky and", x+75, y+90, 0, false);
            guiGraphics.drawString(this.font, "level 10 beacon base!", x+75, y+100, 0, false);
        }


    }

    private ResourceLocation getGUI(int operation) {
        switch(operation) {
            case 1 -> {return GUI_AURA;}
            case 2 -> {return GUI_DIFFUSE;}
            case 3 -> {return GUI_TRANSMUTE;}
            case 4 -> {return GUI_INFUSE;}
            case 5 -> {return GUI_INACTIVE;}
            default -> {return GUI_TEXTURE;}
        }
    }

    public Component OpToString(int i) {
        switch (i) {
            case 1 -> {
                return Component.translatable("block.beaconite813.ethereal_mode.aura");
            }
            case 2 -> {
                return Component.translatable("block.beaconite813.ethereal_mode.diffusion");
            }
            case 3 -> {
                return Component.translatable("block.beaconite813.ethereal_mode.transmute");
            }
            case 4 -> {
                return Component.translatable("block.beaconite813.ethereal_mode.infuse");
            }
            default -> {return Component.literal("Storage");}
        }
    }





}


