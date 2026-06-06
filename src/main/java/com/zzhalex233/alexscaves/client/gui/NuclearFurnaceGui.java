package com.zzhalex233.alexscaves.client.gui;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.server.inventory.NuclearFurnaceContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class NuclearFurnaceGui extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AlexsCaves.MODID, "textures/gui/nuclear_furnace.png");
    private final NuclearFurnaceContainer furnace;

    public NuclearFurnaceGui(NuclearFurnaceContainer furnace, InventoryPlayer inventory) {
        super(furnace);
        this.furnace = furnace;
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = I18n.format("alexscaves.container.nuclear_furnace_blasting");
        fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        drawTexturedModalRect(guiLeft + 91, guiTop + 35, 176, 0, Math.round(furnace.getCookScale() * 24.0F), 16);
        drawTexturedModalRect(guiLeft + 67, guiTop + 35 + Math.round((1.0F - furnace.getFissionScale()) * 14.0F), 176, 16 + Math.round((1.0F - furnace.getFissionScale()) * 14.0F), 14, Math.round(furnace.getFissionScale() * 14.0F));
        drawTexturedModalRect(guiLeft + 37, guiTop + 35, 190, 16, 14, Math.round(furnace.getBarrelScale() * 14.0F));
        drawTexturedModalRect(guiLeft + 8, guiTop + 70 - Math.round(furnace.getWasteScale() * 54.0F), 204, 0, 8, Math.round(furnace.getWasteScale() * 54.0F));
    }
}
